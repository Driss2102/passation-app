package com.passation.passation_backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePassationRequest {

    @NotNull
    private Long employePartantId;

    @NotNull
    private Long remplacantId;

    @NotNull
    private Long managerId;

    private LocalDate dateDepart;

    private String notes;
}
