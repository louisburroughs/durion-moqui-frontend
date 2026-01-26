# GitHub Actions CI/CD - durion-moqui-frontend

Comprehensive CI/CD workflows for the Durion Moqui frontend platform.

## 📋 Workflows Overview

### 1. **ci.yml** - Main CI/CD Pipeline

**Triggers:** Push/PR to main, manual dispatch  
**Path filters:** `runtime/component/**`, `framework/**`, `build.gradle`, `package.json`, `docker/**`

#### Jobs:

- **🏗️ Build Runtime** - Compiles Moqui runtime with Gradle, generates WAR artifact
- **🧪 Test Runtime** - Runs Groovy/Spock tests for Moqui components
- **🎨 Frontend Test** - Jest tests for Vue/TypeScript code, generates coverage
- **✅ Frontend Lint** - ESLint and TypeScript type checking
- **🔒 Security Scan** - npm audit + OWASP dependency checks
- **🐳 Docker Build** - Builds Docker image (on main branch only)
- **🧪 Integration Test** - Full docker-compose stack validation
- **📊 SonarCloud** - Code quality analysis (on main branch)
- **📢 Notify** - Build status aggregation

**Artifacts:**
- `moqui-runtime` - Built WAR file (7 days)
- `component-artifacts` - Component JARs (7 days)
- `runtime-test-results` - Test results (30 days)
- `frontend-coverage` - Code coverage reports (30 days)

---

### 2. **pr-checks.yml** - Smart PR Validation

**Triggers:** PR opened/updated  

#### Jobs:

- **🔍 Detect Changes** - Identifies which areas changed (runtime/frontend/components)
- **⚡ Quick Build** - Fast compilation check (only if runtime changed)
- **🎨 Frontend Quick Check** - Tests + lint (only if frontend changed)
- **📦 Component Changes** - Lists modified components
- **💬 PR Comment** - Auto-posts detailed summary on PR

**Optimization:** Only runs checks for changed code areas

---

### 3. **deploy.yml** - Production Deployment

**Triggers:** GitHub release published, manual dispatch  

#### Jobs:

- **📦 Build Release** - Creates production WAR artifact
- **🐳 Docker Release** - Builds and pushes tagged Docker image
- **🚀 Deploy to Server** - SSH deployment (commented, configure as needed)

**Artifacts:**
- `moqui-release-{tag}` - Versioned WAR (90 days)

---

### 4. **dependency-check.yml** - Maintenance & Security

**Triggers:** Weekly schedule (Monday midnight), manual dispatch  

#### Jobs:

- **📦 Check Gradle Dependencies** - Finds outdated JVM dependencies
- **📦 Check NPM Dependencies** - Finds outdated Node packages
- **🔒 Security Scan** - OWASP vulnerability scanning

**Artifacts:**
- Dependency reports (30 days)
- Security scan reports (90 days)

---

## 🔐 Required Secrets

Add these in: `GitHub → Settings → Secrets and variables → Actions`

### For Docker Push (optional):
```
DOCKER_USERNAME     # Docker Hub username
DOCKER_PASSWORD     # Docker Hub Personal Access Token
```

### For SonarCloud (optional):
```
SONAR_TOKEN         # From sonarcloud.io → My Account → Security
```

### For Slack Notifications (optional):
```
SLACK_WEBHOOK       # Incoming webhook URL from Slack
```

### For SSH Deployment (optional):
```
DEPLOY_HOST         # Server hostname or IP
DEPLOY_USER         # SSH username
DEPLOY_SSH_KEY      # Private SSH key for authentication
```

---

## 🚀 Quick Start

### 1. Enable Workflows

Workflows activate automatically when you:
- Push commits to `main` branch
- Open/update pull requests
- Create GitHub releases

### 2. View Workflow Results

```
GitHub → durion-moqui-frontend → Actions tab
```

### 3. Manual Triggers

**Run any workflow manually:**
```
GitHub → Actions → Select workflow → Run workflow
```

---

## 🔧 Configuration

### Enable Docker Push

In `ci.yml` and `deploy.yml`, uncomment the Docker push sections:

1. Add `DOCKER_USERNAME` and `DOCKER_PASSWORD` secrets
2. Uncomment lines in the workflows
3. Update image names if needed

### Enable SonarCloud

1. Link repository at [sonarcloud.io](https://sonarcloud.io)
2. Get token: My Account → Security → Generate Token
3. Add `SONAR_TOKEN` secret to GitHub
4. Verify project key in `ci.yml` (line 378):
   ```yaml
   -Dsonar.projectKey=louisburroughs_durion-moqui-frontend
   -Dsonar.organization=louisburroughs
   ```

### Enable Slack Notifications

1. Create Slack incoming webhook
2. Add `SLACK_WEBHOOK` secret to GitHub
3. Uncomment notification steps in workflows

---

## 📊 Workflow Features

### Intelligent Path Filtering

Workflows only run when relevant files change:

```yaml
paths:
  - 'runtime/component/**'   # Component changes
  - 'framework/**'           # Framework changes
  - 'build.gradle'           # Build config
  - 'package.json'           # Frontend deps
  - 'docker/**'              # Docker config
```

### Smart Caching

**Gradle cache:**
```yaml
cache: gradle  # Automatic Gradle dependency caching
```

**NPM cache:**
```yaml
cache: 'npm'   # Automatic npm dependency caching
```

**Docker layer cache:**
```yaml
cache-from: type=gha
cache-to: type=gha,mode=max
```

### Parallel Execution

Multiple jobs run concurrently:
- Runtime tests + Frontend tests (parallel)
- Security scan runs independently
- Linting happens separately

---

## 🎯 Best Practices

### Test Coverage

- Target: **80%+ coverage** for critical Vue components
- View reports: Download `frontend-coverage` artifact
- SonarCloud tracks coverage trends

### Code Quality

- Fix linting errors before merging
- Address TypeScript type errors
- Review SonarCloud issues

### Security

- Weekly dependency scans catch vulnerabilities early
- Review npm audit warnings
- Update dependencies regularly

### Performance

- Build time: ~5-10 minutes (with cache)
- PR checks: ~3-5 minutes (only changed areas)
- Use draft PRs for WIP to save CI minutes

---

## 🐛 Troubleshooting

### Build Fails: "Permission denied: gradlew"

**Fix:** File permissions issue. Ensure gradlew is executable:
```bash
git update-index --chmod=+x gradlew
git commit -m "Make gradlew executable"
```

### Docker Build Fails: "Image not found"

**Fix:** Ensure Moqui WAR is built first. Check `build-runtime` job succeeded.

### NPM Tests Fail: "Cannot find module"

**Fix:** Run `npm ci` locally and commit `package-lock.json`:
```bash
npm ci
git add package-lock.json
git commit -m "Update package-lock.json"
```

### SonarCloud: "Project not found"

**Fix:** Verify project key and organization in `ci.yml`:
```
sonarcloud.io → Your Project → Information tab
```

---

## 📈 Monitoring

### GitHub Actions Dashboard

View workflow status:
```
GitHub → Actions → Workflow runs
```

### Artifacts

Download build artifacts:
```
GitHub → Actions → Workflow run → Artifacts section
```

### Badges

Add to README.md:
```markdown
![CI](https://github.com/louisburroughs/durion-moqui-frontend/workflows/Frontend%20CI%2FCD/badge.svg)
```

---

## 🔄 Workflow Dependencies

```
PR Check Flow:
  detect-changes
    ├── quick-build (if runtime changed)
    ├── frontend-quick-check (if frontend changed)
    ├── component-changes (if components changed)
    └── pr-comment (always)

Main CI Flow:
  build-runtime
    ├── test-runtime
    └── docker-build (main only)
         └── integration-test (main only)
  
  frontend-test
    ├── frontend-lint (parallel)
    └── sonarcloud (main only)
  
  security-scan (independent)
```

---

## 📝 Notes

- **Gradle Build**: Uses existing `build.gradle` configuration
- **Node Version**: 18 (matches project requirements)
- **Java Version**: 21 (Temurin distribution)
- **Test Framework**: Jest for frontend, Spock for runtime
- **Docker Compose**: Uses `docker/moqui-postgres-compose.yml`

---

## 🆘 Support

For CI/CD issues:
1. Check workflow logs in Actions tab
2. Review artifact contents
3. Consult [AGENTS.md](../AGENTS.md) for build commands
4. Verify secrets are configured correctly

---

**Last Updated:** January 2026  
**Maintained By:** Durion Platform Team
