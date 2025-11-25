package com.demoproject.candidateservice.service;

import com.demoproject.candidateservice.dto.CandidateResponse;
import com.demoproject.candidateservice.dto.CreateCandidateRequest;
import com.demoproject.candidateservice.dto.UpdateCandidateRequest;
import com.demoproject.candidateservice.entity.Candidate;
import com.demoproject.candidateservice.mapper.CandidateMapper;
import com.demoproject.candidateservice.repository.CandidateRepository;
import com.demoproject.candidateservice.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateServiceImpl implements CandidateService {

    private final CandidateRepository candidateRepository;
    private final CandidateMapper candidateMapper;

    @Override
    @Transactional
    public CandidateResponse createCandidate(CreateCandidateRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        candidateRepository.findByUserId(userId).ifPresent(c -> {
            throw new RuntimeException("Candidate profile already exists for this user.");
        });

        request.setUserId(userId);
        Candidate candidate = candidateMapper.toEntity(request);
        candidate = candidateRepository.save(candidate);
        return candidateMapper.toResponse(candidate);
    }

    @Override
    @Transactional
    public CandidateResponse updateCandidate(UUID id, UpdateCandidateRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        Candidate candidate = candidateRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Candidate not found or access denied"));
        candidateMapper.updateEntityFromRequest(request, candidate);
        candidateRepository.save(candidate);
        return candidateMapper.toResponse(candidate);
    }

    @Override
    public CandidateResponse getCandidate(UUID id) {
        Long userId = SecurityUtils.getCurrentUserId();
        Candidate candidate = candidateRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Candidate not found or access denied"));
        return candidateMapper.toResponse(candidate);
    }

    @Override
    public CandidateResponse getCandidateByUserId(Long userId) {
        Candidate candidate = candidateRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));
        return candidateMapper.toResponse(candidate);
    }

    @Override
    public List<CandidateResponse> getAllPublicCandidates() {
        return candidateRepository.findAll()
                .stream()
                .map(candidateMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CandidateResponse> searchCandidates(Set<String> skills, String location, Integer experienceYears) {
        if (skills != null && skills.isEmpty()) skills = null;
        if (experienceYears != null && experienceYears <= 0) experienceYears = null;
        if (location != null && location.isBlank()) location = null;

        return candidateRepository.searchCandidates(skills, location, experienceYears)
                .stream()
                .map(candidateMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCandidate(UUID id) {
        Long userId = SecurityUtils.getCurrentUserId();
        Candidate candidate = candidateRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Candidate not found or access denied"));
        candidateRepository.delete(candidate);
    }

    @Override
    @Transactional
    public void updateResumeUrl(Long userId, UUID fileId, String resumeUrl) {
        Candidate candidate = candidateRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        candidate.setResumeFileUrl(resumeUrl);
        candidate.setResumeFileId(fileId);

        // If resume deleted
        if (resumeUrl == null) {
            candidate.setResumeFileId(null);
        }

        candidateRepository.save(candidate);
    }

}