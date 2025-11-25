package com.demoproject.applicationservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ApplicationResponse {
    private UUID id;
    private UUID candidateId;
    private UUID jobId;
    private UUID employerId;
    private UUID resumeFileId;
    private String status;
    private String candidateFullName;
    private Integer candidateExperience;
    private String candidateResumeUrl;
}
