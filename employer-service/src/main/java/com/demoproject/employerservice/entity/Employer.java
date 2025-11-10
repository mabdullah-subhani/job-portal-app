package com.demoproject.employerservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;
    private String email;
    private String phone;
    private String website;
    private String address;
    private String industry;
    private String description;

    // 👇 Instead of a direct relationship, store job IDs from Job Service
    @ElementCollection
    @CollectionTable(name = "employer_jobs", joinColumns = @JoinColumn(name = "employer_id"))
    @Column(name = "job_id")
    private List<Long> jobIds;
}
