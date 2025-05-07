package com.example.auth_service.controller;

import com.example.auth_service.constant.JwtConstant;
import com.example.auth_service.dto.ApiResponse;
import com.example.auth_service.dto.request.LoginRequest;
import com.example.auth_service.dto.request.RegisterRequest;
import com.example.auth_service.dto.response.CredentialResponse;
import com.example.auth_service.dto.response.RegisterResponse;
import com.example.auth_service.dto.response.TokenResponse;
import com.example.auth_service.dto.response.UserResponse;
import com.example.auth_service.service.AuthService;
import com.example.auth_service.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@RequestBody RegisterRequest request) {
        var result = authService.register(request);
        return ApiResponse.<UserResponse>builder().data(result).build();
    }

    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@RequestBody LoginRequest request) {
        var result = authService.login(request);
        return ApiResponse.<TokenResponse>builder().data(result).build();
    }

    @PostMapping("/validate")
    public ApiResponse<CredentialResponse> validate(@RequestHeader(JwtConstant.JWT_HEADER) String bearerToken) {
        var result = authService.validate(bearerToken);
        return ApiResponse.<CredentialResponse>builder().data(result)   .build();
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout(@RequestHeader(JwtConstant.JWT_HEADER) String bearerToken) {
        authService.logout(bearerToken);
        return ApiResponse.<String>builder().data("Logout successfully").build();
    }

    @PostMapping("/forgot-password")
    public ApiResponse<Object> forgotPassword(@RequestParam String email) {

        authService.forgotPassword(email);

        return ApiResponse.builder()
            .message("Send link reset password to your email successfully")
            .build();
    }
    @PostMapping("/reset-password")
    public ApiResponse<Object> resetPassword(@RequestParam String token, @RequestParam String newPassword){

        return ApiResponse.builder()
            .message("reset password for your email successfully")
            .data(authService.resetPassword(token, newPassword))
            .build();
    }
}
