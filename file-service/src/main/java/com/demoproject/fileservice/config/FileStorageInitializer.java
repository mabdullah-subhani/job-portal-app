package com.demoproject.fileservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@RequiredArgsConstructor
public class FileStorageInitializer implements ApplicationRunner {

    private final FileStorageProperties fileStorageProperties;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String path = fileStorageProperties.getTempDir();
        File dir = new File(path);

        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (created) {
                System.out.println("Directory created: " + dir.getAbsolutePath());
            } else {
                System.err.println("Failed to create directory: " + dir.getAbsolutePath());
            }
        } else {
            System.out.println("Directory exists: " + dir.getAbsolutePath());
        }
    }
}

