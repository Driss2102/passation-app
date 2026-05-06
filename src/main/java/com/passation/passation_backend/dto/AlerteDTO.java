package com.passation.passation_backend.dto;

import com.passation.passation_backend.model.NiveauSeverite;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlerteDTO {
    private Long id;
    private Long passationId;
    private String type;
    private String message;
    private NiveauSeverite niveauSeverite;
    private LocalDateTime dateCreation;
    private Boolean lu;
}
