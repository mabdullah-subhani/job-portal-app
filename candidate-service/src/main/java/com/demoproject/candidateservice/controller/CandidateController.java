package com.demoproject.candidateservice.controller;

import com.demoproject.candidateservice.dto.*;
import com.demoproject.candidateservice.payload.ApiResponse;
import com.demoproject.candidateservice.service.CandidateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/candidates")
@RequiredArgsConstructor
@Tag(name = "Candidate Management", description = "Endpoints for managing candidate profiles")
public class CandidateController {

    private final CandidateService candidateService;

    @PostMapping
    @Operation(summary = "Create candidate profile",
            description = "Creates a candidate profile for the logged-in user")
    public ResponseEntity<ApiResponse<CandidateResponse>> createCandidate(
            @Valid @RequestBody CreateCandidateRequest request) {

        CandidateResponse created = candidateService.createCandidate(request);
        return ResponseEntity.ok(ApiResponse.success("Candidate profile created", created));
    }

    // Update profile
    @PatchMapping("/{id}")
    @Operation(summary = "Update candidate profile",
            description = "Allows candidate to update their own profile")
    public ResponseEntity<ApiResponse<CandidateResponse>> updateCandidate(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateCandidateRequest request) {

        CandidateResponse updated = candidateService.updateCandidate(id, request);
        return ResponseEntity.ok(ApiResponse.success("Candidate profile updated", updated));
    }

    // Get candidate by UUID
    @GetMapping("/{id}")
    @Operation(summary = "Get candidate by UUID",
            description = "Fetch detailed candidate profile")
    public ResponseEntity<ApiResponse<CandidateResponse>> getCandidate(@PathVariable("id") UUID id) {
        CandidateResponse candidate = candidateService.getCandidate(id);
        return ResponseEntity.ok(ApiResponse.success("Candidate fetched successfully", candidate));
    }

    // Get candidate by linked user ID
    @GetMapping("/by-user/{userId}")
    @Operation(summary = "Get candidate by user ID",
            description = "Fetch candidate profile by user account ID")
    public ResponseEntity<ApiResponse<CandidateResponse>> getCandidateByUserId(
            @PathVariable("userId") Long userId) {

        CandidateResponse candidate = candidateService.getCandidateByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Candidate fetched successfully", candidate));
    }

    // List all public candidates
    @GetMapping("/public")
    @Operation(summary = "List public candidates",
            description = "Fetch all publicly visible candidate profiles")
    public ResponseEntity<ApiResponse<List<CandidateResponse>>> getAllPublicCandidates() {

        List<CandidateResponse> list = candidateService.getAllPublicCandidates();
        return ResponseEntity.ok(ApiResponse.success("Public candidates retrieved", list));
    }

    // Search candidates
    @PostMapping("/search")
    @Operation(summary = "Search candidates",
            description = "Search by skills, location, or experience")
    public ResponseEntity<ApiResponse<List<CandidateResponse>>> searchCandidates(
            @RequestBody CandidateSearchRequest searchRequest) {

        List<CandidateResponse> results = candidateService.searchCandidates(
                searchRequest.getSkills(),
                searchRequest.getLocation(),
                searchRequest.getExperience());

        return ResponseEntity.ok(ApiResponse.success("Search results", results));
    }

    // Delete profile
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Delete candidate profile",
            description = "Delete profile of the logged-in candidate")
    public ResponseEntity<ApiResponse<Void>> deleteCandidate(@PathVariable("id") UUID id) {

        candidateService.deleteCandidate(id);
        return ResponseEntity.ok(ApiResponse.success("Candidate deleted", null));
    }


    @PutMapping("/{userId}/resume")
    public ResponseEntity<ApiResponse<Void>> updateResume(
            @PathVariable("userId") Long userId,
            @RequestBody UpdateResumeRequest request) {

        candidateService.updateResumeUrl(
                userId,
                request.getResumeFileId(),
                request.getResumeUrl()
        );

        return ResponseEntity.ok(ApiResponse.success("Resume updated", null));
    }


}
