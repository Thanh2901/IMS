package com.example.auth_service.dto.response;

import com.example.auth_service.constant.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CredentialResponse {
    @Builder.Default
    private boolean valid = false;
    private String code;
    private String message;

    private String credentialId;
    private String email;
    private List<Role> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
