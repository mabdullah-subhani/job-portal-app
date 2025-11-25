package com.demoproject.employerservice.security;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserPrincipal user) {
            return user.userId();
        }
        throw new RuntimeException("User not authenticated");
    }

    public static String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserPrincipal user) {
            return user.username();
        }
        throw new RuntimeException("User not authenticated");
    }
}

