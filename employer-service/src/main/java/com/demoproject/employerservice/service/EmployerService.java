package com.demoproject.employerservice.service;

import com.demoproject.employerservice.dto.CreateEmployerRequest;
import com.demoproject.employerservice.dto.EmployerResponse;
import com.demoproject.employerservice.dto.UpdateEmployerRequest;

import java.util.List;
import java.util.UUID;

public interface EmployerService {

    EmployerResponse createEmployer(CreateEmployerRequest req);

    EmployerResponse updateEmployer(UUID id, UpdateEmployerRequest req);

    EmployerResponse getEmployerById(UUID id);

    EmployerResponse getEmployerByUserId(Long userId);

    List<EmployerResponse> getAllEmployers(int page, int size);

    void deleteEmployer(UUID id);

    boolean checkEmployerExists(UUID id);

    void addJobIdToEmployer(UUID employerId, UUID jobId);

    UUID getEmployerIdByUserIdOnly(Long userId);
}

