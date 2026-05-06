package com.passation.passation_backend.dto;

import com.passation.passation_backend.model.StatutProjet;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProjetDTO {
    private Long id;
    private String nom;
    private String description;
    private StatutProjet statut;
    private Integer pourcentageAvancement;
    private LocalDate dateDebut;
    private LocalDate dateFinPrevue;
    private UserDTO responsable;
    private LocalDateTime dateCreation;
}
