package com.demoproject.fileservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoredFile extends BaseAuditEntity {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "owner_user_id", nullable = false)
    private Long ownerUserId;

    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private FileCategory category;

    @Column(name = "original_name", nullable = false)
    private String originalName;

    @Column(name = "s3_key", nullable = false, unique = true)
    private String s3Key;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "size", nullable = false)
    private Long size;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private FileStatus status;

    private String checksum;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
    }
}

