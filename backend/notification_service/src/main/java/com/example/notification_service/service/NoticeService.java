package com.example.notification_service.service;
import com.example.notification_service.client.CameraClient;
import com.example.notification_service.dto.NotificationDTO;
import com.example.notification_service.dto.response.CameraUserResponse;
import com.example.notification_service.model.Notice;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeService {

  private final ObjectMapper objectMapper;
  private final DeviceService deviceService;
  private final CameraClient cameraClient;

  public Notice createNotice(String dataEvent) throws JsonProcessingException {

    NotificationDTO notificationDTO = objectMapper.readValue(dataEvent, NotificationDTO.class);

    //read data from kafka
    Map<String, String> eventData = objectMapper.readValue(notificationDTO.getData(), Map.class);

    //cSend notifications to users with permissions related to this camera
    String cameraId = eventData.get("cameraId");

    ResponseEntity<List<CameraUserResponse>> response = cameraClient.getUserByCamId(cameraId);

    List<String> userIds = response.getBody().stream().map(CameraUserResponse::getUserId).collect(
        Collectors.toList());

    List<String> fcmToken = deviceService.getFCMTokenFromListUsers(userIds);

    Notice notice = new Notice();
    notice.setSubject(notificationDTO.getSubject());
    notice.setContent(notificationDTO.getContent());
    notice.setData(eventData);
    notice.setRegistrationTokens(fcmToken);

    return notice;
  }
}
