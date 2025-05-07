package com.example.camera_service.client;

import com.example.camera_service.dto.MediaSourceDTO;
import com.example.camera_service.exception.AppException;
import com.example.camera_service.exception.ErrorCode;
import com.example.camera_service.repository.CameraRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
@RequiredArgsConstructor
public class StreamingClient {

    private final RestClient restClient;
    @Value("${media.server.base-path}")
    private String basePath;

    public String publishingSource(MediaSourceDTO mediaSourceDTO) {
        String response = restClient
                .post()
                .uri(basePath + "/config/paths/add/" + mediaSourceDTO.getName())
                .header("Content-Type", "application/json")
                .body(mediaSourceDTO)
                .retrieve()
                .onStatus(status -> status.equals(HttpStatus.BAD_REQUEST), (req, res) -> {
                    // Xử lý lỗi 400 Bad Request
                    ObjectMapper objectMapper = new ObjectMapper();
                    String responseBody = res.getBody() != null ? new String(res.getBody().readAllBytes()) : "";
                    JsonNode errorNode = objectMapper.readTree(responseBody);
                    String errorMessage = errorNode.path("error").asText("");
                    if ("path already exists".equals(errorMessage)) {
                        throw new AppException(ErrorCode.PATH_ALREADY_EXISTED);
                    } else {
                        throw new RuntimeException("Bad Request: " + responseBody);
                    }
                })
                .body(String.class);

        log.info("Published source for camera {} with RTSP URL {}",
                mediaSourceDTO.getName(), mediaSourceDTO.getSource());

        return response;
    }

    public boolean checkCameraStatus(String cameraName) {
        try {
            String response = restClient
                    .get()
                    .uri(basePath + "/paths/get/" + cameraName.replaceAll(" ", "").toLowerCase())
                    .retrieve()
                    .onStatus(status -> status.value() == 404, (req, res) -> {
                        throw new AppException(ErrorCode.RTSP_NOT_FOUND);
                    })
                    .body(String.class);

            JSONObject jsonObject = new JSONObject(response);
            boolean ready = jsonObject.getBoolean("ready");
            log.info("Camera {} status: ready = {}", cameraName, ready);
            return ready;
        } catch (Exception e) {
            log.warn("Failed to check status for camera {}: {}", cameraName, e.getMessage());
            return false; // Trả về false nếu có lỗi (bao gồm 404)
        }
    }

    public String getPath(String cameraName) {
        return restClient
                .get()
                .uri(basePath + "/config/paths/get/" + cameraName)
                .retrieve()
                .onStatus(status -> status.value() == 404, (req, res) -> {
                    throw new AppException(ErrorCode.RTSP_NOT_FOUND);
                })
                .body(String.class);
    }

    public void deletePath(String cameraName) {
         restClient.delete()
                .uri(basePath + "/config/paths/delete/" + cameraName)
                .retrieve()
                .body(String.class);
    }


}
