package com.demoproject.jobservice.dto;

import lombok.Data;

@Data
public class JobSearchRequest {
    private String keyword;
    private String location;
    private String employmentType;
    private Double minSalary;
    private Double maxSalary;
}

