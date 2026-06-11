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

    private final UserRepository               userRepository;
    private final ProjetRepository             projetRepository;
    private final PassationRepository          passationRepository;
    private final PassationProjetRepository    passationProjetRepository;
    private final ChecklistTemplateRepository  checklistTemplateRepository;
    private final ChecklistTemplateItemRepository checklistTemplateItemRepository;
    private final TimelineEtapeRepository      timelineEtapeRepository;
    private final AlerteRepository             alerteRepository;
    private final CommentaireRepository        commentaireRepository;
    private final AuditLogRepository           auditLogRepository;
    private final PasswordEncoder              passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) return;

        String pwd = passwordEncoder.encode("password123");

        // ══════════════════════════════════════════
        // UTILISATEURS
        // ══════════════════════════════════════════

        User admin = userRepository.save(User.builder()
                .nom("Lambert").prenom("Sophie")
                .email("admin@example.com")
                .password(pwd)
                .departement("Direction Générale").poste("Administratrice Système")
                .role(Role.ADMIN).build());

        User rh = userRepository.save(User.builder()
                .nom("Moreau").prenom("Julien")
                .email("bob.dupont@example.com")
                .password(pwd)
                .departement("Ressources Humaines").poste("Responsable RH")
                .role(Role.MANAGER_RH).build());

        // Employés partants
        User karim = userRepository.save(User.builder()
                .nom("Benzara").prenom("Karim")
                .email("alice.martin@example.com")
                .password(pwd)
                .departement("Informatique").poste("Architecte Logiciel Senior")
                .role(Role.EMPLOYE).build());

        User nadia = userRepository.save(User.builder()
                .nom("Ouali").prenom("Nadia")
                .email("nadia.ouali@example.com")
                .password(pwd)
                .departement("Finance").poste("Responsable Comptabilité")
                .role(Role.EMPLOYE).build());

        User marc = userRepository.save(User.builder()
                .nom("Fontaine").prenom("Marc")
                .email("marc.fontaine@example.com")
                .password(pwd)
                .departement("Marketing").poste("Chef de Projet Digital")
                .role(Role.EMPLOYE).build());

        // Remplaçants
        User yasmine = userRepository.save(User.builder()
                .nom("Amrani").prenom("Yasmine")
                .email("claire.leclerc@example.com")
                .password(pwd)
                .departement("Informatique").poste("Développeuse Full-Stack")
                .role(Role.REMPLACANT).build());

        User thomas = userRepository.save(User.builder()
                .nom("Girard").prenom("Thomas")
                .email("thomas.girard@example.com")
                .password(pwd)
                .departement("Finance").poste("Analyste Financier")
                .role(Role.REMPLACANT).build());

        User lea = userRepository.save(User.builder()
                .nom("Petit").prenom("Léa")
                .email("lea.petit@example.com")
                .password(pwd)
                .departement("Marketing").poste("Chargée de Communication")
                .role(Role.REMPLACANT).build());

        // ══════════════════════════════════════════
        // PROJETS
        // ══════════════════════════════════════════

        Projet ecomm = projetRepository.save(Projet.builder()
                .nom("Plateforme E-Commerce B2B")
                .description("Refonte complète de la plateforme de vente en ligne à destination des clients professionnels. Migration vers microservices, intégration paiement et ERP.")
                .statut(StatutProjet.EN_COURS).pourcentageAvancement(72)
                .dateDebut(LocalDate.now().minusMonths(8))
                .dateFinPrevue(LocalDate.now().plusMonths(2))
                .responsable(karim).build());

        Projet dataLake = projetRepository.save(Projet.builder()
                .nom("Data Lake & Analytics")
                .description("Mise en place d'un lac de données centralisé avec tableaux de bord BI pour la direction. Stack : Kafka, Spark, Databricks, Power BI.")
                .statut(StatutProjet.EN_COURS).pourcentageAvancement(45)
                .dateDebut(LocalDate.now().minusMonths(4))
                .dateFinPrevue(LocalDate.now().plusMonths(5))
                .responsable(karim).build());

        Projet erpMigration = projetRepository.save(Projet.builder()
                .nom("Migration ERP SAP S/4HANA")
                .description("Migration du système ERP legacy vers SAP S/4HANA. Périmètre : Finance, Achats, Logistique. 450 utilisateurs impactés.")
                .statut(StatutProjet.EN_COURS).pourcentageAvancement(28)
                .dateDebut(LocalDate.now().minusMonths(2))
                .dateFinPrevue(LocalDate.now().plusMonths(10))
                .responsable(nadia).build());

        Projet closureCompta = projetRepository.save(Projet.builder()
                .nom("Clôture Comptable Annuelle 2024")
                .description("Pilotage de la clôture comptable annuelle : réconciliation des comptes, reporting IFRS, audit interne et validation CAC.")
                .statut(StatutProjet.TERMINE).pourcentageAvancement(100)
                .dateDebut(LocalDate.now().minusMonths(14))
                .dateFinPrevue(LocalDate.now().minusMonths(11))
                .responsable(nadia).build());

        Projet rebrandingDigital = projetRepository.save(Projet.builder()
                .nom("Rebranding Digital 2025")
                .description("Refonte identité visuelle, site vitrine, réseaux sociaux et supports marketing. Lancement prévu Q3 2025.")
                .statut(StatutProjet.EN_COURS).pourcentageAvancement(60)
                .dateDebut(LocalDate.now().minusMonths(3))
                .dateFinPrevue(LocalDate.now().plusMonths(3))
                .responsable(marc).build());

        Projet campaignAuto = projetRepository.save(Projet.builder()
                .nom("Automatisation Campagnes Marketing")
                .description("Déploiement HubSpot CRM et automatisation des campagnes email / LinkedIn. Objectif : +30% leads qualifiés.")
                .statut(StatutProjet.PLANIFIE).pourcentageAvancement(0)
                .dateDebut(LocalDate.now().plusMonths(1))
                .dateFinPrevue(LocalDate.now().plusMonths(7))
                .responsable(marc).build());

        Projet cyberSec = projetRepository.save(Projet.builder()
                .nom("Audit Cybersécurité & ISO 27001")
                .description("Audit complet de la sécurité du SI et préparation à la certification ISO 27001. Périmètre : infrastructure, applications critiques, formation collaborateurs.")
                .statut(StatutProjet.PLANIFIE).pourcentageAvancement(5)
                .dateDebut(LocalDate.now().plusMonths(2))
                .dateFinPrevue(LocalDate.now().plusMonths(9))
                .responsable(karim).build());

        // ══════════════════════════════════════════
        // CHECKLIST TEMPLATES
        // ══════════════════════════════════════════

        ChecklistTemplate tmplDev = checklistTemplateRepository.save(ChecklistTemplate.builder()
                .nom("Template Passation — Technique IT")
                .typePoste("Architecte / Développeur")
                .description("Checklist de passation pour les profils techniques : transfert de code, documentation, accès systèmes.").build());

        checklistTemplateItemRepository.save(ChecklistTemplateItem.builder().template(tmplDev)
                .libelle("Transfert des accès GitHub / GitLab et revue des droits").obligatoire(true).ordre(1).build());
        checklistTemplateItemRepository.save(ChecklistTemplateItem.builder().template(tmplDev)
                .libelle("Documentation architecture technique (ADR, diagrammes C4)").obligatoire(true).ordre(2).build());
        checklistTemplateItemRepository.save(ChecklistTemplateItem.builder().template(tmplDev)
                .libelle("Revue du code critique et des dette technique connue").obligatoire(true).ordre(3).build());
        checklistTemplateItemRepository.save(ChecklistTemplateItem.builder().template(tmplDev)
                .libelle("Transfert des credentials Vault / secrets management").obligatoire(true).ordre(4).build());
        checklistTemplateItemRepository.save(ChecklistTemplateItem.builder().template(tmplDev)
                .libelle("Formation sur les runbooks de production et procédures d'astreinte").obligatoire(true).ordre(5).build());
        checklistTemplateItemRepository.save(ChecklistTemplateItem.builder().template(tmplDev)
                .libelle("Présentation aux équipes projet et clients clés").obligatoire(false).ordre(6).build());
        checklistTemplateItemRepository.save(ChecklistTemplateItem.builder().template(tmplDev)
                .libelle("Passation des abonnements outils (Jira, Confluence, SonarCloud)").obligatoire(false).ordre(7).build());

        ChecklistTemplate tmplFinance = checklistTemplateRepository.save(ChecklistTemplate.builder()
                .nom("Template Passation — Finance & Comptabilité")
                .typePoste("Responsable Financier")
                .description("Checklist de passation pour les profils finance : comptes, reporting, procédures réglementaires.").build());

        checklistTemplateItemRepository.save(ChecklistTemplateItem.builder().template(tmplFinance)
                .libelle("Revue des comptes ouverts et clôtures en suspens").obligatoire(true).ordre(1).build());
        checklistTemplateItemRepository.save(ChecklistTemplateItem.builder().template(tmplFinance)
                .libelle("Transfert des accès ERP (SAP, Oracle) et droits bancaires").obligatoire(true).ordre(2).build());
        checklistTemplateItemRepository.save(ChecklistTemplateItem.builder().template(tmplFinance)
                .libelle("Documentation des procédures de clôture mensuelle et annuelle").obligatoire(true).ordre(3).build());
        checklistTemplateItemRepository.save(ChecklistTemplateItem.builder().template(tmplFinance)
                .libelle("Présentation aux auditeurs et au CAC").obligatoire(true).ordre(4).build());
        checklistTemplateItemRepository.save(ChecklistTemplateItem.builder().template(tmplFinance)
                .libelle("Transfert du suivi des contrats fournisseurs et échéanciers").obligatoire(false).ordre(5).build());

        ChecklistTemplate tmplMarketing = checklistTemplateRepository.save(ChecklistTemplate.builder()
                .nom("Template Passation — Marketing & Communication")
                .typePoste("Chef de Projet Marketing")
                .description("Checklist de passation pour les profils marketing : campagnes, outils, agences et planning.").build());

        checklistTemplateItemRepository.save(ChecklistTemplateItem.builder().template(tmplMarketing)
                .libelle("Transfert des accès Google Analytics, Meta Business, HubSpot").obligatoire(true).ordre(1).build());
        checklistTemplateItemRepository.save(ChecklistTemplateItem.builder().template(tmplMarketing)
                .libelle("Présentation du planning éditorial et calendrier des campagnes").obligatoire(true).ordre(2).build());
        checklistTemplateItemRepository.save(ChecklistTemplateItem.builder().template(tmplMarketing)
                .libelle("Passation des contacts agences et prestataires externes").obligatoire(true).ordre(3).build());
        checklistTemplateItemRepository.save(ChecklistTemplateItem.builder().template(tmplMarketing)
                .libelle("Documentation des procédures de validation des visuels et textes").obligatoire(false).ordre(4).build());

        // ══════════════════════════════════════════
        // PASSATION 1 — Karim → Yasmine (CRITIQUE, J-12)
        // ══════════════════════════════════════════

        Passation p1 = passationRepository.save(Passation.builder()
                .employePartant(karim).remplacant(yasmine).manager(rh)
                .dateDepart(LocalDate.now().plusDays(12))
                .statut(StatutPassation.EN_COURS)
                .pourcentageGlobal(38.0)
                .notes("Passation urgente suite à mutation internationale de Karim. Yasmine doit reprendre les deux projets critiques. Priorité absolue sur la plateforme e-commerce dont la livraison est dans 2 mois.").build());

        passationProjetRepository.save(PassationProjet.builder()
                .passation(p1).projet(ecomm)
                .pourcentagePassation(45)
                .niveauMaitrise(NiveauMaitrise.MOYEN).build());

        passationProjetRepository.save(PassationProjet.builder()
                .passation(p1).projet(dataLake)
                .pourcentagePassation(20)
                .niveauMaitrise(NiveauMaitrise.FAIBLE).build());

        passationProjetRepository.save(PassationProjet.builder()
                .passation(p1).projet(cyberSec)
                .pourcentagePassation(10)
                .niveauMaitrise(NiveauMaitrise.FAIBLE).build());

        // Timeline P1
        timelineEtapeRepository.save(TimelineEtape.builder().passation(p1)
                .titre("Réunion de lancement — présentation du périmètre")
                .statut(StatutEtape.TERMINE)
                .datePrevu(LocalDate.now().minusDays(14)).ordre(1).build());
        timelineEtapeRepository.save(TimelineEtape.builder().passation(p1)
                .titre("Transfert accès GitHub, Jira, SonarCloud, AWS")
                .statut(StatutEtape.TERMINE)
                .datePrevu(LocalDate.now().minusDays(10)).ordre(2).build());
        timelineEtapeRepository.save(TimelineEtape.builder().passation(p1)
                .titre("Revue architecture e-commerce (sessions x3)")
                .statut(StatutEtape.EN_COURS)
                .datePrevu(LocalDate.now().plusDays(2)).ordre(3).build());
        timelineEtapeRepository.save(TimelineEtape.builder().passation(p1)
                .titre("Formation runbooks production & procédures d'astreinte")
                .statut(StatutEtape.A_FAIRE)
                .datePrevu(LocalDate.now().plusDays(6)).ordre(4).build());
        timelineEtapeRepository.save(TimelineEtape.builder().passation(p1)
                .titre("Présentation équipe et clients grands comptes")
                .statut(StatutEtape.A_FAIRE)
                .datePrevu(LocalDate.now().plusDays(9)).ordre(5).build());
        timelineEtapeRepository.save(TimelineEtape.builder().passation(p1)
                .titre("Validation finale par le manager RH")
                .statut(StatutEtape.A_FAIRE)
                .datePrevu(LocalDate.now().plusDays(11)).ordre(6).build());

        // Alertes P1
        alerteRepository.save(Alerte.builder().passation(p1)
                .type("RETARD_CRITIQUE")
                .message("Passation à seulement 38% à J-12 — risque élevé de rupture de continuité sur la plateforme e-commerce.")
                .niveauSeverite(NiveauSeverite.CRITIQUE).lu(false).build());
        alerteRepository.save(Alerte.builder().passation(p1)
                .type("REMPLACANT_INSUFFISANT")
                .message("Yasmine maîtrise insuffisamment le projet Data Lake (20%). Session de formation complémentaire recommandée.")
                .niveauSeverite(NiveauSeverite.CRITIQUE).lu(false).build());
        alerteRepository.save(Alerte.builder().passation(p1)
                .type("CHECKLIST_INCOMPLETE")
                .message("5 items critiques non complétés sur la checklist technique (runbooks, secrets management).")
                .niveauSeverite(NiveauSeverite.CRITIQUE).lu(false).build());
        alerteRepository.save(Alerte.builder().passation(p1)
                .type("DELAI_COURT")
                .message("Moins de 15 jours avant le départ — escalade recommandée auprès de la direction.")
                .niveauSeverite(NiveauSeverite.WARNING).lu(true).build());

        // Commentaires P1
        commentaireRepository.save(Commentaire.builder().passation(p1).auteur(rh)
                .contenu("Réunion d'urgence planifiée avec la direction IT pour le 15/06. Yasmine devra être disponible à 100% cette semaine.")
                .build());
        commentaireRepository.save(Commentaire.builder().passation(p1).auteur(karim)
                .contenu("J'ai préparé un document d'architecture complet pour l'e-commerce. Les modules paiement et ERP sont les plus critiques. Je reste disponible par email 3 mois après mon départ.")
                .build());
        commentaireRepository.save(Commentaire.builder().passation(p1).auteur(yasmine)
                .contenu("Accès AWS OK. J'ai commencé la lecture de la doc. Le module d'intégration Stripe est complexe — besoin de 2 sessions supplémentaires avec Karim.")
                .build());

        // Audit P1
        auditLogRepository.save(AuditLog.builder().utilisateur(rh)
                .action("CREATION_PASSATION").entiteConcernee("Passation").entiteId(p1.getId())
                .ancienneValeur(null).nouvelleValeur("Passation Karim→Yasmine créée, départ J-26")
                .ipAddress("10.0.1.15").build());
        auditLogRepository.save(AuditLog.builder().utilisateur(karim)
                .action("MISE_A_JOUR_STATUT").entiteConcernee("Passation").entiteId(p1.getId())
                .ancienneValeur("EN_ATTENTE").nouvelleValeur("EN_COURS")
                .ipAddress("10.0.1.22").build());
        auditLogRepository.save(AuditLog.builder().utilisateur(yasmine)
                .action("VALIDATION_ETAPE").entiteConcernee("TimelineEtape").entiteId(1L)
                .ancienneValeur("A_FAIRE").nouvelleValeur("TERMINE")
                .ipAddress("10.0.1.30").build());

        // ══════════════════════════════════════════
        // PASSATION 2 — Nadia → Thomas (EN_COURS, J-35)
        // ══════════════════════════════════════════

        Passation p2 = passationRepository.save(Passation.builder()
                .employePartant(nadia).remplacant(thomas).manager(rh)
                .dateDepart(LocalDate.now().plusDays(35))
                .statut(StatutPassation.EN_COURS)
                .pourcentageGlobal(62.0)
                .notes("Nadia part en retraite anticipée. Thomas, analyste financier depuis 3 ans, prend la responsabilité de la comptabilité. La migration ERP en cours est le point de vigilance principal.").build());

        passationProjetRepository.save(PassationProjet.builder()
                .passation(p2).projet(erpMigration)
                .pourcentagePassation(35)
                .niveauMaitrise(NiveauMaitrise.MOYEN).build());

        passationProjetRepository.save(PassationProjet.builder()
                .passation(p2).projet(closureCompta)
                .pourcentagePassation(90)
                .niveauMaitrise(NiveauMaitrise.BON).build());

        // Timeline P2
        timelineEtapeRepository.save(TimelineEtape.builder().passation(p2)
                .titre("Réunion de cadrage — périmètre financier")
                .statut(StatutEtape.TERMINE)
                .datePrevu(LocalDate.now().minusDays(20)).ordre(1).build());
        timelineEtapeRepository.save(TimelineEtape.builder().passation(p2)
                .titre("Transfert accès SAP, Oracle, outils bancaires")
                .statut(StatutEtape.TERMINE)
                .datePrevu(LocalDate.now().minusDays(15)).ordre(2).build());
        timelineEtapeRepository.save(TimelineEtape.builder().passation(p2)
                .titre("Formation procédures de clôture mensuelle")
                .statut(StatutEtape.TERMINE)
                .datePrevu(LocalDate.now().minusDays(8)).ordre(3).build());
        timelineEtapeRepository.save(TimelineEtape.builder().passation(p2)
                .titre("Revue migration ERP et interfaces comptables")
                .statut(StatutEtape.EN_COURS)
                .datePrevu(LocalDate.now().plusDays(7)).ordre(4).build());
        timelineEtapeRepository.save(TimelineEtape.builder().passation(p2)
                .titre("Présentation auditeurs internes et CAC")
                .statut(StatutEtape.A_FAIRE)
                .datePrevu(LocalDate.now().plusDays(20)).ordre(5).build());
        timelineEtapeRepository.save(TimelineEtape.builder().passation(p2)
                .titre("Simulation clôture mensuelle en autonomie")
                .statut(StatutEtape.A_FAIRE)
                .datePrevu(LocalDate.now().plusDays(28)).ordre(6).build());
        timelineEtapeRepository.save(TimelineEtape.builder().passation(p2)
                .titre("Validation finale et signature passation")
                .statut(StatutEtape.A_FAIRE)
                .datePrevu(LocalDate.now().plusDays(33)).ordre(7).build());

        // Alertes P2
        alerteRepository.save(Alerte.builder().passation(p2)
                .type("RETARD_ERP")
                .message("Thomas n'a pas encore été formé aux interfaces SAP-comptabilité analytique. Risque sur la clôture de juin.")
                .niveauSeverite(NiveauSeverite.WARNING).lu(false).build());
        alerteRepository.save(Alerte.builder().passation(p2)
                .type("BONNE_PROGRESSION")
                .message("Passation à 62% à J-35 — progression satisfaisante. Continuer au rythme actuel.")
                .niveauSeverite(NiveauSeverite.INFO).lu(true).build());

        // Commentaires P2
        commentaireRepository.save(Commentaire.builder().passation(p2).auteur(nadia)
                .contenu("Thomas a bien assimilé les procédures de clôture mensuelle. Il faut maintenant se concentrer sur la migration ERP — les interfaces GL et CO sont spécifiques à notre configuration.")
                .build());
        commentaireRepository.save(Commentaire.builder().passation(p2).auteur(thomas)
                .contenu("Accès SAP OK. J'ai participé à la dernière clôture en observation. Je suis à l'aise sur le reporting standard mais j'ai besoin d'aide sur les écritures d'élimination inter-compagnies.")
                .build());
        commentaireRepository.save(Commentaire.builder().passation(p2).auteur(rh)
                .contenu("Planning de passation respecté. RDV de suivi prévu le 25/06 avec Nadia et Thomas. Pas d'escalade nécessaire à ce stade.")
                .build());

        // Audit P2
        auditLogRepository.save(AuditLog.builder().utilisateur(rh)
                .action("CREATION_PASSATION").entiteConcernee("Passation").entiteId(p2.getId())
                .ancienneValeur(null).nouvelleValeur("Passation Nadia→Thomas créée, départ J-55")
                .ipAddress("10.0.1.15").build());
        auditLogRepository.save(AuditLog.builder().utilisateur(nadia)
                .action("VALIDATION_ETAPE").entiteConcernee("TimelineEtape").entiteId(4L)
                .ancienneValeur("A_FAIRE").nouvelleValeur("TERMINE")
                .ipAddress("10.0.1.18").build());

        // ══════════════════════════════════════════
        // PASSATION 3 — Marc → Léa (EN_COURS, J-50)
        // ══════════════════════════════════════════

        Passation p3 = passationRepository.save(Passation.builder()
                .employePartant(marc).remplacant(lea).manager(rh)
                .dateDepart(LocalDate.now().plusDays(50))
                .statut(StatutPassation.EN_COURS)
                .pourcentageGlobal(78.0)
                .notes("Marc rejoint un concurrent. Léa, en poste depuis 2 ans, est bien préparée. Focus sur la passation des contacts agences et le suivi du rebranding en cours.").build());

        passationProjetRepository.save(PassationProjet.builder()
                .passation(p3).projet(rebrandingDigital)
                .pourcentagePassation(80)
                .niveauMaitrise(NiveauMaitrise.BON).build());

        passationProjetRepository.save(PassationProjet.builder()
                .passation(p3).projet(campaignAuto)
                .pourcentagePassation(60)
                .niveauMaitrise(NiveauMaitrise.MOYEN).build());

        // Timeline P3
        timelineEtapeRepository.save(TimelineEtape.builder().passation(p3)
                .titre("Réunion de lancement passation")
                .statut(StatutEtape.TERMINE)
                .datePrevu(LocalDate.now().minusDays(30)).ordre(1).build());
        timelineEtapeRepository.save(TimelineEtape.builder().passation(p3)
                .titre("Transfert accès Google Analytics, Meta Ads, HubSpot")
                .statut(StatutEtape.TERMINE)
                .datePrevu(LocalDate.now().minusDays(25)).ordre(2).build());
        timelineEtapeRepository.save(TimelineEtape.builder().passation(p3)
                .titre("Présentation planning éditorial et calendrier campagnes")
                .statut(StatutEtape.TERMINE)
                .datePrevu(LocalDate.now().minusDays(18)).ordre(3).build());
        timelineEtapeRepository.save(TimelineEtape.builder().passation(p3)
                .titre("Passation contacts agences (Publicis, 84.Paris, Contentsquare)")
                .statut(StatutEtape.TERMINE)
                .datePrevu(LocalDate.now().minusDays(10)).ordre(4).build());
        timelineEtapeRepository.save(TimelineEtape.builder().passation(p3)
                .titre("Revue du projet Rebranding — suivi livrables restants")
                .statut(StatutEtape.EN_COURS)
                .datePrevu(LocalDate.now().plusDays(5)).ordre(5).build());
        timelineEtapeRepository.save(TimelineEtape.builder().passation(p3)
                .titre("Validation finale et signature")
                .statut(StatutEtape.A_FAIRE)
                .datePrevu(LocalDate.now().plusDays(45)).ordre(6).build());

        // Alertes P3
        alerteRepository.save(Alerte.builder().passation(p3)
                .type("BONNE_PROGRESSION")
                .message("Passation à 78% — bien avancée, dans les délais. Léa est opérationnelle sur le rebranding.")
                .niveauSeverite(NiveauSeverite.INFO).lu(true).build());

        // Commentaires P3
        commentaireRepository.save(Commentaire.builder().passation(p3).auteur(marc)
                .contenu("Léa maîtrise parfaitement HubSpot et les campagnes sociales. Le seul point d'attention est la relation avec l'agence Publicis — je vais l'introduire en réunion la semaine prochaine.")
                .build());
        commentaireRepository.save(Commentaire.builder().passation(p3).auteur(lea)
                .contenu("Super bien avancé ! J'ai pris en main tous les outils. Je prépare la réunion avec Publicis. Le brief du rebranding est clair, je me sens prête.")
                .build());

        // ══════════════════════════════════════════
        // PASSATION 4 — TERMINÉE (historique)
        // ══════════════════════════════════════════

        Passation p4 = passationRepository.save(Passation.builder()
                .employePartant(karim).remplacant(yasmine).manager(rh)
                .dateDepart(LocalDate.now().minusDays(90))
                .statut(StatutPassation.TERMINEE)
                .pourcentageGlobal(100.0)
                .notes("Passation du projet Infrastructure Cloud complétée avec succès. Yasmine a pris en main les responsabilités sans incident. Modèle de réussite à dupliquer.").build());

        projetRepository.save(Projet.builder()
                .nom("Infrastructure Cloud AWS")
                .description("Migration on-premise vers AWS : VPC, EKS, RDS, S3. 99.9% SLA maintenu.")
                .statut(StatutProjet.TERMINE).pourcentageAvancement(100)
                .dateDebut(LocalDate.now().minusMonths(18))
                .dateFinPrevue(LocalDate.now().minusMonths(12))
                .responsable(yasmine).build());

        alerteRepository.save(Alerte.builder().passation(p4)
                .type("PASSATION_REUSSIE")
                .message("Passation Infrastructure Cloud validée à 100%. Yasmine opérationnelle depuis J+0. Aucun incident post-passation.")
                .niveauSeverite(NiveauSeverite.INFO).lu(true).build());

        // Audit final
        auditLogRepository.save(AuditLog.builder().utilisateur(admin)
                .action("CONSULTATION_DASHBOARD").entiteConcernee("Dashboard").entiteId(null)
                .ancienneValeur(null).nouvelleValeur("Consultation tableau de bord global")
                .ipAddress("10.0.1.1").build());
    }
}
