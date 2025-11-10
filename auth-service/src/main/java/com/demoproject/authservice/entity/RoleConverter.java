package com.demoproject.authservice.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Converter
public class RoleConverter implements AttributeConverter<Set<Role>, String> {

    private static final String SEPARATOR = ",";

    @Override
    public String convertToDatabaseColumn(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return null;
        }
        return roles.stream()
                .map(Enum::name) // store as uppercase names
                .collect(Collectors.joining(SEPARATOR));
    }

    @Override
    public Set<Role> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return Set.of();
        }
        return Arrays.stream(dbData.split(SEPARATOR))
                .map(Role::fromStringIgnoreCase) // your safe converter
                .collect(Collectors.toSet());
    }
}
