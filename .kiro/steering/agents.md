# Agent Integration Guide

## Overview

This project includes a comprehensive set of predefined AI agents in `.github/agents/` that provide specialized expertise for different aspects of Moqui Framework development. These agents can be referenced and integrated into Kiro workflows to provide domain-specific guidance.

## Core Development Agents

### Architecture & Design Agents

#### **architecture-agent.md** - Chief Architect
- **Purpose**: Domain-driven design, architectural integrity, system maintainability
- **Key Capabilities**:
  - Enforces domain boundaries and prevents architectural drift
  - Reviews component dependencies and RACI matrix compliance
  - Validates layering architecture (UI → Services → Entities → Integration)
  - Manages durion-positivity integration patterns
  - Creates Architecture Decision Records (ADRs)
- **When to Use**: Before major architectural changes, component design, cross-domain integrations
- **References**: `.github/docs/architecture/project.json`, RACI matrices, component org charts

#### **api-agent.md** - REST API Specialist  
- **Purpose**: REST API development, error handling, API consistency
- **Key Capabilities**:
  - Designs REST endpoints following Moqui conventions
  - Implements comprehensive error handling and validation
  - Creates API documentation with request/response examples
  - Manages API versioning and backward compatibility
- **When to Use**: Creating new REST endpoints, API error handling, API documentation
- **Integration**: Works with moquiDeveloper-agent for implementation

#### **moquiDeveloper-agent.md** - Primary Implementation Agent
- **Purpose**: Core Moqui Framework code implementation
- **Key Capabilities**:
  - Creates entities, services, screens, transitions, forms
  - Implements business logic following architectural patterns
  - Produces testable, secure, maintainable code
  - Follows domain boundaries and service naming conventions
- **When to Use**: All Moqui code implementation tasks
- **Integration**: Receives designs from architecture-agent, coordinates with validation agents

### Infrastructure & Operations Agents

#### **dev-deploy-agent.md** - DevOps Engineer
- **Purpose**: Local development deployment, containerization, CI/CD
- **Key Capabilities**:
  - Manages Docker containerization and orchestration
  - Implements build pipelines and deployment procedures
  - Ensures security best practices in secrets management
  - Optimizes build times and resource utilization
- **When to Use**: Docker setup, build optimization, deployment issues
- **Security Focus**: Never hardcodes secrets, implements proper secrets management

#### **dba-agent.md** - Database Administrator
- **Purpose**: Database design, performance, migrations
- **Key Capabilities**:
  - Designs database schemas and indexing strategies
  - Manages database migrations and performance optimization
  - Ensures data integrity and security
- **When to Use**: Database schema changes, performance issues, data modeling

#### **sre-agent.md** - Site Reliability Engineer
- **Purpose**: System reliability, monitoring, performance
- **Key Capabilities**:
  - Implements monitoring and observability
  - Manages system reliability and scaling
  - Handles incident response and troubleshooting
- **When to Use**: Performance issues, monitoring setup, reliability concerns

### Quality & Validation Agents

#### **test-agent.md** - Testing Specialist
- **Purpose**: Test strategy, test implementation, quality assurance
- **Key Capabilities**:
  - Creates unit, integration, and contract tests
  - Implements test automation and CI/CD testing
  - Validates functional correctness
- **When to Use**: Test implementation, test strategy, quality validation

#### **lint-agent.md** - Code Quality Enforcer
- **Purpose**: Code formatting, static analysis, style enforcement
- **Key Capabilities**:
  - Enforces coding standards and formatting rules
  - Performs static analysis and code quality checks
  - Maintains consistent code style across components
- **When to Use**: Code quality issues, formatting standards, static analysis

### Specialized Domain Agents

#### **docs-agent.md** - Documentation Specialist
- **Purpose**: Technical documentation, API docs, architecture documentation
- **When to Use**: Creating documentation, API documentation, architectural documentation

#### **accessibility.agent.md** - Accessibility Expert
- **Purpose**: Web accessibility compliance, WCAG guidelines
- **When to Use**: UI development, accessibility audits, compliance requirements

#### **i18n-agent.md** - Internationalization Expert
- **Purpose**: Multi-language support, localization
- **When to Use**: Internationalization requirements, multi-language features

#### Language-Specific Agents
- **java-mcp-expert.agent.md** - Java/MCP integration specialist
- **typescript-agent.md** - TypeScript development
- **vue-agent.md** - Vue.js frontend development
- **python-mcp-expert.agent.md** - Python/MCP integration

## Agent Collaboration Patterns

### Primary Development Workflow

1. **Architecture Design** (`architecture-agent`)
   - Define domain boundaries and component responsibilities
   - Review against RACI matrices and project.json
   - Create architectural decisions and patterns

2. **Implementation** (`moquiDeveloper-agent`)
   - Implement entities, services, screens following architectural guidance
   - Follow domain boundaries and naming conventions
   - Coordinate with specialized agents as needed

3. **API Development** (`api-agent`)
   - Design REST endpoints following Moqui conventions
   - Implement error handling and validation
   - Create API documentation

4. **Quality Validation**
   - **Testing** (`test-agent`) - Create comprehensive test suites
   - **Code Quality** (`lint-agent`) - Enforce formatting and style
   - **Documentation** (`docs-agent`) - Generate technical documentation

5. **Deployment** (`dev-deploy-agent`)
   - Build and containerize applications
   - Manage local development environments
   - Implement CI/CD pipelines

### Cross-Agent Communication

#### Architecture → Implementation
```
architecture-agent defines:
- Domain boundaries and component placement
- Service naming conventions
- Integration patterns via durion-positivity

moquiDeveloper-agent implements:
- Entities following domain ownership
- Services following naming conventions
- Integration through positivity layer only
```

#### Implementation → Validation
```
moquiDeveloper-agent produces:
- Testable code with clear interfaces
- Lint-compliant formatting
- API-contract accurate endpoints

Validation agents verify:
- test-agent: Functional correctness
- lint-agent: Code quality and style
- api-agent: REST contract compliance
```

#### Infrastructure Support
```
dev-deploy-agent provides:
- Docker environments for development
- Build pipelines for CI/CD
- Secrets management for security

dba-agent ensures:
- Optimal database performance
- Proper indexing strategies
- Safe migration procedures
```

## Integration with Kiro Specs

### Spec Creation Workflow

When creating specs, reference appropriate agents:

1. **Requirements Phase**
   - Consult `architecture-agent` for domain classification
   - Reference RACI matrices for responsibility assignment
   - Consider integration patterns from durion-positivity

2. **Design Phase**
   - Use `architecture-agent` for component placement decisions
   - Consult `api-agent` for REST endpoint design
   - Reference `dba-agent` for data modeling decisions

3. **Implementation Planning**
   - Plan tasks using `moquiDeveloper-agent` capabilities
   - Include validation tasks for `test-agent` and `lint-agent`
   - Consider deployment requirements from `dev-deploy-agent`

### Task Execution Integration

When executing spec tasks:

1. **Architecture Tasks** - Reference `architecture-agent` patterns and principles
2. **Implementation Tasks** - Follow `moquiDeveloper-agent` guidelines and boundaries
3. **API Tasks** - Use `api-agent` conventions and error handling patterns
4. **Testing Tasks** - Apply `test-agent` strategies and frameworks
5. **Deployment Tasks** - Follow `dev-deploy-agent` security and containerization practices

## Agent Reference Quick Guide

### By Development Phase

| Phase | Primary Agent | Supporting Agents |
|-------|---------------|-------------------|
| **Architecture** | architecture-agent | dba-agent, sre-agent |
| **API Design** | api-agent | architecture-agent, docs-agent |
| **Implementation** | moquiDeveloper-agent | architecture-agent, lint-agent |
| **Testing** | test-agent | moquiDeveloper-agent, api-agent |
| **Deployment** | dev-deploy-agent | sre-agent, dba-agent |
| **Documentation** | docs-agent | api-agent, architecture-agent |

### By Problem Domain

| Problem | Recommended Agent | Key Capabilities |
|---------|-------------------|------------------|
| **Domain Boundaries** | architecture-agent | DDD, component dependencies, RACI |
| **REST APIs** | api-agent | Endpoint design, error handling, contracts |
| **Code Implementation** | moquiDeveloper-agent | Entities, services, screens, business logic |
| **Database Issues** | dba-agent | Schema design, performance, migrations |
| **Build/Deploy Issues** | dev-deploy-agent | Docker, CI/CD, secrets management |
| **Performance Issues** | sre-agent | Monitoring, scaling, reliability |
| **Code Quality** | lint-agent | Formatting, static analysis, standards |
| **Testing Strategy** | test-agent | Unit tests, integration tests, automation |

## Best Practices for Agent Integration

### 1. **Consult Before Deciding**
- Always reference relevant agents before making architectural decisions
- Use agent expertise to validate approaches and patterns
- Follow established conventions from agent guidelines

### 2. **Maintain Consistency**
- Follow naming conventions from `architecture-agent` and `moquiDeveloper-agent`
- Use error handling patterns from `api-agent`
- Apply security practices from `dev-deploy-agent`

### 3. **Validate Continuously**
- Use `test-agent` patterns for comprehensive testing
- Apply `lint-agent` standards for code quality
- Follow `docs-agent` guidelines for documentation

### 4. **Coordinate Dependencies**
- Respect domain boundaries defined by `architecture-agent`
- Use integration patterns from durion-positivity documentation
- Follow deployment practices from `dev-deploy-agent`

## Agent Documentation Locations

All agent definitions are stored in `.github/agents/` with the following key files:

- **Core Development**: `architecture-agent.md`, `moquiDeveloper-agent.md`, `api-agent.md`
- **Infrastructure**: `dev-deploy-agent.md`, `dba-agent.md`, `sre-agent.md`
- **Quality**: `test-agent.md`, `lint-agent.md`, `docs-agent.md`
- **Specialized**: `accessibility.agent.md`, `i18n-agent.md`, language-specific agents

Each agent file contains:
- Role definition and responsibilities
- Key capabilities and expertise areas
- Integration patterns with other agents
- Specific guidelines and best practices
- Boundaries and limitations

## Implementation Notes

When working with these agents in Kiro:

1. **Reference Explicitly** - Mention specific agents when their expertise is needed
2. **Follow Patterns** - Use established patterns and conventions from agent guidelines
3. **Validate Decisions** - Cross-check decisions against multiple relevant agents
4. **Document Integration** - Record how agent guidance influenced implementation decisions
5. **Maintain Consistency** - Ensure all code follows the collaborative agent model