package com.example.infrastructure_service.controller;

import com.example.infrastructure_service.client.MapClient;
import com.example.infrastructure_service.dto.ApiResponse;
import com.example.infrastructure_service.dto.request.FindNearestInfraRequest;
import com.example.infrastructure_service.dto.request.InfraFilterRequest;
import com.example.infrastructure_service.dto.request.NewInfraRequest;
import com.example.infrastructure_service.dto.request.UpdateInfraRequest;
import com.example.infrastructure_service.dto.response.PageResponse;
import com.example.infrastructure_service.model.InfraObject;
import com.example.infrastructure_service.service.InfraProcessingService;
import com.example.infrastructure_service.service.InfraService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/infrastructures")
@RequiredArgsConstructor
public class InfraController {

  private final InfraService infraService;
  private final InfraProcessingService infraProcessingService;
  private final MapClient mapClient;
  private final ObjectMapper objectMapper;
  //get infra object by filter: camId, time, category
  @PostMapping("/filter")
  public ApiResponse<PageResponse<InfraObject>> getInfraObjects(
      @RequestBody InfraFilterRequest request,
      @RequestHeader("X-Credential-Id") String credentialId
  ) {

    Pageable pageable = request.getIsPaged() != null && !request.getIsPaged() ? Pageable.unpaged() : PageRequest.of(request.getPage(), request.getSize());

    Page<InfraObject> infraObjects = infraService.getFilterInfraObject(
        pageable, request.getName(), request.getLocation(), request.getDateCaptured(), request.getStatus(),
        request.getCategory(), request.getCameraId(), credentialId, request.getKeyword(), request.getType()
    );
    return ApiResponse.<PageResponse<InfraObject>>builder()
        .message("Get infra objects by filter successfully")
        .data(new PageResponse<>(infraObjects))
        .build();
  }
  // find the nearest infra by location
  @GetMapping("/find")
  public ApiResponse<InfraObject> findInfraByLocation(
    @RequestBody FindNearestInfraRequest request
  ) {

    InfraObject infraObject = infraProcessingService.findByLocation(request.getCameraId(), request.getCategory(), request.getName(),
        request.getLatitude(), request.getLongitude());

    return ApiResponse.<InfraObject>builder()
        .message("Find infra by location successfully")
        .data(infraObject)
        .build();
  }

  @GetMapping("/{id}")
  public ApiResponse<InfraObject> getInfraById(@PathVariable String id) {
    return ApiResponse.<InfraObject>builder()
        .message("Get infra by id successfully")
        .data(infraService.findById(id))
        .build();
  }

  @GetMapping("/find/radius")
  public ApiResponse<List<InfraObject>> getInfraObjectsByRadius(@RequestParam Double latitude, @RequestParam Double longitude, @RequestParam Double radius) {
    return ApiResponse.<List<InfraObject>>builder()
        .message("Get infra objects by radius successfully")
        .data(infraService.findInfraWithRadius(latitude, longitude, radius))
        .build();
  }

  @PostMapping("/find/polygon")
  public ApiResponse<List<InfraObject>> getInfraObjectsByPolygon(@RequestBody List<double []> points) {
    return ApiResponse.<List<InfraObject>>builder()
        .message("Get infra objects by radius successfully")
        .data(infraService.findInfraWithPolygon(points))
        .build();
  }


  @GetMapping("/geocoding")
  public String getAddress(@RequestParam Double latitude, @RequestParam Double longitude) {
    return mapClient.getAddress(latitude, longitude);
  }

  @GetMapping("/search")
  public ApiResponse<PageResponse<InfraObject>> searchInfraObjects(@RequestParam String keyword, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    Page<InfraObject> infraObjects = infraService.searchByKeyword(keyword, page, size);

    return ApiResponse.<PageResponse<InfraObject>>builder()
        .message("Search infra objects successfully")
        .data(new PageResponse<>(infraObjects))
        .build();
  }

  @GetMapping("/lost")
  public ApiResponse<List<InfraObject>> getLostInfraObjects() {
    return ApiResponse.<List<InfraObject>>builder()
        .message("Get list infra objects successfully")
        .data(infraService.getLostInfraObject())
        .build();
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ApiResponse<InfraObject> addInfraObject(@RequestPart("data") String requestData, @RequestPart(required = false, value = "avatar") MultipartFile avatar, @RequestPart(required = false, value = "originalImage") MultipartFile originalImage) throws JsonProcessingException {
    NewInfraRequest request = objectMapper.readValue(requestData, NewInfraRequest.class);

    return ApiResponse.<InfraObject>builder()
        .message("Add infra object successfully")
        .data(infraService.addNewInfra(request, avatar, originalImage))
        .build();
  }

  @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ApiResponse<InfraObject> updateInfraObject(@RequestPart("data") String requestData, @RequestPart(required = false, value = "avatar") MultipartFile avatar, @RequestPart(required = false, value = "originalImage") MultipartFile originalImage) throws JsonProcessingException {
    UpdateInfraRequest request = objectMapper.readValue(requestData, UpdateInfraRequest.class);

    return ApiResponse.<InfraObject>builder()
        .message("Add infra object successfully")
        .data(infraService.updateInfra(request, avatar, originalImage))
        .build();
  }

  @DeleteMapping("/{id}")
  public ApiResponse<Void> deleteInfraObject(@PathVariable String id) {
    infraService.deleteInfra(id);
    return ApiResponse.<Void>builder()
        .message("Delete infra object successfully")
        .build();
  }


}
