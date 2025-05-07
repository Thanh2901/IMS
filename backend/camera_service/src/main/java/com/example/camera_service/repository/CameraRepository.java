package com.example.camera_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.camera_service.entity.Camera;
import com.example.camera_service.utils.CameraStatus;

@Repository
public interface CameraRepository extends JpaRepository<Camera, String> {
    @Query(value = "select * from camera where name = :name", nativeQuery = true)
    Camera findByName(String name);

    @Query(value = "select * from camera where ip_address = :ipAdress", nativeQuery = true)
    Camera findByIpAddress(String ipAddress);

    @Query(
            value = "SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END FROM camera WHERE name = :name",
            nativeQuery = true)
    boolean existsByName(@Param("name") String name);

    @Query(
            value = "SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END FROM camera WHERE ip_address = :ipAddress",
            nativeQuery = true)
    boolean existsByIpAddress(@Param("ipAddress") String ipAddress);

    @Query("select r from Camera r where r.cameraStatus= :status")
    List<Camera> findByStatus(CameraStatus status);
}
