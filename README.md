# TalentMatch

SaaS de recrutement multi-tenant avec analyse IA des CV (OCR, LLM, recherche sémantique) — Spring Boot 3, Spring AI, PostgreSQL/pgvector, React, Docker.

## Structure

```
backend/   API Spring Boot 3 (Java 21, Maven)
frontend/  React + TypeScript + Vite + Tailwind CSS
docs/      architecture, captures
```

## Prérequis

- JDK 21
- Node.js 20+
- Docker (pour PostgreSQL)

## Démarrer en local

Prérequis : Docker et Docker Compose installés.

1. Copier le fichier d'environnement d'exemple et ajuster les valeurs si besoin :
   ```bash
   cp .env.example .env
   ```

2. Renseigner `GEMINI_API_KEY` dans `.env` (clé créée sur https://aistudio.google.com/apikey) : l'IA passe par Gemini.

3. Lancer les services :
   ```bash
   docker compose up -d --build
   ```
   Cela démarre :
   - **postgres** (`pgvector/pgvector:pg16`) — base de données
   - **minio** — stockage des CV
   - **mailpit** — réception des e-mails de test
   - **backend** — API Spring Boot
   - **frontend** — application React (nginx)

4. Vérifier que tout est démarré et sain :
   ```bash
   docker compose ps
   ```

| Service    | URL locale                        | Identifiants par défaut          |
|------------|------------------------------------|-----------------------------------|
| PostgreSQL | `localhost:5432`                   | `talentmatch` / voir `.env` |
| MinIO      | http://localhost:9001 (console)    | `minioadmin` / voir `.env`  |
| Mailpit    | http://localhost:8025 (UI)         | —                                 |
| Backend    | http://localhost:8000              | —                                 |
| Frontend   | http://localhost:3000              | —                                 |

Pour tout arrêter : `docker compose down` (ajouter `-v` pour aussi supprimer les volumes de données).
## Conventions

- `main` : version stable, `develop` : intégration.
- Une branche par issue, créée depuis `develop` : `feature/TM-xx-nom`.
- Toute fusion passe par une pull request vers `develop`, relue et approuvée par le binôme.

```