package com.passation.passation_backend.service;

import com.passation.passation_backend.dto.AlerteDTO;
import com.passation.passation_backend.model.*;
import com.passation.passation_backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlerteServiceTest {

    @Mock private AlerteRepository alerteRepository;
    @Mock private PassationRepository passationRepository;
    @Mock private PassationProjetRepository passationProjetRepository;
    @Mock private TimelineEtapeRepository timelineEtapeRepository;

    @InjectMocks
    private AlerteService alerteService;

    private Passation passation;
    private Alerte alerte;

    @BeforeEach
    void setUp() {
        User employe = User.builder().id(1L).nom("Martin").prenom("Alice")
                .email("alice@example.com").role(Role.EMPLOYE).build();
        User remplacant = User.builder().id(2L).nom("Leclerc").prenom("Claire")
                .email("claire@example.com").role(Role.REMPLACANT).build();

        passation = Passation.builder()
                .id(1L)
                .employePartant(employe)
                .remplacant(remplacant)
                .dateDepart(LocalDate.now().plusDays(10))
                .statut(StatutPassation.EN_COURS)
                .pourcentageGlobal(45.0)
                .build();

        alerte = Alerte.builder()
                .id(1L)
                .passation(passation)
                .type("RETARD_CRITIQUE")
                .message("Test alerte")
                .niveauSeverite(NiveauSeverite.CRITIQUE)
                .lu(false)
                .build();
    }

    // ── findAll ───────────────────────────────────────────────────────────────

    @Test
    void findAll_returnsAllAlertes() {
        when(alerteRepository.findAll()).thenReturn(List.of(alerte));

        List<AlerteDTO> result = alerteService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getType()).isEqualTo("RETARD_CRITIQUE");
    }

    // ── findNonLues ───────────────────────────────────────────────────────────

    @Test
    void findNonLues_returnsOnlyUnreadAlertes() {
        when(alerteRepository.findByLuFalse()).thenReturn(List.of(alerte));

        List<AlerteDTO> result = alerteService.findNonLues();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLu()).isFalse();
    }

    // ── marquerLue ────────────────────────────────────────────────────────────

    @Test
    void marquerLue_setsLuToTrue_andReturnsDTO() {
        when(alerteRepository.findById(1L)).thenReturn(Optional.of(alerte));
        when(alerteRepository.save(any(Alerte.class))).thenReturn(alerte);

        AlerteDTO result = alerteService.marquerLue(1L);

        assertThat(result).isNotNull();
        verify(alerteRepository).save(alerte);
        assertThat(alerte.getLu()).isTrue();
    }

    @Test
    void marquerLue_throwsException_whenNotFound() {
        when(alerteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> alerteService.marquerLue(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Alerte not found: 99");
    }

    // ── findByPassation ───────────────────────────────────────────────────────

    @Test
    void findByPassation_returnsAlertesForPassation() {
        when(alerteRepository.findByPassationId(1L)).thenReturn(List.of(alerte));

        List<AlerteDTO> result = alerteService.findByPassation(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPassationId()).isEqualTo(1L);
    }

    // ── generateAlertesForPassation ───────────────────────────────────────────

    @Test
    void generateAlertes_createsSansRemplacantAlert_whenNoRemplacant() {
        passation.setRemplacant(null);
        passation.setDateDepart(LocalDate.now().plusDays(60)); // loin → pas d'autres alertes

        when(passationRepository.findById(1L)).thenReturn(Optional.of(passation));
        when(passationProjetRepository.findByPassationId(1L)).thenReturn(List.of());
        when(alerteRepository.findByPassationId(1L)).thenReturn(List.of());
        when(alerteRepository.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));

        List<AlerteDTO> result = alerteService.generateAlertesForPassation(1L);

        assertThat(result).anyMatch(a -> "SANS_REMPLACANT".equals(a.getType()));
    }

    @Test
    void generateAlertes_createsRetardCritiqueAlert_whenConditionsMet() {
        passation.setPourcentageGlobal(20.0); // < 30%
        passation.setDateDepart(LocalDate.now().plusDays(10)); // <= 15 jours

        when(passationRepository.findById(1L)).thenReturn(Optional.of(passation));
        when(passationProjetRepository.findByPassationId(1L)).thenReturn(List.of());
        when(alerteRepository.findByPassationId(1L)).thenReturn(List.of());
        when(alerteRepository.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));

        List<AlerteDTO> result = alerteService.generateAlertesForPassation(1L);

        assertThat(result).anyMatch(a -> "RETARD_CRITIQUE".equals(a.getType()));
    }

    @Test
    void generateAlertes_createsValidationManquanteAlert_whenNotTermineeAndNear() {
        passation.setStatut(StatutPassation.EN_COURS);
        passation.setDateDepart(LocalDate.now().plusDays(5)); // <= 7 jours

        when(passationRepository.findById(1L)).thenReturn(Optional.of(passation));
        when(passationProjetRepository.findByPassationId(1L)).thenReturn(List.of());
        when(alerteRepository.findByPassationId(1L)).thenReturn(List.of());
        when(alerteRepository.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));

        List<AlerteDTO> result = alerteService.generateAlertesForPassation(1L);

        assertThat(result).anyMatch(a -> "VALIDATION_MANQUANTE".equals(a.getType()));
    }

    @Test
    void generateAlertes_doesNotDuplicateExistingAlerts() {
        passation.setRemplacant(null);
        passation.setDateDepart(LocalDate.now().plusDays(60));

        Alerte existingSansRemplacant = Alerte.builder()
                .type("SANS_REMPLACANT").message("Départ sans remplaçant assigné")
                .passation(passation).niveauSeverite(NiveauSeverite.INFO).lu(false).build();

        when(passationRepository.findById(1L)).thenReturn(Optional.of(passation));
        when(passationProjetRepository.findByPassationId(1L)).thenReturn(List.of());
        when(alerteRepository.findByPassationId(1L)).thenReturn(List.of(existingSansRemplacant));

        List<AlerteDTO> result = alerteService.generateAlertesForPassation(1L);

        assertThat(result).noneMatch(a -> "SANS_REMPLACANT".equals(a.getType()));
    }

    @Test
    void generateAlertes_throwsException_whenPassationNotFound() {
        when(passationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> alerteService.generateAlertesForPassation(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Passation not found: 99");
    }

    // ── toDTO ─────────────────────────────────────────────────────────────────

    @Test
    void toDTO_mapsAllFields() {
        AlerteDTO dto = alerteService.toDTO(alerte);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getPassationId()).isEqualTo(1L);
        assertThat(dto.getType()).isEqualTo("RETARD_CRITIQUE");
        assertThat(dto.getNiveauSeverite()).isEqualTo(NiveauSeverite.CRITIQUE);
        assertThat(dto.getLu()).isFalse();
    }
}
