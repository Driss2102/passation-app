package com.passation.passation_backend.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChecklistDTO {
    private Long id;
    private String nom;
    private String typePoste;
    private String description;
    private List<ChecklistItemDTO> items;
}
