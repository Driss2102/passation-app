package com.passation.passation_backend.dto;

import com.passation.passation_backend.model.StatutPassation;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassationDTO {
    private Long id;
    private UserDTO employePartant;
    private UserDTO remplacant;
    private UserDTO manager;
    private LocalDate dateDepart;
    private StatutPassation statut;
    private Double pourcentageGlobal;
    private String notes;
    private LocalDateTime dateCreation;
}
