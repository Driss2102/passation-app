package com.passation.passation_backend.service;

import com.passation.passation_backend.dto.*;
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
class PassationServiceTest {

    @Mock private PassationRepository passationRepository;
    @Mock private PassationProjetRepository passationProjetRepository;
    @Mock private UserRepository userRepository;
    @Mock private ProjetRepository projetRepository;
    @Mock private AlerteRepository alerteRepository;
    @Mock private UserService userService;
    @Mock private ProjetService projetService;

    @InjectMocks
    private PassationService passationService;

    private User employe, remplacant, manager;
    private Passation passation;

    @BeforeEach
    void setUp() {
        employe   = User.builder().id(1L).nom("Martin").prenom("Alice").email("alice@example.com").role(Role.EMPLOYE).build();
        remplacant = User.builder().id(2L).nom("Leclerc").prenom("Claire").email("claire@example.com").role(Role.REMPLACANT).build();
        manager   = User.builder().id(3L).nom("Dupont").prenom("Bob").email("bob@example.com").role(Role.MANAGER_RH).build();

        passation = Passation.builder()
                .id(1L)
                .employePartant(employe)
                .remplacant(remplacant)
                .manager(manager)
                .dateDepart(LocalDate.now().plusDays(30))
                .statut(StatutPassation.EN_COURS)
                .pourcentageGlobal(50.0)
                .build();
    }

    // ── getAll ────────────────────────────────────────────────────────────────

    @Test
    void getAll_returnsAllPassations() {
        when(passationRepository.findAll()).thenReturn(List.of(passation));
        when(passationProjetRepository.findByPassationId(1L)).thenReturn(List.of());

        List<PassationDTO> result = passationService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
    }

    // ── getById ───────────────────────────────────────────────────────────────

    @Test
    void getById_returnsDTO_whenFound() {
        when(passationRepository.findById(1L)).thenReturn(Optional.of(passation));
        when(passationProjetRepository.findByPassationId(1L)).thenReturn(List.of());

        PassationDTO result = passationService.getById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatut()).isEqualTo(StatutPassation.EN_COURS);
    }

    @Test
    void getById_throwsException_whenNotFound() {
        when(passationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> passationService.getById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Passation not found with id: 99");
    }

    // ── createPassation ───────────────────────────────────────────────────────

    @Test
    void createPassation_savesAndReturnsDTO() {
        CreatePassationRequest request = new CreatePassationRequest();
        request.setEmployePartantId(1L);
        request.setRemplacantId(2L);
        request.setManagerId(3L);
        request.setDateDepart(LocalDate.now().plusDays(30));
        request.setNotes("Test notes");

        when(userRepository.findById(1L)).thenReturn(Optional.of(employe));
        when(userRepository.findById(2L)).thenReturn(Optional.of(remplacant));
        when(userRepository.findById(3L)).thenReturn(Optional.of(manager));
        when(passationRepository.save(any(Passation.class))).thenReturn(passation);
        when(passationProjetRepository.findByPassationId(1L)).thenReturn(List.of());

        PassationDTO result = passationService.createPassation(request);

        assertThat(result).isNotNull();
        verify(passationRepository).save(any(Passation.class));
    }

    @Test
    void createPassation_throwsException_whenEmployeNotFound() {
        CreatePassationRequest request = new CreatePassationRequest();
        request.setEmployePartantId(99L);
        request.setRemplacantId(2L);
        request.setManagerId(3L);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> passationService.createPassation(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found: 99");
    }

    // ── changerStatut ─────────────────────────────────────────────────────────

    @Test
    void changerStatut_updatesStatut_andReturnsDTO() {
        when(passationRepository.findById(1L)).thenReturn(Optional.of(passation));
        when(passationRepository.save(any(Passation.class))).thenReturn(passation);
        when(passationProjetRepository.findByPassationId(1L)).thenReturn(List.of());

        PassationDTO result = passationService.changerStatut(1L, StatutPassation.TERMINEE);

        assertThat(result).isNotNull();
        assertThat(passation.getStatut()).isEqualTo(StatutPassation.TERMINEE);
    }

    @Test
    void changerStatut_throwsException_whenNotFound() {
        when(passationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> passationService.changerStatut(99L, StatutPassation.TERMINEE))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Passation not found: 99");
    }

    // ── recalculPourcentageGlobal ──────────────────────────────────────────────

    @Test
    void recalculPourcentageGlobal_setsAverageOfProjets() {
        PassationProjet pp1 = PassationProjet.builder().pourcentagePassation(60).build();
        PassationProjet pp2 = PassationProjet.builder().pourcentagePassation(40).build();

        when(passationProjetRepository.findByPassationId(1L)).thenReturn(List.of(pp1, pp2));
        when(passationRepository.findById(1L)).thenReturn(Optional.of(passation));
        when(passationRepository.save(any(Passation.class))).thenReturn(passation);

        passationService.recalculPourcentageGlobal(1L);

        assertThat(passation.getPourcentageGlobal()).isEqualTo(50.0);
        verify(passationRepository).save(passation);
    }

    @Test
    void recalculPourcentageGlobal_setsZero_whenNoProjets() {
        when(passationProjetRepository.findByPassationId(1L)).thenReturn(List.of());
        when(passationRepository.findById(1L)).thenReturn(Optional.of(passation));
        when(passationRepository.save(any(Passation.class))).thenReturn(passation);

        passationService.recalculPourcentageGlobal(1L);

        assertThat(passation.getPourcentageGlobal()).isEqualTo(0.0);
    }

    // ── getDashboardStats ─────────────────────────────────────────────────────

    @Test
    void getDashboardStats_returnsCorrectStats() {
        when(passationRepository.count()).thenReturn(5L);
        when(passationRepository.findByStatut(StatutPassation.EN_COURS)).thenReturn(List.of(passation));
        when(passationRepository.findByStatut(StatutPassation.TERMINEE)).thenReturn(List.of());
        when(passationRepository.findAll()).thenReturn(List.of(passation));
        when(projetRepository.count()).thenReturn(3L);
        when(projetRepository.findByStatut(StatutProjet.EN_COURS)).thenReturn(List.of());
        when(userRepository.count()).thenReturn(4L);
        when(alerteRepository.findByNiveauSeverite(NiveauSeverite.CRITIQUE)).thenReturn(List.of());
        when(alerteRepository.findByLuFalse()).thenReturn(List.of());

        DashboardStatsDTO stats = passationService.getDashboardStats();

        assertThat(stats.getTotalPassations()).isEqualTo(5L);
        assertThat(stats.getPassationsEnCours()).isEqualTo(1L);
        assertThat(stats.getPassationsTerminees()).isEqualTo(0L);
        assertThat(stats.getTotalUsers()).isEqualTo(4L);
        assertThat(stats.getPourcentageGlobalMoyen()).isEqualTo(50.0);
    }
}
