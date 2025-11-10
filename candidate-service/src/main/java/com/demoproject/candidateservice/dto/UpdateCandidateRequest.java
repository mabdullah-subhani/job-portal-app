package com.demoproject.candidateservice.dto;

import lombok.Data;

@Data
public class UpdateCandidateRequest {

    private String phoneNumber;
    private String location;
    private String education;
    private String experience;
    private String skills;
    private String resumeUrl;
}

