package com.example.infrastructure_service.service;

import com.example.infrastructure_service.client.MinioService;
import com.example.infrastructure_service.enums.EventStatus;
import com.example.infrastructure_service.exception.CustomRuntimeException;
import com.example.infrastructure_service.model.Event;
import com.example.infrastructure_service.model.InfraObject;
import com.example.infrastructure_service.repository.EventRepository;
import com.example.infrastructure_service.repository.InfraObjectRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class EventService {

  private final EventRepository eventRepository;
  private final NotificationService notificationService;
  private final InfraObjectRepository infraObjectRepository;
  private final CameraUserService cameraUserService;
  private final MinioService minioService;
  private final InfraImageService infraImageService;

  public void createEvent(InfraObject infraObject, boolean sendNotification,
      EventStatus eventStatus) {
    Event event = new Event();
    event.setDateCaptured(infraObject.getDateCaptured());
    event.setScheduleId(infraObject.getScheduleId());
    event.setImage(infraObject.getImage());
    event.setInfraObject(infraObject);
    event.setStatus(infraObject.getStatus());
    event.setLevel(infraObject.getLevel());
    event.setBbox(infraObject.getBbox());
    event.setEventStatus(eventStatus.toString());
    event.setConfidence(infraObject.getConfidence());
    event.setRealWidth(infraObject.getRealWidth());
    event.setRealHeight(infraObject.getRealHeight());
    Event savedEvent = eventRepository.save(event);
    if (sendNotification) {
      notificationService.createEventNotification(savedEvent, eventStatus);
      System.out.println("Send notification to kafka");
    }

  }

  public Page<Event> getFilterEvents(
      Pageable pageable,
      String startTime,
      String endTime,
      String eventStatus,
      String status,
      String category,
      String name,
      String cameraId,
      String location,
      String keyword,
      Double confidence,
      Integer level,
      String userId
  ) {


    cameraUserService.checkCameraUser(cameraId, userId);

    return eventRepository.getEventByFilterAndKeyword(
        pageable,
        startTime,
        endTime,
        eventStatus,
        status,
        category,
        name,
        cameraId,
        location,
        keyword,
        confidence,
        level
    );
  }

  public Event getEventById(String id) {
    return eventRepository.findById(id).orElseThrow(() -> new RuntimeException("Event not found"));
  }

  public Event createRepairEvent(String infraId, MultipartFile file) {

    // tao su kien chinh sua noi tiep cho vat the

    InfraObject infraObject = infraObjectRepository.findById(infraId)
        .orElseThrow(() -> new CustomRuntimeException(1001, "Infra object not found"));
    Event lastEvent = eventRepository.findTheLastEvent(infraId);

    if (!infraObject.getStatus().equals("NOT OK")) {
      throw new CustomRuntimeException(1002,
          "Infra object cant create repair event because this is ok");
    }
    if (lastEvent.getEventStatus().equals("REPAIR")) {
      throw new CustomRuntimeException(1003, "Infra object had repair event before");
    }

    Event event = new Event();
    event.setDateCaptured(LocalDateTime.now());
    event.setEventStatus(EventStatus.REPAIR.toString());
    event.setInfraObject(infraObject);
    event.setLevel(0);
    event.setStatus(infraObject.getStatus());

    if (file != null) {
      String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
      String path = minioService.uploadFile(file, "image", fileName);
      event.setImage(infraImageService.save(path));
    }

    updateLastEvent(infraObject.getId(), LocalDateTime.now());

    return eventRepository.save(event);
  }

  public Event updateRepairEvent(String infraId) {

    // sau khi su xong thi set trang thai vat the la OK, neu la duong thi trang thai la finish de ko phai quan ly nua

    InfraObject infraObject = infraObjectRepository.findById(infraId)
        .orElseThrow(() -> new RuntimeException("Infra object not found"));
    infraObject.setStatus("OK");
    infraObject.setLevel(0);
    infraObjectRepository.save(infraObject);
    Event event = updateLastEvent(infraId, LocalDateTime.now());
    event.setStatus(infraObject.getStatus());
    return eventRepository.save(event);
  }

  // set end time for last event if have new event
  public Event updateLastEvent(String infraObjectId, LocalDateTime time) {
    Event lastEvent = eventRepository.findTheLastEvent(infraObjectId);

    if (lastEvent == null) {
      throw new RuntimeException("Last event not found");
    }

    lastEvent.setEndTime(time);

    eventRepository.save(lastEvent);
    return lastEvent;
  }

  public List<Event> getEventsByInfraId(String infraId) {
    return eventRepository.findEventByDate(infraId);
  }

  public Event updateEvent(String eventId, String description, Boolean verified) {
    Event event = eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Event not found"));
    event.setDescription(description);
    event.setVerified(verified);
    return eventRepository.save(event);
  }
}
