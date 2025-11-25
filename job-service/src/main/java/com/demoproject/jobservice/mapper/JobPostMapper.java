package com.demoproject.jobservice.mapper;

import com.demoproject.jobservice.dto.CreateJobPostRequest;
import com.demoproject.jobservice.dto.UpdateJobPostRequest;
import com.demoproject.jobservice.dto.JobPostResponse;
import com.demoproject.jobservice.entity.JobPost;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface JobPostMapper {

    JobPost toEntity(CreateJobPostRequest dto);

    JobPostResponse toResponse(JobPost entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdateJobPostRequest dto, @MappingTarget JobPost entity);
}
