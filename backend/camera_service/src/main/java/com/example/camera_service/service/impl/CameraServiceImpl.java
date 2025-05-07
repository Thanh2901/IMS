package com.example.camera_service.service.impl;

import java.util.List;

import com.example.camera_service.client.StreamingClient;
import com.example.camera_service.dto.MediaSourceDTO;
import com.example.camera_service.repository.CameraUserRepository;
import com.example.camera_service.utils.CameraStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.camera_service.dto.request.CameraRequest;
import com.example.camera_service.dto.request.CameraUpdateRequest;
import com.example.camera_service.dto.response.CameraResponse;
import com.example.camera_service.entity.Camera;
import com.example.camera_service.entity.CameraUser;
import com.example.camera_service.exception.AppException;
import com.example.camera_service.exception.ErrorCode;
import com.example.camera_service.mapper.CameraMapper;
import com.example.camera_service.repository.CameraRepository;
import com.example.camera_service.service.CameraService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.print.attribute.standard.Media;

@Service
@Slf4j
@RequiredArgsConstructor
public class CameraServiceImpl implements CameraService {

    private final CameraRepository cameraRepository;
    private final CameraUserRepository cameraUserRepository;
    private final CameraMapper cameraMapper;
    private final StreamingClient streamingClient;
    @Value("${stream.ip-address}")
    private String streamIpAddress;
    @Value("${stream.port}")
    private String streamPort;

    public CameraResponse createCamera(CameraRequest cameraRequest, String credentialId) {

        if (cameraRepository.existsByName(cameraRequest.getName())) {
            throw new AppException(ErrorCode.CAMERA_EXISTED);
        }
        if (cameraRepository.existsByIpAddress(cameraRequest.getIpAddress())) {
            throw new AppException(ErrorCode.CAMERA_EXISTED);
        }

        // khai bao thong tin camera
        Camera camera = cameraMapper.toCamera(cameraRequest);

        // dang ky nguon len server
        MediaSourceDTO mediaSourceDTO = new MediaSourceDTO(
                camera.getName().replaceAll(" ", "").toLowerCase(),
                camera.getRtsp()
        );
        String publishResponse = streamingClient.publishingSource(mediaSourceDTO); // Ném ngoại lệ nếu thất bại
        log.info("Publish response: {}", publishResponse);

        Camera saveCamera = cameraRepository.save(camera);

        CameraUser cameraUser = new CameraUser();
        cameraUser.setUserId(credentialId);
        cameraUser.setCamera(camera);
        cameraUserRepository.save(cameraUser);

        // kiem tra link kha nang hoat dong cua rtsp
        camera.setCameraStatus(getCameraStatus(camera.getId()));
        if (camera.getCameraStatus() == CameraStatus.INACTIVE) {
            log.warn("{} is {}", camera.getName(), camera.getCameraStatus());
        }

        cameraRepository.save(camera);
        return cameraMapper.toCameraResponse(saveCamera);
    }

    public CameraResponse getCameraById(String id) {
        Camera camera = cameraRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CAMERA_NOT_EXISTED));
        return cameraMapper.toCameraResponse(camera);
    }

    public List<CameraResponse> getAllCameras() {
        return cameraRepository.findAll().stream()
                .map(cameraMapper::toCameraResponse)
                .toList();
    }

    public CameraResponse updateCamera(String id, CameraUpdateRequest request) {
        Camera camera = cameraRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CAMERA_NOT_EXISTED));
        String cameraName = camera.getName().replaceAll("", " ").toLowerCase();
        String path = streamingClient.getPath(cameraName);
        if (!path.isEmpty()) {
            streamingClient.deletePath(cameraName);
            cameraMapper.updateCamera(camera, request);
            MediaSourceDTO mediaSourceDTO = new MediaSourceDTO(camera.getName().replaceAll("", " ").toLowerCase(), camera.getRtsp());
            streamingClient.publishingSource(mediaSourceDTO);
            cameraRepository.save(camera);
        } else {
            cameraMapper.updateCamera(camera, request);
            MediaSourceDTO mediaSourceDTO = new MediaSourceDTO(camera.getName().replaceAll("", " ").toLowerCase(), camera.getRtsp());
            streamingClient.publishingSource(mediaSourceDTO);
            cameraRepository.save(camera);
        }
        return cameraMapper.toCameraResponse(camera);
    }

    public void deleteCamera(String id) {
        Camera camera = cameraRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CAMERA_NOT_EXISTED));
        streamingClient.deletePath(camera.getName().replaceAll(" ", "").toLowerCase());
        cameraRepository.delete(camera);
    }

    @Override
    public String getRtspUrlByCameraId(String id) {
        Camera camera = cameraRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CAMERA_NOT_EXISTED));
        return camera.getRtsp();
    }

    @Override
    public String streaming(String cameraId) {
        Camera camera = cameraRepository.findById(cameraId).orElseThrow(() -> new AppException(ErrorCode.CAMERA_NOT_EXISTED));
        String cameraName = camera.getName().replaceAll(" ", "").toLowerCase(); // Loại bỏ khoảng trắng và chuyển thành chữ thường
        try {
            String path = streamingClient.getPath(cameraName);
            if (path.isEmpty()) {
                MediaSourceDTO mediaSourceDTO = new MediaSourceDTO(cameraName, camera.getRtsp());
                streamingClient.publishingSource(mediaSourceDTO);
            }
        } catch (AppException e) {
            if (e.getErrorCode() == ErrorCode.RTSP_NOT_FOUND) {
                MediaSourceDTO mediaSourceDTO = new MediaSourceDTO(cameraName, camera.getRtsp());
                streamingClient.publishingSource(mediaSourceDTO);
            } else {
                throw e; // Ném lại các ngoại lệ khác
            }
        }
        return "http://" + streamIpAddress + ":" + streamPort + "/" + cameraName;
    }

    private CameraStatus getCameraStatus(String cameraId) {
        Camera camera = cameraRepository.findById(cameraId).orElseThrow(() -> new AppException(ErrorCode.CAMERA_NOT_EXISTED));
        if (streamingClient.checkCameraStatus(camera.getName())) return CameraStatus.ACTIVE;
        else return CameraStatus.INACTIVE;
    }


    //    public MediaUri getStreamUrlByCamId(String id) throws Exception {
    //        Camera camera = cameraRepository.findById(id).orElseThrow(() -> new
    // AppException(ErrorCode.CAMERA_NOT_EXISTED));
    //        onvifCameraService.cameraConnection(camera);
    //        return onvifCameraService.getStreamUri(camera);
    //    }
    //
    //    public Profile getProfileByCamId(String id) throws Exception {
    //        Camera camera = cameraRepository.findById(id).orElseThrow(() -> new
    // AppException(ErrorCode.CAMERA_NOT_EXISTED));
    //        onvifCameraService.cameraConnection(camera);
    //        return onvifCameraService.getProfile(camera);
    //    }
    //
    //    public GetDeviceInformationResponse getDeviceInfoByCamId(String id) throws Exception {
    //        Camera camera = cameraRepository.findById(id).orElseThrow(() -> new
    // AppException(ErrorCode.CAMERA_NOT_EXISTED));
    //        onvifCameraService.cameraConnection(camera);
    //        return onvifCameraService.getDeviceInformation(camera);
    //    }
    //
    //    public List<Camera> getActiveCameras() {
    //        return cameraRepository.findCameraByStatus("active");
    //    }
    //
}
