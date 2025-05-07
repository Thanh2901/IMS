package com.example.infrastructure_service.service;

import com.example.infrastructure_service.client.MapClient;
import com.example.infrastructure_service.enums.ProcessStatus;
import com.example.infrastructure_service.model.Event;
import com.example.infrastructure_service.enums.EventStatus;
import com.example.infrastructure_service.model.InfraInfo;
import com.example.infrastructure_service.model.InfraObject;
import com.example.infrastructure_service.model.InfraObjectProcess;
import com.example.infrastructure_service.repository.EventRepository;
import com.example.infrastructure_service.repository.InfraObjectProcessRepository;
import com.example.infrastructure_service.repository.InfraObjectRepository;
import com.example.infrastructure_service.utils.Utils;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InfraProcessingService {

  private final InfraObjectRepository infraObjectRepository;
  private final EventRepository eventRepository;
  private final MapClient mapClient;
  private final EventService eventService;
  private final HistoryService historyService;
  private final InfraObjectProcessRepository infraObjectProcessRepository;

  protected void processOneInfraObject(InfraObjectProcess item) {
    InfraObject existedInfra = item.getInfraObject();
    if (existedInfra == null) {
      // save infra if not exist and create new event

      InfraObject infraObject = InfraObject.builder()
          .cameraId(item.getCameraId())
          .dateCaptured(item.getDateCaptured())
          .latitude(item.getLatitude())
          .longitude(item.getLongitude())
          .category(item.getCategory())
          .name(item.getName())
          .status(item.getStatus())
          .confidence(item.getConfidence())
          .level(item.getLevel())
          .scheduleId(item.getScheduleId())
          .bbox(item.getBbox())
          .realWidth(item.getRealWidth())
          .realHeight(item.getRealHeight())
          .isUpdated(true)
          .type(item.getType())
          .location(mapClient.getAddress(item.getLatitude(), item.getLongitude()))
          .image(item.getImage())
          .build();

      InfraInfo infraInfo = new InfraInfo();
      infraInfo.setCreateAt(LocalDateTime.now());
      infraInfo.setKeyId(item.getCategory() + "-" + Utils.generateGeoHash(item.getLatitude(),
          item.getLongitude(), 9));
      infraObject.setInfo(infraInfo);
      InfraObject saveInfraObject = infraObjectRepository.save(infraObject);

      historyService.saveHistoryInfra(saveInfraObject);
      eventService.createEvent(saveInfraObject, true, EventStatus.NEW);

    } else {

      Event event = eventRepository.findTheLastEvent(existedInfra.getId());

      // neu nhu dang co su kien sua chua thi ko cap that vao
      if(event.getEventStatus().equals(EventStatus.REPAIR.toString()) && event.getEndTime() == null) {
        return;
      }

      //check different status
      boolean differentStatus = !existedInfra.getStatus().equals(item.getStatus());

      //update infra object new Image and status
      existedInfra.setScheduleId(item.getScheduleId());
      existedInfra.setStatus(item.getStatus());
      existedInfra.setName(item.getName());
      existedInfra.setImage(item.getImage());
      existedInfra.setDateCaptured(item.getDateCaptured());
      InfraObject saveExistInfra = infraObjectRepository.save(existedInfra);

      historyService.saveHistoryInfra(saveExistInfra);

      // set end time for event and create new event have new status
      if(differentStatus ){

        eventService.updateLastEvent(existedInfra.getId(), item.getDateCaptured());

        eventService.createEvent(saveExistInfra, true, EventStatus.UPDATED);

      }

    }

  }


  // find the nearest infra object around radius 50m
  public InfraObject findByLocation(String cameraId, String category, String name, Double latitude,
      Double longitude) {
    return infraObjectRepository.findNearestInfraWithinRadius(cameraId, category, name, latitude,
        longitude, 5.0);
  }

  public List<InfraObjectProcess> getInfraObjectProcessBySchedule(String scheduleId, String status, String processStatus, String eventStatus) {
    return infraObjectProcessRepository.filterInfraProcess(scheduleId, status, processStatus, eventStatus);
  }

  public List<InfraObjectProcess> getAllProcessBySchedule(String scheduleId) {
    return infraObjectProcessRepository.findByScheduleId(scheduleId);
  }

  public InfraObjectProcess processOneInfraObjectProcess(String infraProcessId) {
    InfraObjectProcess infraObjectProcess = infraObjectProcessRepository.findById(infraProcessId).orElseThrow(()-> new RuntimeException("infra object process not found"));
    if(infraObjectProcess.getProcessStatus() != ProcessStatus.PENDING) {
      throw new RuntimeException("infra object process is not pending");
    }
    processOneInfraObject(infraObjectProcess);
    infraObjectProcess.setProcessStatus(ProcessStatus.APPROVED);
    return infraObjectProcessRepository.save(infraObjectProcess);
  }

  public InfraObjectProcess rejectProcess(String processId) {
    InfraObjectProcess infraObjectProcess = infraObjectProcessRepository.findById(processId).orElseThrow(()-> new RuntimeException("infra object process not found"));
    if(infraObjectProcess.getProcessStatus() != ProcessStatus.PENDING) {
      throw new RuntimeException("infra object process is not pending");
    }
    infraObjectProcess.setProcessStatus(ProcessStatus.REJECTED);
    return infraObjectProcessRepository.save(infraObjectProcess);
  }

  public List<InfraObjectProcess> processAllInfraBySchedule(String scheduleId) {
    List<InfraObjectProcess> infraObjectProcesses = infraObjectProcessRepository.findByScheduleIdAndProcessStatus(scheduleId, ProcessStatus.PENDING);
    for (InfraObjectProcess infraObjectProcess : infraObjectProcesses) {
      processOneInfraObject(infraObjectProcess);
      infraObjectProcess.setProcessStatus(ProcessStatus.APPROVED);
    }
    return infraObjectProcessRepository.saveAll(infraObjectProcesses);
  }
}
