package com.demoproject.jobservice.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class JobPostResponse {
    private UUID id;
    private String title;
    private String description;
    private String location;
    private String employmentType;
    private Double salary;
    private UUID employerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
