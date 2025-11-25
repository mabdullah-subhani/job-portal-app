package com.demoproject.candidateservice.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI candidateServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Candidate Service API - Job Portal")
                        .description("Microservice responsible for managing candidate profiles and resumes within the Job Portal platform.")
                        .version("v1.0")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("Job Portal Backend Repository")
                        .url("https://github.com/your-repo/job-portal-backend"));
    }
}

