package com.example.infrastructure_service.service;

import com.example.infrastructure_service.model.History;
import com.example.infrastructure_service.model.InfraObject;
import com.example.infrastructure_service.repository.HistoryRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HistoryService {

  private final HistoryRepository historyRepository;

  public void saveHistoryInfra(InfraObject infraObject) {
    History history = new History();
    history.setScheduleId(infraObject.getScheduleId());
    history.setInfraObject(infraObject);
    history.setConfidence(infraObject.getConfidence());
    history.setImage(infraObject.getImage());
    history.setLevel(infraObject.getLevel());
    history.setDateCaptured(infraObject.getDateCaptured());
    history.setStatus(infraObject.getStatus());
    history.setBbox(infraObject.getBbox());
    history.setRealWidth(infraObject.getRealWidth());
    history.setRealHeight(infraObject.getRealHeight());
    historyRepository.save(history);
  }

  public List<History> getHistoriesByCamAndDate(String cameraId, LocalDate startTime, LocalDate endTime) {
    return historyRepository.findByCameraAndDate(cameraId, startTime, endTime);
  }

  public List<History> getHistoriesByInfra(String infraId){
    return historyRepository.findByInfraObject_Id(infraId);
  }

  public List<History> getHistoriesBySchedule(String scheduleId) {
    return historyRepository.findByScheduleId(scheduleId);
  }
}
