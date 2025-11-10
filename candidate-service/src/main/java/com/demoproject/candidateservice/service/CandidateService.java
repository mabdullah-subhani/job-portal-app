package com.demoproject.candidateservice.service;

import com.demoproject.candidateservice.dto.CandidateResponse;
import com.demoproject.candidateservice.dto.CreateCandidateRequest;

import com.demoproject.candidateservice.dto.UpdateCandidateRequest;

import java.util.List;
import java.util.UUID;

public interface CandidateService {

    CandidateResponse createCandidate(CreateCandidateRequest request);
    CandidateResponse updateCandidate(UUID id, UpdateCandidateRequest request);
    CandidateResponse getCandidate(UUID id);
    List<CandidateResponse> getAllCandidates();
    void deleteCandidate(UUID id);
}

