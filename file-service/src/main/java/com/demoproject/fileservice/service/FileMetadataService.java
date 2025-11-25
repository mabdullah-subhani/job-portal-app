package com.demoproject.fileservice.service;

import com.demoproject.fileservice.entity.StoredFile;

import java.util.List;
import java.util.UUID;

public interface FileMetadataService {
    StoredFile createMetadata(StoredFile file);
    StoredFile getMetadata(UUID fileId);
    List<StoredFile> listByOwner(Long ownerUserId);
    void markAvailable(UUID fileId);
    void markDeleted(UUID fileId);
    void deleteMetadata(UUID fileId);
}


