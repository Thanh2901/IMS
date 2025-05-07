package com.example.auth_service.config;

import com.example.auth_service.dto.response.CredentialResponse;
import com.example.auth_service.model.Credential;
import org.springframework.stereotype.Component;

@Component
public class Mapper {
    public static CredentialResponse map(Credential credential) {
        return CredentialResponse.builder()
                .valid(true)
                .credentialId(credential.getId())
                .email(credential.getEmail())
                .roles(credential.getRoles())
                .createdAt(credential.getCreatedAt())
                .updatedAt(credential.getUpdatedAt())
                .build();
    }
}
