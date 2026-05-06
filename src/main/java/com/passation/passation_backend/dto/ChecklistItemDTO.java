package com.passation.passation_backend.dto;

import lombok.Data;

@Data
public class ChecklistItemDTO {
    private Long id;
    private String libelle;
    private Boolean obligatoire;
    private Integer ordre;
}
