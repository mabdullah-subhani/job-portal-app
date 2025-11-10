package com.demoproject.jobservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String location;
    private String employmentType; // Full-time, Part-time, Remote
    private Double salary;
    private LocalDateTime createdAt;

    // ✅ Just store the employer ID instead of the whole Employer object
    @Column(name = "employer_id", nullable = false)
    private Long employerId;
}
