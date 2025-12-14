# Technology Stack

## Core Framework
- **Moqui Framework** - Java-based enterprise application framework
- **Java 11+** - Required runtime (configured in gradle.properties)
- **Gradle** - Build system with wrapper (gradlew/gradlew.bat)
- **Groovy** - Scripting language for business logic and services

## Database Support
- **PostgreSQL 12+** - Recommended production database
- **MySQL 5.7+** - Alternative production database  
- **H2** - Embedded database for development/testing
- **Derby** - Alternative embedded database

## Search & Analytics
- **OpenSearch 2.4.0** - Primary search engine (successor to Elasticsearch)
- **Elasticsearch 7.10.2** - Legacy search engine support
- Full-text search, analytics, and data indexing

## Containerization
- **Docker** - Container runtime
- **Docker Compose** - Multi-container orchestration
- **nginx-proxy** - Reverse proxy for virtual hosting
- **SSL/TLS** - Certificate management and secure connections

## Build System

### Common Commands

```bash
# Development Build (fast iteration)
./gradlew build -x test

# Full Build with Tests
./gradlew build

# Clean Build
./gradlew clean build

# Component-Specific Build
./gradlew :runtime:component:PopCommerce:build

# Test Commands
./gradlew test                           # All tests
./gradlew test --tests "OrderTest"       # Specific test
./gradlew :runtime:component:HiveMind:test  # Component tests
```

### Docker Commands

```bash
# Start Development Environment
docker-compose -f docker/moqui-postgres-compose.yml up -d

# View Logs
docker-compose -f docker/moqui-postgres-compose.yml logs -f moqui

# Stop Services
docker-compose -f docker/moqui-postgres-compose.yml down

# Clean Restart (removes data)
docker-compose -f docker/moqui-postgres-compose.yml down -v
```

### Database Management

```bash
# Clean Database (H2/Derby)
./gradlew cleanDb

# Start Search Engine
./gradlew startElasticSearch

# Stop Search Engine  
./gradlew stopElasticSearch
```

### Git Operations

```bash
# Update All Repositories
./gradlew gitPullAll

# Check Status Across All Repos
./gradlew gitStatusAll

# Checkout Branch Across All Repos
./gradlew gitCheckoutAll -Pbranch=develop
```

## Configuration Files

- **build.gradle** - Root build configuration and tasks
- **settings.gradle** - Multi-project build settings
- **gradle.properties** - Build environment properties
- **MoquiInit.properties** - Runtime initialization settings
- **addons.xml** - Component repository definitions
- **myaddons.xml** - Local component overrides (not in repo)

## Development Tools

- **IntelliJ IDEA** - Recommended IDE with XML catalog support
- **setupIntellij** - Gradle task to configure IDE autocomplete
- **Gradle Wrapper** - Ensures consistent build environment
- **SSL Configuration** - Built-in certificate handling for development

## Runtime Requirements

- **Java 11+** - Minimum runtime version
- **2GB+ RAM** - Recommended for development
- **PostgreSQL** - For production deployments
- **Docker & Docker Compose** - For containerized development