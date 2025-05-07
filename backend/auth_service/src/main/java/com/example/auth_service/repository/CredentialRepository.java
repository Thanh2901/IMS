package com.example.auth_service.repository;

import com.example.auth_service.model.Credential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CredentialRepository extends JpaRepository<Credential, String> {
    Optional<Credential> findCredentialByEmail(String email);
}
