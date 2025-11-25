package com.demoproject.employerservice.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class UpdateEmployerRequest {
    private String companyName;
    @Email
    private String email;
    private String phone;
    private String website;
    private String address;
    private String industry;
    private String description;
    private Set<UUID> jobIds;
}

