package com.passation.passation_backend.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentaireDTO {
    private Long id;
    private Long passationId;
    private UserDTO auteur;
    private Long projetId;
    private String contenu;
    private LocalDateTime dateCreation;
}
