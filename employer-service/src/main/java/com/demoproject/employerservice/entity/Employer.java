package com.demoproject.employerservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "employers", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"company_name"}),
        @UniqueConstraint(columnNames = {"email"})
})
public class Employer extends BaseAuditEntity {   // create BaseAuditEntity same as candidate

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId; // link to auth user who owns this employer profile

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;
    private String website;
    private String address;
    private String industry;
    @Column(length = 2000)
    private String description;

    @ElementCollection
    @CollectionTable(name = "employer_jobs", joinColumns = @JoinColumn(name = "employer_id"))
    @Column(name = "job_id")
    @Builder.Default
    private Set<UUID> jobIds = new HashSet<>();
}
