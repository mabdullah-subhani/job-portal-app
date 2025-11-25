package com.demoproject.fileservice.config;

import java.util.UUID;

public record UpdateResumeRequest(UUID resumeFileId, String resumeUrl) { }
