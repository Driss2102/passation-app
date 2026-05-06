package com.passation.passation_backend.dto;

import com.passation.passation_backend.model.StatutPassation;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassationSearchRequest {
    private String nomEmploye;
    private String departement;
    private StatutPassation statut;
    private LocalDate dateMin;
    private LocalDate dateMax;
    private String scoreRisque;
}
