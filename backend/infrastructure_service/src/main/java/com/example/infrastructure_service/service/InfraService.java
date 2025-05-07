package com.example.infrastructure_service.service;

import com.example.infrastructure_service.client.CameraClient;
import com.example.infrastructure_service.client.MapClient;
import com.example.infrastructure_service.client.MinioService;
import com.example.infrastructure_service.dto.InfraLogRecord;
import com.example.infrastructure_service.dto.request.NewInfraRequest;
import com.example.infrastructure_service.dto.request.UpdateInfraRequest;
import com.example.infrastructure_service.enums.EventStatus;
import com.example.infrastructure_service.enums.InfraType;
import com.example.infrastructure_service.model.InfraImage;
import com.example.infrastructure_service.model.InfraInfo;
import com.example.infrastructure_service.model.InfraObject;
import com.example.infrastructure_service.model.InfraObjectProcess;
import com.example.infrastructure_service.repository.InfraObjectRepository;
import com.example.infrastructure_service.utils.Utils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
public class InfraService {

  private final InfraObjectRepository infraObjectRepository;
  private final CameraUserService cameraUserService;
  private final MapClient mapClient;
  private final MinioService minioService;
  private final EventService eventService;

  // filter image
  public Page<InfraObject> getFilterInfraObject(
      Pageable pageable,
      String name,
      String location,
      String dateCaptured,
      String status,
      String category,
      String cameraId,
      String userId,
      String keyword,
      String type
  ) {
    
    cameraUserService.checkCameraUser(cameraId, userId);

    return infraObjectRepository.searchAndFilter(keyword, cameraId,category, name, location, dateCaptured, status, pageable, type);
  }

  public InfraObject findById(String id) {
    return infraObjectRepository.findById(id).orElseThrow(()-> new RuntimeException("infra object not found"));
  }

  public List<InfraObject> findInfraWithRadius(Double latitude, Double longitude, Double radius) {
    return infraObjectRepository.findObjectsWithinRadius(latitude, longitude, radius);
  }

  public List<InfraObject> findInfraWithPolygon(List<double []> points) {

    // chuyen danh sach diem thanh polygon trong postgis

    String polygonWKT = "POLYGON((" +
        points.stream()
            .map(p -> p[0] + " " + p[1])
            .collect(Collectors.joining(", ")) +
        ", " + points.get(0)[0] + " " + points.get(0)[1] +
        "))";

    System.out.println("polygonWKT: "+polygonWKT);

    return infraObjectRepository.findInfraWithinPolygon(polygonWKT);
  }


  public Page<InfraObject> searchByKeyword(String keyword, int page, int size) {

    Pageable pageable = PageRequest.of(page, size);

    return infraObjectRepository.searchByKeyword(keyword, pageable);
  }

  public List<InfraObject> getLostInfraObject() {
    return infraObjectRepository.getInfraObjectsWithLost();
  }


  public InfraObject addNewInfra(NewInfraRequest request, MultipartFile avatar, MultipartFile originalImage) {

    InfraObject infraObject = InfraObject.builder()
        .name(request.getName())
        .category(request.getCategory())
        .dateCaptured(LocalDateTime.now())
        .status(request.getStatus())
        .latitude(request.getLatitude())
        .longitude(request.getLongitude())
        .cameraId(request.getCameraId())
        .confidence(1)
        .level(0)
        .type(
            request.getCategory().equals("ROAD") || request.getCategory().equals("LAMP")
                ? InfraType.ASSET
                : InfraType.ABNORMALITY
        )
        .build();

    String address = mapClient.getAddress(request.getLatitude(), request.getLongitude());
    infraObject.setLocation(address);

    InfraInfo infraInfo = new InfraInfo();

    if(avatar != null ) {
      String fileName = UUID.randomUUID() + "_" + avatar.getOriginalFilename();
      String pathUrl = minioService.uploadFile(avatar, "image", fileName);
      infraInfo.setAvatar(pathUrl);
    }

    if(originalImage != null) {
      String fileName = UUID.randomUUID() + "_" + originalImage.getOriginalFilename();
      String pathUrl = minioService.uploadFile(originalImage, "image", fileName);
      infraInfo.setOriginalImage(pathUrl);
    }
    infraInfo.setAdditionalData(request.getAdditional());
    infraInfo.setManageUnit(request.getManageUnit());
    infraInfo.setCreateAt(LocalDateTime.now());
    infraInfo.setKeyId(request.getCategory() + "-" + Utils.generateGeoHash(request.getLatitude(),
        request.getLongitude(), 9));
    infraObject.setInfo(infraInfo);

    InfraObject saveInfra = infraObjectRepository.save(infraObject);

    eventService.createEvent(saveInfra, false, EventStatus.CREATED);

    return saveInfra;
  }


  public InfraObject updateInfra(UpdateInfraRequest request, MultipartFile avatar, MultipartFile originalImage) {

    InfraObject infraObject = findById(request.getInfraId());

    infraObject.setName(request.getName());
    infraObject.setCategory(request.getCategory());
    infraObject.setDateCaptured(LocalDateTime.now());
    infraObject.setStatus(request.getStatus());

    if (request.getLatitude() != infraObject.getLatitude() && request.getLongitude() != infraObject.getLongitude()) {
      infraObject.setLatitude(request.getLatitude());
      infraObject.setLongitude(request.getLongitude());
      infraObject.setLocation(mapClient.getAddress(request.getLatitude(), request.getLongitude()));
      infraObject.getInfo().setKeyId(request.getCategory() + "-" + Utils.generateGeoHash(request.getLatitude(),request.getLongitude(), 9));
    }

    if (avatar != null) {
      String fileName = UUID.randomUUID() + "_" + avatar.getOriginalFilename();
      String pathUrl = minioService.uploadFile(avatar, "image", fileName);
      infraObject.getInfo().setAvatar(pathUrl);
    }

    if (originalImage != null) {
      String fileName = UUID.randomUUID() + "_" + originalImage.getOriginalFilename();
      String pathUrl = minioService.uploadFile(originalImage, "image", fileName);
      infraObject.getInfo().setOriginalImage(pathUrl);
    }

    infraObject.getInfo().setManageUnit(request.getManageUnit());
    infraObject.getInfo().setAdditionalData(request.getAdditional());

    eventService.createEvent(infraObject, false, EventStatus.UPDATED);

    return infraObjectRepository.save(infraObject);
  }

  public void deleteInfra(String infraId) {
    InfraObject infraObject = findById(infraId);
    infraObjectRepository.delete(infraObject);
  }
}
