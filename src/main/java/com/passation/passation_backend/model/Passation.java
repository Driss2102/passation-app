package com.passation.passation_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "passations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Passation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employe_partant_id", nullable = false)
    private User employePartant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "remplacant_id")
    private User remplacant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User manager;

    @Column(name = "date_depart")
    private LocalDate dateDepart;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutPassation statut;

    @Column(name = "pourcentage_global")
    private Integer pourcentageGlobal;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        if (pourcentageGlobal == null) pourcentageGlobal = 0;
        if (statut == null) statut = StatutPassation.PLANIFIEE;
    }
}
