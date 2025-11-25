package com.demoproject.jobservice.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateJobPostRequest {

    private String title;
    private String description;
    private String location;
    private String employmentType;
    private Double salary;
    private UUID employerId;
}
