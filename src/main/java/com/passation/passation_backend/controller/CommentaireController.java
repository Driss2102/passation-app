package com.passation.passation_backend.controller;

import com.passation.passation_backend.dto.CommentaireDTO;
import com.passation.passation_backend.service.CommentaireService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CommentaireController {

    private final CommentaireService commentaireService;

    @GetMapping("/api/passations/{id}/commentaires")
    public ResponseEntity<List<CommentaireDTO>> getCommentaires(@PathVariable Long id) {
        return ResponseEntity.ok(commentaireService.findByPassation(id));
    }

    @PostMapping("/api/passations/{id}/commentaires")
    public ResponseEntity<CommentaireDTO> addCommentaire(@PathVariable Long id,
                                                          @RequestBody Map<String, Object> body) {
        Long auteurId = Long.valueOf(body.get("auteurId").toString());
        Long projetId = body.get("projetId") != null ? Long.valueOf(body.get("projetId").toString()) : null;
        String contenu = (String) body.get("contenu");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentaireService.add(id, auteurId, projetId, contenu));
    }

    @DeleteMapping("/api/commentaires/{id}")
    public ResponseEntity<Void> deleteCommentaire(@PathVariable Long id) {
        commentaireService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
