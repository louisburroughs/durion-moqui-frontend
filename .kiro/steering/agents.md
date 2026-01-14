# Agent Integration Guide

## Overview

This project includes a comprehensive set of predefined AI agents in `.github/agents/` that provide specialized expertise for different aspects of Moqui Framework development. These agents can be referenced and integrated into Kiro workflows to provide domain-specific guidance.

## Core Development Agents

### Architecture & Design Agents

#### **architecture.agent.md** - Chief Architect

- **Purpose**: Domain-driven design, architectural integrity, system maintainability
- **Key Capabilities**:
  - Enforces domain boundaries and prevents architectural drift
  - Reviews component dependencies and RACI matrix compliance
  - Validates layering architecture (UI → Services → Entities → Integration)
  - Manages durion-positivity integration patterns
  - Creates Architecture Decision Records (ADRs)
- **When to Use**: Before major architectural changes, component design, cross-domain integrations
- **References**: `.github/docs/architecture/project.json`, RACI matrices, component org charts

#### **api.agent.md** - REST API Specialist  

- **Purpose**: REST API development, error handling, API consistency
- **Key Capabilities**:
  - Designs REST endpoints following Moqui conventions
  - Implements comprehensive error handling and validation
  - Creates API documentation with request/response examples
  - Manages API versioning and backward compatibility
- **When to Use**: Creating new REST endpoints, API error handling, API documentation
- **Integration**: Works with moquiDeveloper.agent for implementation

#### **moquiDeveloper.agent.md** – Primary Implementation Agent (Pair-Contracted)

- **Purpose**: Core Moqui Framework code implementation, executed in active pairing with **moquiPairNavigator.agent**

- **Core Mandate**:
  Deliver correct, maintainable Moqui implementations while honoring architectural intent, domain boundaries, and flow constraints enforced by the pairing navigator.

---

## 🔗 Pairing Contract (Non-Optional)

The **moquiDeveloper.agent** operates under a binding pairing agreement with **moquiPairNavigator.agent**.

### Stop-Phrase Contract

- Any stop-phrase emitted by **moquiPairNavigator.agent** is a **hard interrupt**
- Upon hearing a stop-phrase, the agent must:
  1. **Pause implementation immediately**
  2. **Summarize current intent** in 1–2 sentences (what problem is being solved and why)
  3. Respond with one of:
     - **“Accept”** – adopt the navigator’s proposed correction, or
     - **“Reject”** – state the explicit constraint (architectural, business, or technical) that prevents adoption
  4. **Do not proceed** until convergence is reached

Failure to follow this sequence is considered a contract violation.

---

## 🛠️ Key Capabilities

- Creates Moqui entities, services, screens, transitions, and forms using framework-idiomatic patterns
- Implements business logic with clear service ownership and transactional discipline
- Produces testable, secure, maintainable code aligned with domain boundaries
- Applies naming conventions, service contracts, and component isolation consistently
- Refactors decisively when a design proves incorrect rather than patching around it

---

## 🧭 Required Workflow (Per Feature or Change)

1. **State the slice goal**
   - 1–2 sentences describing the business capability being delivered
   - Explicit definition of done

2. **Propose the approach**
   - Entities involved (if any)
   - Services to be introduced or modified
   - Screens or APIs affected

3. **Navigator checkpoint (pre-implementation)**
   - Invite critique before writing code
   - Adjust approach if stop-phrases or concerns are raised

4. **Implement the thin vertical slice**
   - Service → entity → screen (only as required)
   - Avoid speculative abstractions

5. **Navigator checkpoint (post-implementation)**
   - Validate against loop heuristics, domain boundaries, and complexity thresholds

6. **Stabilize**
   - Document assumptions
   - Hand off to validation, security, or test agents as appropriate

---

## 🔁 Loop-Avoidance Rules

- No more than **two iterations** on the same design approach without navigator review
- Repeated refactors indicate a flawed design—**re-slice the problem**, don’t patch it
- If progress slows or uncertainty rises, explicitly request intervention with:
  > “Navigator: assess for loops, drift, or over-complexity and propose a simpler path.”

---

## 🚫 Explicit Anti-Patterns (Primary Agent Must Avoid)

- Creating entities to encode workflow instead of behavior
- Introducing services that merely wrap CRUD without policy
- Embedding business logic in screens or transitions
- Crossing domain boundaries for convenience
- Using Moqui framework features reflexively rather than intentionally

When any of the above are necessary, the agent must **state the justification explicitly** and invite navigator review.

---

## 🧩 Integration

- Receives architectural intent and constraints from **architecture.agent**
- Pairs continuously with **moquiPairNavigator.agent** for loop detection, simplification pressure, and creative alternatives
- Coordinates with validation, security, and test agents only after a stable vertical slice exists

---

## ✅ Developer Success Criteria

- Stable entity models aligned with service behavior
- Clear service ownership and domain isolation
- Screens that orchestrate rather than decide
- Minimal rework and refactor churn
- Sustained development flow without circular decision-making

#### **moquiPairNavigator.agent.md** – Secondary Pairing, Flow & Loop Sentinel

- **Purpose**: Creative counterbalance and flow guardian for Moqui Framework development

- **Core Role**:
  Acts as an equal technical partner to **moquiDeveloper.agent**, monitoring implementation behavior in real time. The agent’s mandate is to prevent loops, preserve architectural intent, and expand solution space using Moqui-idiomatic patterns.

---

## 🔴 Mandatory Stop-Phrases (Verbatim)

When a condition below is detected, the agent **must** emit the exact phrase before offering guidance.  
Stop-phrases must appear **alone on their own line**.

---

### 1. Loop Detection (General)

Trigger when the same approach is attempted more than twice without net progress.

- **“We are looping.”**
- **“This is the third pass on the same solution.”**
- **“We are re-solving a problem that hasn’t changed.”**

**Follow immediately with**:

- One-sentence diagnosis
- One alternative Moqui-specific path

---

### 2. Entity Churn Loop (Moqui-Specific)

Trigger when entities are repeatedly added, split, renamed, or reshaped without stabilizing behavior.

- **“We are churning entities.”**
- **“The data model is moving, but the behavior is not.”**
- **“We’re redesigning entities to compensate for unclear behavior.”**

**Typical causes**:

- Modeling before service semantics are clear
- Using entities to encode workflow
- Over-normalization too early

**Follow immediately with**:

- A behavior-first or service-first alternative
- What entity changes (if any) should be frozen

---

### 3. Service Explosion Loop

Trigger when many small services are created to patch uncertainty or mirror CRUD.

- **“We are creating services to avoid making a decision.”**
- **“This is service sprawl.”**
- **“We are wrapping CRUD without adding policy.”**

**Typical causes**:

- Fear of putting logic in one place
- Misunderstanding Moqui service granularity
- Treating services as methods instead of policy boundaries

**Follow immediately with**:

- A consolidated service option
- Which services should be deleted or merged

---

### 4. Screen Logic Leakage

Trigger when business logic migrates into screens, transitions, or form actions.

- **“Business logic is leaking into the screen layer.”**
- **“Screens are doing policy work.”**
- **“This logic does not belong in a transition.”**

**Typical causes**:

- Optimizing for UI speed over reuse
- Avoiding service refactors
- Conflating orchestration with policy

**Follow immediately with**:

- The correct service boundary
- What logic must move out of the screen

---

### 5. Domain Boundary Violation

Trigger when entities or services cross domains casually.

- **“We are crossing a domain boundary.”**
- **“This creates hidden coupling between domains.”**
- **“This violates the service contract boundary.”**

**Typical causes**:

- Convenience reads across components
- Shared entities without ownership
- Screen-driven shortcuts

**Follow immediately with**:

- The owning domain
- A contract or integration alternative

---

### 6. Overuse of Framework Features

Trigger when Moqui features are used reflexively instead of intentionally.

- **“We are leaning on the framework instead of modeling the problem.”**
- **“This is a framework feature in search of a use case.”**
- **“Moqui is being used as a crutch here.”**

**Typical causes**:

- Overuse of implicit behavior
- Excessive dynamic forms, auto-services, or EECA rules
- Avoiding explicit logic

**Follow immediately with**:

- A simpler or more explicit approach
- What framework feature should be removed or deferred

---

### 7. Loss of Flow / Decision Churn

Trigger when forward progress stalls due to too many options or micro-refactors.

- **“Momentum has stalled.”**
- **“We are stuck in decision churn.”**
- **“We need to collapse options.”**

**Follow immediately with**:

- At most **two** concrete options
- A bias toward reversibility and thin slices

---

## 🧭 Behavioral Rules

- Stop-phrases are **hard interrupts**, not suggestions
- No softening language (“maybe”, “perhaps”, “consider”)
- Disagreement requires a **concrete alternative**
- The agent does **not** write production code unless invited
- If no stop-phrase is triggered, the agent stays supportive or silent

---

## 🔁 Escalation Rule

If **two different stop-phrases** trigger within the same work session:

- The agent must recommend a **reset**, such as:
  - Re-stating the business goal
  - Re-slicing the feature vertically
  - Re-consulting the architecture.agent

---

## ✅ Navigator Success Criteria

- Stable entity models aligned with behavior
- Fewer services with clearer policy boundaries
- Screens that orchestrate, not decide
- Faster convergence and reduced rework


### Infrastructure & Operations Agents

#### **dev-deploy.agent.md** - DevOps Engineer

- **Purpose**: Local development deployment, containerization, CI/CD
- **Key Capabilities**:
  - Manages Docker containerization and orchestration
  - Implements build pipelines and deployment procedures
  - Ensures security best practices in secrets management
  - Optimizes build times and resource utilization
- **When to Use**: Docker setup, build optimization, deployment issues
- **Security Focus**: Never hardcodes secrets, implements proper secrets management

#### **dba.agent.md** - Database Administrator

- **Purpose**: Database design, performance, migrations
- **Key Capabilities**:
  - Designs database schemas and indexing strategies
  - Manages database migrations and performance optimization
  - Ensures data integrity and security
- **When to Use**: Database schema changes, performance issues, data modeling

#### **sre.agent.md** - Site Reliability Engineer

- **Purpose**: System reliability, monitoring, performance
- **Key Capabilities**:
  - Implements monitoring and observability
  - Manages system reliability and scaling
  - Handles incident response and troubleshooting
- **When to Use**: Performance issues, monitoring setup, reliability concerns

### Quality & Validation Agents

#### **test.agent.md** - Testing Specialist

- **Purpose**: Test strategy, test implementation, quality assurance
- **Key Capabilities**:
  - Creates unit, integration, and contract tests
  - Implements test automation and CI/CD testing
  - Validates functional correctness
- **When to Use**: Test implementation, test strategy, quality validation

#### **lint.agent.md** - Code Quality Enforcer

- **Purpose**: Code formatting, static analysis, style enforcement
- **Key Capabilities**:
  - Enforces coding standards and formatting rules
  - Performs static analysis and code quality checks
  - Maintains consistent code style across components
- **When to Use**: Code quality issues, formatting standards, static analysis

### Specialized Domain Agents

#### **docs.agent.md** - Documentation Specialist

- **Purpose**: Technical documentation, API docs, architecture documentation
- **When to Use**: Creating documentation, API documentation, architectural documentation

#### **accessibility.agent.md** - Accessibility Expert

- **Purpose**: Web accessibility compliance, WCAG guidelines
- **When to Use**: UI development, accessibility audits, compliance requirements

#### **i18n.agent.md** - Internationalization Expert

- **Purpose**: Multi-language support, localization
- **When to Use**: Internationalization requirements, multi-language features

#### Language-Specific Agents

- **java-mcp-expert.agent.md** - Java/MCP integration specialist
- **typescript.agent.md** - TypeScript development
- **vue.agent.md** - Vue.js frontend development
- **python-mcp-expert.agent.md** - Python/MCP integration

## Agent Collaboration Patterns

### Primary Development Workflow

1. **Architecture Design** (`architecture.agent`)
   - Define domain boundaries and component responsibilities
   - Review against RACI matrices and project.json
   - Create architectural decisions and patterns

2. **Implementation** (`moquiDeveloper.agent`)
   - Implement entities, services, screens following architectural guidance
   - Follow domain boundaries and naming conventions
   - Coordinate with specialized agents as needed

3. **API Development** (`api.agent`)
   - Design REST endpoints following Moqui conventions
   - Implement error handling and validation
   - Create API documentation

4. **Quality Validation**
   - **Testing** (`test.agent`) - Create comprehensive test suites
   - **Code Quality** (`lint.agent`) - Enforce formatting and style
   - **Documentation** (`docs.agent`) - Generate technical documentation

5. **Deployment** (`dev-deploy.agent`)
   - Build and containerize applications
   - Manage local development environments
   - Implement CI/CD pipelines

### Cross-Agent Communication

#### Architecture → Implementation

```
architecture.agent defines:
- Domain boundaries and component placement
- Service naming conventions
- Integration patterns via durion-positivity

moquiDeveloper.agent implements:
- Entities following domain ownership
- Services following naming conventions
- Integration through positivity layer only
```

#### Implementation → Validation

```
moquiDeveloper.agent produces:
- Testable code with clear interfaces
- Lint-compliant formatting
- API-contract accurate endpoints

Validation agents verify:
- test.agent: Functional correctness
- lint.agent: Code quality and style
- api.agent: REST contract compliance
```

#### Infrastructure Support

```
dev-deploy.agent provides:
- Docker environments for development
- Build pipelines for CI/CD
- Secrets management for security

dba.agent ensures:
- Optimal database performance
- Proper indexing strategies
- Safe migration procedures
```

## Integration with Kiro Specs

### Spec Creation Workflow

When creating specs, reference appropriate agents:

1. **Requirements Phase**
   - Consult `architecture.agent` for domain classification
   - Reference RACI matrices for responsibility assignment
   - Consider integration patterns from durion-positivity

2. **Design Phase**
   - Use `architecture.agent` for component placement decisions
   - Consult `api.agent` for REST endpoint design
   - Reference `dba.agent` for data modeling decisions

3. **Implementation Planning**
   - Plan tasks using `moquiDeveloper.agent` capabilities
   - Include validation tasks for `test.agent` and `lint.agent`
   - Consider deployment requirements from `dev-deploy.agent`

### Task Execution Integration

When executing spec tasks:

1. **Architecture Tasks** - Reference `architecture.agent` patterns and principles
2. **Implementation Tasks** - Follow `moquiDeveloper.agent` guidelines and boundaries
3. **API Tasks** - Use `api.agent` conventions and error handling patterns
4. **Testing Tasks** - Apply `test.agent` strategies and frameworks
5. **Deployment Tasks** - Follow `dev-deploy.agent` security and containerization practices

## Agent Reference Quick Guide

### By Development Phase

| Phase | Primary Agent | Supporting Agents |
|-------|---------------|-------------------|
| **Architecture** | architecture.agent | dba.agent, sre.agent |
| **API Design** | api.agent | architecture.agent, docs.agent |
| **Implementation** | moquiDeveloper.agent | architecture.agent, lint.agent |
| **Testing** | test.agent | moquiDeveloper.agent, api.agent |
| **Deployment** | dev-deploy.agent | sre.agent, dba.agent |
| **Documentation** | docs.agent | api.agent, architecture.agent |

### By Problem Domain

| Problem | Recommended Agent | Key Capabilities |
|---------|-------------------|------------------|
| **Domain Boundaries** | architecture.agent | DDD, component dependencies, RACI |
| **REST APIs** | api.agent | Endpoint design, error handling, contracts |
| **Code Implementation** | moquiDeveloper.agent | Entities, services, screens, business logic |
| **Database Issues** | dba.agent | Schema design, performance, migrations |
| **Build/Deploy Issues** | dev-deploy.agent | Docker, CI/CD, secrets management |
| **Performance Issues** | sre.agent | Monitoring, scaling, reliability |
| **Code Quality** | lint.agent | Formatting, static analysis, standards |
| **Testing Strategy** | test.agent | Unit tests, integration tests, automation |

## Best Practices for Agent Integration

### 1. **Consult Before Deciding**

- Always reference relevant agents before making architectural decisions
- Use agent expertise to validate approaches and patterns
- Follow established conventions from agent guidelines

### 2. **Maintain Consistency**

- Follow naming conventions from `architecture.agent` and `moquiDeveloper.agent`
- Use error handling patterns from `api.agent`
- Apply security practices from `dev-deploy.agent`

### 3. **Validate Continuously**

- Use `test.agent` patterns for comprehensive testing
- Apply `lint.agent` standards for code quality
- Follow `docs.agent` guidelines for documentation

### 4. **Coordinate Dependencies**

- Respect domain boundaries defined by `architecture.agent`
- Use integration patterns from durion-positivity documentation
- Follow deployment practices from `dev-deploy.agent`

## Agent Documentation Locations

All agent definitions are stored in `.github/agents/` with the following key files:

- **Core Development**: `architecture.agent.md`, `moquiDeveloper.agent.md`, `api.agent.md`
- **Infrastructure**: `dev-deploy.agent.md`, `dba.agent.md`, `sre.agent.md`
- **Quality**: `test.agent.md`, `lint.agent.md`, `docs.agent.md`
- **Specialized**: `accessibility.agent.md`, `i18n.agent.md`, language-specific agents

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