package com.demoproject.employerservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
public class EmployerDTO {
    private Long id;

    @NotBlank(message = "Company name is required")
    private String companyName;

    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phone;

    private String website;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Industry is required")
    private String industry;

    @Size(max = 500, message = "Description can’t exceed 500 characters")
    private String description;

}
