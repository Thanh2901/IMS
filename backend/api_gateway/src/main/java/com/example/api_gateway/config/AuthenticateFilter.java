package com.example.api_gateway.config;

import com.example.api_gateway.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class AuthenticateFilter implements GatewayFilter {

    private final AuthService authService;

    public AuthenticateFilter(AuthService authService) {
        this.authService = authService;
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {


        ServerHttpRequest request = exchange.getRequest();
        String requestPath = request.getURI().getPath();
        if (requestPath.contains("/public/") || requestPath.endsWith("/public")) {
            log.info("Bypass auth for public URL: {}", requestPath);
            return chain.filter(exchange);
        }
        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            return onError(exchange,"403", "No Authorization header", HttpStatus.UNAUTHORIZED);
        }
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            return onError(exchange,"403", "Invalid Authorization Format", HttpStatus.UNAUTHORIZED);
        }

        String token = bearerToken.substring(7);
        log.info("Token: {}", token);

        return authService.validateToken(token)
                .flatMap(response -> {
                    log.info("Validate token response: {}", response);
                    if (response.getData().isValid()) {
                        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                                .header("X-Credential-Id", response.getData().getCredentialId())
                                //.header("X-User-Role", String.join(",", response.getData().getRoles()))
                                .build();
                        return chain.filter(exchange.mutate().request(mutatedRequest).build());
                    } else {
                        return onError(exchange, response.getData().getCode(), response.getData().getMessage(), HttpStatus.UNAUTHORIZED);
                    }
                }).onErrorResume(e -> onError(exchange, "INTERNAL_ERROR", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));

    }

    private Mono<Void> onError(ServerWebExchange exchange, String code, String message, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");

        String errorResponse = String.format(
            "{\"code\": \"%s\", \"message\": \"%s\", \"status\": %d}",
            code, message, status.value()
        );
        byte[] bytes = errorResponse.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
