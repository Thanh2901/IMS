package com.example.infrastructure_service.repository;

import com.example.infrastructure_service.model.History;
import com.example.infrastructure_service.model.InfraObject;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryRepository extends JpaRepository<History, Integer> {

  List<History> findByInfraObject_Id(String id);

  @Query(value = "SELECT h.* FROM history h " +
      "JOIN infra_objects io ON h.infra_object_id = io.id " +
      "WHERE io.camera_id = :cameraId " +
      "AND (DATE(h.date_captured) BETWEEN :startTime AND :endTime) ",
      nativeQuery = true)
  List<History> findByCameraAndDate(@Param("cameraId") String cameraId,
      @Param("startTime") LocalDate startTime,
      @Param("endTime") LocalDate endTime);

  List<History> findByScheduleId(String scheduleId);

  void deleteAllByInfraObject(InfraObject infraObject);

}
