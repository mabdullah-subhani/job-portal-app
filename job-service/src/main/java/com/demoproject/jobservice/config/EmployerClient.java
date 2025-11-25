package com.demoproject.jobservice.config;

import com.demoproject.jobservice.payload.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.UUID;

// This matches employer-service name from application.yml
@FeignClient(name = "employer-service", path = "/api/employers")
public interface EmployerClient {

    @GetMapping("/{id}/exists")
    ApiResponse<Boolean> checkEmployerExists(@PathVariable("id") UUID id);

    @GetMapping("/user/{userId}/id")
    ApiResponse<UUID> getEmployerIdByUserId(@PathVariable("userId") Long userId);

    @PostMapping("/{employerId}/job/{jobId}")
    void addJobIdToEmployer(@PathVariable("employerId") UUID employerId, @PathVariable("jobId") UUID jobId);
}

