package com.demoproject.jobservice.repository;

import com.demoproject.jobservice.entity.JobPost;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface JobPostRepository extends JpaRepository<JobPost, UUID> {

    List<JobPost> findByEmployerId(UUID employerId, Pageable pageable);

    @Query("""
    SELECT j FROM JobPost j
    WHERE (:keyword IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
      AND (:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%')))
      AND (:employmentType IS NULL OR LOWER(j.employmentType) = LOWER(:employmentType))
      AND (:minSalary IS NULL OR j.salary >= :minSalary)
      AND (:maxSalary IS NULL OR j.salary <= :maxSalary)
""")
    List<JobPost> searchJobs(
            @Param("keyword") String keyword,
            @Param("location") String location,
            @Param("employmentType") String employmentType,
            @Param("minSalary") Double minSalary,
            @Param("maxSalary") Double maxSalary
    );


}
