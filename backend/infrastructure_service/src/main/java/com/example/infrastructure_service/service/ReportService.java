package com.example.infrastructure_service.service;

import com.example.infrastructure_service.dto.EventDTO;
import com.example.infrastructure_service.dto.response.EventReportResponse;
import com.example.infrastructure_service.dto.response.EventReportResponse.ObjectEvent;
import com.example.infrastructure_service.dto.response.ReportResponse;
import com.example.infrastructure_service.dto.response.ReportResponse.ObjectHistory;
import com.example.infrastructure_service.model.Event;
import com.example.infrastructure_service.model.History;
import com.example.infrastructure_service.model.InfraObject;
import com.example.infrastructure_service.repository.EventRepository;
import com.example.infrastructure_service.repository.HistoryRepository;
import com.example.infrastructure_service.repository.InfraObjectRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {
  private final InfraObjectRepository infraObjectRepository;
  private final HistoryRepository historyRepository;
  private final CameraUserService cameraUserService;
  private final EventRepository eventRepository;

  public ReportResponse getInfraReportData(String infraId) {
    ReportResponse reportResponse = new ReportResponse();
    InfraObject infraObject = infraObjectRepository.findById(infraId).orElseThrow(()-> new RuntimeException("object not found"));
    reportResponse.setCameraId(infraObject.getCameraId());
    List<History> histories = historyRepository.findByInfraObject_Id(infraId);
    reportResponse.setObjects(List.of(new ObjectHistory(infraObject, histories, eventRepository.findByInfraObject_Id(infraObject.getId()).stream()
        .map(EventDTO::new)
        .toList())));
    return reportResponse;
  }

  public ReportResponse getCameraReportData(String userId, String cameraId, LocalDate startTime, LocalDate endTime) {
      ReportResponse reportResponse = new ReportResponse();
      cameraUserService.checkCameraUser(cameraId, userId);
      reportResponse.setCameraId(cameraId);

      List<InfraObject> infraObjects = infraObjectRepository.findInfraObjectByCameraId(cameraId);
      List<History> histories = historyRepository.findByCameraAndDate(cameraId, startTime, endTime);

      List<ObjectHistory> objectHistories = infraObjects.stream()
          .map(obj -> new ObjectHistory(
              obj,
              histories.stream().filter(h -> h.getInfraObject().getId().equals(obj.getId())).toList(),
              eventRepository.findByInfraObject_Id(obj.getId()).stream()
                  .map(EventDTO::new)
                  .toList()
          ))
          .toList();

      reportResponse.setObjects(objectHistories);

      return reportResponse;
  }

}
