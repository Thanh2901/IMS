package com.example.camera_service.mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;

import com.example.camera_service.client.StreamingClient;
import com.example.camera_service.exception.AppException;
import com.example.camera_service.exception.ErrorCode;
import com.example.camera_service.repository.CameraRepository;
import com.example.camera_service.utils.CameraStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.camera_service.dto.request.CameraRequest;
import com.example.camera_service.dto.request.CameraUpdateRequest;
import com.example.camera_service.dto.response.CameraResponse;
import com.example.camera_service.entity.Camera;

import lombok.RequiredArgsConstructor;

public interface CameraMapper {
    Camera toCamera(CameraRequest cameraRequest);

    CameraResponse toCameraResponse(Camera camera);

    void updateCamera(Camera camera, CameraUpdateRequest cameraRequest);
}

@Component
@RequiredArgsConstructor
class CameraMapperIml implements CameraMapper {

    private final CameraUserMapper cameraUserMapper;

    @Override
    public Camera toCamera(CameraRequest cameraRequest) {
        if (cameraRequest == null) return null;
        Camera camera = new Camera();
        camera.setName(cameraRequest.getName());
        camera.setIpAddress(cameraRequest.getIpAddress());
        camera.setPort(cameraRequest.getPort());
        if (cameraRequest.getIpAddress() != null && cameraRequest.getPort() != null) {
            String rtsp =
                    String.format("rtsp://%s:%s/live.stream", cameraRequest.getIpAddress(), cameraRequest.getPort());
            camera.setRtsp(rtsp);
        }
        camera.setUsername(cameraRequest.getUsername());
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        camera.setPassword(passwordEncoder.encode(cameraRequest.getPassword()));
        camera.setCameraUserList(new ArrayList<>());
        return camera;
    }

    @Override
    public CameraResponse toCameraResponse(Camera camera) {
        if (camera == null) return null;
        CameraResponse cameraResponse = new CameraResponse();
        cameraResponse.setId(camera.getId());
        cameraResponse.setName(camera.getName());
        cameraResponse.setRtsp(camera.getRtsp());
        cameraResponse.setIpAddress(camera.getIpAddress());
        cameraResponse.setPort(camera.getPort());
        cameraResponse.setUserName(camera.getName());
        cameraResponse.setCameraStatus(camera.getCameraStatus());
        cameraResponse.setCameraUserList(camera.getCameraUserList().stream()
                .map(cameraUserMapper::toCameraUserResponse)
                .toList());
        cameraResponse.setCreatedAt(camera.getCreatedAt());
        cameraResponse.setUpdatedAt(camera.getUpdatedAt());
        return cameraResponse;
    }

    @Override
    public void updateCamera(Camera camera, CameraUpdateRequest cameraRequest) {
        if (cameraRequest.getName() != null && !cameraRequest.getName().isEmpty()) {
            camera.setName(cameraRequest.getName());
        }
        if (cameraRequest.getIpAddress() != null
                && !cameraRequest.getIpAddress().isEmpty()) {
            camera.setIpAddress(cameraRequest.getIpAddress());
            // Cập nhật RTSP chỉ khi IP hoặc port thay đổi
            if (cameraRequest.getPort() != null && !cameraRequest.getPort().isEmpty()) {
                String rtsp = String.format(
                        "rtsp://%s:%s/live.stream", cameraRequest.getIpAddress(), cameraRequest.getPort());
                camera.setRtsp(rtsp);
                camera.setPort(cameraRequest.getPort());
            }
        }
        if (cameraRequest.getUsername() != null && !cameraRequest.getUsername().isEmpty()) {
            camera.setUsername(cameraRequest.getUsername());
        }
        if (cameraRequest.getPassword() != null && !cameraRequest.getPassword().isEmpty()) {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
            camera.setPassword(passwordEncoder.encode(cameraRequest.getPassword()));
        }
        if (cameraRequest.getCameraStatus() != null) {
            camera.setCameraStatus(cameraRequest.getCameraStatus());
        }
        camera.setCameraUserList(new ArrayList<>());
        camera.setUpdatedAt(LocalDateTime.now());
    }
}
