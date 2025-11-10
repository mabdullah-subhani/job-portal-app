package com.demoproject.authservice.controller;

import com.demoproject.authservice.dto.*;
import com.demoproject.authservice.payload.ApiResponse;
import com.demoproject.authservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User registration and login APIs")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register a new user")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User registered successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation or duplicate error")
    })
    @PostMapping("/register")
    public ResponseEntity<com.demoproject.authservice.payload.ApiResponse<RegisterResponse>> registerUser(
            @Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.ok(com.demoproject.authservice.payload.ApiResponse.<RegisterResponse>builder()
                .success(true)
                .message("User registered successfully")
                .data(response)
                .build());
    }

    @Operation(summary = "Login user and get JWT")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<com.demoproject.authservice.payload.ApiResponse<LoginResponse>> loginUser(
            @Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(com.demoproject.authservice.payload.ApiResponse.<LoginResponse>builder()
                .success(true)
                .message("Login successful")
                .data(response)
                .build());
    }


    @PatchMapping("/update/email/{userId}")
    public ResponseEntity<ApiResponse<String>> updateEmail(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateEmailRequest request) {

        authService.updateEmail(userId, request.getEmail());
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Email updated successfully")
                .data(request.getEmail())
                .build());
    }

    @PatchMapping("/update/username/{userId}")
    public ResponseEntity<ApiResponse<String>> updateUsername(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUsernameRequest request) {

        authService.updateUsername(userId, request.getUsername());
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Username updated successfully")
                .data(request.getUsername())
                .build());
    }

    @PatchMapping("/update/password/{userId}")
    public ResponseEntity<ApiResponse<String>> updatePassword(
            @PathVariable Long userId,
            @Valid @RequestBody UpdatePasswordRequest request) {

        authService.updatePassword(userId, request.getPassword());
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Password updated successfully")
                .build());
    }

    @PatchMapping("/upgrade-to-employer/{userId}")
    public ResponseEntity<ApiResponse<String>> upgradeRole(@PathVariable Long userId) {
        authService.upgradeToEmployer(userId);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("User role upgraded to EMPLOYER")
                .build());
    }
}


