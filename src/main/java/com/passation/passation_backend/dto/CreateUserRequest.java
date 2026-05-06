package com.passation.passation_backend.dto;

import com.passation.passation_backend.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateUserRequest {
    @NotBlank
    private String nom;
    @NotBlank
    private String prenom;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    private String departement;
    private String poste;
    private Role role;
}
