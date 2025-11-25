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
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User registered successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation or duplicate error")
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> registerUser(
            @Valid @RequestBody RegisterRequest request) {
        var response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", response));
    }

    @Operation(summary = "Login user and get JWT")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> loginUser(
            @Valid @RequestBody LoginRequest request) {
        var response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PatchMapping("/update/email/{userId}")
    public ResponseEntity<ApiResponse<String>> updateEmail(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateEmailRequest request) {

        authService.updateEmail(userId, request.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Email updated successfully", request.getEmail()));
    }

    @PatchMapping("/update/username/{userId}")
    public ResponseEntity<ApiResponse<String>> updateUsername(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUsernameRequest request) {

        authService.updateUsername(userId, request.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Username updated successfully", request.getUsername()));
    }

    @PatchMapping("/update/password/{userId}")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @PathVariable Long userId,
            @Valid @RequestBody UpdatePasswordRequest request) {

        authService.updatePassword(userId, request.getPassword());
        return ResponseEntity.ok(ApiResponse.success("Password updated successfully", null));
    }

    @PatchMapping("/upgrade-to-employer/{userId}")
    public ResponseEntity<ApiResponse<Void>> upgradeRole(@PathVariable Long userId) {
        authService.upgradeToEmployer(userId);
        return ResponseEntity.ok(ApiResponse.success("User role upgraded to EMPLOYER", null));
    }
}
