package com.demoproject.authservice.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String token;        // JWT token
    private String tokenType;    // e.g., Bearer
    private Long userId;
    private String username;
    private String email;
    private Set<String> roles;
    private LocalDateTime timestamp;
}
