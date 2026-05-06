package com.passation.passation_backend.repository;

import com.passation.passation_backend.model.Alerte;
import com.passation.passation_backend.model.NiveauSeverite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlerteRepository extends JpaRepository<Alerte, Long> {

    List<Alerte> findByPassationId(Long passationId);

    List<Alerte> findByNiveauSeverite(NiveauSeverite niveauSeverite);

    List<Alerte> findByLuFalse();
}
