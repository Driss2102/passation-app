package com.passation.passation_backend.service;

import com.passation.passation_backend.model.*;
import com.passation.passation_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlerteService {

    private final AlerteRepository alerteRepository;
    private final PassationRepository passationRepository;
    private final PassationProjetRepository passationProjetRepository;
    private final TimelineEtapeRepository timelineEtapeRepository;

    public List<Alerte> findAll() {
        return alerteRepository.findAll();
    }

    public List<Alerte> findNonLues() {
        return alerteRepository.findByLuFalse();
    }

    @Transactional
    public Alerte marquerLue(Long alerteId) {
        Alerte alerte = alerteRepository.findById(alerteId)
                .orElseThrow(() -> new RuntimeException("Alerte not found: " + alerteId));
        alerte.setLu(true);
        return alerteRepository.save(alerte);
    }

    public List<Alerte> findByPassation(Long passationId) {
        return alerteRepository.findByPassationId(passationId);
    }

    @Transactional
    public List<Alerte> generateAlertesForPassation(Long passationId) {
        Passation passation = passationRepository.findById(passationId)
                .orElseThrow(() -> new RuntimeException("Passation not found: " + passationId));

        List<Alerte> existing = alerteRepository.findByPassationId(passationId);
        Set<String> existingTypes = existing.stream()
                .map(Alerte::getType)
                .collect(Collectors.toSet());

        List<PassationProjet> pps = passationProjetRepository.findByPassationId(passationId);

        long joursAvantDepart = passation.getDateDepart() != null
                ? ChronoUnit.DAYS.between(LocalDate.now(), passation.getDateDepart())
                : Long.MAX_VALUE;

        double pourcentageGlobal = passation.getPourcentageGlobal() != null ? passation.getPourcentageGlobal() : 0.0;

        List<Alerte> nouvelles = new ArrayList<>();

        // Rule 1: < 30% done AND <= 15 days left → CRITIQUE
        if (!existingTypes.contains("RETARD_CRITIQUE")
                && pourcentageGlobal < 30
                && joursAvantDepart <= 15) {
            nouvelles.add(buildAlerte(passation, "RETARD_CRITIQUE",
                    "Passation critique: moins de 30% réalisée à J-15",
                    NiveauSeverite.CRITIQUE));
        }

        // Rule 2: PassationProjet with FAIBLE maîtrise and EN_COURS project → CRITIQUE per project
        for (PassationProjet pp : pps) {
            if (pp.getNiveauMaitrise() == NiveauMaitrise.FAIBLE
                    && pp.getProjet() != null
                    && pp.getProjet().getStatut() == StatutProjet.EN_COURS) {
                String type = "REMPLACANT_INSUFFISANT";
                String message = "Remplaçant insuffisant sur projet en cours: " + pp.getProjet().getNom();
                // Check by type+message to avoid strict duplicate per project
                boolean alreadyExists = existing.stream()
                        .anyMatch(a -> type.equals(a.getType()) && message.equals(a.getMessage()));
                if (!alreadyExists) {
                    nouvelles.add(buildAlerte(passation, type, message, NiveauSeverite.CRITIQUE));
                }
            }
        }

        // Rule 3: passation not TERMINEE and <= 7 days → WARNING
        if (!existingTypes.contains("VALIDATION_MANQUANTE")
                && passation.getStatut() != StatutPassation.TERMINEE
                && joursAvantDepart <= 7) {
            nouvelles.add(buildAlerte(passation, "VALIDATION_MANQUANTE",
                    "Passation non validée à J-7",
                    NiveauSeverite.WARNING));
        }

        // Rule 4: checklist < 50% done and <= 10 days → WARNING
        if (!existingTypes.contains("CHECKLIST_INCOMPLETE") && joursAvantDepart <= 10) {
            List<TimelineEtape> etapes = timelineEtapeRepository.findByPassationIdOrderByOrdreAsc(passationId);
            if (!etapes.isEmpty()) {
                long total = etapes.size();
                long terminees = etapes.stream()
                        .filter(e -> e.getStatut() == StatutEtape.TERMINE)
                        .count();
                double pctDone = (double) terminees / total * 100.0;
                if (pctDone < 50) {
                    nouvelles.add(buildAlerte(passation, "CHECKLIST_INCOMPLETE",
                            "Checklist incomplète à J-10",
                            NiveauSeverite.WARNING));
                }
            }
        }

        // Rule 5: no remplaçant → INFO
        if (!existingTypes.contains("SANS_REMPLACANT") && passation.getRemplacant() == null) {
            nouvelles.add(buildAlerte(passation, "SANS_REMPLACANT",
                    "Départ sans remplaçant assigné",
                    NiveauSeverite.INFO));
        }

        if (!nouvelles.isEmpty()) {
            alerteRepository.saveAll(nouvelles);
        }
        return nouvelles;
    }

    private Alerte buildAlerte(Passation passation, String type, String message, NiveauSeverite severite) {
        return Alerte.builder()
                .passation(passation)
                .type(type)
                .message(message)
                .niveauSeverite(severite)
                .lu(false)
                .build();
    }
}
