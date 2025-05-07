// package com.example.camera_service.controller;
//
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
//
// import com.example.camera_service.entity.Camera;
// import com.example.camera_service.exception.AppException;
// import com.example.camera_service.exception.ErrorCode;
// import com.example.camera_service.repository.CameraRepository;
// import com.example.camera_service.service.impl.OnvifSOAPMessageServiceImpl;
//
// import lombok.extern.slf4j.Slf4j;
//
//// Spring Boot Controller
// @RestController
// @RequestMapping("/api/cameras/onvif")
// @Slf4j
// public class OnvifController {
//
//    private static final Logger logger = LoggerFactory.getLogger(OnvifController.class);
//
//    private final OnvifSOAPMessageServiceImpl onvifSOAPMessageService;
//    private final CameraRepository cameraRepository;
//
//    @Autowired
//    public OnvifController(OnvifSOAPMessageServiceImpl onvifSOAPMessageService, CameraRepository cameraRepository) {
//        this.onvifSOAPMessageService = onvifSOAPMessageService;
//        this.cameraRepository = cameraRepository;
//    }
//
//    @GetMapping("/{cameraId}/device-info")
//    public ResponseEntity<String> getDeviceInfo(@PathVariable("cameraId") String id) {
//        try {
//            Camera camera =
//                    cameraRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CAMERA_NOT_EXISTED));
//            onvifSOAPMessageService.cameraConnection(camera);
//            String deviceInfo = onvifSOAPMessageService.getDeviceInformation(camera);
//            return ResponseEntity.ok(deviceInfo);
//        } catch (Exception e) {
//            logger.error("Error retrieving device information", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error retrieving device information: " + e.getMessage());
//        }
//    }
//
//    @GetMapping("/{cameraId}/stream-url")
//    public ResponseEntity<String> getStreamUrl(@PathVariable("cameraId") String id) {
//        try {
//            Camera camera =
//                    cameraRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CAMERA_NOT_EXISTED));
//            String deviceInfo = onvifSOAPMessageService.getStreamUri(camera);
//            return ResponseEntity.ok(deviceInfo);
//        } catch (Exception e) {
//            logger.error("Error retrieving device information", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error retrieving device information: " + e.getMessage());
//        }
//    }
//
//    @GetMapping("/{cameraId}/profile")
//    public ResponseEntity<String> getProfile(@PathVariable("cameraId") String id) {
//        try {
//            Camera camera =
//                    cameraRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CAMERA_NOT_EXISTED));
//            String deviceInfo = onvifSOAPMessageService.getProfile(camera);
//            return ResponseEntity.ok(deviceInfo);
//        } catch (Exception e) {
//            logger.error("Error retrieving device information", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error retrieving device information: " + e.getMessage());
//        }
//    }
// }
