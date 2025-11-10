package com.demoproject.gatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {
    private static final String[] PUBLIC = {
            "/api/auth/**", "/auth/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**", "/actuator/**"
    };
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(ex -> ex
                        .pathMatchers(PUBLIC).permitAll()
                        .anyExchange().permitAll() // JWT gate is your GlobalFilter
                )
                .build();
    }
}

