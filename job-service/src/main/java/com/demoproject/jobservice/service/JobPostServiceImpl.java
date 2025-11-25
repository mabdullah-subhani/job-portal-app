package com.demoproject.jobservice.service;

import com.demoproject.jobservice.config.EmployerClient;
import com.demoproject.jobservice.dto.CreateJobPostRequest;
import com.demoproject.jobservice.dto.JobPostResponse;
import com.demoproject.jobservice.dto.UpdateJobPostRequest;
import com.demoproject.jobservice.entity.JobPost;
import com.demoproject.jobservice.exception.ResourceNotFoundException;
import com.demoproject.jobservice.mapper.JobPostMapper;
import com.demoproject.jobservice.payload.ApiResponse;
import com.demoproject.jobservice.repository.JobPostRepository;
import com.demoproject.jobservice.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobPostServiceImpl implements JobPostService {

    private final JobPostRepository repo;
    private final JobPostMapper mapper;
    private final EmployerClient employerClient;

    @Override
    @Transactional
    public JobPostResponse createJobPost(CreateJobPostRequest dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        ApiResponse<UUID> response = employerClient.getEmployerIdByUserId(userId);
        UUID employerId = response.getData();
        if (employerId == null) {
            throw new ResourceNotFoundException("Employer profile not found for user ID: " + userId);
        }
        JobPost entity = mapper.toEntity(dto);
        entity.setEmployerId(employerId);
        JobPost saved = repo.save(entity);

        employerClient.addJobIdToEmployer(employerId, saved.getId());
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public JobPostResponse updateJobPost(UUID jobPostId, UpdateJobPostRequest dto) {

        Long userId = SecurityUtils.getCurrentUserId();
        ApiResponse<UUID> response = employerClient.getEmployerIdByUserId(userId);
        UUID employerId = response.getData();
        JobPost existing = repo.findById(jobPostId)
                .orElseThrow(() -> new ResourceNotFoundException("Job post not found with ID: " + jobPostId));

        // Security: prevent employer from editing another company's job
        if (!existing.getEmployerId().equals(employerId)) {
            throw new AccessDeniedException("You are not allowed to update this job post.");
        }

        mapper.updateEntityFromRequest(dto, existing);

        return mapper.toResponse(repo.save(existing));
    }


    @Override
    public List<JobPostResponse> getAllJobPosts(int page, int size) {
        return repo.findAll(PageRequest.of(Math.max(page, 0), Math.max(size, 10)))
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobPostResponse> getJobPostsByEmployer(UUID employerId, int page, int size) {
        ApiResponse<Boolean> response = employerClient.checkEmployerExists(employerId);
        if (!Boolean.TRUE.equals(response.getData())) {
            throw new ResourceNotFoundException("Employer not found with ID: " + employerId);
        }

        return repo.findByEmployerId(employerId, PageRequest.of(page, size))
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteJobPost(UUID jobPostId) {
        JobPost existing = repo.findById(jobPostId)
                .orElseThrow(() -> new ResourceNotFoundException("Job post not found with ID: " + jobPostId));
        repo.delete(existing);
    }

    @Override
    public List<JobPostResponse> searchJobs(String keyword, String location,
                                            String employmentType, Double minSalary,
                                            Double maxSalary) {

        // Clean up input
        if (keyword != null && keyword.isBlank()) keyword = null;
        if (location != null && location.isBlank()) location = null;
        if (employmentType != null && employmentType.isBlank()) employmentType = null;
        if (minSalary != null && minSalary <= 0) minSalary = null;
        if (maxSalary != null && maxSalary <= 0) maxSalary = null;

        List<JobPost> results = repo.searchJobs(keyword, location, employmentType, minSalary, maxSalary);

        if (results.isEmpty()) {
            throw new ResourceNotFoundException("No matching job posts found.");
        }

        return results.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }
}