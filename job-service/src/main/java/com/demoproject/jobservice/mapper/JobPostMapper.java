package com.demoproject.jobservice.mapper;
import com.demoproject.jobservice.dto.JobPostDTO;
import com.demoproject.jobservice.entity.JobPost;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface JobPostMapper {

    JobPostDTO toDTO(JobPost jobPost);

    JobPost toEntity(JobPostDTO dto);
}
