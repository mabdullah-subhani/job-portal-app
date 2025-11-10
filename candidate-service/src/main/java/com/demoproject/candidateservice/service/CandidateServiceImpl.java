package com.demoproject.candidateservice.service;

import com.demoproject.candidateservice.dto.CandidateResponse;
import com.demoproject.candidateservice.dto.CreateCandidateRequest;
import com.demoproject.candidateservice.dto.UpdateCandidateRequest;
import com.demoproject.candidateservice.entity.Candidate;
import com.demoproject.candidateservice.exception.ResourceNotFoundException;
import com.demoproject.candidateservice.mapper.CandidateMapper;
import com.demoproject.candidateservice.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidateServiceImpl implements CandidateService {

    private final CandidateRepository candidateRepository;
    private final CandidateMapper candidateMapper;

    @Override
    @Transactional
    public CandidateResponse createCandidate(CreateCandidateRequest request) {
        Candidate candidate = candidateMapper.toEntity(request);
        candidate = candidateRepository.save(candidate);
        return candidateMapper.toResponse(candidate);
    }

    @Override
    @Transactional
    public CandidateResponse updateCandidate(UUID id, UpdateCandidateRequest request) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

        candidateMapper.updateEntityFromRequest(request, candidate);
        candidateRepository.save(candidate);

        return candidateMapper.toResponse(candidate);
    }

    @Override
    public CandidateResponse getCandidate(UUID id) {
        return candidateRepository.findById(id)
                .map(candidateMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));
    }

    @Override
    public List<CandidateResponse> getAllCandidates() {
        return candidateRepository.findAll().stream()
                .map(candidateMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCandidate(UUID id) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));
        candidateRepository.delete(candidate);
    }
}

