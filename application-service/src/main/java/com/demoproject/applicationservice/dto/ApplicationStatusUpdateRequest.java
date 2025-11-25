package com.demoproject.applicationservice.dto;

import com.demoproject.applicationservice.entity.ApplicationStatus;
import com.demoproject.applicationservice.exception.ValidationException;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ApplicationStatusUpdateRequest {
    @NotBlank
    private String status;// SHORTLISTED, REJECTED, SELECTED, WITHDRAWN

    public void validateStatus(String s) {
        try {
            ApplicationStatus.valueOf(s);
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("Invalid status");
        }
    }

}
