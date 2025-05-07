package com.example.camera_service.controller;

import java.util.List;

import com.example.camera_service.client.StreamingClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.camera_service.dto.request.CameraRequest;
import com.example.camera_service.dto.request.CameraUpdateRequest;
import com.example.camera_service.dto.response.ApiResponse;
import com.example.camera_service.dto.response.CameraResponse;
import com.example.camera_service.service.impl.CameraServiceImpl;

@RestController
@RequestMapping("/api/cameras")
public class CameraController {
    private final CameraServiceImpl cameraService;
    private final StreamingClient streamingClient;

    @Autowired
    public CameraController(CameraServiceImpl cameraService, StreamingClient streamingClient) {
        this.cameraService = cameraService;
        this.streamingClient = streamingClient;
    }

    // insert link rtsp
    @PostMapping("/create")
    public ApiResponse<CameraResponse> createCamera(
            @RequestBody CameraRequest request, @RequestHeader("X-Credential-Id") String credentialId) {
        return ApiResponse.<CameraResponse>builder()
                .data(cameraService.createCamera(request, credentialId))
                .build();
    }

    @GetMapping("/get/{cameraId}")
    public ApiResponse<CameraResponse> getCamera(@PathVariable("cameraId") String id) {
        return ApiResponse.<CameraResponse>builder()
                .data(cameraService.getCameraById(id))
                .build();
    }

    @GetMapping("/get/list")
    public ApiResponse<List<CameraResponse>> getCameraList() {
        return ApiResponse.<List<CameraResponse>>builder()
                .data(cameraService.getAllCameras())
                .build();
    }

    @PutMapping("/update/{cameraId}")
    public ApiResponse<CameraResponse> updateCamera(
            @PathVariable("cameraId") String id, @RequestBody CameraUpdateRequest request) {
        return ApiResponse.<CameraResponse>builder()
                .data(cameraService.updateCamera(id, request))
                .build();
    }

    @DeleteMapping("/delete/{cameraId}")
    public ApiResponse<String> deleteCamera(@PathVariable("cameraId") String id) {
        cameraService.deleteCamera(id);
        return ApiResponse.<String>builder()
                .data("camera " + id + " is deleted")
                .build();
    }

    @GetMapping("/streaming/{cameraId}")
    public ApiResponse<String> streamingVideo(@PathVariable String cameraId) {
        return ApiResponse.<String>builder()
                .data(cameraService.streaming(cameraId))
                .build();
    }

    @GetMapping("/status")
    public ApiResponse<Boolean> checkStatusVideo(@RequestParam String cameraName) {
        return ApiResponse.<Boolean>builder()
                .data(streamingClient.checkCameraStatus(cameraName))
                        .build();
    }

    //    @GetMapping("/{cameraId}/stream-url")
    //    public ApiResponse<MediaUri> getStreamUrlByCamId(@PathVariable("cameraId") String id) throws Exception {
    //        return ApiResponse.<MediaUri>builder()
    //                .data(cameraService.getStreamUrlByCamId(id))
    //                .build();
    //    }
    //
    //    @GetMapping("/{cameraId}/profile")
    //    public ApiResponse<Profile> getProfileByCamId(@PathVariable("cameraId") String id) throws Exception {
    //        return ApiResponse.<Profile>builder()
    //                .data(cameraService.getProfileByCamId(id))
    //                .build();
    //    }
    //
    //    @GetMapping("/{cameraId}/device-info")
    //    public ApiResponse<GetDeviceInformationResponse> getDeviceInfoByCamId(@PathVariable("cameraId") String id)
    //            throws Exception {
    //        return ApiResponse.<GetDeviceInformationResponse>builder()
    //                .data(cameraService.getDeviceInfoByCamId(id))
    //                .build();
    //    }
}
