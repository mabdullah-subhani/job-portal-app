package com.demoproject.applicationservice.dto;

import lombok.Builder;
import lombok.Data;

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
}
