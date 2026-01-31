# Durion Moqui Frontend Documentation

This directory contains consolidated documentation for the durion-moqui-frontend project.

## Documentation

| Document | Description |
|----------|-------------|
| [ARCHITECTURE_GUIDE.md](./ARCHITECTURE_GUIDE.md) | API gateway architecture, component patterns, port configuration |
| [DEVELOPMENT_GUIDE.md](./DEVELOPMENT_GUIDE.md) | Build commands, ESLint enforcement, creating API clients, testing |
| [OPERATIONS_RUNBOOK.md](./OPERATIONS_RUNBOOK.md) | Running locally, deployment, monitoring, troubleshooting |

## Quick Links

- **Build**: `./gradlew build -x test`
- **Run**: `java -jar runtime/build/libs/moqui.war`
- **Lint**: `npm run lint`
- **Test**: `npm test`

## Related Documentation

- Platform-level: `durion/docs/`
- Backend: `durion-positivity-backend/docs/`
- ADR-0010: Frontend Domain Responsibilities Guide
