package com.demoproject.applicationservice.controller;

import com.demoproject.applicationservice.config.feign.EmployerClient;
import com.demoproject.applicationservice.config.feign.JobClient;
import com.demoproject.applicationservice.dto.*;
import com.demoproject.applicationservice.payload.ApiResponse;
import com.demoproject.applicationservice.security.SecurityUtils;
import com.demoproject.applicationservice.service.ApplicationService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService service;
    private final JobClient jobClient;
    private final EmployerClient employerClient;

    // Candidate applies to a job
    @PostMapping("/apply/{jobId}")
    public ApiResponse<ApplicationResponse> apply(
            @PathVariable("jobId") UUID jobId) {

        ApplicationResponse resp = service.apply(jobId);
        return ApiResponse.success("Application created", resp);
    }

    // Candidate: list my applications
    @GetMapping("/my")
    public ApiResponse<List<ApplicationResponse>> myApplications() {
        return ApiResponse.success("My applications", service.getMyApplications());
    }

    // Check if applied
    @GetMapping("/job/{jobId}/check")
    public ApiResponse<Boolean> checkApplied(@PathVariable("jobId") UUID jobId) {
        return ApiResponse.success("Checked", service.checkIfApplied(jobId));
    }

    // Employer: list applicants & analytics for a job
    @GetMapping("/employer/job/{jobId}")
    public ApiResponse<EmployerJobApplicantsResponse> getApplicantsForJob(
            @PathVariable("jobId") UUID jobId) {

        // Fetch job
        ApiResponse<JobPostResponse> jresp = jobClient.getJobById(jobId);
        JobPostResponse job = jresp.getData();
        if (job == null) {
            throw new com.demoproject.applicationservice.exception.ResourceNotFoundException("Job not found");
        }

        // Validate employer ownership
        Long currentUserId = SecurityUtils.getCurrentUserId();
        ApiResponse<UUID> eResp = employerClient.getEmployerIdByUserId(currentUserId);
        UUID employerId = eResp.getData();

        if (!job.getEmployerId().equals(employerId)) {
            throw new com.demoproject.applicationservice.exception.UnauthorizedException("Not allowed to view applicants for this job");
        }

        EmployerJobApplicantsResponse resp =
                service.getApplicantsForEmployerJob(jobId, 0, 50);

        return ApiResponse.success("Applicants retrieved", resp);
    }

    // Employer updates application status
    @PatchMapping("/{applicationId}/status")
    public ApiResponse<ApplicationResponse> updateStatus(
            @PathVariable("applicationId") UUID applicationId,
            @Valid @RequestBody ApplicationStatusUpdateRequest req) {

        ApplicationResponse updated = service.updateStatus(applicationId, req);
        return ApiResponse.success("Status updated", updated);
    }
}
