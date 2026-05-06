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
    @JoinColumn(name = "passation_id")
    private Passation passation;

    private String type;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    private NiveauSeverite niveauSeverite;

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    private Boolean lu;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        if (lu == null) lu = false;
    }
}
