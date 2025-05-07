package com.example.user_service.service;

import com.example.user_service.config.Mapper;
import com.example.user_service.dto.request.RegisterUserRequest;
import com.example.user_service.dto.request.SettingRequest;
import com.example.user_service.dto.request.UpdateMeRequest;
import com.example.user_service.dto.response.UserResponse;
import com.example.user_service.model.Users;
import com.example.user_service.model.UserSetting;
import com.example.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    public UserResponse registerUser(RegisterUserRequest request) {
        log.info("User registration: {}", request);

        userRepository.findByCredentialId(request.getCredentialId())
                .ifPresent(user -> {throw new RuntimeException("User already exists");});

        UserSetting userSetting = new UserSetting();
        userSetting.setSettingNotification(true);
        userSetting.setSettingAddInfrastructure(false);

        Users user = Users.builder()
                .credentialId(request.getCredentialId())
                .fullName(request.getFullName())
                .userSetting(userSetting)
                .build();

        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }

        return Mapper.map(userRepository.save(user));
    }

    public UserResponse updateMe(String credentialId, UpdateMeRequest request) {
        log.info("Update my profile: {}", request);

        Users user = userRepository.findByCredentialId(credentialId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        if (request.getPhone() != null) {
            user.setFullName(request.getPhone());
        }

        if (request.getAddress() != null) {
            user.setFullName(request.getAddress());
        }

        return Mapper.map(user);
    }

    public UserResponse getMe(String credentialId) {
        log.info("Get my profile");

        Users user = userRepository.findByCredentialId(credentialId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return Mapper.map(user);
    }

    public UserSetting updateSetting(String credentialId, SettingRequest request) {

        Users user = userRepository.findByCredentialId(credentialId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserSetting userSetting = user.getUserSetting();
        userSetting.setSettingNotification(request.getSettingNotification());
        userSetting.setSettingAddInfrastructure(request.getSettingAddInfrastructure());

        user.setUserSetting(userSetting);

        return userRepository.save(user).getUserSetting();
    }

}