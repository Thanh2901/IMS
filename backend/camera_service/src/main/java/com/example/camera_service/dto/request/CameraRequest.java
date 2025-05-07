package com.example.camera_service.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

import com.example.camera_service.utils.CameraStatus;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CameraRequest {
    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "ip address is required")
    private String ipAddress;

    @NotBlank(message = "port is required")
    private String port;

    @NotBlank(message = "user name is required")
    private String username;

    @NotBlank(message = "password is required")
    private String password;

    private List<CameraUserRequest> cameraUserList;
}
