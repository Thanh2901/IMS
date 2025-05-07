package com.example.infrastructure_service.service;

import com.example.infrastructure_service.dto.response.CategoryStatusCount;
import com.example.infrastructure_service.dto.response.EventDateCount;
import com.example.infrastructure_service.dto.response.StatisticsResponse;
import com.example.infrastructure_service.repository.EventRepository;
import com.example.infrastructure_service.repository.InfraObjectRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatisticsService {
  private final EventRepository eventRepository;
  private final InfraObjectRepository infraObjectRepository;

  public StatisticsResponse getStatistics(LocalDate startDate, LocalDate endDate, String cameraId) {

    // Lấy thống kê sự kiện
    List<EventDateCount> eventStatistics = eventRepository.getEventStatistics(startDate, endDate, cameraId);

    List<CategoryStatusCount> objectStatistics = infraObjectRepository.getInfraObjectStatisticsByCamera(cameraId);

    return new StatisticsResponse(eventStatistics, objectStatistics);
  }
}
