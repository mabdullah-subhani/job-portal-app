package com.demoproject.applicationservice.service;

import com.demoproject.applicationservice.dto.ApplicationResponse;
import com.demoproject.applicationservice.dto.CreateApplicationRequest;
import com.demoproject.applicationservice.dto.EmployerJobApplicantsResponse;
import com.demoproject.applicationservice.dto.ApplicationStatusUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface ApplicationService {

    ApplicationResponse apply(UUID jobId);

    List<ApplicationResponse> getMyApplications();

    EmployerJobApplicantsResponse getApplicantsForEmployerJob(UUID jobId, int page, int size);

    ApplicationResponse updateStatus(UUID applicationId, ApplicationStatusUpdateRequest request);

    boolean checkIfApplied(UUID jobId);
}
