package com.passation.passation_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "checklist_templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChecklistTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nom;

    @Column(name = "type_poste")
    private String typePoste;

    @Column(columnDefinition = "TEXT")
    private String description;
}
