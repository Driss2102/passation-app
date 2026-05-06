package com.passation.passation_backend.service;

import com.passation.passation_backend.dto.CommentaireDTO;
import com.passation.passation_backend.model.Commentaire;
import com.passation.passation_backend.model.Passation;
import com.passation.passation_backend.model.User;
import com.passation.passation_backend.repository.CommentaireRepository;
import com.passation.passation_backend.repository.PassationRepository;
import com.passation.passation_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentaireService {

    private final CommentaireRepository commentaireRepository;
    private final PassationRepository passationRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public List<CommentaireDTO> findByPassation(Long passationId) {
        return commentaireRepository.findByPassationId(passationId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentaireDTO add(Long passationId, Long auteurId, Long projetId, String contenu) {
        Passation passation = passationRepository.findById(passationId)
                .orElseThrow(() -> new RuntimeException("Passation not found with id: " + passationId));
        User auteur = userRepository.findById(auteurId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + auteurId));

        Commentaire commentaire = Commentaire.builder()
                .passation(passation)
                .auteur(auteur)
                .projetId(projetId)
                .contenu(contenu)
                .build();
        return toDTO(commentaireRepository.save(commentaire));
    }

    @Transactional
    public void delete(Long id) {
        if (!commentaireRepository.existsById(id)) {
            throw new RuntimeException("Commentaire not found with id: " + id);
        }
        commentaireRepository.deleteById(id);
    }

    private CommentaireDTO toDTO(Commentaire commentaire) {
        return CommentaireDTO.builder()
                .id(commentaire.getId())
                .passationId(commentaire.getPassation() != null ? commentaire.getPassation().getId() : null)
                .auteur(userService.toDTO(commentaire.getAuteur()))
                .projetId(commentaire.getProjetId())
                .contenu(commentaire.getContenu())
                .dateCreation(commentaire.getDateCreation())
                .build();
    }
}
