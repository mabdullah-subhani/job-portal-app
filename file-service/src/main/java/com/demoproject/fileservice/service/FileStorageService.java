package com.demoproject.fileservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public String uploadFile(MultipartFile file) {
        String key = "resumes/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

        try {
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .contentType(file.getContentType())
                            .build(),
                    software.amazon.awssdk.core.sync.RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            log.info("File uploaded successfully to S3: {}", key);
            return key;

        } catch (IOException e) {
            log.error("File upload failed: {}", e.getMessage());
            throw new RuntimeException("File upload failed", e);
        }
    }

    public byte[] downloadFile(String key) {
        try {
            GetObjectResponse response = s3Client.getObject(
                    GetObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build(),
                    software.amazon.awssdk.core.sync.ResponseTransformer.toBytes()
            );

            log.info("File downloaded successfully: {}", key);
            return response.asByteArray();

        } catch (Exception e) {
            log.error("File download failed: {}", e.getMessage());
            throw new RuntimeException("File download failed", e);
        }
    }

    public void deleteFile(String key) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());

            log.info("File deleted successfully: {}", key);
        } catch (Exception e) {
            log.error("File deletion failed: {}", e.getMessage());
            throw new RuntimeException("File deletion failed", e);
        }
    }
}

