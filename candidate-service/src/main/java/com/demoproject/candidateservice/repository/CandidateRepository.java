package com.demoproject.candidateservice.repository;

import com.demoproject.candidateservice.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface CandidateRepository extends JpaRepository<Candidate, UUID> {

    // Find candidate by ID and userId for ownership check
    Optional<Candidate> findByIdAndUserId(UUID id, Long userId);
    @Query("""
SELECT c FROM Candidate c
WHERE (:location IS NULL OR c.location = :location)
AND (:experience IS NULL OR c.experience >= :experience)
AND (
       :skills IS NULL 
       OR EXISTS (
             SELECT 1 FROM Candidate c2 JOIN c2.skills s
             WHERE c2.id = c.id AND s IN :skills
       )
    )
""")
    List<Candidate> searchCandidates(
            @Param("skills") Set<String> skills,
            @Param("location") String location,
            @Param("experience") Integer experience
    );




    Optional<Candidate> findByUserId(Long userId);

}
