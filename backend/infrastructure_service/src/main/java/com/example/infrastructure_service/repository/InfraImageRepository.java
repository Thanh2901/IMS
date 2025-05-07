package com.example.infrastructure_service.repository;

import com.example.infrastructure_service.model.InfraImage;
import com.example.infrastructure_service.model.InfraObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfraImageRepository extends JpaRepository<InfraImage, Long> {
  InfraImage findByPathUrl(String pathUrl);
}
