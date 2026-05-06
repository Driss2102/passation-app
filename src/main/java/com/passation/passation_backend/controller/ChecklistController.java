package com.passation.passation_backend.controller;

import com.passation.passation_backend.dto.ChecklistDTO;
import com.passation.passation_backend.dto.ChecklistItemDTO;
import com.passation.passation_backend.service.ChecklistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checklists")
@RequiredArgsConstructor
public class ChecklistController {

    private final ChecklistService checklistService;

    @GetMapping
    public ResponseEntity<List<ChecklistDTO>> getAllChecklists() {
        return ResponseEntity.ok(checklistService.findAllTemplates());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChecklistDTO> getChecklistById(@PathVariable Long id) {
        return ResponseEntity.ok(checklistService.findTemplateById(id));
    }

    @PostMapping
    public ResponseEntity<ChecklistDTO> createChecklist(@RequestBody ChecklistDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(checklistService.createTemplate(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChecklistDTO> updateChecklist(@PathVariable Long id,
                                                         @RequestBody ChecklistDTO dto) {
        return ResponseEntity.ok(checklistService.updateTemplate(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChecklist(@PathVariable Long id) {
        checklistService.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/items")
    public ResponseEntity<List<ChecklistItemDTO>> getItems(@PathVariable Long id) {
        return ResponseEntity.ok(checklistService.findItemsByTemplate(id));
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<ChecklistItemDTO> addItem(@PathVariable Long id,
                                                     @RequestBody ChecklistItemDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(checklistService.addItem(id, dto));
    }

    @DeleteMapping("/{id}/items/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id, @PathVariable Long itemId) {
        checklistService.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }
}
