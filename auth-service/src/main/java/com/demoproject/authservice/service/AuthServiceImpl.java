package com.demoproject.authservice.service;

import com.demoproject.authservice.config.JwtUtil;
import com.demoproject.authservice.dto.LoginRequest;
import com.demoproject.authservice.dto.LoginResponse;
import com.demoproject.authservice.dto.RegisterRequest;
import com.demoproject.authservice.dto.RegisterResponse;
import com.demoproject.authservice.entity.Role;
import com.demoproject.authservice.entity.User;
import com.demoproject.authservice.exception.DuplicateResourceException;
import com.demoproject.authservice.exception.ResourceNotFoundException;
import com.demoproject.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        log.info("Attempting to register user: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email is already registered");
        }

        // Assign default role USER
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_USER);

        // Create user entity
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .build();

        userRepository.save(user);
        log.info("User {} registered successfully", user.getUsername());

        // Build proper response
        return RegisterResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()))
                .message("User registered successfully")
                .timestamp(LocalDateTime.now())
                .build();
    }


    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for user/email: {}", request.getLogin());

        // Authenticate credentials
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLogin(),
                        request.getPassword()
                )
        );

        log.info("Authentication successful for user/email: {}", request.getLogin());

        // Load user by username or email
        User user = userRepository.findByUsername(request.getLogin())
                .or(() -> userRepository.findByEmail(request.getLogin()))
                .orElseThrow(() -> {
                    log.warn("User not found for login identifier: {}", request.getLogin());
                    return new ResourceNotFoundException("User not found with username or email: " + request.getLogin());
                });

        // Convert Role enums to String set
        Set<String> roleStrings = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        // Generate JWT
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), roleStrings);
        log.info("JWT token generated successfully for user/email: {}", request.getLogin());

        // Build login response with candidate UUID
        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(roleStrings)
                .timestamp(LocalDateTime.now())
                .build();
    }


    @Override
    @Transactional
    public void updateEmail(Long userId, String newEmail) {
        if (userRepository.existsByEmail(newEmail)) {
            throw new DuplicateResourceException("Email is already taken");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        user.setEmail(newEmail);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUsername(Long userId, String newUsername) {
        if (userRepository.existsByUsername(newUsername)) {
            throw new DuplicateResourceException("Username is already taken");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        user.setUsername(newUsername);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updatePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void upgradeToEmployer(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (user.getRoles().contains(Role.ROLE_EMPLOYER)) {
            throw new DuplicateResourceException("User is already an employer");
        }

        user.getRoles().add(Role.ROLE_EMPLOYER);
        userRepository.save(user);

        log.info("User {} upgraded to EMPLOYER role", user.getUsername());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasEmployerAccess(Long userId) {
        return userRepository.findById(userId)
                .map(user -> user.getRoles().contains(Role.ROLE_EMPLOYER))
                .orElse(false);
    }

}
