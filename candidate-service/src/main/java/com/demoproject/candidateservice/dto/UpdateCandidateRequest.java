package com.demoproject.candidateservice.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class UpdateCandidateRequest {
    private String fullName;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String location;
    private String education;
    private Integer experience;
    private Set<String> skills;
}