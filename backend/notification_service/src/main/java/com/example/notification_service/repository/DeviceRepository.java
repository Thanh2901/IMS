package com.example.notification_service.repository;

import com.example.notification_service.model.Device;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

  Optional<Device> findByUserIdAndFcmToken(String userId, String fcmToken);

  List<Device> findByUserId(String userId);

  List<Device> findByUserIdIn(List<String> userIds);

  List<Device> findByFcmTokenIn(List<String> fcmToken);
}
