package com.demoproject.applicationservice.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class EmployerJobApplicantsResponse {
    private long total;
    private long shortlisted;
    private long rejected;
    private long selected;
    private long pending;
    private List<ApplicationResponse> applicants;
}
