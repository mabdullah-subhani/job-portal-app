package com.demoproject.fileservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class GatewayAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain)
            throws ServletException, IOException {

        String userIdHeader = request.getHeader("X-Auth-UserId");
        String username = request.getHeader("X-Auth-Username");
        String rolesHeader = request.getHeader("X-Auth-Roles");

        if (username != null && rolesHeader != null && userIdHeader != null) {
            try {
                // Parse userId safely
                Long userId = Long.parseLong(userIdHeader);

                // Convert roles string to authorities for Spring Security
                List<SimpleGrantedAuthority> authorities = Arrays.stream(rolesHeader.split(","))
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.trim()))
                        .collect(Collectors.toList());

                // Immutable set of roles for CustomUserPrincipal
                Set<String> roleSet = Arrays.stream(rolesHeader.split(","))
                        .map(String::trim)
                        .collect(Collectors.toUnmodifiableSet());

                // Create principal and authentication token
                CustomUserPrincipal principal = new CustomUserPrincipal(userId, username, roleSet);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(principal, null, authorities);

                // Set authentication in SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (NumberFormatException ex) {
                // Log and reject invalid userId header
                log.warn("Invalid userId header: {}", userIdHeader);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid userId header");
                return;
            }
        }
// Continue filter chain
        chain.doFilter(request, response);
    }
}

