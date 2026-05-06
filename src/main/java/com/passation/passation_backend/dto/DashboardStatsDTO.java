package com.passation.passation_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private long totalPassations;
    private long passationsEnCours;
    private long passationsTerminees;
    private long totalProjets;
    private long projetsEnCours;
    private long totalUsers;
    private long alertesCritiques;
    private long alertesNonLues;
    private double pourcentageGlobalMoyen;
}
