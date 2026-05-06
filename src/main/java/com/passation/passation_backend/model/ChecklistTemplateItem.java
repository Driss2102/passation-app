package com.passation.passation_backend.model;

import jakarta.persistence.*;
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
    @JoinColumn(name = "template_id")
    private ChecklistTemplate template;

    private String libelle;

    private Boolean obligatoire;

    private Integer ordre;
}
