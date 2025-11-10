package com.demoproject.authservice.entity;

import java.util.Arrays;

public enum Role {
    ROLE_USER,
    ROLE_EMPLOYER;
    public static Role fromStringIgnoreCase(String value) {
        return Arrays.stream(Role.values())
                .filter(role -> role.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid role: " + value));
    }
}
