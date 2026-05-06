package com.passation.passation_backend.service;

import com.passation.passation_backend.model.*;
import com.passation.passation_backend.repository.PassationProjetRepository;
import com.passation.passation_backend.repository.PassationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RiskScoreService {

    private final PassationRepository passationRepository;
    private final PassationProjetRepository passationProjetRepository;

    public String calculateRiskScore(Long passationId) {
        int score = computeScore(passationId);
        return toNiveau(score);
    }

    public Map<String, Object> getRiskScoreDetails(Long passationId) {
        Passation passation = passationRepository.findById(passationId)
                .orElseThrow(() -> new RuntimeException("Passation not found: " + passationId));

        List<PassationProjet> pps = passationProjetRepository.findByPassationId(passationId);

        long joursAvantDepart = passation.getDateDepart() != null
                ? ChronoUnit.DAYS.between(LocalDate.now(), passation.getDateDepart())
                : Long.MAX_VALUE;

        double pourcentageGlobal = passation.getPourcentageGlobal() != null ? passation.getPourcentageGlobal() : 0.0;
        double pourcentageRestant = 100.0 - pourcentageGlobal;

        long nombreProjetsACritiques = pps.stream()
                .filter(pp -> pp.getProjet() != null
                        && pp.getProjet().getStatut() == StatutProjet.EN_COURS
                        && pp.getProjet().getPourcentageAvancement() != null
                        && pp.getProjet().getPourcentageAvancement() < 50)
                .count();

        int score = computeScoreInternal(joursAvantDepart, pourcentageRestant, pps);

        Map<String, Object> details = new HashMap<>();
        details.put("score", score);
        details.put("niveau", toNiveau(score));
        details.put("joursAvantDepart", joursAvantDepart);
        details.put("pourcentageRestant", pourcentageRestant);
        details.put("nombreProjetsACritiques", nombreProjetsACritiques);
        return details;
    }

    private int computeScore(Long passationId) {
        Passation passation = passationRepository.findById(passationId)
                .orElseThrow(() -> new RuntimeException("Passation not found: " + passationId));
        List<PassationProjet> pps = passationProjetRepository.findByPassationId(passationId);

        long joursAvantDepart = passation.getDateDepart() != null
                ? ChronoUnit.DAYS.between(LocalDate.now(), passation.getDateDepart())
                : Long.MAX_VALUE;

        double pourcentageGlobal = passation.getPourcentageGlobal() != null ? passation.getPourcentageGlobal() : 0.0;
        double pourcentageRestant = 100.0 - pourcentageGlobal;

        return computeScoreInternal(joursAvantDepart, pourcentageRestant, pps);
    }

    private int computeScoreInternal(long joursAvantDepart, double pourcentageRestant, List<PassationProjet> pps) {
        int score = 0;

        // Jours avant départ scoring
        if (joursAvantDepart < 7) {
            score += 3;
        } else if (joursAvantDepart < 15) {
            score += 2;
        } else if (joursAvantDepart < 30) {
            score += 1;
        }

        // Pourcentage restant scoring
        if (pourcentageRestant > 70) {
            score += 3;
        } else if (pourcentageRestant > 40) {
            score += 2;
        } else if (pourcentageRestant > 20) {
            score += 1;
        }

        // Niveau maîtrise scoring
        for (PassationProjet pp : pps) {
            if (pp.getNiveauMaitrise() == NiveauMaitrise.FAIBLE) {
                score += 2;
            } else if (pp.getNiveauMaitrise() == NiveauMaitrise.MOYEN) {
                score += 1;
            }
        }

        // Criticité projets
        for (PassationProjet pp : pps) {
            if (pp.getProjet() != null
                    && pp.getProjet().getStatut() == StatutProjet.EN_COURS
                    && pp.getProjet().getPourcentageAvancement() != null
                    && pp.getProjet().getPourcentageAvancement() < 50) {
                score += 1;
            }
        }

        return score;
    }

    private String toNiveau(int score) {
        if (score >= 6) return "CRITIQUE";
        if (score >= 3) return "MODERE";
        return "FAIBLE";
    }
}
