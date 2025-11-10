package com.demoproject.authservice.config;


import com.demoproject.candidateservice.dto.CreateCandidateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "candidate-service") // matches Eureka name
public interface CandidateClient {

    @PostMapping("/api/candidates")
    void createCandidate(@RequestBody CreateCandidateRequest request);
}