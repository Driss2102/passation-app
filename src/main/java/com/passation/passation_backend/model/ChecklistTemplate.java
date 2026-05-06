package com.passation.passation_backend.model;

import jakarta.persistence.*;
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

    private String nom;

    private String typePoste;

    @Column(columnDefinition = "TEXT")
    private String description;
}
