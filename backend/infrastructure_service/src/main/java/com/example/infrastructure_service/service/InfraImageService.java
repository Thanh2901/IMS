package com.example.infrastructure_service.service;

import com.example.infrastructure_service.model.InfraImage;
import com.example.infrastructure_service.repository.InfraImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InfraImageService {
  private final InfraImageRepository infraImageRepository;

  public InfraImage save(String path) {
    InfraImage image = new InfraImage();
    image.setPathUrl(path);
    return infraImageRepository.save(image);
  }

  public InfraImage getInfraImage(String path) {
    return infraImageRepository.findByPathUrl(path);
  }

  public InfraImage processImage(InfraImage image) {
    InfraImage existingImage = getInfraImage(image.getPathUrl());
    if (existingImage == null) {
      InfraImage infraImage = new InfraImage();
      infraImage.setPathUrl(image.getPathUrl());
      infraImage.setFrame(image.getFrame());
      return infraImageRepository.save(infraImage);
    }
    return existingImage;
  }
}
