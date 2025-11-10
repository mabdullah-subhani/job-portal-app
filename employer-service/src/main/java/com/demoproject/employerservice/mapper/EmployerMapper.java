package com.demoproject.employerservice.mapper;

import com.demoproject.employerservice.dto.EmployerDTO;
import com.demoproject.employerservice.entity.Employer;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmployerMapper {
    EmployerDTO toDTO(Employer employer);

    Employer toEntity(EmployerDTO dto);
}
