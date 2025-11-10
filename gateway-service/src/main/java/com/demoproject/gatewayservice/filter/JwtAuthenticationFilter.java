package com.demoproject.gatewayservice.filter;


import com.demoproject.gatewayservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter {

    private final JwtUtil jwtUtil;
    private final AntPathMatcher matcher = new AntPathMatcher();

    private static final String[] PUBLIC_PATHS = {
            "/api/auth/**","/auth/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**", "/actuator/**"
    };

    private boolean isPublic(ServerWebExchange exchange) {
        String path = exchange.getRequest().getURI().getPath();
        for (String p : PUBLIC_PATHS) {
            if (matcher.match(p, path)) return true;
        }
        return false;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // Let OPTIONS (preflight) and public endpoints pass
        if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS || isPublic(exchange)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.isValid(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // Forward original Authorization header
        var mutated = exchange.getRequest().mutate()
                .header("X-Auth-UserId", String.valueOf(jwtUtil.getUserId(token)))
                .header("X-Auth-Username", jwtUtil.getUsername(token))
                .header("X-Auth-Roles", String.join(",", jwtUtil.getRoles(token)))
                .build();
        return chain.filter(exchange.mutate().request(mutated).build());
    }
}




