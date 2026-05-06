package com.passation.passation_backend.dto;

import com.passation.passation_backend.model.StatutProjet;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateProjetRequest {
    @NotBlank
    private String nom;
    private String description;
    private StatutProjet statut;
    private Integer pourcentageAvancement;
    private LocalDate dateDebut;
    private LocalDate dateFinPrevue;
    private Long responsableId;
}
