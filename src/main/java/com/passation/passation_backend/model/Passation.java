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
    @JoinColumn(name = "employe_partant_id")
    private User employePartant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "remplacant_id")
    private User remplacant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User manager;

    private LocalDate dateDepart;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutPassation statut;

    private Double pourcentageGlobal;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
    }
}
