package com.demoproject.candidateservice.service;

import com.demoproject.candidateservice.dto.CandidateResponse;
import com.demoproject.candidateservice.dto.CreateCandidateRequest;
import com.demoproject.candidateservice.dto.UpdateCandidateRequest;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface CandidateService {

    CandidateResponse createCandidate(CreateCandidateRequest request);

    CandidateResponse updateCandidate(UUID id, UpdateCandidateRequest request);

    CandidateResponse getCandidate(UUID id);

    CandidateResponse getCandidateByUserId(Long userId);

    List<CandidateResponse> getAllPublicCandidates();

    List<CandidateResponse> searchCandidates(Set<String> skills, String location, Integer experienceYears);

    void deleteCandidate(UUID id);

    void updateResumeUrl(Long userId, UUID fileId, String resumeUrl);


}
