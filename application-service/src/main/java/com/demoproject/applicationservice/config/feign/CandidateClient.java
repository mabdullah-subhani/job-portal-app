package com.demoproject.applicationservice.config.feign;


import com.demoproject.applicationservice.payload.ApiResponse;
import com.demoproject.applicationservice.dto.CandidateResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "candidate-service", path = "/api/candidates")
public interface CandidateClient {

    // your candidate service has GET /by-user/{userId}
    @GetMapping("/by-user/{userId}")
    ApiResponse<CandidateResponse> getCandidateByUserId(@PathVariable("userId") Long userId);

    @GetMapping("/{id}")
    ApiResponse<CandidateResponse> getCandidate(@PathVariable("id") UUID id);
}

