package com.example.auth_service.client;

import com.example.auth_service.dto.ApiResponse;
import com.example.auth_service.dto.request.RegisterUserRequest;
import com.example.auth_service.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "user-service")
public interface UserClient {
    @PostMapping("/api/users/register")
    ApiResponse<UserResponse> registerUser(RegisterUserRequest request);
    @GetMapping("/api/users/{credentialId}")
    ApiResponse<UserResponse> getUser(@PathVariable("credentialId") String credentialId);
}
