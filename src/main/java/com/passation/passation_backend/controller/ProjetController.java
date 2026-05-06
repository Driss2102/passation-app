package com.passation.passation_backend.controller;

import com.passation.passation_backend.dto.CreateProjetRequest;
import com.passation.passation_backend.dto.ProjetDTO;
import com.passation.passation_backend.service.ProjetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projets")
@RequiredArgsConstructor
public class ProjetController {

    private final ProjetService projetService;

    @GetMapping
    public ResponseEntity<List<ProjetDTO>> getAllProjets() {
        return ResponseEntity.ok(projetService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjetDTO> getProjetById(@PathVariable Long id) {
        return ResponseEntity.ok(projetService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProjetDTO> createProjet(@Valid @RequestBody CreateProjetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projetService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjetDTO> updateProjet(@PathVariable Long id,
                                                   @Valid @RequestBody CreateProjetRequest request) {
        return ResponseEntity.ok(projetService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjet(@PathVariable Long id) {
        projetService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
