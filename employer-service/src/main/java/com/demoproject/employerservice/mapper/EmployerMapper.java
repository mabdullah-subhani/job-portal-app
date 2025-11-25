package com.demoproject.employerservice.mapper;

import com.demoproject.employerservice.dto.CreateEmployerRequest;
import com.demoproject.employerservice.dto.EmployerResponse;
import com.demoproject.employerservice.dto.UpdateEmployerRequest;
import com.demoproject.employerservice.entity.Employer;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmployerMapper {

    Employer toEntity(CreateEmployerRequest req);

    EmployerResponse toResponse(Employer employer);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdateEmployerRequest dto, @MappingTarget Employer employer);
}

