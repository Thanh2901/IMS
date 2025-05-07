package com.example.infrastructure_service.mapper;

import com.example.infrastructure_service.dto.EventNotificationDTO;
import com.example.infrastructure_service.enums.EventStatus;
import com.example.infrastructure_service.model.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventMapper {

  @Mapping(target = "cameraId", source = "event.infraObject.cameraId")
  @Mapping(target = "eventId", source = "event.id")
  @Mapping(target = "time", expression = "java(event.getDateCaptured().toString())")
  @Mapping(target = "status", source = "event.status")
  @Mapping(target = "urlImage", source = "event.image.pathUrl")
  @Mapping(target = "category", source = "event.infraObject.category")
  @Mapping(target = "name", source = "event.infraObject.name")
  @Mapping(target = "level", expression = "java(event.getLevel().toString())")
  @Mapping(target = "confidence", expression = "java(String.valueOf(event.getConfidence()))")
  @Mapping(target = "longitude", expression = "java(String.valueOf(event.getInfraObject().getLongitude()))")
  @Mapping(target = "latitude", expression = "java(String.valueOf(event.getInfraObject().getLatitude()))")
  @Mapping(target = "location", expression = "java(String.valueOf(event.getInfraObject().getLocation()))")
  EventNotificationDTO toEventNotificationDTO(Event event);
}

