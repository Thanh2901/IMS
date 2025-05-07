package com.example.auth_service.service;

import com.example.auth_service.client.NotificationClient;
import com.example.auth_service.config.Mapper;
import com.example.auth_service.client.UserClient;
import com.example.auth_service.constant.JwtConstant;
import com.example.auth_service.constant.Role;
import com.example.auth_service.dto.request.LoginRequest;
import com.example.auth_service.dto.request.RegisterRequest;
import com.example.auth_service.dto.request.RegisterUserRequest;
import com.example.auth_service.dto.request.SaveFcmTokenRequest;
import com.example.auth_service.dto.response.CredentialResponse;
import com.example.auth_service.dto.response.JwtValidationResult;
import com.example.auth_service.dto.response.TokenResponse;
import com.example.auth_service.dto.response.UserResponse;
import com.example.auth_service.model.Credential;
import com.example.auth_service.model.PasswordResetToken;
import com.example.auth_service.repository.CredentialRepository;
import com.example.auth_service.repository.PasswordResetTokenRepository;
import io.jsonwebtoken.Claims;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final CredentialRepository credentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;
    private final UserClient userClient;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final NotificationClient notificationClient;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        credentialRepository.findCredentialByEmail(request.getEmail())
                .ifPresent(credential -> {
                    throw new RuntimeException("Credential already exists");
                });

        Credential newCredential = Credential.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Collections.singletonList(Role.ROLE_USER))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Credential credential = credentialRepository.save(newCredential);

        RegisterUserRequest registerUserRequest = RegisterUserRequest.builder()
                .credentialId(credential.getId())
                .phone(request.getPhone())
                .fullName(request.getFullName())
                .address(request.getAddress())
                .build();

        var userResponse = userClient.registerUser(registerUserRequest);
        if (userResponse.getCode() != 1000) {
            throw new RuntimeException("Failed to register new user profile");
        }

        return userResponse.getData();
    }

    public TokenResponse login(LoginRequest request) {
        String token = authenticateAndGenerateToken(
                request.getEmail(),
                request.getPassword()
        );

        log.info("Token: {}", token);
        Credential credential = findCredentialByToken(token);

        log.info("Credential: {}", credential);


        SaveFcmTokenRequest tokenRequest = new SaveFcmTokenRequest();
        tokenRequest.setFcmToken(request.getFcmToken());
        tokenRequest.setUserId(credential.getId());

        try {
            notificationClient.saveToken(tokenRequest);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        return TokenResponse.builder()
                .token(token)
                .build();
    }

    public CredentialResponse validate(String bearerToken) {
        log.info("Bearer token: {}", bearerToken);
        String token = bearerToken.substring(7);

        log.info("Header token: {}", token);

        JwtValidationResult result = jwtService.validateToken(token);

        if (!result.isValid()) {
            // Token không hợp lệ, trả về phản hồi với mã lỗi và thông báo tương ứng
            return CredentialResponse.builder()
                .valid(false)
                .code(result.getErrorCode().getCode())
                .message(result.getErrorCode().getMessage())
                .build();
        }

        Credential credential = findCredentialByToken(token);
        if (tokenBlacklistService.isTokenBlackListed(token)) {
            return CredentialResponse.builder()
                .valid(false)
                .code("JWT_006")
                .message("Token is blacklisted")
                .build();
        }

        CredentialResponse credentialResponse =  Mapper.map(credential);
        credentialResponse.setCode(result.getErrorCode().getCode());
        credentialResponse.setMessage(result.getErrorCode().getMessage());
        return credentialResponse;
    }

    public void logout(String bearerToken) {
        String token = bearerToken.substring(7);
        log.info("Blacklisting token: {}", token);
        tokenBlacklistService.blacklistToken(token, JwtConstant.jwtExpiration);
    }

    private String authenticateAndGenerateToken(String email, String password) {
        UserDetails userDetails = userService.loadUserByUsername(email);

        log.info("Authenticate user details: {}", userDetails);

        // check hashed password
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtService.generateToken(new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities()));
    }

    private Credential findCredentialByToken(String token) {
        String email = jwtService.getEmailFromJwt(token);
        log.info("Email extracted from token: {}", email);
        return credentialRepository.findCredentialByEmail(email)
                .orElseThrow(() -> new RuntimeException("Credential not exists"));
    }

    @Transactional
    public void forgotPassword(String email) {

        if(credentialRepository.findCredentialByEmail(email).isEmpty()) {
            throw new RuntimeException("Credential not exists");
        }

        //create token for forgot password, have exprire
        tokenRepository.findByEmail(email).ifPresent(tokenRepository::delete);

        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = PasswordResetToken.builder()
            .email(email)
            .token(token)
            .createdAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plusMinutes(30))
            .build();
        tokenRepository.save(passwordResetToken);
        // send link hava token for reset password
        String resetLink = "http://localhost:3000/auth/reset-password?token=" + token;
        String emailBody = "<p>Click vào link sau để đặt lại mật khẩu:</p>" +
            "<a href='" + resetLink + "'>Đặt lại mật khẩu</a>";
        try {
            emailService.sendEmail(email, "Reset Password", emailBody);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String resetPassword(String token, String newPassword) {
        //check token if exist and have not expired
        PasswordResetToken passwordResetToken = tokenRepository.findByToken(token).orElse(null);

        if (passwordResetToken == null) {
            throw new RuntimeException("Invalid token");
        }

        if (passwordResetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token has expired");
        }
        // find email by this token and reset new user password
        Credential user = credentialRepository.findCredentialByEmail(passwordResetToken.getEmail()).orElse(null);
        user.setPassword(passwordEncoder.encode(newPassword));
        credentialRepository.save(user);
        tokenRepository.delete(passwordResetToken);
        return user.getEmail();

    }
}
