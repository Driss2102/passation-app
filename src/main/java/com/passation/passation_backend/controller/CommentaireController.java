package com.passation.passation_backend.controller;

import com.passation.passation_backend.dto.CommentaireDTO;
import com.passation.passation_backend.model.User;
import com.passation.passation_backend.repository.UserRepository;
import com.passation.passation_backend.service.CommentaireService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CommentaireController {

    private final CommentaireService commentaireService;
    private final UserRepository userRepository;

    @GetMapping("/api/passations/{id}/commentaires")
    public ResponseEntity<List<CommentaireDTO>> getCommentaires(@PathVariable Long id) {
        return ResponseEntity.ok(commentaireService.findByPassation(id));
    }

    @PostMapping("/api/passations/{id}/commentaires")
    public ResponseEntity<CommentaireDTO> addCommentaire(@PathVariable Long id,
                                                          @RequestBody Map<String, Object> body) {
        // Resolve auteur from JWT (email in Security context) or fallback to body auteurId
        Long auteurId = resolveAuteurId(body);
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

    private Long resolveAuteurId(Map<String, Object> body) {
        // Try to resolve from Spring Security context (JWT email)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null) {
            User user = userRepository.findByEmail(auth.getName()).orElse(null);
            if (user != null) return user.getId();
        }
        // Fallback: read auteurId from body
        if (body.get("auteurId") != null) {
            return Long.valueOf(body.get("auteurId").toString());
        }
        throw new RuntimeException("Cannot resolve auteur: no authenticated user and no auteurId in body");
    }
}
