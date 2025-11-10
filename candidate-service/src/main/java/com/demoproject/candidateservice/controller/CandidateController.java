package com.demoproject.candidateservice.controller;

import com.demoproject.candidateservice.dto.CreateCandidateRequest;
import com.demoproject.candidateservice.dto.UpdateCandidateRequest;
import com.demoproject.candidateservice.dto.CandidateResponse;
import com.demoproject.candidateservice.payload.ApiResponse;
import com.demoproject.candidateservice.service.CandidateService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/candidates")
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;

    @PostMapping
    @Operation(
            summary = "Register a new candidate profile",
            description = "Creates a candidate profile using user details collected during signup."
    )
    public ResponseEntity<ApiResponse<CandidateResponse>> createCandidate(
            @Valid @RequestBody CreateCandidateRequest request
    ) {
        CandidateResponse created = candidateService.createCandidate(request);
        return ResponseEntity.ok(ApiResponse.success("Candidate profile created successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update candidate profile",
            description = "Updates personal details, skills, experience, or resume information for the candidate."
    )
    public ResponseEntity<ApiResponse<CandidateResponse>> updateCandidate(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCandidateRequest request
    ) {
        CandidateResponse updated = candidateService.updateCandidate(id, request);
        return ResponseEntity.ok(ApiResponse.success("Candidate profile updated successfully", updated));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get candidate by ID",
            description = "Fetches complete candidate profile details using the candidate’s unique ID."
    )
    public ResponseEntity<ApiResponse<CandidateResponse>> getCandidate(@PathVariable UUID id) {
        CandidateResponse candidate = candidateService.getCandidate(id);
        return ResponseEntity.ok(ApiResponse.success("Candidate fetched successfully", candidate));
    }

    @GetMapping
    @Operation(
            summary = "Get all candidates",
            description = "Retrieves a list of all registered candidates with their basic profile information."
    )
    public ResponseEntity<ApiResponse<List<CandidateResponse>>> getAllCandidates() {
        List<CandidateResponse> candidates = candidateService.getAllCandidates();
        return ResponseEntity.ok(ApiResponse.success("Candidates retrieved successfully", candidates));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete candidate profile",
            description = "Removes a candidate’s profile permanently by their unique ID."
    )
    public ResponseEntity<ApiResponse<Void>> deleteCandidate(@PathVariable UUID id) {
        candidateService.deleteCandidate(id);
        return ResponseEntity.ok(ApiResponse.success("Candidate deleted successfully", null));
    }
}
