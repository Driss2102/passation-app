package com.passation.passation_backend.repository;

import com.passation.passation_backend.model.ChecklistTemplateItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChecklistTemplateItemRepository extends JpaRepository<ChecklistTemplateItem, Long> {

    List<ChecklistTemplateItem> findByTemplateIdOrderByOrdreAsc(Long templateId);
}
