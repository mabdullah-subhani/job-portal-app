package com.demoproject.applicationservice.config.feign;

import com.demoproject.applicationservice.payload.ApiResponse;
import com.demoproject.applicationservice.dto.JobPostResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "job-service", path = "/api/job-posts")
public interface JobClient {
    @GetMapping("/{id}")
    ApiResponse<JobPostResponse> getJobById(@PathVariable("id") UUID id);
}

