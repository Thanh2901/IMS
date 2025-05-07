package com.example.infrastructure_service.model;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;


public class FilterSpecification {
  //Defines how to filter through parameters
  public static Specification<Event> filterEvent(
      LocalDateTime startTime,
      LocalDateTime endTime,
      String status,
      String eventStatus,
      String category,
      String cameraId,
      String infraObjectId,
      List<String> cameras
  ) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();
      predicates.add(root.get("infraObject").get("cameraId").in(cameras));
      if (category != null && !category.isEmpty()) {
        predicates.add(criteriaBuilder.equal(root.join("infraObject").get("category"), category));
      }
      if (cameraId != null && !cameraId.isEmpty()) {
        predicates.add(criteriaBuilder.equal(root.join("infraObject").get("cameraId"), cameraId));
      }
      if (startTime != null && endTime != null) {
        predicates.add(criteriaBuilder.between(root.get("dateCaptured"), startTime, endTime));
      } else if (startTime != null) {
        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dateCaptured"), startTime));
      } else if (endTime != null) {
        predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dateCaptured"), endTime));
      }
      if (status != null && !status.isEmpty()) {
        predicates.add(criteriaBuilder.equal(root.get("status"), status));
      }
      if (eventStatus != null && !eventStatus.isEmpty()) {
        predicates.add(criteriaBuilder.equal(root.get("eventStatus"), eventStatus));
      }
      if (infraObjectId != null && !infraObjectId.isEmpty()) {
        predicates.add(criteriaBuilder.equal(root.join("infraObject").get("id"), infraObjectId));
      }

      query.orderBy(criteriaBuilder.desc(root.get("dateCaptured")));
      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }


  public static Specification<InfraObject> filterInfraObject(
      String name,
      String location,
      String dateCaptured,
      String status,
      String category,
      String cameraId,
      List<String> cameras,
      String keyword
  ) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();
      predicates.add(root.get("cameraId").in(cameras));
      if (category != null && !category.isEmpty()) {
        predicates.add(criteriaBuilder.equal(root.get("category"), category));
      }
      if (cameraId != null && !cameraId.isEmpty()) {
        predicates.add(criteriaBuilder.equal(root.get("cameraId"), cameraId));
      }
      if (status != null && !status.isEmpty()) {
        predicates.add(criteriaBuilder.equal(root.get("status"), status));
      }
      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }
}
