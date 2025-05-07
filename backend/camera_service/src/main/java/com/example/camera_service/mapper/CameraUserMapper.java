package com.example.camera_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.example.camera_service.dto.request.CameraUserRequest;
import com.example.camera_service.dto.response.CameraUserResponse;
import com.example.camera_service.entity.CameraUser;

@Mapper(componentModel = "spring")
public interface CameraUserMapper {
    CameraUser toCameraUser(CameraUserRequest cameraUserRequest);

    CameraUserResponse toCameraUserResponse(CameraUser cameraUser);

    void updateCameraUser(@MappingTarget CameraUser cameraUser, CameraUserRequest cameraUserRequest);
}
