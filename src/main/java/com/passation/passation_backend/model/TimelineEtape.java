package com.passation.passation_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "timeline_etapes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimelineEtape {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passation_id", nullable = false)
    private Passation passation;

    @NotBlank
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "date_prevu")
    private LocalDate datePrevu;

    @Column(name = "date_reelle")
    private LocalDate dateReelle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutEtape statut;

    @Column(nullable = false)
    private Integer ordre = 0;

    @PrePersist
    protected void onCreate() {
        if (statut == null) statut = StatutEtape.A_FAIRE;
    }
}
