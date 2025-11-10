package com.demoproject.candidateservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class CandidateResponse {
    private UUID id;
    private Long userId;
    private String username;
    private String email;
    private String phoneNumber;
    private String location;
    private String education;
    private String experience;
    private String skills;
    private String resumeUrl;
    private LocalDate dateOfBirth;
}

