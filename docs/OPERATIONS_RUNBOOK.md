# Durion Moqui Frontend Operations Runbook

**Last Updated**: 2026-01-31  
**Stack**: Moqui Framework, Java 11, Groovy, Vue.js 3, Quasar, TypeScript

---

## Overview

This runbook documents operational procedures for the **durion-moqui-frontend** project. It focuses on building, running, deploying, and troubleshooting the Moqui-based frontend and its Vue/Quasar UI.

**Related runbooks:**
- Platform-level: `durion/docs/OPERATIONS_RUNBOOK.md`
- Backend: `durion-positivity-backend/docs/OPERATIONS_RUNBOOK.md`

---

## Running Locally

### With Docker Compose (Recommended)

```bash
cd durion-moqui-frontend

# (Optional) Create a local env file (do NOT commit secrets)
cat > .env.local << EOF
DB_USER=moqui_user
DB_PASSWORD=dev_password
ADMIN_PASSWORD=admin_password
EOF
chmod 600 .env.local

# Start PostgreSQL + Moqui stack
docker-compose -f docker/moqui-postgres-compose.yml up -d

# Follow logs
docker-compose -f docker/moqui-postgres-compose.yml logs -f moqui

# Access application at http://localhost/webroot/ (port 80)
```

### Without Docker (Development)

```bash
cd durion-moqui-frontend

# Build and create runtime
./gradlew build

# Start Moqui (embedded H2 for development)
java -jar runtime/build/libs/moqui.war

# Access at http://localhost:8080/webroot/
```

### Custom Port Configuration

```bash
# Embedded Jetty with custom port
java -jar runtime/build/libs/moqui.war port=8180 threads=100
```

For Docker, edit `docker/moqui-postgres-compose.yml`:
```yaml
nginx-proxy:
  ports:
    - 8180:80    # Map external 8180 to internal 80
```

### Stop Services

```bash
# Docker
docker-compose -f docker/moqui-postgres-compose.yml down

# Clean state (removes volumes)
docker-compose -f docker/moqui-postgres-compose.yml down -v
```

---

## Build and Test

### Gradle Builds

```bash
# Fast development build (skip tests)
./gradlew build -x test

# Full build with tests
./gradlew build

# Clean build
./gradlew clean build
```

### Frontend Tests

```bash
# Run all configured tests (Spock/JUnit for Groovy/Java, Jest for Vue/TS)
./gradlew test

# Run frontend tests only
npm test
```

### Linting

```bash
# Check for violations
npm run lint

# Auto-fix issues
npm run lint:fix

# Check API gateway compliance
npm run lint:api-gateway
```

---

## Deployment

### Build Artifacts

```bash
cd durion-moqui-frontend

# Ensure a clean build
./gradlew clean build

# Artifacts: runtime/build/libs/moqui.war
```

### Environment Configuration

Required environment variables for production:
- Database connection (host, port, user, password)
- Admin credentials
- External service endpoints
- SSL/TLS certificates

**Never commit secrets to the repository.**

---

## Monitoring and Health Checks

### Basic Health Checks

```bash
# HTTP check
curl -f http://<host>:<port>/webroot/ || echo "frontend health check failed"

# Check Docker logs
docker-compose -f docker/moqui-postgres-compose.yml logs -f moqui
```

### What to Monitor

- HTTP 200 responses from key entry points (`/webroot/`, application routes)
- Log output (no repeated exceptions)
- Database connectivity
- Backend API connectivity (via durion-positivity gateway)

---

## Troubleshooting

### Issue: Moqui fails to start

**Symptoms**: Application won't start, crashes on boot

**Resolution**:
1. Check database configuration (host, port, user, password)
2. Ensure required DB schemas exist
3. Review logs for stack traces

```bash
# Docker logs
docker-compose -f docker/moqui-postgres-compose.yml logs moqui

# Embedded Jetty logs
# Check console output or runtime/log/
```

### Issue: Frontend cannot reach backend APIs

**Symptoms**: API calls fail, "Service Unavailable" errors

**Resolution**:
1. Confirm backend services (durion-positivity-backend) are healthy and reachable
2. Verify the durion-positivity component configuration under `runtime/component/durion-positivity/`
3. Check CORS, gateway, or proxy configuration if requests are blocked
4. Verify network connectivity between frontend and backend containers

### Issue: ESLint or pre-commit failures

**Symptoms**: Commits rejected, lint errors

**Resolution**:
```bash
# Reinstall dependencies
rm -rf node_modules package-lock.json
npm install

# Re-setup husky
npx husky install

# Check lint status
npm run lint

# Auto-fix what's fixable
npm run lint:fix
```

### Issue: Port conflicts

**Symptoms**: "Address already in use" errors

**Resolution**:
```bash
# Find what's using the port
lsof -i :8080

# Use a different port
java -jar runtime/build/libs/moqui.war port=8180

# Or update docker-compose ports section
```

### Issue: Database connection failures

**Symptoms**: Entity errors, connection timeouts

**Resolution**:
1. Verify database container is running: `docker ps`
2. Check database logs: `docker logs moqui-database`
3. Verify credentials in environment variables
4. Test connectivity: `psql -h 127.0.0.1 -p 5432 -U moqui moqui`

---

## Cross-Layer Troubleshooting

When issues span frontend and backend:

1. **Start here** — Ensure frontend is healthy (logs, HTTP checks)
2. **Check API gateway** — Verify durion-positivity component is working
3. **Check backend** — Use backend runbook for service health
4. **Check network** — Verify connectivity between services
5. **Escalate** — Use platform-level runbook for coordinated response

---

## Agent Structure and Automation

The frontend agent structure plan lives in:
- `.kiro/specs/agent-structure/tasks.md`

To advance agent-structure work:
```bash
cd durion-moqui-frontend

# Run a single Kiro step for the frontend agent structure
MAX_STEPS=1 ./kiro-run-agent-structure.zsh
```

---

## Quick Reference

| Task | Command |
|------|---------|
| Start (Docker) | `docker-compose -f docker/moqui-postgres-compose.yml up -d` |
| Start (Local) | `java -jar runtime/build/libs/moqui.war` |
| Stop (Docker) | `docker-compose -f docker/moqui-postgres-compose.yml down` |
| Build (fast) | `./gradlew build -x test` |
| Build (full) | `./gradlew build` |
| Logs (Docker) | `docker-compose -f docker/moqui-postgres-compose.yml logs -f moqui` |
| Health check | `curl -f http://localhost:8080/webroot/` |

---

## References

- **Architecture Guide**: [ARCHITECTURE_GUIDE.md](./ARCHITECTURE_GUIDE.md)
- **Development Guide**: [DEVELOPMENT_GUIDE.md](./DEVELOPMENT_GUIDE.md)
- **Platform Runbook**: `durion/docs/OPERATIONS_RUNBOOK.md`
- **Backend Runbook**: `durion-positivity-backend/docs/OPERATIONS_RUNBOOK.md`
