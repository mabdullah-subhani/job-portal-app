package com.demoproject.fileservice.repository;

import com.demoproject.fileservice.entity.StoredFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;


public interface StoredFileRepository extends JpaRepository<StoredFile, UUID> {
    List<StoredFile> findByOwnerUserIdAndStatusNot(Long ownerUserId, com.demoproject.fileservice.entity.FileStatus excludedStatus);
    List<StoredFile> findByOwnerUserId(Long ownerUserId);
}

