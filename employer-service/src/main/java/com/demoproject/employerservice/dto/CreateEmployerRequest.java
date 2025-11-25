package com.demoproject.employerservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
public class CreateEmployerRequest {

    @NotBlank(message = "Company name required")
    private String companyName;

    @NotBlank(message = "Email required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Phone number required")
    private String phone;

    private String website;

    @NotBlank(message = "Address required")
    private String address;

    @NotBlank(message = "Industry required")
    private String industry;

    @Size(max = 500)
    private String description;
}


