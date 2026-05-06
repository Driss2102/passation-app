package com.passation.passation_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "passation_projets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassationProjet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passation_id", nullable = false)
    private Passation passation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projet_id", nullable = false)
    private Projet projet;

    @Column(name = "pourcentage_passation")
    private Integer pourcentagePassation;

    @Enumerated(EnumType.STRING)
    @Column(name = "niveau_maitrise")
    private NiveauMaitrise niveauMaitrise;

    @Column(name = "sujets_en_cours", columnDefinition = "TEXT")
    private String sujetsEnCours;

    @Column(name = "taches_restantes", columnDefinition = "TEXT")
    private String tachesRestantes;

    @Column(name = "contacts_cles", columnDefinition = "TEXT")
    private String contactsCles;

    @Column(columnDefinition = "TEXT")
    private String documents;

    @Column(columnDefinition = "TEXT")
    private String risques;

    @PrePersist
    protected void onCreate() {
        if (pourcentagePassation == null) pourcentagePassation = 0;
    }
}
