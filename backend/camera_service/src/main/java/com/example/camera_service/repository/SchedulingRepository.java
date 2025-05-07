package com.example.camera_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.camera_service.entity.Scheduling;

@Repository
public interface SchedulingRepository extends JpaRepository<Scheduling, String> {
    @Query("SELECT s " + "FROM Scheduling s "
            + "WHERE s.camera.id = :cameraId "
            + "AND s.startTime <= :endTime "
            + "AND s.endTime >= :startTime")
    List<Scheduling> getScheduleByTimeFilter(
            @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("cameraId") String cameraId);

    @Query("select s from Scheduling s where s.camera.id = :cameraId order by s.startTime asc ")
    List<Scheduling> getSchedulingByCamera(String cameraId);
}
