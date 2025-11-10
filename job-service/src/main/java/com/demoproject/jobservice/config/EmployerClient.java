package com.demoproject.jobservice.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// This matches employer-service name from application.yml
@FeignClient(name = "employer-service", path = "/api/employers")
public interface EmployerClient {

    @GetMapping("/{id}/exists")
    Boolean checkEmployerExists(@PathVariable("id") Long id);
}

