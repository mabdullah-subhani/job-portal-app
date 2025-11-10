package com.demoproject.candidateservice.repository;

import com.demoproject.candidateservice.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CandidateRepository extends JpaRepository<Candidate, UUID> {
    Optional<Candidate> findByUserId(Long userId);
}

