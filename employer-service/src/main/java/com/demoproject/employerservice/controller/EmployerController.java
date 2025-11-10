package com.demoproject.employerservice.controller;

import com.demoproject.employerservice.dto.ApiResponse;
import com.demoproject.employerservice.dto.EmployerDTO;
import com.demoproject.employerservice.entity.Employer;
import com.demoproject.employerservice.mapper.EmployerMapper;
import com.demoproject.employerservice.service.EmployerService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employers")
@RequiredArgsConstructor
public class EmployerController {

    private final EmployerService employerService;
    private final EmployerMapper employerMapper;


    @PostMapping
    @Operation(
            summary = "Create a new employer profile",
            description = "Registers a new employer by providing company name, description, and other related details."
    )
    public ResponseEntity<ApiResponse<EmployerDTO>> createEmployer(@Valid @RequestBody EmployerDTO dto) {
        Employer employer = employerMapper.toEntity(dto);
        Employer saved = employerService.createEmployer(employer);
        EmployerDTO responseDTO = employerMapper.toDTO(saved);
        return ResponseEntity.ok(ApiResponse.success("Employer created successfully", responseDTO));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update an existing employer profile",
            description = "Updates an employer’s information such as company name, email, address, industry, and description using the employer ID."
    )
    public ResponseEntity<ApiResponse<EmployerDTO>> updateEmployer(
            @PathVariable Long id,
            @Valid @RequestBody EmployerDTO dto) {

        Employer updatedEmployer = employerMapper.toEntity(dto);
        Employer savedEmployer = employerService.updateEmployer(id, updatedEmployer);
        EmployerDTO responseDTO = employerMapper.toDTO(savedEmployer);

        return ResponseEntity.ok(ApiResponse.success("Employer updated successfully", responseDTO));
    }

    @GetMapping
    @Operation(summary = "Get all employers", description = "Fetches a list of all registered employers on the Job Portal platform.")
    public ResponseEntity<ApiResponse<List<EmployerDTO>>> getAllEmployers() {
        List<Employer> employers = employerService.getAllEmployers();
        List<EmployerDTO> response = employers.stream().map(employerMapper::toDTO).toList();
        return ResponseEntity.ok(ApiResponse.success("Employers fetched successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get employer by ID", description = "Fetches detailed employer information using the employer ID.")
    public ResponseEntity<ApiResponse<EmployerDTO>> getEmployerById(@PathVariable Long id) {
        Employer employer = employerService.getEmployerById(id);
        EmployerDTO dto = employerMapper.toDTO(employer);
        return ResponseEntity.ok(ApiResponse.success("Employer fetched successfully", dto));
    }

    @GetMapping("/{id}/exists")
    @Operation(
            summary = "Check if employer exists",
            description = "Verifies whether an employer with the given ID exists in the system. Used by other services like Job Service via Feign client."
    )
    public ResponseEntity<ApiResponse<Boolean>> checkEmployerExists(@PathVariable Long id) {
        boolean exists = employerService.checkEmployerExists(id);
        String message = exists ? "Employer exists." : "Employer not found.";
        return ResponseEntity.ok(ApiResponse.success(message, exists));
    }

}
