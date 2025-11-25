package com.demoproject.fileservice.dto;

import com.demoproject.fileservice.entity.FileCategory;
import com.demoproject.fileservice.entity.FileStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class FileMetadataDto {
    private UUID id;
    private Long ownerUserId;
    private FileCategory category;
    private String originalName;
    private String contentType;
    private Long size;
    private FileStatus status;
    private String downloadUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
