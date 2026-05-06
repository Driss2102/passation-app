package com.passation.passation_backend.dto;

import com.passation.passation_backend.model.Role;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String departement;
    private String poste;
    private Role role;
    private LocalDateTime dateCreation;
}
