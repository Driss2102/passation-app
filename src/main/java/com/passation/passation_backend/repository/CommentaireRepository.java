package com.passation.passation_backend.repository;

import com.passation.passation_backend.model.Commentaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentaireRepository extends JpaRepository<Commentaire, Long> {

    List<Commentaire> findByPassationId(Long passationId);

    List<Commentaire> findByProjetId(Long projetId);
}
