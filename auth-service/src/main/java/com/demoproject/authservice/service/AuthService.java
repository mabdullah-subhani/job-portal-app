package com.demoproject.authservice.service;

import com.demoproject.authservice.dto.LoginRequest;
import com.demoproject.authservice.dto.LoginResponse;
import com.demoproject.authservice.dto.RegisterRequest;
import com.demoproject.authservice.dto.RegisterResponse;

public interface AuthService {

    RegisterResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    void updateEmail(Long userId, String newEmail);

    void updateUsername(Long userId, String newUsername);

    void updatePassword(Long userId, String newPassword);

    void upgradeToEmployer(Long userId);

    boolean hasEmployerAccess(Long userId);

}


