package com.demoproject.jobservice.controller;

import com.demoproject.jobservice.dto.ApiResponse;
import com.demoproject.jobservice.dto.JobPostDTO;
import com.demoproject.jobservice.entity.JobPost;
import com.demoproject.jobservice.mapper.JobPostMapper;
import com.demoproject.jobservice.service.JobPostService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job-posts")
@RequiredArgsConstructor
public class JobPostController {

    private final JobPostService jobPostService;
    private final JobPostMapper jobPostMapper;


    @PostMapping("/{employerId}")
    @Operation(
            summary = "Create a new job post",
            description = "Allows an employer to create a new job posting associated with their company profile."
    )
    public ResponseEntity<ApiResponse<JobPostDTO>> createJobPost(
            @PathVariable Long employerId,
            @Valid @RequestBody JobPostDTO dto
    ) {
        JobPostDTO created = jobPostService.createJobPost(employerId, dto);
        return ResponseEntity.ok(ApiResponse.success("Job post created successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a job post",
            description = "Updates an existing job post’s title, description, location, type, salary, or associated employer."
    )
    public ResponseEntity<ApiResponse<JobPostDTO>> updateJobPost(
            @PathVariable Long id,
            @Valid @RequestBody JobPostDTO dto) {

        JobPost updatedJobPost = jobPostMapper.toEntity(dto);
        JobPost savedJobPost = jobPostService.updateJobPost(id, updatedJobPost);
        JobPostDTO responseDTO = jobPostMapper.toDTO(savedJobPost);

        return ResponseEntity.ok(ApiResponse.success("Job post updated successfully", responseDTO));
    }

    @GetMapping
    @Operation(summary = "Get all job posts", description = "Fetches all available job postings from different employers.")
    public ResponseEntity<ApiResponse<List<JobPostDTO>>> getAllJobPosts() {
        return ResponseEntity.ok(ApiResponse.success("Job posts retrieved successfully", jobPostService.getAllJobPosts()));
    }

    @GetMapping("/{employerId}")
    @Operation(summary = "Get job posts by employer", description = "Retrieves all job posts created by a specific employer using their employer ID.")
    public ResponseEntity<ApiResponse<List<JobPostDTO>>> getJobPostsByEmployer(@PathVariable Long employerId) {
        return ResponseEntity.ok(ApiResponse.success("Employer job posts fetched successfully", jobPostService.getJobPostsByEmployer(employerId)));
    }

    @DeleteMapping("/{jobPostId}")
    @Operation(summary = "Delete a job post", description = "Removes a specific job post by its ID from the employer’s job listings.")
    public ResponseEntity<ApiResponse<Void>> deleteJobPost(@PathVariable Long jobPostId) {
        jobPostService.deleteJobPost(jobPostId);
        return ResponseEntity.ok(ApiResponse.success("Job post deleted successfully", null));
    }

    @GetMapping("/search")
    @Operation(
            summary = "Search and filter job posts",
            description = "Allows candidates to search jobs using keywords, location, or employment type filters."
    )
    public ResponseEntity<ApiResponse<List<JobPostDTO>>> searchJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String employmentType
    ) {
        List<JobPostDTO> results = jobPostService.searchJobs(keyword, location, employmentType);
        return ResponseEntity.ok(ApiResponse.success("Filtered job posts retrieved successfully", results));
    }

}
