package com.passation.passation_backend.repository;

import com.passation.passation_backend.model.Passation;
import com.passation.passation_backend.model.StatutPassation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PassationRepository extends JpaRepository<Passation, Long> {

    List<Passation> findByStatut(StatutPassation statut);

    List<Passation> findByEmployePartantId(Long employePartantId);

    List<Passation> findByRemplacantId(Long remplacantId);

    List<Passation> findByManagerId(Long managerId);
}
