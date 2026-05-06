package com.passation.passation_backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreatePassationRequest {
    @NotNull
    private Long employePartantId;
    private Long remplacantId;
    private Long managerId;
    private LocalDate dateDepart;
    private String notes;
}
