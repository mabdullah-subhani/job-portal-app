package com.demoproject.candidateservice.mapper;

import com.demoproject.candidateservice.dto.CandidateResponse;
import com.demoproject.candidateservice.dto.CreateCandidateRequest;
import com.demoproject.candidateservice.dto.UpdateCandidateRequest;
import com.demoproject.candidateservice.entity.Candidate;
import org.mapstruct.*;

import java.util.HashSet;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CandidateMapper {

    // -------------------- Create --------------------
    @Mapping(target = "id", ignore = true)
    Candidate toEntity(CreateCandidateRequest request);

    // -------------------- Response --------------------
    CandidateResponse toResponse(Candidate candidate);

    // -------------------- Partial Update --------------------
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdateCandidateRequest request, @MappingTarget Candidate candidate);

    // This runs after the regular field mapping
    @AfterMapping
    default void updateSkillsAfterMapping(UpdateCandidateRequest request, @MappingTarget Candidate candidate) {
        if (request.getSkills() != null && !request.getSkills().isEmpty()) {
            candidate.setSkills(new HashSet<>(request.getSkills()));
        }
        // If null or empty -> do nothing (retain old skills)
    }
}