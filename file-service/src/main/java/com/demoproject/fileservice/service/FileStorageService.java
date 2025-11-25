package com.demoproject.fileservice.service;

import com.demoproject.fileservice.dto.FileUploadResponse;
import com.demoproject.fileservice.entity.FileCategory;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.UUID;

public interface FileStorageService {
    FileUploadResponse presignUpload(String originalName, String contentType, long size,
                                     UUID fileId, String s3Key, FileCategory category, Duration expiresIn);

    String generateDownloadUrl(UUID fileId,String s3Key, Duration expiresIn);

    void deleteFromStorage(String s3Key);

    FileUploadResponse uploadDirect(Long ownerUserId, MultipartFile file, FileCategory category);

    FileUploadResponse uploadLocal(Long ownerUserId, MultipartFile file, FileCategory category);

}


