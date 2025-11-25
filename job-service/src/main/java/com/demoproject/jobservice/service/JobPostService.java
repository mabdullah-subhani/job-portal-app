package com.demoproject.jobservice.service;

import com.demoproject.jobservice.dto.CreateJobPostRequest;
import com.demoproject.jobservice.dto.UpdateJobPostRequest;
import com.demoproject.jobservice.dto.JobPostResponse;

import java.util.List;
import java.util.UUID;

public interface JobPostService {

    JobPostResponse createJobPost(CreateJobPostRequest dto);

    JobPostResponse updateJobPost(UUID jobPostId, UpdateJobPostRequest dto);

    List<JobPostResponse> getAllJobPosts(int page, int size);

    List<JobPostResponse> getJobPostsByEmployer(UUID employerId, int page, int size);

    void deleteJobPost(UUID jobPostId);

    List<JobPostResponse> searchJobs(String keyword, String location,
                                     String employmentType, Double minSalary,
                                     Double maxSalary);
}
