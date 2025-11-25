 package com.demoproject.fileservice.service;

import com.demoproject.fileservice.config.AwsS3Properties;
import com.demoproject.fileservice.config.FileStorageProperties;
import com.demoproject.fileservice.dto.FileUploadResponse;
import com.demoproject.fileservice.entity.FileCategory;
import com.demoproject.fileservice.entity.FileStatus;
import com.demoproject.fileservice.entity.StoredFile;
import com.demoproject.fileservice.exception.BadRequestException;
import com.demoproject.fileservice.exception.StorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final AwsS3Properties props;
    private final FileMetadataService metadataService;
    private final FileStorageProperties fileStorageProperties; // NEW

    @Override
    public FileUploadResponse presignUpload(String originalName, String contentType, long size,
                                            UUID fileId, String s3Key, FileCategory category, Duration expiresIn) {

        validateContentType(contentType, category);
        validateSize(size, category);

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(props.getBucketName())
                .key(s3Key)
                .contentType(contentType)
                .contentLength(size)
                .acl(ObjectCannedACL.PRIVATE)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(expiresIn)
                .putObjectRequest(putRequest)
                .build();

        PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(presignRequest);

        return FileUploadResponse.builder()
                .fileId(fileId)
                .s3Key(s3Key)
                .uploadUrl(presigned.url().toString())
                .expiresInSeconds(expiresIn.getSeconds())
                .build();
    }


    @Override
    public FileUploadResponse uploadLocal(Long ownerUserId, MultipartFile file, FileCategory category) {
        try {
            String originalName = file.getOriginalFilename();
            String contentType = file.getContentType();
            long size = file.getSize();

            validateContentType(contentType, category);
            validateSize(size, category);

            UUID fileId = UUID.randomUUID();

            // build filename and path
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf('.'));
            }
            String filename = fileId.toString() + ext;
            String relativePath = switch (category) {
                case RESUME -> "candidates/" + ownerUserId + "/resumes/" + filename;
                case PROFILE_IMAGE -> "candidates/" + ownerUserId + "/profile/" + filename;
                case COMPANY_LOGO -> "employers/" + ownerUserId + "/logo/" + filename;
                default -> "others/" + ownerUserId + "/" + filename;
            };

            // ensure directories exist
            java.nio.file.Path baseDir = java.nio.file.Paths.get(fileStorageProperties.getTempDir()).toAbsolutePath();
            java.nio.file.Path fullPath = baseDir.resolve(relativePath);
            java.nio.file.Files.createDirectories(fullPath.getParent());

            // write file
            java.nio.file.Files.write(fullPath, file.getBytes());

            // store metadata; use "local:" prefix in s3Key to differentiate
            String s3Key = "local:" + relativePath.replace("\\", "/");

            StoredFile meta = StoredFile.builder()
                    .id(fileId)
                    .ownerUserId(ownerUserId)
                    .category(category)
                    .originalName(originalName)
                    .contentType(contentType)
                    .size(size)
                    .s3Key(s3Key)
                    .status(FileStatus.AVAILABLE)
                    .build();

            metadataService.createMetadata(meta);

            // build download URL that points through gateway to file-service local-download endpoint
            String downloadUrl = fileStorageProperties.getPublicBaseUrl()
                    + "/api/files/" + fileId + "/local-download";

            return FileUploadResponse.builder()
                    .fileId(fileId)
                    .s3Key(s3Key)
                    .uploadUrl("local")
                    .expiresInSeconds(0L)
                    .downloadUrl(downloadUrl)
                    .build();

        } catch (Exception e) {
            log.error("Local upload failed", e);
            throw new StorageException("Failed to upload file locally", e);
        }
    }



    @Override
    public String generateDownloadUrl(UUID fileId,String s3Key, Duration expiresIn) {
        if (s3Key == null) return null;
        if (s3Key.startsWith("local:")) {
            return fileStorageProperties.getPublicBaseUrl() +
                    "/api/files/" + fileId + "/local-download";
        }
 else {
            // S3 flow unchanged: generate presigned GET URL
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(props.getBucketName())
                    .key(s3Key)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(expiresIn)
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presigned = s3Presigner.presignGetObject(presignRequest);
            return presigned.url().toString();
        }
    }

    @Override
    public void deleteFromStorage(String s3Key) {
        try {
            DeleteObjectRequest dreq = DeleteObjectRequest.builder()
                    .bucket(props.getBucketName())
                    .key(s3Key)
                    .build();
            s3Client.deleteObject(dreq);
        } catch (S3Exception e) {
            log.error("Failed to delete S3 object {}", s3Key, e);
            throw new StorageException("Failed to delete object from S3", e);
        }
    }

    @Override
    public FileUploadResponse uploadDirect(Long ownerUserId, MultipartFile file, FileCategory category) {
        try {
            String originalName = file.getOriginalFilename();
            String contentType = file.getContentType();
            long size = file.getSize();

            validateContentType(contentType, category);
            validateSize(size, category);

            UUID fileId = UUID.randomUUID();
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf('.'));
            }
            String s3Key = "uploads/" + ownerUserId + "/" + fileId + ext;

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(props.getBucketName())
                    .key(s3Key)
                    .acl(ObjectCannedACL.PRIVATE)
                    .contentType(contentType)
                    .contentLength(size)
                    .build();

            log.info("Uploading file: {} ({} bytes) to s3Key: {}", originalName, size, s3Key);
            s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));
            log.info("Upload finished successfully");

            StoredFile stored = StoredFile.builder()
                    .id(fileId)
                    .ownerUserId(ownerUserId)
                    .category(category)
                    .originalName(originalName)
                    .contentType(contentType)
                    .size(size)
                    .s3Key(s3Key)
                    .status(FileStatus.AVAILABLE)
                    .build();

            metadataService.createMetadata(stored);

            String downloadUrl = generateDownloadUrl(fileId, s3Key, props.getPresignDownloadTtl());

            return FileUploadResponse.builder()
                    .fileId(fileId)
                    .s3Key(s3Key)
                    .downloadUrl(downloadUrl)
                    .expiresInSeconds(props.getPresignDownloadTtl().getSeconds())
                    .build();

        } catch (Exception ex) {
            log.error("Direct upload failed", ex);
            throw new StorageException("Failed to upload file directly", ex);
        }
    }

    private void validateContentType(String contentType, FileCategory category) {
        if (contentType == null) throw new BadRequestException("Content type required");
        switch (category) {
            case RESUME -> {
                if (!List.of("application/pdf", "application/msword",
                                "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                        .contains(contentType)) {
                    throw new BadRequestException("Invalid resume type. Only PDF/DOC/DOCX allowed");
                }
            }
            case PROFILE_IMAGE, COMPANY_LOGO -> {
                if (!List.of("image/jpeg", "image/png").contains(contentType)) {
                    throw new BadRequestException("Invalid image type. Only JPG/PNG allowed");
                }
            }
            default -> throw new BadRequestException("Unsupported file category");
        }
    }

    private void validateSize(long size, FileCategory category) {
        if (category == FileCategory.RESUME && size > props.getMaxResumeSize()) {
            throw new BadRequestException("Resume exceeds max allowed size of " + props.getMaxResumeSize() + " bytes");
        }
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
