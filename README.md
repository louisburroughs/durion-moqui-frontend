# Durion Frontend Platform

A modern frontend application built on Moqui Framework for the Tire Industry Open Technology Foundation (TIOTF). This project provides responsive web interfaces for ERP, e-commerce, project management, and manufacturing operations using Vue.js 3, Quasar, and TypeScript.

**Part of:** [Tire Industry Open Technology Foundation (TIOTF)](https://www.tiotf.org) | **Organization:** [Durion Solutions, Inc.](https://www.durioneq.com)

## Overview

[![license](https://img.shields.io/badge/license-CC0%201.0%20Universal-blue.svg)](LICENSE.md)
[![Moqui Framework](https://img.shields.io/badge/Built%20with-Moqui%20Framework-blue)](https://www.moqui.org)
[![Vue.js 3](https://img.shields.io/badge/Frontend-Vue.js%203-4FC08D?logo=vue.js)](https://vuejs.org)
[![TypeScript](https://img.shields.io/badge/Language-TypeScript%205.x-3178C6?logo=typescript)](https://www.typescriptlang.org)
[![Java 11](https://img.shields.io/badge/Java-11-ED8B00?logo=java)](https://www.oracle.com/java/)

## About Durion

Durion Solutions, Inc. is the operational subsidiary of the Tire Industry Open Technology Foundation (TIOTF), a Delaware 501(c)(6) non-profit dedicated to open, vendor-neutral technology for the tire, fleet, and service-provider industries.

This frontend platform provides:

- **Modern Web UI** - Responsive interfaces built with Vue.js 3 and TypeScript
- **Business Functionality** - E-commerce, project management, manufacturing operations, and master data management
- **Enterprise Integration** - Seamless integration with Moqui Framework backend
- **Accessibility & Performance** - Built for both desktop and mobile experiences
├── runtime/
│   ├── component/                 # Business components
│   │   ├── PopCommerce/           # E-commerce domain
│   │   ├── HiveMind/              # Project management
│   │   ├── MarbleERP/             # Manufacturing
│   │   ├── SimpleScreens/         # UI components
│   │   ├── mantle-udm/            # Universal data model
│   │   ├── mantle-usl/            # Reusable services
│   │   ├── moqui-fop/             # FOP integration
│   │   ├── example/               # Example/demo
│   │   ├── durion-accounting/     # Durion accounting
│   │   ├── durion-common/         # Shared UI/utilities
│   │   ├── durion-crm/            # Durion CRM
│   │   ├── durion-demo-data/      # Demo datasets
│   │   ├── durion-experience/     # Frontend experience
│   │   ├── durion-inventory/      # Inventory management
│   │   ├── durion-positivity/     # Integration with durion-positivity-backend
│   │   ├── durion-product/        # Product catalogs
│   │   ├── durion-theme/          # Theme assets
│   │   └── durion-workexec/       # Work execution
│   ├── conf/                       # Configuration
│   ├── db/                         # Database scripts
│   ├── lib/                        # Libraries
│   ├── sessions/                   # Session store
│   ├── log/                        # Logs
│   └── template/                   # Templates

#### 2. Build Application

```bash
# Development build (skip tests for faster iteration)
./gradlew build -x test

# Full build with tests
./gradlew build
```

#### 3. Run Locally with Docker Compose

```bash
# Create development environment file (NEVER commit this!)
cat > .env.local << EOF
DB_USER=moqui_user
DB_PASSWORD=dev_password
ADMIN_PASSWORD=admin_password
EOF

chmod 600 .env.local

# Start all services (PostgreSQL, Moqui, Elasticsearch, Nginx)
docker-compose -f docker/moqui-postgres-compose.yml up -d

# Check logs
docker-compose -f docker/moqui-postgres-compose.yml logs -f moqui

# Access application
open http://localhost:8080/webroot/
```

#### 4. Stop Services

```bash
docker-compose -f docker/moqui-postgres-compose.yml down

# Remove data (clean slate)
docker-compose -f docker/moqui-postgres-compose.yml down -v
```

### Alternative: Run Without Docker

```bash
# Build and create runtime
./gradlew build

# Start Moqui (using embedded H2 database for development)
java -jar runtime/build/libs/moqui.war

# Access at http://localhost:8080/webroot/
```

## Project Structure

```
.
├── framework/                      # Moqui Framework core
│   ├── entity/                    # Core entity definitions
│   ├── service/                   # Core service implementations
│   ├── screen/                    # Core screens
│   └── src/                       # Java/Groovy source code
├── runtime/
│   ├── component/                 # Business components
│   │   ├── PopCommerce/           # E-commerce domain (orders, products, pricing)
│   │   ├── HiveMind/              # Project management and collaboration
│   │   ├── MarbleERP/             # Manufacturing and MRP
│   │   ├── SimpleScreens/         # Reusable dashboard/UI components
│   │   ├── mantle-udm/            # Universal Data Model (core entities)
│   │   ├── mantle-usl/            # Universal Service Library (reusable patterns)
│   │   ├── moqui-fop/             # FOP integration for document rendering
│   │   ├── example/               # Example/demo components
│   │   ├── durion-accounting/     # Durion accounting domain
│   │   ├── durion-common/         # Shared UI components, styles, utilities
│   │   ├── durion-crm/            # Customer relationship management
│   │   ├── durion-demo-data/      # Demo datasets for Durion
│   │   ├── durion-experience/     # Frontend experience layer (themes/layouts)
│   │   ├── durion-inventory/      # Inventory management
│   │   ├── durion-positivity/     # Integration with durion-positivity-backend
│   │   ├── durion-product/        # Product master data and catalogs
│   │   ├── durion-theme/          # Durion theme assets and configuration
│   │   └── durion-workexec/       # Work execution and operations
│   │   └── durion-hr/             # Manage People, Roles and Permissions
│   │   └── durion-shopmgr/        # Manage the shop , mechanics and appointments
│   ├── conf/                      # Configuration files
│   ├── db/                        # Database scripts
│   ├── lib/                       # Runtime libraries
│   ├── sessions/                  # Session store
│   ├── log/                       # Application logs
│   └── template/                  # Template files
├── docker/                        # Docker build & compose files
│   ├── moqui-postgres-compose.yml # PostgreSQL dev environment
│   ├── moqui-mysql-compose.yml    # MySQL alternative
│   ├── build-compose-up.sh        # Docker build and startup script
│   ├── clean.sh                   # Docker cleanup script
│   ├── compose-down.sh            # Docker stop script
│   ├── compose-up.sh              # Docker start script
│   └── simple/                    # Simple Dockerfile for custom builds
├── .github/
│   ├── agents/                    # AI agent guides (40+ specialized agents)
│   │   ├── architecture-agent.md  # System architecture & DDD guidance
│   │   ├── api-agent.md           # REST API development patterns
│   │   ├── vue-agent.md           # Vue.js 3 frontend expert
│   │   ├── moquiDeveloper-agent.md # Moqui implementation patterns
│   │   ├── sre-agent.md           # SRE and observability
│   │   ├── test-agent.md          # Testing strategies (Spock/Jest)
│   │   └── ...
│   ├── docs/
│   │   ├── architecture/          # Architecture documentation
│   │   ├── adr/                   # Architecture Decision Records
│   │   └── governance/            # Governance policies
│   ├── mcp-servers/               # Model Context Protocol server configs
│   ├── instructions/              # Development instructions
│   ├── prompts/                   # Agent prompts
│   └── workflows/                 # GitHub Actions workflows
├── .ai/                           # AI context files
│   ├── context.md                 # Project context for agents
│   └── glossary.md                # Domain terminology glossary
├── .kiro/                         # Kiro steering documentation
│   ├── specs/                     # Specifications
│   ├── steering/                  # Steering guidance
│   └── templates/                 # Documentation templates
├── gradle/                        # Gradle wrapper
├── scripts/                       # Build and utility scripts
├── build.gradle                   # Root build configuration
├── settings.gradle                # Gradle multi-project configuration
├── MoquiInit.properties           # Application initialization
├── addons.xml                     # Moqui addons configuration
├── myaddons.xml                   # Project-specific addons
└── README.md                      # This file
```

## Development

### Architecture & Design

This project follows **Domain-Driven Design (DDD)** principles:

- **Bounded Contexts** - Each domain owns its entities, services, and screens
- **Layering** - UI → Services → Entities (strict separation)
- **Integration Pattern** - External systems via durion-positivity component to durion-positivity-backend
- **Service Naming** - Consistent: `{domain}.{service-type}#{Action}`

See [Architecture Guide](.github/agents/architecture-agent.md) for detailed design principles.

### REST API Development

All REST endpoints follow consistent patterns:

- **Endpoint Structure** - `/rest/api/v{version}/{resource}/{id}/{action}`
- **Error Handling** - Standardized error codes and messages
- **Validation** - Input validation at service layer
- **Authorization** - Role-based access control

See [API Agent Guide](.github/agents/api-agent.md) for REST API design patterns.

### Database

#### PostgreSQL (Recommended)

```bash
# Start PostgreSQL service
docker-compose -f docker/postgres-compose.yml up -d

# Connect to database
docker exec -it moqui-dev-postgres psql -U moqui_user -d moqui
```

#### MySQL

```bash
# Alternative MySQL setup
docker-compose -f docker/moqui-mysql-compose.yml up -d
```

## Development Workflow

### Frontend Development

```bash
# Install frontend dependencies
npm install

# Run development server with hot reload
npm run dev

# Build for production
npm run build

# Run linter and type checking
npm run lint
npm run type-check
```

### Backend Development

```bash
# Build backend
./gradlew build -x test

# Run with hot reload
./gradlew build --continuous
```

### Testing

```bash
# Run all tests (backend + frontend)
./gradlew test
npm run test

# Run specific test suites
./gradlew test --tests "ComponentNameTest"
npm run test -- ComponentName

# Test with coverage
./gradlew test --profile
npm run test:coverage
```

### Building & Deployment

#### Development Build

```bash
./gradlew build -x test  # Skip tests for faster builds
```

#### Docker Image

```bash
# Build development image
docker build -t moqui-dev:latest -f docker/simple/Dockerfile .

# Run container
docker run -p 8080:8080 moqui-dev:latest
```

See [Dev-Deploy Agent](.github/agents/dev-deploy-agent.md) for comprehensive deployment guidance.

## Documentation

### Documentation

### Frontend Development

- **[Vue.js 3 Guide](.github/instructions/vuejs3.instructions.md)** - Frontend development standards with Composition API & TypeScript
- **[TypeScript 5 Guide](.github/instructions/typescript-5-es2022.instructions.md)** - TypeScript configuration and best practices
- **[Workspace Configuration](.vscode/)** - VSCode settings for optimal development experience

### Core Documentation

- **[Moqui Framework Docs](https://www.moqui.org/docs/framework)** - Official backend framework documentation
- **[Community Guide](https://www.moqui.org/docs/moqui/Community+Guide)** - Moqui community resources

### Project Architecture & Governance

- **[Architecture Guides](.github/docs/architecture/)** - System design and domain models
- **[Governance Documents](.github/docs/governance/)** - TIOTF and Durion organizational structure
- **[Architecture Decision Records](.github/adr/)** - Major architectural decisions

### Components

- **PopCommerce/** - E-commerce order and product management
- **HiveMind/** - Project management and collaboration platform
- **SimpleScreens/** - Reusable UI components and screens
- **MarbleERP/** - Manufacturing and resource planning
- **mantle-udm/** - Universal Data Model (core entities)
- **mantle-usl/** - Universal Service Library (reusable patterns)
- **moqui-fop/** - FOP integration for document rendering
- **example/** - Example/demo components
- **durion-accounting/** - Durion accounting domain screens and services
- **durion-common/** - Shared UI, styles, and utilities for Durion
- **durion-crm/** - Customer relationship management for Durion
- **durion-demo-data/** - Demo datasets for Durion components
- **durion-experience/** - Frontend experience layer (themes/layouts)
- **durion-inventory/** - Inventory management for Durion
- **durion-positivity/** - Integration component for durion-positivity-backend services
- **durion-product/** - Product master data and catalogs
- **durion-theme/** - Durion theme assets and configuration
- **durion-workexec/** - Work execution and operations
- **durion-hr/** - People Management
- **durion-shopmgr/** - Shop Management

## AI Agents

This repository includes specialized AI agents to guide development:

### Architecture Agent

Reviews architectural decisions and enforces domain boundaries.

- Prevents architectural drift
- Validates layering rules
- Manages cross-domain dependencies

**Usage:** Consult for system design, feature decomposition, and architectural reviews.

### API Agent

Guides REST API development with consistent patterns.

- Designs endpoints following REST standards
- Implements comprehensive error handling
- Creates API documentation and tests

**Usage:** Create new REST endpoints, design error handlers, document API contracts.

### Dev-Deploy Agent

Manages builds, containerization, and local deployment.

- Orchestrates Docker builds and deployments
- Secures secrets and credentials
- Optimizes build performance

**Usage:** Build application, deploy to local Docker environment, troubleshoot deployments.

## Troubleshooting

### Java Version Issues

```bash
# Set up Java 11 with sdkman
sdk install java 11.0.x-tem
sdk use java 11.0.x-tem

# Or use .sdkmanrc for automatic version switching
sdk env install
```

### Frontend Build Issues

```bash
# Clear node modules and reinstall
rm -rf node_modules package-lock.json
npm install

# Clear npm cache
npm cache clean --force

# Check Node.js version compatibility
node --version  # Should be 16+
```

### Application Won't Start

```bash
# Check logs
docker logs moqui-dev-app

# Verify database connectivity
docker-compose exec moqui psql -h postgres -U moqui_user -d moqui -c "SELECT 1"

# Rebuild without cache
docker-compose down
docker-compose build --no-cache
docker-compose up
```

### Build Failures

```bash
# Clean and rebuild
./gradlew clean build

# Check Java version
java -version  # Should be 11+

# Check disk space
df -h
```

### Database Issues

```bash
# Start fresh database
docker-compose down -v
docker-compose up -d postgres

# Initialize schema
docker-compose exec moqui gradle load
```

## Contributing

1. **Understand Architecture** - Review [architecture guides](.github/docs/architecture/) for design decisions
2. **Follow Code Standards** - See [code review guidelines](.github/instructions/code-review-generic.instructions.md)
3. **Create Feature Branch** - `git checkout -b feature/your-feature`
4. **Develop & Test** - Write code and tests following project standards
5. **Code Review** - Submit PR with clear description and testing results
6. **Merge** - After all checks and reviews pass

### Code Standards

- **Frontend**: Vue.js 3 with Composition API, TypeScript, ESLint + Prettier
- **Backend**: Java/Groovy following Moqui conventions
- **Security**: Follow OWASP guidelines (see [security-and-owasp.instructions.md](.github/instructions/security-and-owasp.instructions.md))
- **Performance**: Follow performance optimization guidelines (see [performance-optimization.instructions.md](.github/instructions/performance-optimization.instructions.md))

## Community & Support

### Project Resources

- **Issues** - [GitHub Issues](https://github.com/louisburroughs/durion-moqui-frontend/issues)
- **Discussions** - GitHub Discussions (if enabled)
- **Architecture Docs** - [.github/docs/architecture/](.github/docs/architecture/)

### Moqui Framework Resources

- **Forum** - [forum.moqui.org](https://forum.moqui.org)
- **Google Group** - [Moqui Google Group](https://groups.google.com/d/forum/moqui)
- **Stack Overflow** - Tag questions with `[moqui]`
- **Gitter Chat** - [Moqui Gitter](https://gitter.im/moqui/moqui-framework)

### TIOTF & Durion

- **TIOTF Website** - [tiotf.org](https://www.tiotf.org)
- **Durion Website** - [durioneq.com](https://www.durion.org)

## License

See [LICENSE.md](LICENSE.md) for details. This project uses CC0 1.0 Universal (Public Domain) licensing, consistent with Moqui Framework.

## Quick Reference

### Common Commands

```bash
# Frontend
npm install                              # Install dependencies
npm run dev                              # Start dev server
npm run build                            # Production build
npm run lint                             # Run linter
npm run type-check                       # TypeScript check

# Backend
./gradlew build                          # Full build with tests
./gradlew build -x test                  # Skip tests
./gradlew build --continuous             # Watch mode
./gradlew :runtime:component:PopCommerce:build  # Component build

# Test
./gradlew test                           # Backend tests
npm run test                             # Frontend tests

# Docker
docker-compose up -d                     # Start services
docker-compose down                      # Stop services
docker-compose logs -f                   # Follow logs

# Database
docker exec -it moqui-dev-postgres psql -U moqui_user -d moqui
```

### Development URLs

- **Application** - http://localhost:8080/webroot/
- **Frontend Dev** - http://localhost:5173 (if running `npm run dev`)
- **Database** - localhost:5432 (PostgreSQL)
- **API Base** - http://localhost:8080/rest/

---

**Built with ❤️ for the tire, fleet, and service-provider industries by [TIOTF](https://www.tiotf.org) | Operated by [Durion Solutions](https://www.durioneq.com)**
