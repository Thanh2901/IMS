package com.example.infrastructure_service.controller;

import com.example.infrastructure_service.dto.ApiResponse;
import com.example.infrastructure_service.dto.request.EventFilterRequest;
import com.example.infrastructure_service.dto.request.UpdateEventRequest;
import com.example.infrastructure_service.dto.response.PageResponse;
import com.example.infrastructure_service.model.Event;
import com.example.infrastructure_service.service.EventService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/infrastructures/events")
@RequiredArgsConstructor
public class EventController {

  private final EventService eventService;


  @PostMapping("/filter")
  public ApiResponse<PageResponse<Event>> getEvents(
      @RequestBody EventFilterRequest request,
      @RequestHeader("X-Credential-Id") String credentialId
  ) {

    Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

    Page<Event> events = eventService.getFilterEvents(
        pageable, request.getStartTime(), request.getEndTime(), request.getEventStatus(), request.getStatus(),
        request.getCategory(), request.getName(), request.getCameraId(), request.getLocation(), request.getKeyword(), request.getConfidence(), request.getLevel()
        , credentialId
    );
    return ApiResponse.<PageResponse<Event>>builder()
        .message("Get events by filter successfully")
        .data(new PageResponse<>(events))
        .build();
  }


  @GetMapping("/{id}")
  public ApiResponse<Event> getEvent(@PathVariable String id){

    Event event = eventService.getEventById(id);

    return ApiResponse.<Event>builder()
        .message("Get event by id successfully")
        .data(event)
        .build();
  }

  @PostMapping(value = "/repair/{infraId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ApiResponse<Event> createRepair(@PathVariable String infraId, @RequestPart(required = false) MultipartFile file){
    return ApiResponse.<Event>builder()
        .message("Create repair event successfully")
        .data(eventService.createRepairEvent(infraId, file))
        .build();
  }

  @PatchMapping("/repair/{infraId}")
  public ApiResponse<Event> updateRepair(@PathVariable String infraId){
    return ApiResponse.<Event>builder()
        .message("Update finish repair event successfully")
        .data(eventService.updateRepairEvent(infraId))
        .build();
  }

  @GetMapping("/infra/{infraId}")
  public ApiResponse<List<Event>> getEventByInfra(@PathVariable String infraId){
    return ApiResponse.<List<Event>>builder()
        .message("get event by infra successfully")
        .data(eventService.getEventsByInfraId(infraId))
        .build();
  }

  @PatchMapping("/{eventId}")
  public ApiResponse<Event> updateEvent(@PathVariable String eventId, @RequestBody
      UpdateEventRequest request){
    return ApiResponse.<Event>builder()
        .message("Update detail and verify event successfully")
        .data(eventService.updateEvent(eventId, request.getDescription(), request.getVerified()))
        .build();
  }



}
