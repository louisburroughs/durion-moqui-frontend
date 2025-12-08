# Agent Collaboration Framework

This document describes how the various AI agents work together in this repository to ensure high-quality, maintainable code.

## Agent Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                      architecture_agent                          │
│         (Chief Architect - Design & Domain Boundaries)           │
└────────────────────────────┬────────────────────────────────────┘
                             │
                    ┌────────┴────────┐
                    │   Provides:     │
                    │   - Designs     │
                    │   - Boundaries  │
                    │   - Patterns    │
                    └────────┬────────┘
                             │
                             ▼
         ┌───────────────────────────────────────────────┐
         │         moqui_developer_agent                 │
         │    (Implementation - Turns Design into Code)  │
         └───────┬───────────────────────────┬───────────┘
                 │                           │
        ┌────────┴────────┐         ┌───────┴────────┐
        │  Consults:      │         │  Produces:     │
        │  - dba_agent    │         │  - Entities    │
        │  - sre_agent    │         │  - Services    │
        │  - dev_deploy   │         │  - Screens     │
        └─────────────────┘         │  - APIs        │
                                    │  - Vue UI      │
                                    └────────┬───────┘
                                             │
                    ┌────────────────────────┴────────────────────────┐
                    │              Submits to:                        │
                    │                                                 │
         ┌──────────▼───────┐  ┌──────────▼───────┐  ┌─────────▼────────┐
         │   test_agent     │  │   lint_agent     │  │   api_agent      │
         │  (QA Testing)    │  │  (Code Quality)  │  │  (API Contracts) │
         └──────────┬───────┘  └──────────┬───────┘  └─────────┬────────┘
                    │                     │                     │
                    └─────────────────────┴─────────────────────┘
                                          │
                                          ▼
                            ┌─────────────────────────┐
                            │  Validation Results     │
                            │  ← Fix & Resubmit →    │
                            │  moqui_developer_agent  │
                            └─────────────────────────┘

┌──────────────────────────────────────────────────────────────────────────┐
│                      Supporting Infrastructure Agents                     │
├──────────────────┬──────────────────┬──────────────────┬────────────────┤
│   dba_agent      │   sre_agent      │ dev_deploy_agent │  docs_agent    │
│ (Schema/DB)      │ (Observability)  │ (Docker/Deploy)  │ (Documentation)│
└──────────────────┴──────────────────┴──────────────────┴────────────────┘
```

## Agent Roles & Responsibilities

### Core Agents

#### `architecture_agent` - Chief Architect
**Role:** Define and enforce architectural boundaries, patterns, and domain structure

**Responsibilities:**
- Design system architecture and domain boundaries
- Enforce layering (UI → Services → Entities → Integration)
- Approve all architectural changes
- Maintain RACI matrices and component dependencies
- Review implementations for architectural compliance

**Authority:** Highest - all agents must consult before making architectural decisions

---

#### `moqui_developer_agent` - Implementation Expert
**Role:** Turn approved designs into working Moqui code

**Responsibilities:**
- Implement entities, services, screens, APIs, Vue components
- Follow architectural designs from `architecture_agent`
- Instrument code with metrics per `sre_agent` guidelines
- Consult `dba_agent` before schema changes
- Submit all code to validation agents
- Resolve failures before completion

**Authority:** Implementation decisions within approved design boundaries

---

### Validation Agents

#### `test_agent` - QA Engineer
**Role:** Ensure code quality through comprehensive testing

**Responsibilities:**
- Generate unit, integration, and contract tests
- Execute test suites and analyze results
- Report failures to `moqui_developer_agent`
- Document coverage gaps
- Validate observability instrumentation

**Authority:** Can block code completion until tests pass

---

#### `lint_agent` - Code Quality Engineer
**Role:** Enforce style and static analysis standards

**Responsibilities:**
- Run Checkstyle, CodeNarc, XML validation
- Enforce formatting and naming conventions
- Report violations to `moqui_developer_agent`
- Maintain linting configuration

**Authority:** Can block code completion until violations are resolved

---

#### `api_agent` - API Architect
**Role:** Design and validate REST APIs and contracts

**Responsibilities:**
- Design REST endpoint structures
- Implement error handling and validation
- Validate API contracts and integration compliance
- Document API specifications
- Test error scenarios

**Authority:** Can block API changes until contracts are validated

---

### Infrastructure Agents

#### `dba_agent` - Database Administrator
**Role:** Ensure optimal database design and performance

**Responsibilities:**
- Design entity schemas and indexes
- Provide performance tuning recommendations
- Review all schema changes before approval
- Monitor database security vulnerabilities
- Optimize queries in services

**Authority:** Must approve all schema changes

---

#### `sre_agent` - Site Reliability Engineer
**Role:** Ensure observability and operational excellence

**Responsibilities:**
- Define functional and RED metrics
- Guide instrumentation with OpenTelemetry
- Configure metrics export to Grafana
- Document all metrics in METRICS.md
- Ensure zero business logic impact

**Authority:** Must approve observability patterns

---

#### `dev_deploy_agent` - DevOps Engineer
**Role:** Manage containerization and local deployment

**Responsibilities:**
- Build Docker images and compose configurations
- Deploy applications to dev environments
- Manage secrets and security
- Set up database and infrastructure containers
- Support test environments

**Authority:** Controls deployment pipeline and infrastructure

---

#### `docs_agent` - Technical Writer
**Role:** Create and maintain comprehensive documentation

**Responsibilities:**
- Document services, entities, and screens
- Create component documentation
- Write API documentation
- Document architectural decisions
- Create METRICS.md files

**Authority:** Can request clarification but cannot block implementation

---

## Collaboration Workflows

### Feature Implementation Workflow

```
1. architecture_agent → Design approved
                    ↓
2. moqui_developer_agent → Consult dba_agent (schema)
                         → Consult sre_agent (metrics)
                         → Implement code
                    ↓
3. moqui_developer_agent → Submit to validation agents
                    ↓
4. test_agent → Run tests → Report results
   lint_agent → Run linters → Report violations
   api_agent → Validate contracts → Report issues
                    ↓
5. moqui_developer_agent → Fix all failures
                         → Resubmit to validation
                    ↓
6. architecture_agent → Final structural approval
                    ↓
7. docs_agent → Document implementation
```

### Schema Change Workflow

```
1. architecture_agent → Approve entity ownership
                    ↓
2. dba_agent → Design schema and indexes
             → Approve schema structure
                    ↓
3. moqui_developer_agent → Implement entity definition
                         → Add instrumentation
                    ↓
4. test_agent → Validate entity tests
   lint_agent → Validate XML formatting
                    ↓
5. dev_deploy_agent → Deploy with migrations
                    ↓
6. docs_agent → Document entity and relationships
```

### API Development Workflow

```
1. architecture_agent → Approve API boundary
                    ↓
2. api_agent → Design endpoint structure
             → Define error handling
                    ↓
3. moqui_developer_agent → Implement endpoint
                         → Add metrics (sre_agent)
                         → Optimize queries (dba_agent)
                    ↓
4. test_agent → API contract tests
   api_agent → Validate contracts
   lint_agent → Code quality checks
                    ↓
5. docs_agent → Document API endpoints
```

## Agent Communication Patterns

### Request-Response
- `moqui_developer_agent` → `dba_agent`: "Can I create this entity?"
- `dba_agent` → `moqui_developer_agent`: "Yes, with these indexes..."

### Consultation
- `moqui_developer_agent` consults `sre_agent` before instrumenting code
- `api_agent` consults `architecture_agent` before defining boundaries

### Validation
- `moqui_developer_agent` submits to `test_agent` → receives pass/fail
- `moqui_developer_agent` submits to `lint_agent` → receives violations

### Approval
- `architecture_agent` approves final implementation
- `dba_agent` approves schema changes

## Key Principles

1. **Architecture First**: All work starts with `architecture_agent` approval
2. **Implementation Focus**: `moqui_developer_agent` is the execution engine
3. **Quality Gates**: Validation agents must pass before completion
4. **Domain Boundaries**: Never cross without `architecture_agent` approval
5. **Schema Control**: All schema changes through `dba_agent`
6. **Observability**: All business logic instrumented per `sre_agent`
7. **Documentation**: All implementations documented by `docs_agent`

## Escalation Paths

### Conflicting Requirements
**Escalate to:** `architecture_agent`
**Example:** DBA wants denormalization, but it violates domain boundary

### Performance vs. Design Trade-offs
**Escalate to:** `architecture_agent` + `dba_agent` + `sre_agent`
**Example:** Query optimization requires cross-domain entity access

### API Contract Changes
**Escalate to:** `architecture_agent` + `api_agent`
**Example:** Breaking change needed for business requirement

### Security Concerns
**Escalate to:** `architecture_agent` + `dev_deploy_agent`
**Example:** New dependency with security implications

## Success Criteria

Code is complete when:
- ✅ `architecture_agent` approved design
- ✅ `moqui_developer_agent` implemented code
- ✅ `dba_agent` approved schema (if applicable)
- ✅ `sre_agent` verified instrumentation
- ✅ `test_agent` all tests passing
- ✅ `lint_agent` no violations
- ✅ `api_agent` contracts validated (if applicable)
- ✅ `dev_deploy_agent` deployed successfully
- ✅ `docs_agent` documentation complete

## Agent Invocation

To invoke a specific agent in your prompts, reference them by name:

```
@architecture_agent Please review this domain boundary decision...
@moqui_developer_agent Implement this service following the approved design...
@test_agent Run comprehensive tests on the order processing module...
@dba_agent Review this entity definition for performance...
@sre_agent How should I instrument this payment processing service?
```

## References

- Individual agent files: `.github/agents/*.md`
- Architecture documentation: `.github/docs/architecture/`
- Project structure: `project.json`, RACI matrices
