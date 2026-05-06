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
    @JoinColumn(name = "passation_id")
    private Passation passation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projet_id")
    private Projet projet;

    private Integer pourcentagePassation;

    @Enumerated(EnumType.STRING)
    private NiveauMaitrise niveauMaitrise;

    @Column(columnDefinition = "TEXT")
    private String sujetsEnCours;

    @Column(columnDefinition = "TEXT")
    private String tachesRestantes;

    @Column(columnDefinition = "TEXT")
    private String contactsCles;

    @Column(columnDefinition = "TEXT")
    private String documents;

    @Column(columnDefinition = "TEXT")
    private String risques;
}
