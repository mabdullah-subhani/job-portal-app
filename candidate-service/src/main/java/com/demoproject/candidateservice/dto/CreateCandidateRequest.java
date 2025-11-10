package com.demoproject.candidateservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCandidateRequest {

    private Long userId;

    @NotBlank(message = "Username is required")
    private String username;

    @Email(message = "Invalid email format")
    private String email;

    private String phoneNumber;
    private String location;
    private String education;
    private String experience;
    @Size(max = 500, message = "Skills description too long")
    private String skills;}

