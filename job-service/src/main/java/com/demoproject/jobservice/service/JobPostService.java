package com.demoproject.jobservice.service;

import com.demoproject.jobservice.config.EmployerClient;
import com.demoproject.jobservice.dto.JobPostDTO;
import com.demoproject.jobservice.entity.JobPost;
import com.demoproject.jobservice.exception.BadRequestException;
import com.demoproject.jobservice.exception.ResourceNotFoundException;
import com.demoproject.jobservice.mapper.JobPostMapper;
import com.demoproject.jobservice.repository.JobPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobPostService {

    private final JobPostRepository jobPostRepository;
    private final JobPostMapper jobPostMapper;
    private final EmployerClient employerClient; // ✅ Inject Feign Client

    @Transactional
    public JobPostDTO createJobPost(Long employerId, JobPostDTO dto) {
        // ✅ Verify employer exists via Feign client
        Boolean exists = employerClient.checkEmployerExists(employerId);
        if (exists == null || !exists) {
            throw new ResourceNotFoundException("Employer not found with ID: " + employerId);
        }

        if (dto.getTitle() == null || dto.getDescription() == null) {
            throw new BadRequestException("Job title and description are required.");
        }

        JobPost jobPost = jobPostMapper.toEntity(dto);
        jobPost.setEmployerId(employerId); // store only employerId in job post
        JobPost saved = jobPostRepository.save(jobPost);

        return jobPostMapper.toDTO(saved);
    }

    @Transactional
    public JobPost updateJobPost(Long id, JobPost updatedJobPost) {
        JobPost existingJobPost = jobPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job post not found with ID: " + id));

        existingJobPost.setTitle(updatedJobPost.getTitle());
        existingJobPost.setDescription(updatedJobPost.getDescription());
        existingJobPost.setLocation(updatedJobPost.getLocation());
        existingJobPost.setEmploymentType(updatedJobPost.getEmploymentType());
        existingJobPost.setSalary(updatedJobPost.getSalary());

        if (updatedJobPost.getEmployerId() != null) {
            Boolean exists = employerClient.checkEmployerExists(updatedJobPost.getEmployerId());
            if (exists == null || !exists) {
                throw new ResourceNotFoundException("Employer not found with ID: " + updatedJobPost.getEmployerId());
            }
            existingJobPost.setEmployerId(updatedJobPost.getEmployerId());
        }

        return jobPostRepository.save(existingJobPost);
    }

    public List<JobPostDTO> getAllJobPosts() {
        List<JobPost> posts = jobPostRepository.findAll();
        if (posts.isEmpty()) {
            throw new ResourceNotFoundException("No job posts found.");
        }
        return posts.stream()
                .map(jobPostMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<JobPostDTO> getJobPostsByEmployer(Long employerId) {
        Boolean exists = employerClient.checkEmployerExists(employerId);
        if (exists == null || !exists) {
            throw new ResourceNotFoundException("Employer not found with ID: " + employerId);
        }

        List<JobPost> posts = jobPostRepository.findByEmployerId(employerId);
        if (posts.isEmpty()) {
            throw new ResourceNotFoundException("No job posts found for employer ID: " + employerId);
        }

        return posts.stream()
                .map(jobPostMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteJobPost(Long jobPostId) {
        if (!jobPostRepository.existsById(jobPostId)) {
            throw new ResourceNotFoundException("Job post not found with ID: " + jobPostId);
        }
        jobPostRepository.deleteById(jobPostId);
    }

    public List<JobPostDTO> searchJobs(String keyword, String location, String employmentType) {
        List<JobPost> results;

        // If all filters are null, just return all jobs
        if ((keyword == null || keyword.isBlank()) &&
                (location == null || location.isBlank()) &&
                (employmentType == null || employmentType.isBlank())) {
            results = jobPostRepository.findAll();
        } else {
            results = jobPostRepository.findAll().stream()
                    .filter(job -> (keyword == null || job.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                            job.getDescription().toLowerCase().contains(keyword.toLowerCase())))
                    .filter(job -> (location == null || job.getLocation().equalsIgnoreCase(location)))
                    .filter(job -> (employmentType == null || job.getEmploymentType().equalsIgnoreCase(employmentType)))
                    .collect(Collectors.toList());
        }

        if (results.isEmpty()) {
            throw new ResourceNotFoundException("No matching job posts found.");
        }

        return results.stream()
                .map(jobPostMapper::toDTO)
                .collect(Collectors.toList());
    }

}
