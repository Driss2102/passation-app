package com.passation.passation_backend.model;

import jakarta.persistence.*;
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
    @JoinColumn(name = "passation_id")
    private Passation passation;

    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDate datePrevu;

    private LocalDate dateReelle;

    @Enumerated(EnumType.STRING)
    private StatutEtape statut;

    private Integer ordre;
}
