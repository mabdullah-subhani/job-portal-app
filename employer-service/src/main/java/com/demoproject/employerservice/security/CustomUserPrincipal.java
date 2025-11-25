package com.demoproject.employerservice.security;

import java.util.Set;

public record CustomUserPrincipal(Long userId, String username, Set<String> roles) {

    public CustomUserPrincipal(Long userId, String username, Set<String> roles) {
        this.userId = userId;
        this.username = username;
        // Make an immutable copy of the roles
        this.roles = Set.copyOf(roles);
    }
}


