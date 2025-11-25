package com.demoproject.applicationservice.config.feign;

import com.demoproject.applicationservice.payload.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "employer-service", path = "/api/employers")
public interface EmployerClient {

    // your EmployerController exposes /user/{userId}/id
    @GetMapping("/user/{userId}/id")
    ApiResponse<UUID> getEmployerIdByUserId(@PathVariable("userId") Long userId);
}

