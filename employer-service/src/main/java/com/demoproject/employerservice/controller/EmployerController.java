package com.demoproject.employerservice.controller;

import com.demoproject.employerservice.payload.ApiResponse;
import com.demoproject.employerservice.dto.CreateEmployerRequest;
import com.demoproject.employerservice.dto.EmployerResponse;
import com.demoproject.employerservice.dto.UpdateEmployerRequest;
import com.demoproject.employerservice.service.EmployerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/employers")
@RequiredArgsConstructor
@Tag(name = "Employer Management", description = "APIs to manage employer profiles and company data")
public class EmployerController {

    private final EmployerService employerService;

    @PostMapping
    @Operation(summary = "Create employer profile",
            description = "Create a new employer profile for the logged-in user.")
    public ResponseEntity<ApiResponse<EmployerResponse>> createEmployer(
            @Valid @RequestBody CreateEmployerRequest request) {

        EmployerResponse response = employerService.createEmployer(request);
        return ResponseEntity.ok(ApiResponse.success("Employer created successfully", response));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update employer profile",
            description = "Partially update employer profile fields. Only non-null fields in request are applied.")
    public ResponseEntity<ApiResponse<EmployerResponse>> updateEmployer(
            @PathVariable("id") UUID id,
            @RequestBody UpdateEmployerRequest request) {

        EmployerResponse response = employerService.updateEmployer(id, request);
        return ResponseEntity.ok(ApiResponse.success("Employer updated successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get employer by ID",
            description = "Retrieve full employer profile using employer ID.")
    public ResponseEntity<ApiResponse<EmployerResponse>> getEmployerById(@PathVariable("id") UUID id) {
        EmployerResponse response = employerService.getEmployerById(id);
        return ResponseEntity.ok(ApiResponse.success("Employer fetched successfully", response));
    }

    @GetMapping("/by-user/{userId}")
    @Operation(summary = "Get employer by user ID",
            description = "Fetch employer profile linked to a specific user account (owner).")
    public ResponseEntity<ApiResponse<EmployerResponse>> getEmployerByUserId(@PathVariable("userId") Long userId) {
        EmployerResponse response = employerService.getEmployerByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Employer fetched successfully", response));
    }


    @GetMapping
    @Operation(summary = "List employers (paginated)",
            description = "Retrieve paginated list of employers. Provide page and size as query params.")
    public ResponseEntity<ApiResponse<List<EmployerResponse>>> listEmployers(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {

        List<EmployerResponse> list = employerService.getAllEmployers(page, size);
        return ResponseEntity.ok(ApiResponse.success("Employers retrieved successfully", list));
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Delete employer profile",
            description = "Delete the employer profile owned by the logged-in user.")
    public ResponseEntity<ApiResponse<Void>> deleteEmployer(@PathVariable("id") UUID id) {
        employerService.deleteEmployer(id);
        return ResponseEntity.ok(ApiResponse.success("Employer deleted successfully", null));
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<ApiResponse<Boolean>> checkEmployerExists(@PathVariable("id") UUID id) {
        boolean exists = employerService.checkEmployerExists(id);
        return ResponseEntity.ok(ApiResponse.success("Checked existence", exists));
    }

    @PostMapping("/{employerId}/job/{jobId}")
    public ResponseEntity<ApiResponse<Void>> addJobIdToEmployer(
            @PathVariable("employerId") UUID employerId,
            @PathVariable("jobId") UUID jobId) {

        employerService.addJobIdToEmployer(employerId, jobId);
        return ResponseEntity.ok(ApiResponse.success("Job added to employer successfully", null));
    }

    @GetMapping("/user/{userId}/id")
    public ResponseEntity<ApiResponse<UUID>> getEmployerId(@PathVariable("userId") Long userId) {
        UUID employerId = employerService.getEmployerIdByUserIdOnly(userId);
        return ResponseEntity.ok(ApiResponse.success("Employer ID fetched", employerId));
    }

}