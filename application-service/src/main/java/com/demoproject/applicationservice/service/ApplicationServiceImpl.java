package com.demoproject.applicationservice.service;

import com.demoproject.applicationservice.config.feign.CandidateClient;
import com.demoproject.applicationservice.config.feign.EmployerClient;
import com.demoproject.applicationservice.config.feign.JobClient;
import com.demoproject.applicationservice.dto.*;
import com.demoproject.applicationservice.entity.ApplicationEntity;
import com.demoproject.applicationservice.entity.ApplicationStatus;
import com.demoproject.applicationservice.exception.AlreadyAppliedException;
import com.demoproject.applicationservice.exception.ResourceNotFoundException;
import com.demoproject.applicationservice.exception.ServiceUnavailableException;
import com.demoproject.applicationservice.exception.ValidationException;
import com.demoproject.applicationservice.payload.ApiResponse;
import com.demoproject.applicationservice.repository.ApplicationRepository;
import com.demoproject.applicationservice.security.SecurityUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository repo;
    private final CandidateClient candidateClient;
    private final JobClient jobClient;
    private final EmployerClient employerClient;

    // ------------------------------------------------------------------------
    // APPLY JOB
    // ------------------------------------------------------------------------
    @Override
    @Transactional
    public ApplicationResponse apply(UUID jobId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        CandidateResponse candidate = fetchCandidate(currentUserId);
        JobPostResponse job = fetchJob(jobId);

        boolean alreadyApplied = repo.existsByCandidateIdAndJobId(candidate.getId(), jobId);
        if (alreadyApplied) throw new AlreadyAppliedException("You have already applied to this job");

        // Use candidate's resumeFileId automatically
        UUID resumeFileId = candidate.getResumeFileId();
        if (resumeFileId == null) throw new ValidationException("Candidate does not have a resume");

        UUID employerId = job.getEmployerId();

        ApplicationEntity saved = repo.save(
                ApplicationEntity.builder()
                        .candidateId(candidate.getId())
                        .jobId(jobId)
                        .employerId(employerId)
                        .resumeFileId(resumeFileId)
                        .status(ApplicationStatus.APPLIED)
                        .build()
        );

        return buildResponse(saved, candidate);
    }



    // ------------------------------------------------------------------------
    // GET MY APPLICATIONS
    // ------------------------------------------------------------------------
    @Override
    public List<ApplicationResponse> getMyApplications() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        CandidateResponse candidate = fetchCandidate(currentUserId);

        List<ApplicationEntity> list = repo.findByCandidateId(candidate.getId());

        return list.stream()
                .map(this::buildBasicResponse)
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------------------
    // EMPLOYER → GET APPLICANTS FOR JOB
    // ------------------------------------------------------------------------
    @Override
    public EmployerJobApplicantsResponse getApplicantsForEmployerJob(UUID jobId, int page, int size) {
        fetchJob(jobId); // validate job exists

        List<ApplicationEntity> apps = repo.findByJobId(jobId);

        long total = apps.size();
        long shortlisted = apps.stream().filter(a -> a.getStatus() == ApplicationStatus.SHORTLISTED).count();
        long rejected = apps.stream().filter(a -> a.getStatus() == ApplicationStatus.REJECTED).count();
        long selected = apps.stream().filter(a -> a.getStatus() == ApplicationStatus.SELECTED).count();
        long pending = total - (shortlisted + rejected + selected);

        List<ApplicationResponse> applicants = apps.stream()
                .map(this::buildBasicResponse)
                .collect(Collectors.toList());

        return EmployerJobApplicantsResponse.builder()
                .total(total)
                .shortlisted(shortlisted)
                .rejected(rejected)
                .selected(selected)
                .pending(pending)
                .applicants(applicants)
                .build();
    }

    // ------------------------------------------------------------------------
    // UPDATE STATUS
    // ------------------------------------------------------------------------
    @Override
    @Transactional
    public ApplicationResponse updateStatus(UUID applicationId, ApplicationStatusUpdateRequest request) {

        ApplicationEntity app = repo.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        ApplicationStatus newStatus = parseStatus(request.getStatus());
        app.setStatus(newStatus);
        repo.save(app);

        return buildBasicResponse(app);
    }

    // ------------------------------------------------------------------------
    // CHECK IF CURRENT USER ALREADY APPLIED
    // ------------------------------------------------------------------------
    @Override
    public boolean checkIfApplied(UUID jobId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        CandidateResponse candidate = fetchCandidate(currentUserId);

        return repo.existsByCandidateIdAndJobId(candidate.getId(), jobId);
    }


    // ------------------------------------------------------------------------
    // RESPONSE BUILDERS
    // ------------------------------------------------------------------------
    private ApplicationResponse buildResponse(ApplicationEntity entity, CandidateResponse candidate) {
        return ApplicationResponse.builder()
                .id(entity.getId())
                .jobId(entity.getJobId())
                .candidateId(entity.getCandidateId())
                .employerId(entity.getEmployerId())
                .resumeFileId(entity.getResumeFileId())
                .status(entity.getStatus().name())
                .candidateFullName(candidate.getFullName())
                .candidateExperience(candidate.getExperience())
                .candidateResumeUrl(candidate.getResumeFileUrl())
                .build();
    }

    private ApplicationResponse buildBasicResponse(ApplicationEntity e) {
        return ApplicationResponse.builder()
                .id(e.getId())
                .jobId(e.getJobId())
                .candidateId(e.getCandidateId())
                .employerId(e.getEmployerId())
                .resumeFileId(e.getResumeFileId())
                .status(e.getStatus().name())
                .build();
    }

    // ------------------------------------------------------------------------
    // HELPER FUNCTIONS
    // ------------------------------------------------------------------------
    private CandidateResponse fetchCandidate(Long userId) {
        try {
            log.info("Fetching candidate for userId={}", userId);
            ApiResponse<CandidateResponse> resp = candidateClient.getCandidateByUserId(userId);
            log.info("Candidate response: {}", resp);

            if (resp == null || resp.getData() == null) {
                throw new ResourceNotFoundException("Candidate not found");
            }
            return resp.getData();
        } catch (feign.FeignException e) {
            log.error("FeignException when calling CandidateService", e);
            throw e; // rethrow so we see stack trace in logs
        }
    }

    private JobPostResponse fetchJob(UUID jobId) {
        try {
            log.info("Fetching job for jobId={}", jobId);
            ApiResponse<JobPostResponse> resp = jobClient.getJobById(jobId);
            log.info("Job response: {}", resp);

            if (resp == null || resp.getData() == null) {
                throw new ResourceNotFoundException("Job not found");
            }
            return resp.getData();
        } catch (feign.FeignException e) {
            log.error("FeignException when calling JobService", e);
            throw e;
        }
    }

    private UUID fetchEmployerId(Long userId) {
        try {
            ApiResponse<UUID> resp = employerClient.getEmployerIdByUserId(userId);
            if (resp == null || resp.getData() == null) {
                throw new ResourceNotFoundException("Employer not found");
            }
            return resp.getData();
        } catch (feign.FeignException.NotFound e) {
            throw new ResourceNotFoundException("Employer not found");
        } catch (feign.FeignException e) {
            throw new ServiceUnavailableException("Employer service is currently unavailable");
        }
    }

    private ApplicationStatus parseStatus(String status) {
        try {
            return ApplicationStatus.valueOf(status);
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("Invalid status: " + status);
        }
    }
}
