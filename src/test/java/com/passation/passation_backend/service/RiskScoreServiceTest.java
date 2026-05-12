package com.passation.passation_backend.service;

import com.passation.passation_backend.model.*;
import com.passation.passation_backend.repository.PassationProjetRepository;
import com.passation.passation_backend.repository.PassationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RiskScoreServiceTest {

    @Mock private PassationRepository passationRepository;
    @Mock private PassationProjetRepository passationProjetRepository;

    @InjectMocks
    private RiskScoreService riskScoreService;

    private Passation passation;

    @BeforeEach
    void setUp() {
        passation = Passation.builder()
                .id(1L)
                .statut(StatutPassation.EN_COURS)
                .pourcentageGlobal(50.0)
                .dateDepart(LocalDate.now().plusDays(30))
                .build();
    }

    // ── FAIBLE ────────────────────────────────────────────────────────────────

    @Test
    void calculateRiskScore_returnsFaible_whenLowRisk() {
        passation.setDateDepart(LocalDate.now().plusDays(60)); // > 30 jours → score 0
        passation.setPourcentageGlobal(90.0); // restant 10% → score 0

        when(passationRepository.findById(1L)).thenReturn(Optional.of(passation));
        when(passationProjetRepository.findByPassationId(1L)).thenReturn(List.of());

        String result = riskScoreService.calculateRiskScore(1L);

        assertThat(result).isEqualTo("FAIBLE");
    }

    // ── MODERE ────────────────────────────────────────────────────────────────

    @Test
    void calculateRiskScore_returnsModere_whenMediumRisk() {
        passation.setDateDepart(LocalDate.now().plusDays(12)); // 7-15 j → +2
        passation.setPourcentageGlobal(55.0); // restant 45% → +2

        when(passationRepository.findById(1L)).thenReturn(Optional.of(passation));
        when(passationProjetRepository.findByPassationId(1L)).thenReturn(List.of());

        String result = riskScoreService.calculateRiskScore(1L);

        assertThat(result).isEqualTo("MODERE");
    }

    // ── CRITIQUE ──────────────────────────────────────────────────────────────

    @Test
    void calculateRiskScore_returnsCritique_whenHighRisk() {
        passation.setDateDepart(LocalDate.now().plusDays(3)); // < 7 j → +3
        passation.setPourcentageGlobal(10.0); // restant 90% → +3

        Projet projet = Projet.builder()
                .statut(StatutProjet.EN_COURS)
                .pourcentageAvancement(30) // < 50 → +1
                .build();
        PassationProjet pp = PassationProjet.builder()
                .projet(projet)
                .niveauMaitrise(NiveauMaitrise.FAIBLE) // +2
                .build();

        when(passationRepository.findById(1L)).thenReturn(Optional.of(passation));
        when(passationProjetRepository.findByPassationId(1L)).thenReturn(List.of(pp));

        String result = riskScoreService.calculateRiskScore(1L);

        assertThat(result).isEqualTo("CRITIQUE"); // score = 3+3+2+1 = 9
    }

    // ── NiveauMaitrise scoring ────────────────────────────────────────────────

    @Test
    void calculateRiskScore_addsMoyenScore_whenNiveauMaitriseIsMoyen() {
        passation.setDateDepart(LocalDate.now().plusDays(60));
        passation.setPourcentageGlobal(90.0);

        PassationProjet pp = PassationProjet.builder()
                .niveauMaitrise(NiveauMaitrise.MOYEN) // +1
                .build();

        when(passationRepository.findById(1L)).thenReturn(Optional.of(passation));
        when(passationProjetRepository.findByPassationId(1L)).thenReturn(List.of(pp));

        String result = riskScoreService.calculateRiskScore(1L);

        assertThat(result).isEqualTo("FAIBLE"); // score = 1
    }

    // ── getRiskScoreDetails ───────────────────────────────────────────────────

    @Test
    void getRiskScoreDetails_returnsAllFields() {
        passation.setDateDepart(LocalDate.now().plusDays(20));
        passation.setPourcentageGlobal(60.0);

        when(passationRepository.findById(1L)).thenReturn(Optional.of(passation));
        when(passationProjetRepository.findByPassationId(1L)).thenReturn(List.of());

        Map<String, Object> details = riskScoreService.getRiskScoreDetails(1L);

        assertThat(details).containsKeys("score", "niveau", "joursAvantDepart",
                "pourcentageRestant", "nombreProjetsACritiques");
        assertThat(details.get("pourcentageRestant")).isEqualTo(40.0);
        assertThat((Long) details.get("nombreProjetsACritiques")).isEqualTo(0L);
    }

    @Test
    void getRiskScoreDetails_countsCriticalProjects() {
        passation.setDateDepart(LocalDate.now().plusDays(60));
        passation.setPourcentageGlobal(80.0);

        Projet projetCritique = Projet.builder()
                .statut(StatutProjet.EN_COURS)
                .pourcentageAvancement(30) // < 50
                .build();
        PassationProjet pp = PassationProjet.builder()
                .projet(projetCritique)
                .niveauMaitrise(NiveauMaitrise.BON)
                .build();

        when(passationRepository.findById(1L)).thenReturn(Optional.of(passation));
        when(passationProjetRepository.findByPassationId(1L)).thenReturn(List.of(pp));

        Map<String, Object> details = riskScoreService.getRiskScoreDetails(1L);

        assertThat((Long) details.get("nombreProjetsACritiques")).isEqualTo(1L);
    }

    @Test
    void getRiskScoreDetails_throwsException_whenPassationNotFound() {
        when(passationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> riskScoreService.getRiskScoreDetails(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Passation not found: 99");
    }

    @Test
    void calculateRiskScore_handlesNullDateDepart() {
        passation.setDateDepart(null); // joursAvantDepart = MAX_VALUE → score 0
        passation.setPourcentageGlobal(90.0);

        when(passationRepository.findById(1L)).thenReturn(Optional.of(passation));
        when(passationProjetRepository.findByPassationId(1L)).thenReturn(List.of());

        String result = riskScoreService.calculateRiskScore(1L);

        assertThat(result).isEqualTo("FAIBLE");
    }

    @Test
    void calculateRiskScore_handlesNullPourcentageGlobal() {
        passation.setPourcentageGlobal(null); // → 0, restant = 100 → +3
        passation.setDateDepart(LocalDate.now().plusDays(60));

        when(passationRepository.findById(1L)).thenReturn(Optional.of(passation));
        when(passationProjetRepository.findByPassationId(1L)).thenReturn(List.of());

        String result = riskScoreService.calculateRiskScore(1L);

        assertThat(result).isEqualTo("MODERE"); // score = 3
    }
}
