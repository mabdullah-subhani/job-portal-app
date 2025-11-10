package com.demoproject.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class LoginRequest {

    @NotBlank(message = "Username or email is required")
    private String login;

    @NotBlank(message = "Password is required")
    private String password;

}

