# Development Instructions Integration

## Overview

This project includes comprehensive development instructions in `.github/instructions/` that provide detailed guidance for specific technologies, languages, and practices. These instructions should be integrated into all development work to ensure consistency, quality, and security.

## Available Instructions

### Language-Specific Instructions

#### **groovy.instructions.md** - Moqui Groovy Development
- **Scope**: `**/*.groovy`, `**/*Service.groovy`, `**/*RestEvents.groovy`
- **Key Guidelines**:
  - Use `@CompileStatic` for better performance and IDE support
  - Leverage Groovy's concise syntax (safe navigation `?.`, Elvis operator `?:`)
  - Follow Moqui service patterns and naming conventions
  - Implement proper error handling and resource management
  - Use static typing for method parameters and return values
- **Integration**: Apply when implementing Moqui services, entities, and REST endpoints

#### **java.instructions.md** - Java Development Standards
- **Scope**: `**/*.java`
- **Key Guidelines**:
  - Use Java Records for data classes and DTOs
  - Leverage pattern matching and type inference (`var`)
  - Favor immutability and use `Optional<T>` for null handling
  - Follow Google's Java style guide for naming conventions
  - Implement proper resource management with try-with-resources
- **Integration**: Apply when creating Java components, utilities, and framework extensions

#### **typescript-5-es2022.instructions.md** - TypeScript Development
- **Scope**: `**/*.ts`, `**/*.tsx`
- **Key Guidelines**:
  - Use strict TypeScript configuration
  - Leverage modern ES2022+ features
  - Implement proper type definitions and interfaces
  - Use utility types and generics effectively
- **Integration**: Apply when developing frontend components and API integrations

#### **vuejs3.instructions.md** - Vue.js 3 Development
- **Scope**: `**/*.vue`, `**/*.ts`, `**/*.js`, `**/*.scss`
- **Key Guidelines**:
  - Use Composition API with `<script setup>` syntax
  - Implement TypeScript integration with `defineProps` and `defineEmits`
  - Use Pinia for state management
  - Follow component design principles and performance optimization
  - Ensure accessibility and security best practices
- **Integration**: Apply when building frontend interfaces and user experiences

### Cross-Cutting Instructions

#### **security-and-owasp.instructions.md** - Security Guidelines
- **Scope**: All files (`*`)
- **Key Guidelines**:
  - Enforce principle of least privilege and deny-by-default access control
  - Use strong cryptographic algorithms (Argon2, bcrypt, AES-256)
  - Prevent injection attacks with parameterized queries and input validation
  - Implement secure session management and authentication
  - Never hardcode secrets; use environment variables or secret stores
- **Integration**: Apply to all development work, especially authentication, data handling, and API development

#### **performance-optimization.instructions.md** - Performance Best Practices
- **Scope**: All files (`*`)
- **Key Guidelines**:
  - Measure first, optimize second using profiling tools
  - Optimize algorithms and data structures for common use cases
  - Implement efficient caching strategies with proper invalidation
  - Use asynchronous I/O and avoid blocking operations
  - Minimize resource usage (memory, CPU, network, disk)
- **Integration**: Apply during code reviews, performance-critical implementations, and optimization tasks

#### **code-review-generic.instructions.md** - Code Review Standards
- **Scope**: All files (`**`)
- **Key Guidelines**:
  - Prioritize issues: Critical (security, correctness) → Important (quality, tests) → Suggestions (readability)
  - Check for clean code principles, proper error handling, and security vulnerabilities
  - Verify test coverage and quality
  - Ensure architectural consistency and documentation
- **Integration**: Use during all code reviews and quality assessments

### Specialized Instructions

#### **durion-thought-logging.instructions.md** - Process Tracking
- **Scope**: All files (`**`)
- **Purpose**: Structured approach to complex development tasks
- **Key Guidelines**:
  - Follow 4-phase process: Initialization → Planning → Execution → Summary
  - Create `\Durion-Processing.md` for tracking progress
  - Work silently without verbose announcements
  - Execute phases sequentially without combining
- **Integration**: Use for complex feature development and multi-step implementations

#### **localization.instructions.md** - Internationalization
- **Purpose**: Multi-language support and localization
- **Integration**: Apply when implementing user-facing features that require internationalization

#### **markdown.instructions.md** - Documentation Standards
- **Purpose**: Consistent documentation formatting and structure
- **Integration**: Apply when creating or updating documentation files

## Integration Strategies

### 1. **Spec Creation Integration**

When creating Kiro specs, reference relevant instructions:

```markdown
## Technical Requirements
- Follow `groovy.instructions.md` for Moqui service implementation
- Apply `security-and-owasp.instructions.md` for authentication and data handling
- Use `performance-optimization.instructions.md` for database query optimization
- Implement according to `vuejs3.instructions.md` for frontend components
```

### 2. **Task Execution Integration**

When executing spec tasks, apply instruction guidelines:

- **Architecture Tasks**: Reference `code-review-generic.instructions.md` for quality standards
- **Implementation Tasks**: Apply language-specific instructions (`groovy.instructions.md`, `java.instructions.md`)
- **API Tasks**: Follow `security-and-owasp.instructions.md` and `performance-optimization.instructions.md`
- **Frontend Tasks**: Use `vuejs3.instructions.md` and `typescript-5-es2022.instructions.md`
- **Complex Tasks**: Apply `durion-thought-logging.instructions.md` for structured execution

### 3. **Code Review Integration**

Use instruction guidelines during code reviews:

1. **Security Review**: Apply `security-and-owasp.instructions.md` checklist
2. **Performance Review**: Use `performance-optimization.instructions.md` guidelines
3. **Language Review**: Apply relevant language-specific instructions
4. **Quality Review**: Follow `code-review-generic.instructions.md` standards

### 4. **Agent Coordination Integration**

Coordinate instructions with predefined agents:

| Agent | Primary Instructions | Supporting Instructions |
|-------|---------------------|------------------------|
| **moquiDeveloper-agent** | `groovy.instructions.md`, `java.instructions.md` | `security-and-owasp.instructions.md`, `performance-optimization.instructions.md` |
| **api-agent** | `security-and-owasp.instructions.md` | `performance-optimization.instructions.md`, `groovy.instructions.md` |
| **architecture-agent** | `code-review-generic.instructions.md` | `performance-optimization.instructions.md`, `security-and-owasp.instructions.md` |
| **test-agent** | `code-review-generic.instructions.md` | Language-specific instructions for test implementation |
| **dev-deploy-agent** | `security-and-owasp.instructions.md` | `performance-optimization.instructions.md` |

## Instruction Application Patterns

### 1. **By File Type**

Apply instructions based on file extensions:

```
*.groovy → groovy.instructions.md + security-and-owasp.instructions.md
*.java → java.instructions.md + security-and-owasp.instructions.md  
*.vue → vuejs3.instructions.md + typescript-5-es2022.instructions.md
*.ts → typescript-5-es2022.instructions.md + performance-optimization.instructions.md
*.md → markdown.instructions.md
```

### 2. **By Development Phase**

Apply instructions based on development phase:

- **Planning**: `durion-thought-logging.instructions.md`, `code-review-generic.instructions.md`
- **Implementation**: Language-specific instructions + `security-and-owasp.instructions.md`
- **Testing**: `code-review-generic.instructions.md` + language-specific testing guidelines
- **Review**: `code-review-generic.instructions.md` + `performance-optimization.instructions.md`
- **Documentation**: `markdown.instructions.md` + `localization.instructions.md`

### 3. **By Component Type**

Apply instructions based on Moqui component type:

- **Entities**: `groovy.instructions.md` + `security-and-owasp.instructions.md`
- **Services**: `groovy.instructions.md` + `performance-optimization.instructions.md`
- **Screens**: `vuejs3.instructions.md` + `typescript-5-es2022.instructions.md`
- **REST APIs**: `groovy.instructions.md` + `security-and-owasp.instructions.md`
- **Utilities**: `java.instructions.md` + `performance-optimization.instructions.md`

## Quality Assurance Integration

### Automated Checks

Integrate instruction compliance into automated checks:

1. **Static Analysis**: Configure tools to enforce instruction guidelines
2. **Code Review**: Use instruction checklists during reviews
3. **CI/CD**: Include instruction compliance in build pipelines
4. **Documentation**: Generate compliance reports based on instruction adherence

### Manual Reviews

Use instructions during manual code reviews:

1. **Security Review**: Apply OWASP guidelines from `security-and-owasp.instructions.md`
2. **Performance Review**: Use optimization patterns from `performance-optimization.instructions.md`
3. **Code Quality**: Follow standards from `code-review-generic.instructions.md`
4. **Language Compliance**: Check against language-specific instructions

## Best Practices for Instruction Integration

### 1. **Consistent Application**
- Always reference relevant instructions when starting new development work
- Use instruction guidelines as checklists during implementation
- Apply instructions consistently across all team members and projects

### 2. **Contextual Usage**
- Select appropriate instructions based on the specific task and technology
- Combine multiple instructions when working on cross-cutting concerns
- Prioritize security and performance instructions for critical components

### 3. **Continuous Improvement**
- Update instructions based on lessons learned and new best practices
- Regularly review instruction compliance and effectiveness
- Integrate feedback from code reviews and quality assessments

### 4. **Documentation Integration**
- Reference specific instructions in spec requirements and design decisions
- Document instruction compliance in implementation notes
- Include instruction references in code comments where appropriate

## Instruction Compliance Checklist

When completing development work, verify compliance with relevant instructions:

- [ ] **Security**: Applied `security-and-owasp.instructions.md` guidelines
- [ ] **Performance**: Followed `performance-optimization.instructions.md` best practices
- [ ] **Language Standards**: Adhered to language-specific instruction guidelines
- [ ] **Code Quality**: Met `code-review-generic.instructions.md` standards
- [ ] **Documentation**: Used `markdown.instructions.md` formatting standards
- [ ] **Process**: Followed `durion-thought-logging.instructions.md` for complex tasks

## Integration with Kiro Workflow

### Spec Creation
1. **Requirements Phase**: Reference security and performance instructions for non-functional requirements
2. **Design Phase**: Apply architectural and language-specific instructions for technical design
3. **Task Planning**: Include instruction compliance as task acceptance criteria

### Task Execution
1. **Implementation**: Apply relevant language and technology instructions
2. **Testing**: Use code review instructions for test quality standards
3. **Documentation**: Follow markdown and localization instructions

### Quality Assurance
1. **Code Review**: Use instruction checklists for comprehensive reviews
2. **Performance Testing**: Apply performance optimization guidelines
3. **Security Audit**: Follow OWASP security instruction guidelines

This integration ensures that all development work follows established best practices, maintains consistency across the project, and meets quality, security, and performance standards.