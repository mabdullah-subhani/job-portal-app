package com.demoproject.candidateservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCandidateRequest {
    private Long userId;
    @NotBlank(message = "Full name is required")
    private String fullName;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String location;
    private String education;
    private Integer experience;
    @Builder.Default
    private Set<String> skills = new HashSet<>();
}