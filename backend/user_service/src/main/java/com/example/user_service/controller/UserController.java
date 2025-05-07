package com.example.user_service.controller;

import com.example.user_service.dto.ApiResponse;
import com.example.user_service.dto.request.RegisterUserRequest;
import com.example.user_service.dto.request.SettingRequest;
import com.example.user_service.dto.request.UpdateMeRequest;
import com.example.user_service.dto.response.UserResponse;
import com.example.user_service.model.UserSetting;
import com.example.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ApiResponse<UserResponse> registerUser(@RequestBody RegisterUserRequest request) {
        var res = userService.registerUser(request);
        return ApiResponse.<UserResponse>builder().data(res).build();
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> getMe(@RequestHeader("X-Credential-Id") String credentialId) {
        var res = userService.getMe(credentialId);
        return ApiResponse.<UserResponse>builder().data(res).build();
    }

    @PostMapping("/me")
    public ApiResponse<UserResponse> updateMe(@RequestHeader("X-Credential-Id") String credentialId, @RequestBody UpdateMeRequest request) {
        var res = userService.updateMe(credentialId, request);
        return ApiResponse.<UserResponse>builder().data(res).build();
    }

    @GetMapping("/{credentialId}")
    public ApiResponse<UserResponse> getUser(@PathVariable("credentialId") String credentialId) {
        var res = userService.getMe(credentialId);
        return ApiResponse.<UserResponse>builder().data(res).build();
    }

    @PostMapping("/setting")
    public ApiResponse<UserSetting> updateSetting(@RequestHeader("X-Credential-Id") String credentialId, @RequestBody SettingRequest request) {
        return ApiResponse.<UserSetting>builder()
            .message("Setting updated successfully")
            .data(userService.updateSetting(credentialId, request))
            .build();
    }
}
