package com.passation.passation_backend.dto;

import com.passation.passation_backend.model.StatutProjet;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProjetRequest {

    @NotBlank
    private String nom;

    private String description;

    @NotNull
    private StatutProjet statut;

    private Integer pourcentageAvancement;

    private LocalDate dateDebut;

    private LocalDate dateFinPrevue;

    private Long responsableId;
}
