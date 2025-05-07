package com.example.infrastructure_service.service;

import com.example.infrastructure_service.model.Event;
import com.example.infrastructure_service.model.FakeEvent;
import com.example.infrastructure_service.model.InfraObject;
import com.example.infrastructure_service.repository.EventRepository;
import com.example.infrastructure_service.repository.FakeEventRepository;
import com.example.infrastructure_service.repository.HistoryRepository;
import com.example.infrastructure_service.repository.InfraObjectRepository;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FakeEventService {
  private final FakeEventRepository fakeEventRepository;
  private final EventRepository eventRepository;
  private final InfraObjectRepository infraObjectRepository;
  private final HistoryRepository historyRepository;

  @Transactional
  public FakeEvent updateFakeEvent(String id) {
    Event event = eventRepository.findById(id).orElse(null);
    if(event == null) { throw new RuntimeException("Event not found"); }

    // tao bang luu tru cac su kien bao gia
    FakeEvent fakeEvent = FakeEvent.builder()
        .name(event.getInfraObject().getName())
        .category(event.getInfraObject().getCategory())
        .confidence(event.getConfidence())
        .latitude(event.getInfraObject().getLatitude())
        .longitude(event.getInfraObject().getLongitude())
        .time(event.getDateCaptured())
        .status(event.getStatus())
        .image(event.getImage())
        .location(event.getInfraObject().getLocation())
        .build();

    handleInfraEvent(event);

    return fakeEventRepository.save(fakeEvent);

  }

  @Transactional
  public void handleInfraEvent(Event event) {

    // xoa su kien gia, cap nhat vat the ve trang thai gan nhat, neu chi co 1 su kien thi xoa ca vat the

    List<Event> events = eventRepository.findByInfraObject_Id(event.getInfraObject().getId());

    if(events.isEmpty()) return;

    if(events.size() > 1) {

      eventRepository.delete(event);

      events.sort(Comparator.comparing(Event::getDateCaptured));

      InfraObject infraObject = event.getInfraObject();

      Event lastEvent = events.get(events.size() - 2);

      infraObject.setStatus(lastEvent.getStatus());
      infraObject.setConfidence(lastEvent.getConfidence());
      infraObject.setLevel(lastEvent.getLevel());

      infraObjectRepository.save(infraObject);
    }else{
      historyRepository.deleteAllByInfraObject(event.getInfraObject());
      eventRepository.delete(event);
      infraObjectRepository.delete(event.getInfraObject());
    }

  }

  public List<FakeEvent> getAllFakeEvents() {
    return fakeEventRepository.findAll();
  }

  public void deleteFakeEvent(String id) {
    fakeEventRepository.deleteById(id);
  }

}

