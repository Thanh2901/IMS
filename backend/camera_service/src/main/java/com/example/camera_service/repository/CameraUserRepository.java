package com.example.camera_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.camera_service.entity.CameraUser;

@Repository
public interface CameraUserRepository extends JpaRepository<CameraUser, Long> {
    @Query(value = "select * from camera_user where camera_id=:camId", nativeQuery = true)
    List<CameraUser> getUserByCamId(@Param("camId") String camId);

    @Query(value = "select camera_id from camera_user where user_id=:userId group by camera_id", nativeQuery = true)
    List<String> getCameraByUserId(@Param("userId") String userId);

    @Query(value = "select * from camera_user where camera_id=:camId", nativeQuery = true)
    List<CameraUser> findByCameraId(@Param("camId") String camId);
}
