package com.demoproject.employerservice.service;

import com.demoproject.employerservice.dto.CreateEmployerRequest;
import com.demoproject.employerservice.dto.EmployerResponse;
import com.demoproject.employerservice.dto.UpdateEmployerRequest;
import com.demoproject.employerservice.entity.Employer;
import com.demoproject.employerservice.exception.BadRequestException;
import com.demoproject.employerservice.exception.ResourceNotFoundException;
import com.demoproject.employerservice.mapper.EmployerMapper;
import com.demoproject.employerservice.repository.EmployerRepository;
import com.demoproject.employerservice.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployerServiceImpl implements EmployerService {

    private final EmployerRepository repo;
    private final EmployerMapper mapper;

    @Override
    @Transactional
    public EmployerResponse createEmployer(CreateEmployerRequest req) {
        Long userId = SecurityUtils.getCurrentUserId();

        if (repo.findByUserId(userId).isPresent()) {
            throw new BadRequestException("User already has an employer profile");
        }
        if (repo.existsByCompanyName(req.getCompanyName())) {
            throw new BadRequestException("Company name already used");
        }
        if (repo.existsByEmail(req.getEmail())) {
            throw new BadRequestException("Email already used");
        }

        Employer employer = mapper.toEntity(req);
        employer.setUserId(userId);
        Employer saved = repo.save(employer);
        log.info("Employer created id={} by userId={}", saved.getId(), userId);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public EmployerResponse updateEmployer(UUID id, UpdateEmployerRequest req) {
        Long userId = SecurityUtils.getCurrentUserId();
        Employer existing = repo.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found or access denied"));

        mapper.updateEntityFromRequest(req, existing);
        Employer saved = repo.save(existing);
        log.info("Employer updated id={} by userId={}", saved.getId(), userId);
        return mapper.toResponse(saved);
    }

    @Override
    public EmployerResponse getEmployerById(UUID id) {
        Long userId = SecurityUtils.getCurrentUserId();
        Employer emp = repo.findByIdAndUserId(id, userId).orElseThrow(() -> new ResourceNotFoundException("Employer not found"));
        return mapper.toResponse(emp);
    }

    @Override
    public EmployerResponse getEmployerByUserId(Long userId) {
        Employer emp = repo.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("Employer not found"));
        return mapper.toResponse(emp);
    }

    @Override
    public List<EmployerResponse> getAllEmployers(int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 10));
        return repo.findAll(pageable)
                .map(mapper::toResponse)
                .getContent();
    }


    @Override
    @Transactional
    public void deleteEmployer(UUID id) {
        Long userId = SecurityUtils.getCurrentUserId();
        Employer existing = repo.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found or access denied"));
        repo.delete(existing);
        log.info("Employer deleted id={} by userId={}", id, userId);
    }

    @Override
    public boolean checkEmployerExists(UUID id) {
        return repo.existsById(id);
    }

    @Override
    @Transactional
    public void addJobIdToEmployer(UUID employerId, UUID jobId) {
        Employer employer = repo.findById(employerId)
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found"));

        Set<UUID> jobIds = employer.getJobIds();
        if (jobIds == null) {
            jobIds = new HashSet<>();
            employer.setJobIds(jobIds);
        }
        if (!jobIds.contains(jobId)) {
            jobIds.add(jobId);
            repo.save(employer);
        }
    }

    public UUID getEmployerIdByUserIdOnly(Long userId) {
        Employer employer = repo.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found"));
        return employer.getId();
    }

}
