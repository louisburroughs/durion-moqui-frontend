# AGENTS.md — durion-moqui-frontend

## Project Overview
Moqui Framework runtime + UI assets for the Durion platform. UI stack is Vue 3 + Quasar + TypeScript 5 and runtime is Moqui (Java/Groovy).

## Quick Prerequisites
- Java 21+ (for runtime)
- Gradle (`./gradlew`) for runtime builds
- Node 18+ / npm for UI toolchain
- Docker + Docker Compose for local stacks

## Setup & Build
```bash
cd durion-moqui-frontend
npm install          # install UI deps
./gradlew build -x test  # build runtime + assets (skip tests for fast iteration)
```
Local compose (Postgres + Moqui):
```bash
docker-compose -f docker/moqui-postgres-compose.yml up -d
```

## Dev Workflow
- UI dev: `npm run dev` (see `package.json` scripts)
- Runtime build: `./gradlew build`
- Run runtime locally (after build):
```bash
java -jar runtime/build/libs/moqui.war
# then open http://localhost:8080/webroot/
```

## Testing & Linting
```bash
# Frontend unit tests (Jest or configured runner)
npm test
# Moqui/server-side tests (if configured)
./gradlew test
# Lint
npm run lint
```

## Observability (frontend-focused)
- Record Web Vitals (LCP, INP, CLS) and JS errors; attach `release`/`service.version`.
- Instrument XHR/fetch with W3C trace context (`traceparent`) to correlate browser → backend traces.
- Upload sourcemaps for each frontend release for actionable stack traces.
- Reference: `../docs/architecture/observability/OBSERVABILITY.md` and `.github/agents/sre.agent.md`.

## Useful Commands
```bash
# quick dev build of runtime (fast)
./gradlew build -x test
# build and run with embedded H2
java -jar runtime/build/libs/moqui.war
```

## Agent Docs to Consult
- `.github/agents/moqui-developer.agent.md`
- `.github/agents/sre.agent.md` (observability)
- `../AGENTS.md` (workspace-level guidance)
- Frontend test agent: `../durion-moqui-frontend/.github/agents/test.agent.md` (if present)

## Notes for Agents
- Keep PII out of telemetry. Do not upload secrets/sensitive data in spans/attributes.
- For frontend incidents, correlate browser traces to backend via gateway route names and `service.version`.
