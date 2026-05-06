# Passation Backend

API REST Spring Boot pour la gestion des passations de poste.

## Stack technique

- Java 17
- Spring Boot 4.0.6
- Spring Security 7 (JWT stateless)
- Spring Data JPA + H2 (in-memory)
- Lombok
- Apache PDFBox 3.0.3
- jjwt 0.12.6

## Prérequis

- JDK 17+
- Maven 3.8+

## Démarrage

```bash
mvn spring-boot:run
```

Le serveur démarre sur **http://localhost:9090**

## Console H2

Accessible sur **http://localhost:9090/h2-console**

- JDBC URL: `jdbc:h2:mem:passationdb`
- Username: `sa`
- Password: *(vide)*

## Comptes de démo (créés au démarrage)

| Email | Password | Rôle |
|-------|----------|------|
| alice.martin@example.com | password123 | EMPLOYE |
| bob.dupont@example.com | password123 | MANAGER_RH |
| claire.leclerc@example.com | password123 | REMPLACANT |
| admin@example.com | password123 | ADMIN |

## Endpoints principaux

### Auth
| Méthode | URL | Description |
|---------|-----|-------------|
| POST | `/api/auth/login` | Connexion → JWT |
| POST | `/api/auth/register` | Inscription |

### Passations
| Méthode | URL | Description |
|---------|-----|-------------|
| GET | `/api/passations` | Liste toutes |
| POST | `/api/passations` | Créer |
| GET | `/api/passations/{id}` | Détail |
| PUT | `/api/passations/{id}/statut` | Changer statut |
| POST | `/api/passations/{id}/projets` | Ajouter projet |
| GET | `/api/passations/stats/dashboard` | KPIs dashboard |
| GET | `/api/passations/search` | Recherche avancée |
| GET | `/api/passations/{id}/risk-score` | Score de risque |

### Users
| Méthode | URL | Description |
|---------|-----|-------------|
| GET | `/api/users` | Liste |
| POST | `/api/users` | Créer |
| PUT | `/api/users/{id}` | Modifier |
| DELETE | `/api/users/{id}` | Supprimer |
| GET | `/api/users/role/{role}` | Par rôle |

### Projets
| Méthode | URL | Description |
|---------|-----|-------------|
| GET | `/api/projets` | Liste |
| POST | `/api/projets` | Créer |
| PUT | `/api/projets/{id}` | Modifier |
| DELETE | `/api/projets/{id}` | Supprimer |

### Alertes
| Méthode | URL | Description |
|---------|-----|-------------|
| GET | `/api/alertes` | Toutes |
| GET | `/api/alertes/non-lues` | Non lues |
| PUT | `/api/alertes/{id}/lu` | Marquer lue |
| POST | `/api/alertes/generate/{passationId}` | Générer alertes |

### PDF
| Méthode | URL | Description |
|---------|-----|-------------|
| GET | `/api/pdf/passation/{id}` | Télécharger rapport PDF |

### Checklist / Timeline / Commentaires
- `GET/POST /api/checklists`
- `GET/POST /api/timeline`
- `GET/POST /api/commentaires`

## Architecture

```
src/main/java/com/passation/passation_backend/
├── config/
│   ├── SecurityConfig.java       # Spring Security JWT
│   └── DataInitializer.java      # Données de démo
├── controller/                   # REST controllers
├── dto/                          # Data Transfer Objects
├── model/                        # Entités JPA
├── repository/                   # Spring Data JPA
├── security/
│   ├── JwtUtil.java
│   ├── JwtAuthenticationFilter.java
│   └── CustomUserDetailsService.java
└── service/
    ├── RiskScoreService.java     # Score FAIBLE/MODERE/CRITIQUE
    ├── AlerteService.java        # Alertes intelligentes auto-générées
    └── PdfService.java           # Génération PDF PDFBox
```

## Rôles et accès

| Rôle | Description |
|------|-------------|
| EMPLOYE | Ses propres passations |
| REMPLACANT | Passations où il est remplaçant |
| MANAGER_RH | Toutes les passations + dashboard complet |
| ADMIN | Accès total + gestion utilisateurs |
