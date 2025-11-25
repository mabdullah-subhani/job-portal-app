package com.demoproject.applicationservice.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateApplicationRequest {

    // resumeFileId is optional; we will read candidate profile resume if not provided
    private UUID resumeFileId;
}
