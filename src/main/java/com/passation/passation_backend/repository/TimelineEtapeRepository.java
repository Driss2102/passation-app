package com.passation.passation_backend.repository;

import com.passation.passation_backend.model.TimelineEtape;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimelineEtapeRepository extends JpaRepository<TimelineEtape, Long> {

    List<TimelineEtape> findByPassationIdOrderByOrdreAsc(Long passationId);
}
