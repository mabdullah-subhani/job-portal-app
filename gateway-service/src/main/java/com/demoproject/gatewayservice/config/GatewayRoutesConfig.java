    package com.demoproject.gatewayservice.config;

    import org.springframework.cloud.gateway.route.RouteLocator;
    import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;

    @Configuration
    public class GatewayRoutesConfig {

        @Bean
        public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
            return builder.routes()

                    // ===================== AUTH SERVICE =====================
                    .route("auth-service", r -> r.path("/api/auth/**")
                            .filters(f -> f.circuitBreaker(c -> c
                                    .setName("authServiceCircuitBreaker")
                                    .setFallbackUri("forward:/fallback/auth")))
                            .uri("lb://AUTH-SERVICE"))

                    // ===================== EMPLOYER SERVICE =====================
                    .route("employer-service", r -> r.path("/api/employers/**")
                            .filters(f -> f.circuitBreaker(c -> c
                                    .setName("employerServiceCircuitBreaker")
                                    .setFallbackUri("forward:/fallback/employers")))
                            .uri("lb://EMPLOYER-SERVICE"))

                    // ===================== CANDIDATE SERVICE =====================
                    .route("candidate-service", r -> r.path("/api/candidates/**")
                            .filters(f -> f.circuitBreaker(c -> c
                                    .setName("candidateServiceCircuitBreaker")
                                    .setFallbackUri("forward:/fallback/candidates")))
                            .uri("lb://CANDIDATE-SERVICE"))

                    // ===================== JOB SERVICE =====================
                    .route("job-service", r -> r.path("/api/job-posts/**")
                            .filters(f -> f.circuitBreaker(c -> c
                                    .setName("jobServiceCircuitBreaker")
                                    .setFallbackUri("forward:/fallback/job-posts")))
                            .uri("lb://JOB-SERVICE"))

                    // ===================== APPLICATION SERVICE =====================
                    .route("application-service", r -> r.path("/api/applications/**")
                            .filters(f -> f.circuitBreaker(c -> c
                                    .setName("applicationServiceCircuitBreaker")
                                    .setFallbackUri("forward:/fallback/applications")))
                            .uri("lb://APPLICATION-SERVICE"))

                    // ===================== FILE SERVICE =====================
                    .route("file-service", r -> r.path("/api/files/**")
                            .filters(f -> f.circuitBreaker(c -> c
                                    .setName("fileServiceCircuitBreaker")
                                    .setFallbackUri("forward:/fallback/files")))
                            .uri("lb://FILE-SERVICE"))

                    .build();
        }
    }
