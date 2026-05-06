package com.passation.passation_backend.repository;

import com.passation.passation_backend.model.Projet;
import com.passation.passation_backend.model.StatutProjet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjetRepository extends JpaRepository<Projet, Long> {

    List<Projet> findByStatut(StatutProjet statut);

    List<Projet> findByResponsableId(Long responsableId);
}
