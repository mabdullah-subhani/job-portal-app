package com.demoproject.jobservice.controller;

import com.demoproject.jobservice.dto.CreateJobPostRequest;
import com.demoproject.jobservice.dto.JobSearchRequest;
import com.demoproject.jobservice.dto.UpdateJobPostRequest;
import com.demoproject.jobservice.dto.JobPostResponse;
import com.demoproject.jobservice.payload.ApiResponse;
import com.demoproject.jobservice.service.JobPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/job-posts")
@RequiredArgsConstructor
@Tag(name = "Job Management", description = "APIs for managing job postings")
public class JobPostController {

    private final JobPostService service;

    @PostMapping
    @Operation(summary = "Create a new job post")
    public ResponseEntity<ApiResponse<JobPostResponse>> createJobPost(
            @Valid @RequestBody CreateJobPostRequest dto) {

        JobPostResponse response = service.createJobPost(dto);
        return ResponseEntity.ok(ApiResponse.success("Job post created successfully", response));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update a job post")
    public ResponseEntity<ApiResponse<JobPostResponse>> updateJobPost(
            @PathVariable("id") UUID id,
            @RequestBody UpdateJobPostRequest dto) {

        JobPostResponse response = service.updateJobPost(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Job post updated successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all job posts (paginated)")
    public ResponseEntity<ApiResponse<List<JobPostResponse>>> getAllJobPosts(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {

        List<JobPostResponse> list = service.getAllJobPosts(page, size);
        return ResponseEntity.ok(ApiResponse.success("Job posts retrieved successfully", list));
    }

    @GetMapping("/employer/{employerId}")
    @Operation(summary = "Get job posts by employer")
    public ResponseEntity<ApiResponse<List<JobPostResponse>>> getJobPostsByEmployer(
            @PathVariable("employerId") UUID employerId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {

        List<JobPostResponse> list = service.getJobPostsByEmployer(employerId, page, size);
        return ResponseEntity.ok(ApiResponse.success("Employer job posts retrieved successfully", list));
    }

    @DeleteMapping("/{jobPostId}")
    @Operation(summary = "Delete a job post")
    public ResponseEntity<ApiResponse<Void>> deleteJobPost(@PathVariable("jobPostId") UUID jobPostId) {
        service.deleteJobPost(jobPostId);
        return ResponseEntity.ok(ApiResponse.success("Job post deleted successfully", null));
    }

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<List<JobPostResponse>>> searchJobs(
            @RequestBody JobSearchRequest request) {

        List<JobPostResponse> results = service.searchJobs(
                request.getKeyword(),
                request.getLocation(),
                request.getEmploymentType(),
                request.getMinSalary(),
                request.getMaxSalary()
        );

        return ResponseEntity.ok(ApiResponse.success(
                "Filtered job posts retrieved successfully", results
        ));
    }
}