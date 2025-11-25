package com.demoproject.fileservice.dto;

import com.demoproject.fileservice.entity.FileCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FileUploadRequest {

    @NotBlank
    private String originalName;

    @NotBlank
    private String contentType;

    @NotNull
    private Long size;

    @NotNull
    private FileCategory category;
}
