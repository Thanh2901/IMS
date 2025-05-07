package com.example.infrastructure_service.repository;

import com.example.infrastructure_service.dto.response.EventDateCount;
import com.example.infrastructure_service.model.Event;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, String>,
    JpaSpecificationExecutor<Event> {

  @Query("SELECT e from Event e where e.infraObject.id = :infraObjectId ORDER BY e.dateCaptured DESC LIMIT 1")
  Event findTheLastEvent(@Param("infraObjectId") String infraObjectId);

  @Query(value = "SELECT new com.example.infrastructure_service.dto.response.EventDateCount(CAST(e.dateCaptured AS date), COUNT(e)) " +
      "FROM Event e " +
      "JOIN e.infraObject io " +
      "WHERE CAST(e.dateCaptured AS date) >= COALESCE(:startDate, CAST('1970-01-01' AS date)) " +
      "AND CAST(e.dateCaptured AS date) <= COALESCE(:endDate, CAST('9999-12-31' AS date)) " +
      "AND io.cameraId = COALESCE(:cameraId, io.cameraId) " +
      "GROUP BY CAST(e.dateCaptured AS date) " +
      "ORDER BY CAST(e.dateCaptured AS date) ASC")
  List<EventDateCount> getEventStatistics(
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate,
      @Param("cameraId") String cameraId
  );

  List<Event> findByInfraObject_Id(String infraObjectId);

  @Query("select e from Event e where e.infraObject.id = :id order by e.dateCaptured desc ")
  List<Event> findEventByDate(@Param("id") String infraObjectId);

  @Query(value = """
    SELECT e.* FROM infra_objects i, event e
    WHERE 
        e.infra_object_id  = i.id
        AND (COALESCE(:cameraId, '') = '' OR camera_id = :cameraId)
        AND (COALESCE(:category, '') = '' OR category = :category)
        AND (COALESCE(:name, '') = '' OR unaccent(name) ILIKE '%' || unaccent(:name) || '%')
        AND (COALESCE(:location, '') = '' OR NOT EXISTS (
            SELECT 1 
            FROM regexp_split_to_table(unaccent(:location), '\\s*,\\s*') AS loc_phrase
            WHERE unaccent(replace(location, ' ', '')) NOT ILIKE '%' || replace(loc_phrase, ' ', '') || '%'
        ))
        AND (COALESCE(:startTime, '') = '' OR TO_CHAR(e.date_captured, 'YYYY-MM-DD') = :startTime)
        AND (COALESCE(:endTime, '') = '' OR TO_CHAR(e.end_time, 'YYYY-MM-DD') = :endTime)                
        AND (COALESCE(:status, '') = '' OR e.status = :status)
        AND (COALESCE(:eventStatus, '') = '' OR e.event_status = :eventStatus)
        AND (COALESCE(:confidence, 0) = 0 OR (e.confidence >= :confidence - 0.2 AND e.confidence <= :confidence))
        AND (:level is null OR (e.level = :level))  
        AND NOT EXISTS (
            SELECT 1 
            FROM regexp_split_to_table(unaccent(:keyword), '\\s*,\\s*') AS phrase
            WHERE unaccent(replace(CONCAT_WS('', location, name, category, e.status, e.event_status), ' ', '')) 
            NOT ILIKE '%' || replace(phrase, ' ', '') || '%'
        )
        ORDER BY e.date_captured DESC
    """,
      countQuery = """
    SELECT COUNT(*) FROM event e, infra_objects i
    WHERE
        e.infra_object_id  = i.id
        AND (COALESCE(:cameraId, '') = '' OR camera_id = :cameraId)
        AND (COALESCE(:category, '') = '' OR category = :category)
        AND (COALESCE(:name, '') = '' OR unaccent(name) ILIKE '%' || unaccent(:name) || '%')
        AND (COALESCE(:location, '') = '' OR NOT EXISTS (
            SELECT 1 
            FROM regexp_split_to_table(unaccent(:location), '\\s*,\\s*') AS loc_phrase
            WHERE unaccent(replace(location, ' ', '')) NOT ILIKE '%' || replace(loc_phrase, ' ', '') || '%'
        ))
        AND (COALESCE(:startTime, '') = '' OR TO_CHAR(e.date_captured, 'YYYY-MM-DD') = :startTime)
        AND (COALESCE(:endTime, '') = '' OR TO_CHAR(e.end_time, 'YYYY-MM-DD') = :endTime)       
        AND (COALESCE(:status, '') = '' OR e.status = :status)
        AND (COALESCE(:eventStatus, '') = '' OR e.event_status = :eventStatus)
        AND (COALESCE(:confidence, 0) = 0 OR (e.confidence >= (:confidence - 0.2) AND e.confidence <= :confidence))
        AND (:level is null OR (e.level = :level))  
        AND NOT EXISTS (
            SELECT 1 
            FROM regexp_split_to_table(unaccent(:keyword), '\\s*,\\s*') AS phrase
            WHERE unaccent(replace(CONCAT_WS('', location, name, category, e.status, e.event_status), ' ', '')) 
            NOT ILIKE '%' || replace(phrase, ' ', '') || '%'
        )
    """,
      nativeQuery = true)
  Page<Event> getEventByFilterAndKeyword(
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
      Integer level
  );

}
