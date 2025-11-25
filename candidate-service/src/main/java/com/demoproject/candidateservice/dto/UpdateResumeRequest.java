package com.demoproject.candidateservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class UpdateResumeRequest {
    private UUID resumeFileId;
    private String resumeUrl;
}


