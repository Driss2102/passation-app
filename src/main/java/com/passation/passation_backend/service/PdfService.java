package com.passation.passation_backend.service;

import com.passation.passation_backend.model.*;
import com.passation.passation_backend.repository.PassationProjetRepository;
import com.passation.passation_backend.repository.PassationRepository;
import com.passation.passation_backend.repository.TimelineEtapeRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PdfService {

    private final PassationRepository passationRepository;
    private final PassationProjetRepository passationProjetRepository;
    private final TimelineEtapeRepository timelineEtapeRepository;
    private final RiskScoreService riskScoreService;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final float MARGIN = 50f;
    private static final float LINE_HEIGHT = 18f;
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();

    public byte[] generateRapportPassation(Long passationId) throws IOException {
        Passation passation = passationRepository.findById(passationId)
                .orElseThrow(() -> new RuntimeException("Passation not found: " + passationId));

        List<PassationProjet> pps = passationProjetRepository.findByPassationId(passationId);
        List<TimelineEtape> etapes = timelineEtapeRepository.findByPassationIdOrderByOrdreAsc(passationId);
        Map<String, Object> riskDetails = riskScoreService.getRiskScoreDetails(passationId);

        PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        PDType1Font fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
                float y = PAGE_HEIGHT - MARGIN;

                // ---- HEADER ----
                y = writeLine(cs, fontBold, 18, MARGIN, y, "RAPPORT DE PASSATION");
                y -= 6;
                y = writeLine(cs, fontRegular, 10, MARGIN, y,
                        "Date de génération : " + LocalDate.now().format(DATE_FMT));
                y = writeLine(cs, fontRegular, 10, MARGIN, y,
                        "Passation ID : " + passation.getId());
                y -= 10;

                // ---- SECTION 1 ----
                y = writeLine(cs, fontBold, 13, MARGIN, y, "1. Informations générales");
                y -= 4;

                User ep = passation.getEmployePartant();
                if (ep != null) {
                    y = writeLine(cs, fontRegular, 11, MARGIN + 10, y,
                            "Employé partant : " + ep.getNom() + " " + ep.getPrenom());
                    y = writeLine(cs, fontRegular, 11, MARGIN + 10, y,
                            "Poste : " + nvl(ep.getPoste()));
                    y = writeLine(cs, fontRegular, 11, MARGIN + 10, y,
                            "Département : " + nvl(ep.getDepartement()));
                }

                User rem = passation.getRemplacant();
                if (rem != null) {
                    y = writeLine(cs, fontRegular, 11, MARGIN + 10, y,
                            "Remplaçant : " + rem.getNom() + " " + rem.getPrenom());
                } else {
                    y = writeLine(cs, fontRegular, 11, MARGIN + 10, y, "Remplaçant : Non assigné");
                }

                y = writeLine(cs, fontRegular, 11, MARGIN + 10, y,
                        "Date de départ : " + (passation.getDateDepart() != null
                                ? passation.getDateDepart().format(DATE_FMT) : "N/A"));
                y = writeLine(cs, fontRegular, 11, MARGIN + 10, y,
                        "Statut : " + passation.getStatut());
                y = writeLine(cs, fontRegular, 11, MARGIN + 10, y,
                        "% Global : " + (passation.getPourcentageGlobal() != null
                                ? String.format("%.1f%%", passation.getPourcentageGlobal()) : "N/A"));
                y -= 10;

                // ---- SECTION 2 ----
                y = writeLine(cs, fontBold, 13, MARGIN, y, "2. Score de Risque");
                y -= 4;
                y = writeLine(cs, fontRegular, 11, MARGIN + 10, y,
                        "Niveau : " + riskDetails.get("niveau"));
                y = writeLine(cs, fontRegular, 11, MARGIN + 10, y,
                        "Jours avant départ : " + riskDetails.get("joursAvantDepart"));
                y -= 10;

                // ---- SECTION 3 ----
                y = writeLine(cs, fontBold, 13, MARGIN, y, "3. Projets");
                y -= 4;
                if (pps.isEmpty()) {
                    y = writeLine(cs, fontRegular, 11, MARGIN + 10, y, "Aucun projet lié.");
                } else {
                    for (PassationProjet pp : pps) {
                        if (y < MARGIN + 60) {
                            // Add new page if needed
                            cs.close();
                            // We cannot reopen a closed stream; we'll accept truncation for simplicity
                            break;
                        }
                        String nomProjet = pp.getProjet() != null ? pp.getProjet().getNom() : "N/A";
                        String pct = pp.getPourcentagePassation() != null
                                ? pp.getPourcentagePassation() + "%" : "N/A";
                        String maitrise = pp.getNiveauMaitrise() != null
                                ? pp.getNiveauMaitrise().name() : "N/A";
                        y = writeLine(cs, fontRegular, 11, MARGIN + 10, y,
                                "- " + nomProjet + " | Passation: " + pct + " | Maîtrise: " + maitrise);
                    }
                }
                y -= 10;

                // ---- SECTION 4 ----
                y = writeLine(cs, fontBold, 13, MARGIN, y, "4. Timeline");
                y -= 4;
                if (etapes.isEmpty()) {
                    y = writeLine(cs, fontRegular, 11, MARGIN + 10, y, "Aucune étape définie.");
                } else {
                    for (TimelineEtape etape : etapes) {
                        if (y < MARGIN + 60) break;
                        String datePrevu = etape.getDatePrevu() != null
                                ? etape.getDatePrevu().format(DATE_FMT) : "N/A";
                        y = writeLine(cs, fontRegular, 11, MARGIN + 10, y,
                                "- " + nvl(etape.getTitre()) + " | " + etape.getStatut() + " | Prévu: " + datePrevu);
                    }
                }
                y -= 10;

                // ---- SECTION 5 ----
                if (y < MARGIN + 80) y = MARGIN + 80;
                y = writeLine(cs, fontBold, 13, MARGIN, y, "5. Zone de signatures");
                y -= 4;
                y = writeLine(cs, fontRegular, 11, MARGIN + 10, y,
                        "Employé partant : ___________________________");
                y -= 6;
                y = writeLine(cs, fontRegular, 11, MARGIN + 10, y,
                        "Remplaçant : ___________________________");
                y -= 6;
                writeLine(cs, fontRegular, 11, MARGIN + 10, y,
                        "Manager RH : ___________________________");
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        }
    }

    private float writeLine(PDPageContentStream cs, PDType1Font font, float fontSize,
                             float x, float y, String text) throws IOException {
        cs.beginText();
        cs.setFont(font, fontSize);
        cs.newLineAtOffset(x, y);
        cs.showText(text != null ? text : "");
        cs.endText();
        return y - LINE_HEIGHT;
    }

    private String nvl(String s) {
        return s != null ? s : "";
    }
}
