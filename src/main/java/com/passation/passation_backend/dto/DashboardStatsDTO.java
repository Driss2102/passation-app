package com.passation.passation_backend.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsDTO {
    private Long totalPassations;
    private Long passationsEnCours;
    private Long passationsTerminees;
    private Long totalProjets;
    private Long projetsEnCours;
    private Long totalUsers;
    private Long alertesCritiques;
    private Long alertesNonLues;
    private Double pourcentageGlobalMoyen;
}
