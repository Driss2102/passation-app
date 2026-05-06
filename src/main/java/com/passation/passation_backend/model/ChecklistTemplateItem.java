package com.passation.passation_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "checklist_template_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChecklistTemplateItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private ChecklistTemplate template;

    @NotBlank
    private String libelle;

    @Column(nullable = false)
    private Boolean obligatoire = false;

    @Column(nullable = false)
    private Integer ordre = 0;
}
