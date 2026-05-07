package com.passation.passation_backend.controller;

import com.passation.passation_backend.dto.AlerteDTO;
import com.passation.passation_backend.service.AlerteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alertes")
@RequiredArgsConstructor
public class AlerteController {

    private final AlerteService alerteService;

    @GetMapping
    public ResponseEntity<List<AlerteDTO>> getAll() {
        return ResponseEntity.ok(alerteService.findAll());
    }

    @GetMapping("/non-lues")
    public ResponseEntity<List<AlerteDTO>> getNonLues() {
        return ResponseEntity.ok(alerteService.findNonLues());
    }

    @GetMapping("/passation/{passationId}")
    public ResponseEntity<List<AlerteDTO>> getByPassation(@PathVariable Long passationId) {
        return ResponseEntity.ok(alerteService.findByPassation(passationId));
    }

    @PutMapping("/{id}/lu")
    public ResponseEntity<AlerteDTO> marquerLue(@PathVariable Long id) {
        return ResponseEntity.ok(alerteService.marquerLue(id));
    }

    @PostMapping("/generate/{passationId}")
    public ResponseEntity<List<AlerteDTO>> generateAlertes(@PathVariable Long passationId) {
        return ResponseEntity.ok(alerteService.generateAlertesForPassation(passationId));
    }
}
