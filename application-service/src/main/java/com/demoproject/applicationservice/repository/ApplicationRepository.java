package com.demoproject.applicationservice.repository;

import com.demoproject.applicationservice.entity.ApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApplicationRepository extends JpaRepository<ApplicationEntity, UUID> {
    boolean existsByCandidateIdAndJobId(UUID candidateId, UUID jobId);
    List<ApplicationEntity> findByJobId(UUID jobId);
    List<ApplicationEntity> findByEmployerId(UUID employerId);
    Optional<ApplicationEntity> findByIdAndCandidateId(UUID id, UUID candidateId);
    List<ApplicationEntity> findByCandidateId(UUID candidateId);
}
