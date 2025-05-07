package com.example.infrastructure_service.controller;

import com.example.infrastructure_service.dto.ApiResponse;
import com.example.infrastructure_service.model.FakeEvent;
import com.example.infrastructure_service.service.FakeEventService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/infrastructures/events/fake")
@RequiredArgsConstructor
public class FakeEventController {

  private final FakeEventService fakeEventService;

  @PatchMapping("/{eventId}")
  public ApiResponse<FakeEvent> updateFakeEvent(@PathVariable String eventId){
    return ApiResponse.<FakeEvent>builder()
        .message("Update fake event successfully")
        .data(fakeEventService.updateFakeEvent(eventId))
        .build();
  }

  @GetMapping("")
  public ApiResponse<List<FakeEvent>> getAllFakeEvents(){
    return ApiResponse.<List<FakeEvent>>builder()
        .message("Get all fake message success fully")
        .data(fakeEventService.getAllFakeEvents())
        .build();
  }

  @DeleteMapping("/{fakeEventId}")
  public ApiResponse<Void> deleteFakeEvent(@PathVariable String fakeEventId){

    fakeEventService.deleteFakeEvent(fakeEventId);

    return ApiResponse.<Void>builder()
        .message("Delete fake event successfully")
        .data(null)
        .build();
  }

}
