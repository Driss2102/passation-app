package com.passation.passation_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id")
    private User utilisateur;

    private String action;

    private String entiteConcernee;

    private Long entiteId;

    @Column(columnDefinition = "TEXT")
    private String ancienneValeur;

    @Column(columnDefinition = "TEXT")
    private String nouvelleValeur;

    @Column(name = "date_action", updatable = false)
    private LocalDateTime dateAction;

    private String ipAddress;

    @PrePersist
    protected void onCreate() {
        dateAction = LocalDateTime.now();
    }
}
