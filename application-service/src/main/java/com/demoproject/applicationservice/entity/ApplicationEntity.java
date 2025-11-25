package com.demoproject.applicationservice.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "applications",
        indexes = {
                @Index(name = "idx_app_job", columnList = "job_id"),
                @Index(name = "idx_app_candidate", columnList = "candidate_id"),
                @Index(name = "idx_app_employer", columnList = "employer_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_candidate_job", columnNames = {"candidate_id","job_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationEntity extends BaseAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(name = "candidate_id", nullable = false, columnDefinition = "uuid")
    private UUID candidateId;
    @Column(name = "job_id", nullable = false, columnDefinition = "uuid")
    private UUID jobId;
    @Column(name = "employer_id", nullable = false, columnDefinition = "uuid")
    private UUID employerId;
    @Column(name = "resume_file_id", columnDefinition = "uuid")
    private UUID resumeFileId;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ApplicationStatus status;
}
