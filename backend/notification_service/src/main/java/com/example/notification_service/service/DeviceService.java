package com.example.notification_service.service;

import com.example.notification_service.model.Device;
import com.example.notification_service.repository.DeviceRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceService {

  private final DeviceRepository deviceRepository;
  //create fcm token for user and get fcmtoken
  public void saveOrUpdateFCMToken(String userId, String fcmToken) {

    Optional<Device> existingDevice = deviceRepository.findByUserIdAndFcmToken(userId, fcmToken);

    if (existingDevice.isEmpty()) {
      Device device = new Device();
      device.setUserId(userId);
      device.setFcmToken(fcmToken);
      device.setCreatedAt(LocalDateTime.now());
      deviceRepository.save(device);
    }
  }


  public List<String> getFCMTokenFromListUsers(List<String> userIds) {
    return deviceRepository.findByUserIdIn(userIds)
        .stream()
        .map(Device::getFcmToken)
        .collect(Collectors.toList());
  }
}
