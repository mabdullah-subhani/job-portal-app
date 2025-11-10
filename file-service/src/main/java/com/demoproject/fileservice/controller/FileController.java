package com.demoproject.fileservice.controller;

import com.demoproject.fileservice.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String key = fileStorageService.uploadFile(file);
        return ResponseEntity.ok("File uploaded successfully with key: " + key);
    }

    @GetMapping("/download/{key}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String key) {
        byte[] content = fileStorageService.downloadFile(key);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + key)
                .contentType(MediaType.APPLICATION_PDF)
                .body(content);
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<String> deleteFile(@PathVariable String key) {
        fileStorageService.deleteFile(key);
        return ResponseEntity.ok("File deleted successfully");
    }
}

