package com.passation.passation_backend.repository;

import com.passation.passation_backend.model.PassationProjet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PassationProjetRepository extends JpaRepository<PassationProjet, Long> {

    List<PassationProjet> findByPassationId(Long passationId);

    List<PassationProjet> findByProjetId(Long projetId);
}
