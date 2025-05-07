package com.example.camera_service.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CameraStreamResponseDto {
    private String cameraId;
    private String name;
    private String streamUri;
    private StreamMetadata metadata;
    private LocalDateTime timestamp;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StreamMetadata {
        private boolean invalidAfterConnect;
        private boolean invalidAfterReboot;
        private String timeout;
    }
}
