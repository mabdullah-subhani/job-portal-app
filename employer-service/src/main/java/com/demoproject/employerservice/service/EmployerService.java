package com.demoproject.employerservice.service;

import com.demoproject.employerservice.entity.Employer;
import com.demoproject.employerservice.exception.BadRequestException;
import com.demoproject.employerservice.exception.ResourceNotFoundException;
import com.demoproject.employerservice.repository.EmployerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployerService {

    private final EmployerRepository employerRepository;

    @Transactional
    public Employer createEmployer(Employer employer) {
        if (employer.getCompanyName() == null || employer.getEmail() == null) {
            throw new BadRequestException("Company name and email are required fields.");
        }
        return employerRepository.save(employer);
    }

    @Transactional
    public Employer updateEmployer(Long id, Employer updatedEmployer) {
        Employer existing = employerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found with ID: " + id));

        existing.setCompanyName(updatedEmployer.getCompanyName());
        existing.setEmail(updatedEmployer.getEmail());
        existing.setPhone(updatedEmployer.getPhone());
        existing.setWebsite(updatedEmployer.getWebsite());
        existing.setAddress(updatedEmployer.getAddress());
        existing.setIndustry(updatedEmployer.getIndustry());
        existing.setDescription(updatedEmployer.getDescription());

        return employerRepository.save(existing);
    }

    public List<Employer> getAllEmployers() {
        List<Employer> employers = employerRepository.findAll();
        if (employers.isEmpty()) {
            throw new ResourceNotFoundException("No employers found.");
        }
        return employers;
    }

    public Employer getEmployerById(Long id) {
        return employerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found with ID: " + id));
    }

    public boolean checkEmployerExists(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid employer ID.");
        }

        boolean exists = employerRepository.existsById(id);
        if (!exists) {
            throw new ResourceNotFoundException("Employer not found with ID: " + id);
        }

        return true;
    }

}
