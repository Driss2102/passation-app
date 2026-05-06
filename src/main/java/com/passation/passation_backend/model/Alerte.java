package com.passation.passation_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "alertes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alerte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passation_id", nullable = false)
    private Passation passation;

    private String type;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "niveau_severite", nullable = false)
    private NiveauSeverite niveauSeverite;

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @Column(nullable = false)
    private Boolean lu = false;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        if (lu == null) lu = false;
        if (niveauSeverite == null) niveauSeverite = NiveauSeverite.INFO;
    }
}
