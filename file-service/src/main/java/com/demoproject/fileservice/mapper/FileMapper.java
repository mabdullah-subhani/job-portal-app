package com.demoproject.fileservice.mapper;

import com.demoproject.fileservice.dto.FileMetadataDto;
import com.demoproject.fileservice.entity.StoredFile;

public class FileMapper {
    public static FileMetadataDto toDto(StoredFile file, String downloadUrl) {
        return FileMetadataDto.builder()
                .id(file.getId())
                .ownerUserId(file.getOwnerUserId())
                .category(file.getCategory())
                .originalName(file.getOriginalName())
                .contentType(file.getContentType())
                .size(file.getSize())
                .status(file.getStatus())
                .downloadUrl(downloadUrl)
                .createdAt(file.getCreatedAt())
                .updatedAt(file.getUpdatedAt())
                .build();
    }
}
