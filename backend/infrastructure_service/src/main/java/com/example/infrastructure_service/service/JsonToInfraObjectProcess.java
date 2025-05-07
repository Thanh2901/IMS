package com.example.infrastructure_service.service;

import com.example.infrastructure_service.client.MapClient;
import com.example.infrastructure_service.dto.InfraLogRecord;
import com.example.infrastructure_service.enums.EventStatus;
import com.example.infrastructure_service.enums.InfraType;
import com.example.infrastructure_service.enums.ProcessStatus;
import com.example.infrastructure_service.model.InfraImage;
import com.example.infrastructure_service.model.InfraObject;
import com.example.infrastructure_service.model.InfraObjectProcess;
import com.example.infrastructure_service.model.JsonObject;
import com.example.infrastructure_service.model.JsonObject.Annotation;
import com.example.infrastructure_service.model.JsonObject.Category;
import com.example.infrastructure_service.model.JsonObject.Image;
import com.example.infrastructure_service.repository.InfraObjectProcessRepository;
import com.example.infrastructure_service.repository.InfraObjectRepository;
import com.example.infrastructure_service.utils.RiskLevelLoader;
import com.example.infrastructure_service.utils.Utils;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class JsonToInfraObjectProcess {
  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  private final MapClient mapClient;
  private final InfraObjectProcessRepository infraObjectProcessRepository;
  private final InfraProcessingService infraProcessingService;
  private final InfraImageService infraImageService;

  // parser from json from kafka to List infraObject
  public List<InfraObjectProcess> processParseObject(JsonObject jsonObject) {
    List<InfraObjectProcess> infraObjects = new ArrayList<>();
    InfraObjectProcess saveModel;

    // Lặp qua từng annotation
    for (Annotation annotation : jsonObject.getAnnotations()) {
      InfraObjectProcess model = new InfraObjectProcess();

      // Lấy category tương ứng với annotation
      Category category = jsonObject.getCategories()
          .stream()
          .filter(cat -> cat.getId() == annotation.getCategoryId())
          .findFirst()
          .orElseThrow(() -> new RuntimeException(
              "No matching category found for annotation ID " + annotation.getId()));

      // Lấy image tương ứng với annotation
      Image image = jsonObject.getImages()
          .stream()
          .filter(img -> img.getId() == annotation.getImageId())
          .findFirst()
          .orElseThrow(() -> new RuntimeException(
              "No matching image found for annotation ID " + annotation.getImageId()));

      // Set thông tin cơ bản
      model.setCameraId(jsonObject.getInfo().getCameraId());
      model.setScheduleId(jsonObject.getInfo().getScheduleId());
      model.setCategory(category.getSupercategory());
      model.setName(category.getName());
      model.setDateCaptured(LocalDateTime.parse(image.getDateCaptured(), formatter));
      model.setLongitude(annotation.getLocation().getLongitude());
      model.setLatitude(annotation.getLocation().getLatitude());
      model.setStatus(annotation.getStatus());
      model.setConfidence(annotation.getConf());
      model.setLevel(RiskLevelLoader.getRiskLevels(category.getName()));
      model.setProcessStatus(ProcessStatus.PENDING);
      model.setBbox(Arrays.toString(annotation.getBbox()));
      model.setRealWidth(annotation.getRealWidth());
      model.setRealHeight(annotation.getRealHeight());
      model.setLocation(mapClient.getAddress(annotation.getLocation().getLatitude(), annotation.getLocation().getLongitude()));
      if(model.getCategory().equals("SIGN") || model.getCategory().equals("LAMP")){
        model.setType(InfraType.ASSET);
      } else {
        model.setType(InfraType.ABNORMALITY);
      }

      InfraImage infraImage = new InfraImage();
      infraImage.setPathUrl(image.getPathUrl());
      infraImage.setFrame(image.getFrame());
      // Thêm object vào list
      model.setImage(infraImageService.processImage(infraImage));

      InfraObject existedInfra = infraProcessingService.findByLocation(model.getCameraId(), model.getCategory(), model.getName(),
          model.getLatitude(), model.getLongitude());

      if(existedInfra == null) {
        model.setEventStatus(EventStatus.NEW);
        saveModel = infraObjectProcessRepository.save(model);
      }else {
        model.setEventStatus(EventStatus.UPDATED);
        model.setInfraObject(existedInfra);
        saveModel = infraObjectProcessRepository.save(model);}
      infraObjects.add(saveModel);
    }

    return infraObjectProcessRepository.saveAll(infraObjects);
  }
}