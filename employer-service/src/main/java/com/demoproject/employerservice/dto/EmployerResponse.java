package com.demoproject.employerservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class EmployerResponse {
    private UUID id;
    private Long userId;
    private String companyName;
    private String email;
    private String phone;
    private String website;
    private String address;
    private String industry;
    private String description;
    private Set<UUID> jobIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

