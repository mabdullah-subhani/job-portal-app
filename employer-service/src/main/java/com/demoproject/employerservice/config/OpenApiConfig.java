package com.demoproject.employerservice.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI employerServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Employer Service API - Job Portal")
                        .description("Microservice responsible for managing employer profiles and job postings within the Job Portal platform.")
                        .version("v1.0")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("Job Portal Backend Repository")
                        .url("https://github.com/your-repo/job-portal-backend"));
    }
}

