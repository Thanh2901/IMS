package com.example.infrastructure_service.repository;

import com.example.infrastructure_service.dto.response.CategoryStatusCount;
import com.example.infrastructure_service.model.InfraObject;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface InfraObjectRepository extends JpaRepository<InfraObject, String>,
    JpaSpecificationExecutor<InfraObject> {

  // find the nearest infra, first filter by camId and category then Filter out objects within radius of the new object, order by min distance limit 1
  @Query(value = """
    SELECT * FROM infra_objects infra
    WHERE infra.camera_id = :cameraId
      AND infra.category = :category
      AND (coalesce(:name,'') = '' or infra.name = :name)
      AND ST_DWithin(
            ST_SetSRID(ST_MakePoint(infra.longitude, infra.latitude), 4326)::geography,
            ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography,
            :radius
          )
    ORDER BY ST_SetSRID(ST_MakePoint(infra.longitude, infra.latitude), 4326) <->
             ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)
    LIMIT 1
    """,
      nativeQuery = true)
  InfraObject findNearestInfraWithinRadius(String cameraId, String category, String name, Double latitude,
      Double longitude, Double radius);

  @Query("""
    SELECT new com.example.infrastructure_service.dto.response.CategoryStatusCount(
        o.category, o.status, COUNT(o)
    )
    FROM InfraObject o
    WHERE o.cameraId = :cameraId
    GROUP BY o.category, o.status
    """)
  List<CategoryStatusCount> getInfraObjectStatisticsByCamera(@Param("cameraId") String cameraId);



  @Query(value = """
        SELECT * FROM infra_objects infra
        WHERE ST_DWithin(
            ST_SetSRID(ST_MakePoint(infra.longitude, infra.latitude), 4326)::geography,
            ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography,
            :radius
        )
    """, nativeQuery = true)
  List<InfraObject> findObjectsWithinRadius(@Param("latitude") double latitude,
      @Param("longitude") double longitude,
      @Param("radius") double radius);


  @Query(value = """
        SELECT * FROM infra_objects infra
        WHERE ST_Within(
            ST_SetSRID(ST_MakePoint(infra.longitude, infra.latitude), 4326),
            ST_SetSRID(ST_GeomFromText(:polygonWKT), 4326)
        )
    """, nativeQuery = true)
  List<InfraObject> findInfraWithinPolygon(@Param("polygonWKT") String polygonWKT);


  @Query(value = """
        select * from infra_objects infra
            where infra.camera_id = :cameraId
            and infra.date_captured not between :startTime and :endTime
            and infra.type = 'ASSET'
    """, nativeQuery = true)
  List<InfraObject> getNotUpdateInfraObjects(String cameraId, LocalDateTime startTime, LocalDateTime endTime);

  @Query(value = "select * from infra_objects infra where infra.status like 'LOST' ", nativeQuery = true)
  List<InfraObject> getInfraObjectsWithLost();


  @Query(value = """
    SELECT infra_objects.* FROM infra_objects join infra_info on infra_objects.info_id = infra_info.id
    WHERE 
        (COALESCE(:cameraId, '') = '' OR camera_id = :cameraId)
        AND (COALESCE(:category, '') = '' OR category = :category)
        AND (COALESCE(:name, '') = '' OR unaccent(name) ILIKE '%' || unaccent(:name) || '%')
        AND (COALESCE(:location, '') = '' OR NOT EXISTS (
            SELECT 1 
            FROM regexp_split_to_table(unaccent(:location), '\\s*,\\s*') AS loc_phrase
            WHERE unaccent(replace(location, ' ', '')) NOT ILIKE '%' || replace(loc_phrase, ' ', '') || '%'
        ))
        AND (COALESCE(:date, '') = '' OR TO_CHAR(date_captured, 'YYYY-MM-DD HH24:MI:SS') LIKE '%' || :date || '%')
        AND (COALESCE(:status, '') = '' OR status = :status)
        AND (COALESCE(:type, '') = '' OR type = :type)
        AND NOT EXISTS (
            SELECT 1 
            FROM regexp_split_to_table(unaccent(:keyword), '\\s*,\\s*') AS phrase
            WHERE unaccent(replace(CONCAT_WS('', location, name, additional_data, key_id, category, status), ' ', '')) 
            NOT ILIKE '%' || replace(phrase, ' ', '') || '%'
        )
        ORDER BY date_captured DESC
    """,
      countQuery = """
    SELECT infra_objects.* FROM infra_objects join infra_info on infra_objects.info_id = infra_info.id
    WHERE 
        (COALESCE(:cameraId, '') = '' OR camera_id = :cameraId)
        AND (COALESCE(:category, '') = '' OR category = :category)
        AND (COALESCE(:name, '') = '' OR unaccent(name) ILIKE '%' || unaccent(:name) || '%')
        AND (COALESCE(:location, '') = '' OR NOT EXISTS (
            SELECT 1 
            FROM regexp_split_to_table(unaccent(:location), '\\s*,\\s*') AS loc_phrase
            WHERE unaccent(replace(location, ' ', '')) NOT ILIKE '%' || replace(loc_phrase, ' ', '') || '%'
        ))
        AND (COALESCE(:date, '') = '' OR TO_CHAR(date_captured, 'YYYY-MM-DD HH24:MI:SS') LIKE '%' || :date || '%')
        AND (COALESCE(:status, '') = '' OR status = :status)
        AND (COALESCE(:type, '') = '' OR type = :type)
        AND NOT EXISTS (
            SELECT 1 
            FROM regexp_split_to_table(unaccent(:keyword), '\\s*,\\s*') AS phrase
            WHERE unaccent(replace(CONCAT_WS('', location, name, additional_data, key_id, category, status), ' ', '')) 
            NOT ILIKE '%' || replace(phrase, ' ', '') || '%'
        )
    """,
      nativeQuery = true)
  Page<InfraObject> searchAndFilter(
      @Param("keyword") String keyword,
      @Param("cameraId") String cameraId,
      @Param("category") String category,
      @Param("name") String name,
      @Param("location") String location,
      @Param("date") String date,
      @Param("status") String status,
      Pageable pageable,
      @Param("type") String type

  );


  @Query(value = """
    SELECT infra_objects.* FROM infra_objects join infra_info on infra_objects.info_id = infra_info.id
    WHERE NOT EXISTS (
        SELECT 1 
        FROM regexp_split_to_table(unaccent(:keyword), '\\s*,\\s*') AS phrase
        WHERE unaccent(replace(CONCAT_WS('', location, name, category, additional_data, key_id, status, TO_CHAR(date_captured, 'DD/MM/YYYY')),' ','')) 
        NOT ILIKE '%' || replace(phrase,' ','') || '%'
    )
    """,
      countQuery = """
    SELECT infra_objects.* FROM infra_objects join infra_info on infra_objects.info_id = infra_info.id
    WHERE NOT EXISTS (
        SELECT 1 
        FROM regexp_split_to_table(unaccent(:keyword), '\\s*,\\s*') AS phrase
        WHERE unaccent(replace(CONCAT_WS('', location, name, category, additional_data, key_id, status, TO_CHAR(date_captured, 'DD/MM/YYYY')),' ','')) 
        NOT ILIKE '%' || replace(phrase,' ','') || '%'
    )
    """,
      nativeQuery = true)
  Page<InfraObject> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

  List<InfraObject> findInfraObjectByCameraId(@Param("cameraId") String cameraId);

}
//CREATE EXTENSION IF NOT EXISTS unaccent;
//create index for table
//CREATE INDEX infra_location_gist ON infra_objects USING GIST (ST_SetSRID(ST_MakePoint(longitude, latitude), 4326));