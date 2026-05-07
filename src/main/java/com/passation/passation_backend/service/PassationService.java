package com.passation.passation_backend.service;

import com.passation.passation_backend.dto.*;
import com.passation.passation_backend.model.*;
import com.passation.passation_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PassationService {

    private final PassationRepository passationRepository;
    private final PassationProjetRepository passationProjetRepository;
    private final UserRepository userRepository;
    private final ProjetRepository projetRepository;
    private final AlerteRepository alerteRepository;
    private final UserService userService;
    private final ProjetService projetService;

    public List<PassationDTO> getAll() {
        return passationRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public PassationDTO getById(Long id) {
        Passation passation = passationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Passation not found with id: " + id));
        return toDTO(passation);
    }

    @Transactional
    public PassationDTO createPassation(CreatePassationRequest request) {
        User employePartant = userRepository.findById(request.getEmployePartantId())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getEmployePartantId()));
        User remplacant = userRepository.findById(request.getRemplacantId())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getRemplacantId()));
        User manager = userRepository.findById(request.getManagerId())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getManagerId()));

        Passation passation = Passation.builder()
                .employePartant(employePartant)
                .remplacant(remplacant)
                .manager(manager)
                .dateDepart(request.getDateDepart())
                .statut(StatutPassation.PLANIFIEE)
                .pourcentageGlobal(0.0)
                .notes(request.getNotes())
                .build();
        return toDTO(passationRepository.save(passation));
    }

    @Transactional
    public PassationProjetDTO lierProjet(Long passationId, Long projetId, Integer pourcentagePassation,
                                         NiveauMaitrise niveauMaitrise, String sujetsEnCours,
                                         String tachesRestantes, String contactsCles,
                                         String documents, String risques) {
        Passation passation = passationRepository.findById(passationId)
                .orElseThrow(() -> new RuntimeException("Passation not found: " + passationId));
        Projet projet = projetRepository.findById(projetId)
                .orElseThrow(() -> new RuntimeException("Projet not found: " + projetId));

        PassationProjet pp = PassationProjet.builder()
                .passation(passation)
                .projet(projet)
                .pourcentagePassation(pourcentagePassation)
                .niveauMaitrise(niveauMaitrise)
                .sujetsEnCours(sujetsEnCours)
                .tachesRestantes(tachesRestantes)
                .contactsCles(contactsCles)
                .documents(documents)
                .risques(risques)
                .build();

        PassationProjet saved = passationProjetRepository.save(pp);
        recalculPourcentageGlobal(passationId);
        return toPassationProjetDTO(saved);
    }

    @Transactional
    public void recalculPourcentageGlobal(Long passationId) {
        List<PassationProjet> pps = passationProjetRepository.findByPassationId(passationId);
        OptionalDouble avg = pps.stream()
                .filter(pp -> pp.getPourcentagePassation() != null)
                .mapToInt(PassationProjet::getPourcentagePassation)
                .average();

        Passation passation = passationRepository.findById(passationId)
                .orElseThrow(() -> new RuntimeException("Passation not found: " + passationId));
        passation.setPourcentageGlobal(avg.isPresent() ? avg.getAsDouble() : 0.0);
        passationRepository.save(passation);
    }

    @Transactional
    public PassationDTO changerStatut(Long id, StatutPassation statut) {
        Passation passation = passationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Passation not found: " + id));
        passation.setStatut(statut);
        return toDTO(passationRepository.save(passation));
    }

    public DashboardStatsDTO getDashboardStats() {
        long totalPassations = passationRepository.count();
        long passationsEnCours = passationRepository.findByStatut(StatutPassation.EN_COURS).size();
        long passationsTerminees = passationRepository.findByStatut(StatutPassation.TERMINEE).size();
        long totalProjets = projetRepository.count();
        long projetsEnCours = projetRepository.findByStatut(StatutProjet.EN_COURS).size();
        long totalUsers = userRepository.count();
        long alertesCritiques = alerteRepository.findByNiveauSeverite(NiveauSeverite.CRITIQUE).size();
        long alertesNonLues = alerteRepository.findByLuFalse().size();

        OptionalDouble moyenneGlobale = passationRepository.findAll().stream()
                .filter(p -> p.getPourcentageGlobal() != null)
                .mapToDouble(Passation::getPourcentageGlobal)
                .average();

        return DashboardStatsDTO.builder()
                .totalPassations(totalPassations)
                .passationsEnCours(passationsEnCours)
                .passationsTerminees(passationsTerminees)
                .totalProjets(totalProjets)
                .projetsEnCours(projetsEnCours)
                .totalUsers(totalUsers)
                .alertesCritiques(alertesCritiques)
                .alertesNonLues(alertesNonLues)
                .pourcentageGlobalMoyen(moyenneGlobale.isPresent() ? moyenneGlobale.getAsDouble() : 0.0)
                .build();
    }

    public PassationDTO toDTO(Passation passation) {
        if (passation == null) return null;
        List<PassationProjetDTO> projets = passationProjetRepository.findByPassationId(passation.getId())
                .stream()
                .map(this::toPassationProjetDTO)
                .collect(Collectors.toList());
        return PassationDTO.builder()
                .id(passation.getId())
                .employePartant(userService.toDTO(passation.getEmployePartant()))
                .remplacant(userService.toDTO(passation.getRemplacant()))
                .manager(userService.toDTO(passation.getManager()))
                .dateDepart(passation.getDateDepart())
                .statut(passation.getStatut())
                .pourcentageGlobal(passation.getPourcentageGlobal())
                .notes(passation.getNotes())
                .dateCreation(passation.getDateCreation())
                .passationProjets(projets)
                .build();
    }

    private PassationProjetDTO toPassationProjetDTO(PassationProjet pp) {
        return PassationProjetDTO.builder()
                .id(pp.getId())
                .passationId(pp.getPassation() != null ? pp.getPassation().getId() : null)
                .projet(projetService.toDTO(pp.getProjet()))
                .pourcentagePassation(pp.getPourcentagePassation())
                .niveauMaitrise(pp.getNiveauMaitrise())
                .sujetsEnCours(pp.getSujetsEnCours())
                .tachesRestantes(pp.getTachesRestantes())
                .contactsCles(pp.getContactsCles())
                .documents(pp.getDocuments())
                .risques(pp.getRisques())
                .build();
    }
}
