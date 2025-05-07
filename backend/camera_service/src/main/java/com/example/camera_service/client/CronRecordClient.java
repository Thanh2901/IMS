package com.example.camera_service.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
@RequiredArgsConstructor
public class CronRecordClient {
    private final RestClient restClient;
    @Value("${scheduling.ip-address}")
    private String ipAddress;
    @Value("${scheduling.port}")
    private String port;

    public void updateRecordingTimeWithParams(String startTime, String endTime, String rtspUrl, String schedulingId) {

        String response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host(ipAddress)
                        .port(port)
                        .path("/api/records/update/time") // Just the path, no full URL
                        .queryParam("startTimeInput", startTime)
                        .queryParam("endTimeInput", endTime)
                        .queryParam("rtspUrl", rtspUrl)
                        .queryParam("schedulingId", schedulingId)
                        .build())
                .retrieve()
                .body(String.class);

        log.info("Received response from external service: {}", response);
    }


}