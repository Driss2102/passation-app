package com.passation.passation_backend.dto;

import com.passation.passation_backend.model.NiveauMaitrise;
import lombok.Data;

@Data
public class PassationProjetDTO {
    private Long id;
    private Long passationId;
    private ProjetDTO projet;
    private Integer pourcentagePassation;
    private NiveauMaitrise niveauMaitrise;
    private String sujetsEnCours;
    private String tachesRestantes;
    private String contactsCles;
    private String documents;
    private String risques;
}
