package com.demoproject.employerservice.repository;

import com.demoproject.employerservice.entity.Employer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmployerRepository extends JpaRepository<Employer, UUID> {

    Optional<Employer> findByUserId(Long userId);


    boolean existsByCompanyName(String companyName);

    boolean existsByEmail(String email);

    Optional<Employer> findByIdAndUserId(UUID id, Long userId);

}
