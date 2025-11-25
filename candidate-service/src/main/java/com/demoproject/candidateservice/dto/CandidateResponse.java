package com.demoproject.candidateservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class CandidateResponse {
    private UUID id;
    private Long userId;
    private String fullName;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String location;
    private String education;
    private Integer experience;
    private Set<String> skills;
    private UUID resumeFileId;
    private String resumeFileUrl;
}