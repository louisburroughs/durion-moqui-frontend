# GitHub Copilot Instructions – Durion Moqui Frontend

These instructions guide AI coding agents working in this repository.

## Big Picture

- This repo is the **Durion Frontend Platform**: a Moqui Framework application with business components under [runtime/component](../runtime/component) and core framework under [framework](../framework).
- The UI stack is **Vue 3 + Quasar + TypeScript 5** layered on top of Moqui screens and REST APIs provided by components like [runtime/component/durion-experience](../runtime/component/durion-experience) and [runtime/component/durion-positivity](../runtime/component/durion-positivity).
- The system follows **DDD-style bounded contexts**: each `durion-*` component (for example [runtime/component/durion-inventory](../runtime/component/durion-inventory), [runtime/component/durion-accounting](../runtime/component/durion-accounting)) owns its UI, services, and data model.

## Component & Coding Conventions

- Treat each directory under [runtime/component](../runtime/component) as a Moqui component; when adding features, mirror the existing structure of that component (entities, services, screens, Vue assets) instead of inventing new layouts.
- Service names follow the pattern documented in the root [README](../README.md): `{domain}.{service-type}#Action` and REST endpoints follow `/rest/api/v{version}/{resource}/{id}/{action}`; keep new services and endpoints consistent with those patterns.
- When touching integration with the Java/Spring backend, use the existing bridge in [runtime/component/durion-positivity](../runtime/component/durion-positivity) rather than introducing ad‑hoc HTTP calls; align with the contracts defined in the backend repo.
- For frontend work, follow the patterns in the Vue/Quasar components already present in Durion components and the guidance in the Vue/TypeScript instruction sets (see below) instead of introducing unrelated frameworks.

## Builds, Running, and Tests

- **Backend/Moqui build:** use Gradle from the repo root:
  - Fast dev build: `./gradlew build -x test`
  - Full build with tests: `./gradlew build`
- **Run with Docker (recommended for Moqui + DB):** use the compose files in [docker](../docker):
  - Dev stack (PostgreSQL, Moqui, Nginx): `docker-compose -f docker/moqui-postgres-compose.yml up -d` (requires a `.env.local` as shown in [README](../README.md)).
- **Run without Docker:** after a build, start Moqui directly with `java -jar runtime/build/libs/moqui.war` and access `http://localhost:8080/webroot/`.
- **Frontend tooling:** from the project root, use the npm scripts defined in [package.json](../package.json): `npm install`, `npm run dev`, `npm run build`, plus `npm run lint` / `npm run type-check` for quality gates.
- **Testing:** server-side tests use Spock/Groovy via Gradle, and frontend tests use Jest for Vue/TypeScript; follow the patterns and commands described in the testing agent under [.github/agents/test.agent.md](./agents/test.agent.md).

## Documentation, Governance, and ADRs

- Start with the root [README](../README.md) for an overview of architecture, domains, and key directories.
- Architecture and decision records live under [.github/docs](./docs) (for example architecture docs and ADRs); prefer following those decisions rather than introducing new patterns.
- TIOTF and Durion governance context is in [.github/docs/governance](./docs/governance); respect these constraints when making security, data, or interoperability changes.
- Domain terminology and shared context for AI agents is captured in [.ai/context.md](../.ai/context.md) and [.ai/glossary.md](../.ai/glossary.md); consult these to keep naming and behavior aligned with the business domain.

## Working with Agents, Instructions, and MCP Servers

- This repo already defines many specialized AI agents under [.github/agents](./agents) (for example `architecture-agent`, `api-agent`, `vue-agent`, `moquiDeveloper-agent`, `sre-agent`, `test-agent`); when generating code or docs, keep responsibilities consistent with these roles rather than mixing concerns.
- Language- and framework-specific standards live in [.github/instructions](./instructions) (for example `groovy.instructions`, `java.instructions`, `vuejs3.instructions`, `typescript-5-es2022.instructions`, `security-and-owasp.instructions`); prefer those rules over generic advice.
- Model Context Protocol servers configured in [.github/mcp-servers](./mcp-servers) expose agents, instructions, and project analysis; when suggesting MCP usage, align with the commands and environment variables documented in [mcp-servers/README.md](./mcp-servers/README.md).
- When adding new docs or components, link them from the closest relevant README (component-level or under `.github/docs`) so both humans and agents can discover them.
