package com.example.notification_service.repository;

import com.example.notification_service.model.NotificationDB;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationDB, Long> {

  List<NotificationDB> findByDevicesId(Long deviceId);

  @Query("SELECT n FROM NotificationDB n JOIN n.devices d WHERE d.id = :deviceId ORDER BY n.createdAt DESC")
  List<NotificationDB> findByDevicesIdOrderByCreatedAtDesc(Long deviceId);

  List<NotificationDB> findByDevicesIdAndIsRead(Long deviceId, Boolean isRead);

  @Query("SELECT n FROM NotificationDB n JOIN n.devices d WHERE d.id = :deviceId AND (:isRead is null or n.isRead = :isRead) ORDER BY n.createdAt DESC")
  List<NotificationDB> getNotificationsByIdAndRead(Long deviceId, Boolean isRead);
}
