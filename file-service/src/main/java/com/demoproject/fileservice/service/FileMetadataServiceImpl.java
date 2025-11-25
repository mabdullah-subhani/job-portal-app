 package com.demoproject.fileservice.service;

import com.demoproject.fileservice.entity.FileStatus;
import com.demoproject.fileservice.entity.StoredFile;
import com.demoproject.fileservice.repository.StoredFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileMetadataServiceImpl implements FileMetadataService {

    private final StoredFileRepository repo;

    @Override
    @Transactional
    public StoredFile createMetadata(StoredFile file) {
        return repo.save(file);
    }

    @Override
    public StoredFile getMetadata(UUID fileId) {
        return repo.findById(fileId).orElseThrow(() -> new com.demoproject.fileservice.exception.NotFoundException("File not found"));
    }

    @Override
    public List<StoredFile> listByOwner(Long ownerUserId) {
        return repo.findByOwnerUserIdAndStatusNot(ownerUserId, FileStatus.DELETED);
    }

    @Override
    @Transactional
    public void markAvailable(UUID fileId) {
        StoredFile file = getMetadata(fileId);
        file.setStatus(FileStatus.AVAILABLE);
        repo.save(file);
    }

    @Override
    @Transactional
    public void markDeleted(UUID fileId) {
        StoredFile file = getMetadata(fileId);
        file.setStatus(FileStatus.DELETED);
        repo.save(file);
    }

    @Override
    @Transactional
    public void deleteMetadata(UUID fileId) {
        repo.deleteById(fileId);
    }
}
