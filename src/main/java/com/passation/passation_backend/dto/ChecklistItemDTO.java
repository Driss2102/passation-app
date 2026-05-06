package com.passation.passation_backend.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChecklistItemDTO {
    private Long id;
    private String libelle;
    private Boolean obligatoire;
    private Integer ordre;
}
