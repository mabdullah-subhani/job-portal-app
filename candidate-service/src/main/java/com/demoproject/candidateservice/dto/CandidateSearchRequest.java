package com.demoproject.candidateservice.dto;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class CandidateSearchRequest {
    private Set<String> skills = new HashSet<>(); // For multi-skill search
    private String location;
    private Integer experience;
}
