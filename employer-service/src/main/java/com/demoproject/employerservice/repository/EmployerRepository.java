package com.demoproject.employerservice.repository;
import com.demoproject.employerservice.entity.Employer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployerRepository extends JpaRepository<Employer, Long> {
}
