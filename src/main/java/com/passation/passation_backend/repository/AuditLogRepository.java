package com.passation.passation_backend.repository;

import com.passation.passation_backend.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByUtilisateurIdOrderByDateActionDesc(Long utilisateurId);

    List<AuditLog> findByEntiteConcerneeAndEntiteId(String entiteConcernee, Long entiteId);
}
