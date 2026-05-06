package com.passation.passation_backend.dto;

import com.passation.passation_backend.model.StatutEtape;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TimelineDTO {
    private Long id;
    private Long passationId;
    private String titre;
    private String description;
    private LocalDate datePrevu;
    private LocalDate dateReelle;
    private StatutEtape statut;
    private Integer ordre;
}
