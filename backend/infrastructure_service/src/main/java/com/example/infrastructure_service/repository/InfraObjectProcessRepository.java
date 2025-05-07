package com.example.infrastructure_service.repository;

import com.example.infrastructure_service.enums.ProcessStatus;
import com.example.infrastructure_service.model.InfraObjectProcess;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InfraObjectProcessRepository extends JpaRepository<InfraObjectProcess, String> {

  List<InfraObjectProcess> findByScheduleIdAndProcessStatus(String scheduleId,
      ProcessStatus processStatus);

  @Query(value = """
    SELECT * FROM infra_object_process
    WHERE schedule_id = :scheduleId
    AND (COALESCE(:status, '') = '' OR status = :status)
    AND (COALESCE(:processStatus, '') = '' OR process_status = :processStatus)
    AND (COALESCE(:eventStatus, '') = '' OR event_status = :eventStatus)
    order by date_captured asc
    """,nativeQuery = true)
  List<InfraObjectProcess> filterInfraProcess(String scheduleId, String status, String processStatus, String eventStatus);

  List<InfraObjectProcess> findByScheduleId(String scheduleId);
}
