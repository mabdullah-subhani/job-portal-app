package com.demoproject.jobservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class JobPostDTO {
    private Long id;

    @NotBlank(message = "Job title is required")
    private String title;

    @NotBlank(message = "Job description is required")
    private String description;

    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "Employment type is required")
    private String employmentType;

    @NotNull(message = "Salary is required")
    private Double salary;

    private LocalDateTime createdAt;
    private Long employerId;
}
