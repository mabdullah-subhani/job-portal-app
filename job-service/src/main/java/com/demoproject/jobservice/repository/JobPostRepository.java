package com.demoproject.jobservice.repository;

import com.demoproject.jobservice.entity.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobPostRepository extends JpaRepository<JobPost, Long> {
    List<JobPost> findByEmployerId(Long employerId);
}