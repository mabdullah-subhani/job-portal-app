package com.demoproject.fileservice.dto;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class FileUploadResponse {
    private UUID fileId;
    private String s3Key;
    private String uploadUrl;
    private Long expiresInSeconds;
    private String downloadUrl;

}


