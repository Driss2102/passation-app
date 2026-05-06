package com.passation.passation_backend.service;

import com.passation.passation_backend.dto.AuditLogDTO;
import com.passation.passation_backend.model.AuditLog;
import com.passation.passation_backend.model.User;
import com.passation.passation_backend.repository.AuditLogRepository;
import com.passation.passation_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional
    public AuditLogDTO log(Long userId, String action, String entiteConcernee, Long entiteId,
                            String ancienneValeur, String nouvelleValeur, String ipAddress) {
        User utilisateur = null;
        if (userId != null) {
            utilisateur = userRepository.findById(userId).orElse(null);
        }
        AuditLog auditLog = AuditLog.builder()
                .utilisateur(utilisateur)
                .action(action)
                .entiteConcernee(entiteConcernee)
                .entiteId(entiteId)
                .ancienneValeur(ancienneValeur)
                .nouvelleValeur(nouvelleValeur)
                .ipAddress(ipAddress)
                .build();
        return toDTO(auditLogRepository.save(auditLog));
    }

    public List<AuditLogDTO> findByUser(Long userId) {
        return auditLogRepository.findByUtilisateurIdOrderByDateActionDesc(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<AuditLogDTO> findByEntity(String entiteConcernee, Long entiteId) {
        return auditLogRepository.findByEntiteConcerneeAndEntiteId(entiteConcernee, entiteId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private AuditLogDTO toDTO(AuditLog auditLog) {
        return AuditLogDTO.builder()
                .id(auditLog.getId())
                .utilisateur(userService.toDTO(auditLog.getUtilisateur()))
                .action(auditLog.getAction())
                .entiteConcernee(auditLog.getEntiteConcernee())
                .entiteId(auditLog.getEntiteId())
                .ancienneValeur(auditLog.getAncienneValeur())
                .nouvelleValeur(auditLog.getNouvelleValeur())
                .dateAction(auditLog.getDateAction())
                .ipAddress(auditLog.getIpAddress())
                .build();
    }
}
