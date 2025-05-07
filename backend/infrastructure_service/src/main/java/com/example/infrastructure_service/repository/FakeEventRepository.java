package com.example.infrastructure_service.repository;

import com.example.infrastructure_service.model.FakeEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FakeEventRepository extends JpaRepository<FakeEvent, String> {

}
