package com.demoproject.candidateservice.mapper;

import com.demoproject.candidateservice.dto.CandidateResponse;
import com.demoproject.candidateservice.dto.CreateCandidateRequest;
import com.demoproject.candidateservice.dto.UpdateCandidateRequest;
import com.demoproject.candidateservice.entity.Candidate;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CandidateMapper {

    Candidate toEntity(CreateCandidateRequest request);
    CandidateResponse toResponse(Candidate candidate);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdateCandidateRequest request, @MappingTarget Candidate candidate);
}

