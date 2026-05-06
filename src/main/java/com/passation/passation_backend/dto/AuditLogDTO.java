package com.passation.passation_backend.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogDTO {
    private Long id;
    private UserDTO utilisateur;
    private String action;
    private String entiteConcernee;
    private Long entiteId;
    private String ancienneValeur;
    private String nouvelleValeur;
    private LocalDateTime dateAction;
    private String ipAddress;
}
