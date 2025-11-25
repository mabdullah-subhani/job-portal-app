package com.demoproject.fileservice.controller;

import com.demoproject.fileservice.config.CandidateClient;
import com.demoproject.fileservice.config.FileStorageProperties;
import com.demoproject.fileservice.config.UpdateResumeRequest;
import com.demoproject.fileservice.dto.FileMetadataDto;
import com.demoproject.fileservice.dto.FileUploadRequest;
import com.demoproject.fileservice.dto.FileUploadResponse;
import com.demoproject.fileservice.entity.FileCategory;
import com.demoproject.fileservice.entity.FileStatus;
import com.demoproject.fileservice.entity.StoredFile;
import com.demoproject.fileservice.exception.UnauthorizedException;
import com.demoproject.fileservice.mapper.FileMapper;
import com.demoproject.fileservice.payload.ApiResponse;
import com.demoproject.fileservice.security.SecurityUtils;
import com.demoproject.fileservice.service.FileMetadataService;
import com.demoproject.fileservice.service.FileStorageService;
import com.demoproject.fileservice.config.AwsS3Properties;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Validated
public class FileController {

    private final FileStorageService storageService;
    private final FileMetadataService metadataService;
    private final FileStorageProperties fileStorageProperties;

    // Feign client to candidate-service
    private final CandidateClient candidateClient;

    private final AwsS3Properties props;

    @PostMapping("/pre-sign")
    @Operation(summary = "Generate pre-signed S3 upload URL")
    public ResponseEntity<ApiResponse<FileUploadResponse>> presignUpload(
            @Valid @RequestBody FileUploadRequest request
    ) {
        Long ownerUserId = SecurityUtils.getCurrentUserId();
        UUID fileId = UUID.randomUUID();
        String s3Key = buildS3Key(ownerUserId, request.getCategory(), fileId, request.getOriginalName());

        StoredFile meta = StoredFile.builder()
                .id(fileId)
                .ownerUserId(ownerUserId)
                .category(request.getCategory())
                .originalName(request.getOriginalName())
                .contentType(request.getContentType())
                .size(request.getSize())
                .s3Key(s3Key)
                .status(FileStatus.UPLOADING)
                .build();

        metadataService.createMetadata(meta);

        FileUploadResponse response = storageService.presignUpload(
                request.getOriginalName(),
                request.getContentType(),
                request.getSize(),
                fileId,
                s3Key,
                request.getCategory(),
                props.getPresignUploadTtl()
        );

        return ResponseEntity.ok(ApiResponse.ok("Upload URL generated", response));
    }

    @PostMapping("/{fileId}/complete")
    @Operation(summary = "Mark upload as completed")
    public ResponseEntity<ApiResponse<Void>> completeUpload(@PathVariable("fileId") UUID fileId) {

        StoredFile meta = metadataService.getMetadata(fileId);
        Long currentUserId = SecurityUtils.getCurrentUserId();

        if (!currentUserId.equals(meta.getOwnerUserId())) {
            throw new UnauthorizedException("Not file owner");
        }

        metadataService.markAvailable(fileId);

        // If resume uploaded → Update resume URL via Feign → Candidate service
        if (meta.getCategory() == FileCategory.RESUME) {
            // generate download URL for this file (works for both local and s3 because impl uses fileId + s3Key)
            String downloadUrl = storageService.generateDownloadUrl(meta.getId(), meta.getS3Key(), props.getPresignDownloadTtl());

            try {
                // notify candidate-service with (fileId, downloadUrl)
                candidateClient.updateResumeUrl(
                        meta.getOwnerUserId(),
                        new UpdateResumeRequest(meta.getId(), downloadUrl)
                );
            } catch (Exception ex) {
                // Log but don't block the upload success (feign/gateway might be temporarily down)
            }
        }

        return ResponseEntity.ok(ApiResponse.ok("File upload completed", null));
    }

    @GetMapping("/{fileId}/download-url")
    @Operation(summary = "Generate S3 download URL")
    public ResponseEntity<ApiResponse<String>> downloadUrl(@PathVariable("fileId") UUID fileId) {

        StoredFile meta = metadataService.getMetadata(fileId);
        Long currentUserId = SecurityUtils.getCurrentUserId();

        if (!currentUserId.equals(meta.getOwnerUserId())) {
            throw new UnauthorizedException("Not allowed to download this file");
        }

        String url = storageService.generateDownloadUrl(meta.getId(), meta.getS3Key(), props.getPresignDownloadTtl());

        return ResponseEntity.ok(ApiResponse.ok("Download URL generated", url));
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<ApiResponse<FileMetadataDto>> getMetadata(@PathVariable("fileId") UUID fileId) {

        StoredFile meta = metadataService.getMetadata(fileId);
        Long currentUserId = SecurityUtils.getCurrentUserId();

        if (!currentUserId.equals(meta.getOwnerUserId())) {
            throw new UnauthorizedException("Not allowed to view this file");
        }

        // Build download URL
        String downloadUrl;
        if (meta.getS3Key().startsWith("local:")) {
            downloadUrl = fileStorageProperties.getPublicBaseUrl()
                    + "/api/files/" + meta.getId() + "/local-download";
        } else {
            downloadUrl = storageService.generateDownloadUrl(meta.getId(), meta.getS3Key(), props.getPresignDownloadTtl());
        }

        return ResponseEntity.ok(ApiResponse.ok(
                "File metadata fetched",
                FileMapper.toDto(meta, downloadUrl)
        ));
    }

    // Local upload endpoint
    @PostMapping("/local-upload")
    @Operation(summary = "Upload file to local storage (not S3)")
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadLocal(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "category", required = false) FileCategory category
    ) {
        Long ownerUserId = SecurityUtils.getCurrentUserId();
        FileCategory cat = category == null ? FileCategory.RESUME : category;

        FileUploadResponse response = storageService.uploadLocal(ownerUserId, file, cat);

        // If resume then update candidate profile with download URL
        if (cat == FileCategory.RESUME) {
            try {
                candidateClient.updateResumeUrl(
                        ownerUserId,
                        new UpdateResumeRequest(response.getFileId(), response.getDownloadUrl())
                );
            } catch (Exception ex) {
                // ignore best effort
            }
        }

        return ResponseEntity.ok(ApiResponse.ok("File uploaded locally successfully", response));
    }

    // Local download: streams file from local storage
    @GetMapping("/{fileId}/local-download")
    public ResponseEntity<org.springframework.core.io.Resource> localDownload(@PathVariable("fileId") UUID fileId) {
        StoredFile meta = metadataService.getMetadata(fileId);

        if (!meta.getS3Key().startsWith("local:")) {
            // Not a local file
            throw new com.demoproject.fileservice.exception.BadRequestException("File is not stored locally");
        }

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!currentUserId.equals(meta.getOwnerUserId())) {
            throw new UnauthorizedException("Not allowed to download this file");
        }

        String relativePath = meta.getS3Key().substring("local:".length());
        java.nio.file.Path baseDir = java.nio.file.Paths.get(fileStorageProperties.getTempDir()).toAbsolutePath();
        java.nio.file.Path fullPath = baseDir.resolve(relativePath);

        if (!java.nio.file.Files.exists(fullPath)) {
            throw new com.demoproject.fileservice.exception.NotFoundException("File not found on local storage");
        }

        org.springframework.core.io.Resource resource = new org.springframework.core.io.PathResource(fullPath);
        String contentType = meta.getContentType() != null ? meta.getContentType() : "application/octet-stream";

        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + meta.getOriginalName() + "\"")
                .header(org.springframework.http.HttpHeaders.CONTENT_LENGTH, String.valueOf(meta.getSize()))
                .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                .body(resource);
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<ApiResponse<Void>> deleteFile(@PathVariable("fileId") UUID fileId) {

        StoredFile meta = metadataService.getMetadata(fileId);
        Long currentUserId = SecurityUtils.getCurrentUserId();

        if (!currentUserId.equals(meta.getOwnerUserId())) {
            throw new UnauthorizedException("Not allowed to delete this file");
        }

        metadataService.markDeleted(fileId);
        storageService.deleteFromStorage(meta.getS3Key());
        metadataService.deleteMetadata(fileId);

        // Clear resume URL from candidate profile
        if (meta.getCategory() == FileCategory.RESUME) {
            try {
                candidateClient.updateResumeUrl(
                        currentUserId,
                        new UpdateResumeRequest(null, null) // clear both fileId and url
                );
            } catch (Exception ex) {
                // best-effort
            }
        }

        return ResponseEntity.ok(ApiResponse.ok("File deleted successfully", null));
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "category", required = false) FileCategory category
    ) {
        Long ownerUserId = SecurityUtils.getCurrentUserId();
        FileCategory cat = category == null ? FileCategory.RESUME : category;

        // 1️⃣ Upload directly (local OR S3)
        FileUploadResponse response = storageService.uploadDirect(ownerUserId, file, cat);

        // 2️⃣ Fetch metadata from DB
        StoredFile meta = metadataService.getMetadata(response.getFileId());

        // 3️⃣ Build correct download URL depending on storage
        String downloadUrl;
        if (meta.getS3Key().startsWith("local:")) {
            downloadUrl = fileStorageProperties.getPublicBaseUrl()
                    + "/api/files/" + meta.getId() + "/local-download";
        } else {
            downloadUrl = storageService.generateDownloadUrl(
                    meta.getId(),          // UUID fileId
                    meta.getS3Key(),       // String s3Key
                    props.getPresignDownloadTtl()
            );
        }

        // 4️⃣ Add generated URL to response
        response.setDownloadUrl(downloadUrl);

        // 5️⃣ Update Candidate resume URL if category = RESUME
        if (cat == FileCategory.RESUME) {
            try {
                candidateClient.updateResumeUrl(
                        ownerUserId,
                        new UpdateResumeRequest(response.getFileId(), downloadUrl)
                );
            } catch (Exception ignored) {
                // ignore candidate service failure (network issue etc.)
            }
        }

        // 6️⃣ Final response
        return ResponseEntity.ok(
                ApiResponse.ok("File uploaded successfully", response)
        );
    }

    private String buildS3Key(Long ownerUserId, FileCategory category, UUID id, String originalName) {
        String folder = switch (category) {
            case RESUME -> "candidates/" + ownerUserId + "/resumes/";
            case PROFILE_IMAGE -> "candidates/" + ownerUserId + "/profile/";
            case COMPANY_LOGO -> "employers/" + ownerUserId + "/logo/";
            default -> "others/" + ownerUserId + "/";
        };

        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf('.'));
        }

        return folder + id + ext;
    }
}
