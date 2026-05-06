package com.passation.passation_backend.repository;

import com.passation.passation_backend.model.Passation;
import com.passation.passation_backend.model.StatutPassation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PassationRepository extends JpaRepository<Passation, Long> {

    List<Passation> findByStatut(StatutPassation statut);

    List<Passation> findByEmployePartantId(Long employePartantId);

    List<Passation> findByRemplacantId(Long remplacantId);

    List<Passation> findByManagerId(Long managerId);

    @Query("SELECT p FROM Passation p WHERE " +
            "(:nomEmploye IS NULL OR LOWER(CONCAT(p.employePartant.nom, ' ', p.employePartant.prenom)) LIKE LOWER(CONCAT('%', :nomEmploye, '%'))) AND " +
            "(:departement IS NULL OR p.employePartant.departement = :departement) AND " +
            "(:statut IS NULL OR p.statut = :statut) AND " +
            "(:dateMin IS NULL OR p.dateDepart >= :dateMin) AND " +
            "(:dateMax IS NULL OR p.dateDepart <= :dateMax)")
    List<Passation> searchPassations(
            @Param("nomEmploye") String nomEmploye,
            @Param("departement") String departement,
            @Param("statut") StatutPassation statut,
            @Param("dateMin") LocalDate dateMin,
            @Param("dateMax") LocalDate dateMax
    );
}
