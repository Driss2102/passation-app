package com.passation.passation_backend.repository;

import com.passation.passation_backend.model.ChecklistTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChecklistTemplateRepository extends JpaRepository<ChecklistTemplate, Long> {

    List<ChecklistTemplate> findByTypePoste(String typePoste);
}
