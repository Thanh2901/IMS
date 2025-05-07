package com.example.camera_service.service;

import java.util.List;

import com.example.camera_service.dto.request.CameraRequest;
import com.example.camera_service.dto.request.CameraUpdateRequest;
import com.example.camera_service.dto.response.CameraResponse;

public interface CameraService {
    CameraResponse createCamera(CameraRequest cameraRequest, String credentialId);

    CameraResponse getCameraById(String id);

    List<CameraResponse> getAllCameras();

    CameraResponse updateCamera(String id, CameraUpdateRequest request);

    void deleteCamera(String id);

    String getRtspUrlByCameraId(String id);

    String streaming(String cameraId);

    //    MediaUri getStreamUrlByCamId(String id) throws Exception;
    //
    //    Profile getProfileByCamId(String id) throws Exception;
    //
    //    GetDeviceInformationResponse getDeviceInfoByCamId(String id) throws Exception;
    //
    //    List<Camera> getActiveCameras();
    //

}
