package com.passation.passation_backend.config;

import com.passation.passation_backend.model.*;
import com.passation.passation_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Order(1)
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProjetRepository projetRepository;
    private final PassationRepository passationRepository;
    private final PassationProjetRepository passationProjetRepository;
    private final ChecklistTemplateRepository checklistTemplateRepository;
    private final ChecklistTemplateItemRepository checklistTemplateItemRepository;
    private final TimelineEtapeRepository timelineEtapeRepository;
    private final AlerteRepository alerteRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) return;

        String encodedPassword = passwordEncoder.encode("password123");

        // ---- Users ----
        User alice = userRepository.save(User.builder()
                .nom("Martin").prenom("Alice")
                .email("alice.martin@example.com")
                .password(encodedPassword)
                .departement("Informatique").poste("Développeur Senior")
                .role(Role.EMPLOYE).build());

        User bob = userRepository.save(User.builder()
                .nom("Dupont").prenom("Bob")
                .email("bob.dupont@example.com")
                .password(encodedPassword)
                .departement("RH").poste("Manager RH")
                .role(Role.MANAGER_RH).build());

        User claire = userRepository.save(User.builder()
                .nom("Leclerc").prenom("Claire")
                .email("claire.leclerc@example.com")
                .password(encodedPassword)
                .departement("Informatique").poste("Développeur Junior")
                .role(Role.REMPLACANT).build());

        userRepository.save(User.builder()
                .nom("System").prenom("Admin")
                .email("admin@example.com")
                .password(encodedPassword)
                .departement("Direction").poste("Administrateur")
                .role(Role.ADMIN).build());

        // ---- Projets ----
        Projet refonteSI = projetRepository.save(Projet.builder()
                .nom("Refonte SI").description("Refonte du système d'information")
                .statut(StatutProjet.EN_COURS).pourcentageAvancement(65)
                .dateDebut(LocalDate.now().minusMonths(6))
                .dateFinPrevue(LocalDate.now().plusMonths(3))
                .responsable(alice).build());

        Projet migrationCloud = projetRepository.save(Projet.builder()
                .nom("Migration Cloud").description("Migration vers le cloud")
                .statut(StatutProjet.EN_COURS).pourcentageAvancement(30)
                .dateDebut(LocalDate.now().minusMonths(3))
                .dateFinPrevue(LocalDate.now().plusMonths(6))
                .responsable(alice).build());

        projetRepository.save(Projet.builder()
                .nom("Audit Sécurité").description("Audit de sécurité")
                .statut(StatutProjet.PLANIFIE).pourcentageAvancement(0)
                .dateDebut(LocalDate.now().plusMonths(1))
                .dateFinPrevue(LocalDate.now().plusMonths(4))
                .responsable(alice).build());

        projetRepository.save(Projet.builder()
                .nom("Formation Équipe").description("Formation de l'équipe")
                .statut(StatutProjet.TERMINE).pourcentageAvancement(100)
                .dateDebut(LocalDate.now().minusMonths(12))
                .dateFinPrevue(LocalDate.now().minusMonths(9))
                .responsable(bob).build());

        projetRepository.save(Projet.builder()
                .nom("Dashboard Analytics").description("Tableau de bord analytique")
                .statut(StatutProjet.EN_COURS).pourcentageAvancement(45)
                .dateDebut(LocalDate.now().minusMonths(2))
                .dateFinPrevue(LocalDate.now().plusMonths(4))
                .responsable(alice).build());

        // ---- Passations ----
        Passation passation1 = passationRepository.save(Passation.builder()
                .employePartant(alice).remplacant(claire).manager(bob)
                .dateDepart(LocalDate.now().plusDays(20))
                .statut(StatutPassation.EN_COURS)
                .pourcentageGlobal(45.0).build());

        passationRepository.save(Passation.builder()
                .employePartant(alice).remplacant(claire).manager(bob)
                .dateDepart(LocalDate.now().minusDays(30))
                .statut(StatutPassation.TERMINEE)
                .pourcentageGlobal(100.0).build());

        // ---- PassationProjets for Passation 1 ----
        passationProjetRepository.save(PassationProjet.builder()
                .passation(passation1).projet(refonteSI)
                .pourcentagePassation(50)
                .niveauMaitrise(NiveauMaitrise.BON).build());

        passationProjetRepository.save(PassationProjet.builder()
                .passation(passation1).projet(migrationCloud)
                .pourcentagePassation(20)
                .niveauMaitrise(NiveauMaitrise.FAIBLE).build());

        // ---- ChecklistTemplate ----
        ChecklistTemplate template = checklistTemplateRepository.save(ChecklistTemplate.builder()
                .nom("Template Standard Passation")
                .typePoste("Développeur")
                .description("Template de passation pour les développeurs").build());

        checklistTemplateItemRepository.save(ChecklistTemplateItem.builder()
                .template(template)
                .libelle("Transfert de connaissances techniques")
                .obligatoire(true).ordre(1).build());

        checklistTemplateItemRepository.save(ChecklistTemplateItem.builder()
                .template(template)
                .libelle("Documentation des processus")
                .obligatoire(true).ordre(2).build());

        checklistTemplateItemRepository.save(ChecklistTemplateItem.builder()
                .template(template)
                .libelle("Présentation équipe")
                .obligatoire(false).ordre(3).build());

        // ---- TimelineEtapes for Passation 1 ----
        timelineEtapeRepository.save(TimelineEtape.builder()
                .passation(passation1).titre("Réunion initiale")
                .statut(StatutEtape.TERMINE)
                .datePrevu(LocalDate.now().minusDays(10)).ordre(1).build());

        timelineEtapeRepository.save(TimelineEtape.builder()
                .passation(passation1).titre("Formation remplaçant")
                .statut(StatutEtape.EN_COURS)
                .datePrevu(LocalDate.now().plusDays(5)).ordre(2).build());

        timelineEtapeRepository.save(TimelineEtape.builder()
                .passation(passation1).titre("Validation finale")
                .statut(StatutEtape.A_FAIRE)
                .datePrevu(LocalDate.now().plusDays(18)).ordre(3).build());

        // ---- Alertes for Passation 1 ----
        alerteRepository.save(Alerte.builder()
                .passation(passation1)
                .type("RETARD_CRITIQUE")
                .message("Passation critique: moins de 30% réalisée à J-15")
                .niveauSeverite(NiveauSeverite.CRITIQUE)
                .lu(false).build());

        alerteRepository.save(Alerte.builder()
                .passation(passation1)
                .type("REMPLACANT_INSUFFISANT")
                .message("Remplaçant insuffisant sur projet Migration Cloud")
                .niveauSeverite(NiveauSeverite.CRITIQUE)
                .lu(false).build());

        alerteRepository.save(Alerte.builder()
                .passation(passation1)
                .type("CHECKLIST_INCOMPLETE")
                .message("Checklist incomplète à J-10")
                .niveauSeverite(NiveauSeverite.WARNING)
                .lu(true).build());
    }
}
