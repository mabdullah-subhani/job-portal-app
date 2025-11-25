 package com.demoproject.fileservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
 @Data
 @ConfigurationProperties(prefix = "aws.s3")
 public class AwsS3Properties {
     private String bucketName;
     private String region;
     private String accessKey;    // add this
     private String secretKey;    // add this
     private Duration presignUploadTtl = Duration.ofMinutes(15);
     private Duration presignDownloadTtl = Duration.ofMinutes(5);
     private long maxResumeSize = 5 * 1024 * 1024;
 }

