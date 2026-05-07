package com.passation.passation_backend.controller;

import com.passation.passation_backend.dto.CreatePassationRequest;
import com.passation.passation_backend.dto.DashboardStatsDTO;
import com.passation.passation_backend.dto.PassationDTO;
import com.passation.passation_backend.dto.PassationProjetDTO;
import com.passation.passation_backend.model.NiveauMaitrise;
import com.passation.passation_backend.model.StatutPassation;
import com.passation.passation_backend.repository.PassationRepository;
import com.passation.passation_backend.service.PassationService;
import com.passation.passation_backend.service.RiskScoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/passations")
@RequiredArgsConstructor
public class PassationController {

    private final PassationService passationService;
    private final PassationRepository passationRepository;
    private final RiskScoreService riskScoreService;

    @GetMapping
    public ResponseEntity<List<PassationDTO>> getAllPassations() {
        return ResponseEntity.ok(passationService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PassationDTO> getPassationById(@PathVariable Long id) {
        return ResponseEntity.ok(passationService.getById(id));
    }

    @PostMapping
    public ResponseEntity<PassationDTO> createPassation(@Valid @RequestBody CreatePassationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(passationService.createPassation(request));
    }

    @PutMapping("/{id}/statut")
    public ResponseEntity<PassationDTO> changerStatut(@PathVariable Long id,
                                                       @RequestBody Map<String, String> body) {
        StatutPassation statut = StatutPassation.valueOf(body.get("statut"));
        return ResponseEntity.ok(passationService.changerStatut(id, statut));
    }

    @PostMapping("/{id}/projets")
    public ResponseEntity<PassationProjetDTO> lierProjet(@PathVariable Long id,
                                                          @RequestBody Map<String, Object> body) {
        Long projetId = Long.valueOf(body.get("projetId").toString());
        Integer pourcentagePassation = body.get("pourcentagePassation") != null
                ? Integer.valueOf(body.get("pourcentagePassation").toString()) : null;
        NiveauMaitrise niveauMaitrise = body.get("niveauMaitrise") != null
                ? NiveauMaitrise.valueOf(body.get("niveauMaitrise").toString()) : null;
        String sujetsEnCours = (String) body.get("sujetsEnCours");
        String tachesRestantes = (String) body.get("tachesRestantes");
        String contactsCles = (String) body.get("contactsCles");
        String documents = (String) body.get("documents");
        String risques = (String) body.get("risques");

        PassationProjetDTO result = passationService.lierProjet(id, projetId, pourcentagePassation,
                niveauMaitrise, sujetsEnCours, tachesRestantes, contactsCles, documents, risques);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/stats/dashboard")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        return ResponseEntity.ok(passationService.getDashboardStats());
    }

    @GetMapping("/{id}/risk-score")
    public ResponseEntity<Map<String, Object>> getRiskScore(@PathVariable Long id) {
        return ResponseEntity.ok(riskScoreService.getRiskScoreDetails(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<PassationDTO>> searchPassations(
            @RequestParam(required = false) String nomEmploye,
            @RequestParam(required = false) String departement,
            @RequestParam(required = false) StatutPassation statut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateMin,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateMax,
            @RequestParam(required = false) String scoreRisque) {

        List<PassationDTO> results = passationRepository
                .searchPassations(nomEmploye, departement, statut, dateMin, dateMax)
                .stream()
                .map(passationService::toDTO)
                .collect(Collectors.toList());

        if (scoreRisque != null && !scoreRisque.isBlank()) {
            results = results.stream()
                    .filter(dto -> scoreRisque.equalsIgnoreCase(riskScoreService.calculateRiskScore(dto.getId())))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(results);
    }
}
