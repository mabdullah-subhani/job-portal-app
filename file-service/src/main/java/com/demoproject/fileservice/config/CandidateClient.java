package com.demoproject.fileservice.config;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "candidate-service", path = "/api/candidates")
public interface CandidateClient {

    @PutMapping("/{userId}/resume")
    void updateResumeUrl(
            @PathVariable("userId") Long userId,
            @RequestBody UpdateResumeRequest request
    );
}


