# Durion Moqui Frontend Operations Runbook

## Overview

This runbook documents operational procedures for the **durion-moqui-frontend** project (Moqui Framework, Java 11, Groovy, Vue.js 3, Quasar, TypeScript). It focuses on building, running, deploying, and troubleshooting the Moqui-based frontend and its Vue/Quasar UI.

This runbook is **frontend-local**. For cross-repository coordination (workspace-level agents, story orchestration, and incidents that span backend and frontend), use the workspace-level runbook in the **durion/workspace-agents** project.

**Related runbooks and specs:**
- Workspace-level: `durion/workspace-agents/docs/OperationsRunbook.md`
- Workspace agent structure: `durion/.kiro/specs/workspace-agent-structure/tasks.md`
- Frontend agent structure plan: `.kiro/specs/agent-structure/tasks.md`

---

## How to Use This Runbook

Use this runbook when you are operating or debugging **durion-moqui-frontend**:

- For **build & test** commands, see section 1.
- For **running locally** (with or without Docker), see section 2.
- For **deployment** guidance, see section 3.
- For **monitoring & troubleshooting** common Moqui/frontend issues, see section 4.
- For **agent-structure / Kiro automation** in this repo, see section 5.

When an issue involves both Moqui/frontend and backend services, start here to ensure the frontend is healthy, then follow the backend and workspace-level runbooks.

---

## 1. Build and Test

### 1.1 Gradle builds

```bash
cd durion-moqui-frontend

# Fast development build (skip tests)
./gradlew build -x test

# Full build with tests
./gradlew build
```

### 1.2 Frontend and agent tests

Depending on configuration, tests may include:
- Spock/JUnit tests for Groovy/Java code
- Jest tests for Vue/TypeScript components
- Property-based tests for Moqui agents (if configured)

```bash
# Run all configured tests
./gradlew test
```

---

## 2. Run Locally

### 2.1 With Docker Compose

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

# Access application
# http://localhost:8080/webroot/
```

### 2.2 Without Docker

```bash
cd durion-moqui-frontend

# Build and create runtime
./gradlew build

# Start Moqui (embedded H2 for development)
java -jar runtime/build/libs/moqui.war

# Access at
# http://localhost:8080/webroot/
```

Stop services with:

```bash
# Docker
docker-compose -f docker/moqui-postgres-compose.yml down
# or clean state
docker-compose -f docker/moqui-postgres-compose.yml down -v
```

---

## 3. Deployment

Deployment patterns (e.g., Docker images, Kubernetes) will be defined by your environment. This section covers the typical steps.

### 3.1 Build artifacts

```bash
cd durion-moqui-frontend

# Ensure a clean build
./gradlew clean build

# Resulting artifacts include the Moqui runtime and any packaged web assets.
```

### 3.2 Environment configuration

Ensure production/staging environment variables are set securely (DB connection, admin credentials, external service endpoints). Never commit secrets to the repository.

---

## 4. Monitoring and Troubleshooting

### 4.1 Basic health checks

Moqui health is generally inferred from:
- HTTP 200 responses from key entry points (`/webroot/`, application-specific routes)
- Log output (no repeated exceptions)
- Database connectivity

```bash
# Basic HTTP check (adjust host/port)
curl -f http://<host>:<port>/webroot/ || echo "frontend health check failed"

# Check logs in Docker setup
cd durion-moqui-frontend
docker-compose -f docker/moqui-postgres-compose.yml logs -f moqui
```

### 4.2 Common issues

**Issue: Moqui fails to start**
- Check database configuration (host, port, user, password)
- Ensure required DB schemas exist
- Review logs for stack traces

```bash
# Docker logs
cd durion-moqui-frontend
docker-compose -f docker/moqui-postgres-compose.yml logs moqui
```

**Issue: Frontend cannot reach backend APIs**
- Confirm backend services (durion-positivity-backend) are healthy and reachable
- Verify the durion-positivity component configuration under `runtime/component/durion-positivity/`
- Check CORS, gateway, or proxy configuration if requests are blocked

When cross-layer issues arise, pair this section with the backend and workspace-level runbooks.

---

## 5. Agent Structure and Kiro Automation

The frontend agent structure plan for this repo lives in:

- `.kiro/specs/agent-structure/tasks.md`

To advance frontend agent-structure work in a controlled way, use the Kiro helper script. Each run completes **exactly one** unchecked task and updates the associated HANDOFF file.

```bash
cd durion-moqui-frontend

# Run a single Kiro step for the frontend agent structure
MAX_STEPS=1 ./kiro-run-agent-structure.zsh
```

Use this when you want an agent or automation to apply one more change to the agent structure without overstepping into unrelated work.

---

## 6. When to Escalate to Workspace-Level Runbook

Use the workspace-level runbook in `durion/workspace-agents/docs/OperationsRunbook.md` when:

- Issues clearly involve both frontend (Moqui/Vue) and backend services
- Story orchestration outputs (story-sequence, frontend-/backend-coordination) and actual behaviour diverge
- You need coordinated SRE/DR actions across multiple repositories

Keep this frontend runbook focused on Moqui/JS concerns and treat the workspace-level runbook as the source of truth for cross-repo operations.
