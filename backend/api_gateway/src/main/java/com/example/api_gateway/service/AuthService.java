package com.example.api_gateway.service;

import com.example.api_gateway.dto.ApiResponse;
import com.example.api_gateway.dto.CredentialResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final WebClient.Builder webClientBuilder;

    public Mono<ApiResponse<CredentialResponse>> validateToken(String token) {
        return webClientBuilder
                .baseUrl("lb://AUTH-SERVICE")
                .build()
                .post()
                .uri("/api/auth/validate")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<CredentialResponse>>() { })
                .doOnNext(response -> log.info("Token validation response: {}", response))
                .doOnError(error -> log.error("Error during token validation: {}", error.getMessage(), error));
    }
}
