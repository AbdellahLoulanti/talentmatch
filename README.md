# TalentMatch

SaaS de recrutement multi-tenant avec analyse IA des CV (OCR, LLM, recherche sémantique) — Spring Boot 3, Spring AI, PostgreSQL/pgvector, React, Docker.

## Structure

```
backend/   API Spring Boot 3 (Java 21, Maven)
frontend/  React + TypeScript + Vite + Tailwind CSS
infra/     docker-compose, Nginx, scripts
docs/      architecture, captures
```

## Prérequis

- JDK 21
- Node.js 20+
- Docker (pour PostgreSQL)

## Démarrage

```bash
# Base de données
docker compose -f infra/docker-compose.yml up -d

# Backend (http://localhost:8080)
cd backend
./mvnw spring-boot:run        # Windows : .\mvnw.cmd spring-boot:run

# Frontend (http://localhost:5173)
cd frontend
npm install
npm run dev
```

## Build

```bash
cd backend && ./mvnw verify
cd frontend && npm run build
```

## Conventions

- `main` : version stable, `develop` : intégration.
- Une branche par issue, créée depuis `develop` : `feature/TM-xx-nom`.
- Toute fusion passe par une pull request vers `develop`, relue et approuvée par le binôme.
