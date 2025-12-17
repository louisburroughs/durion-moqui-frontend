package agents.quality

import agents.interfaces.PairNavigatorAgent
import agents.models.AgentRequest
import agents.models.AgentResponse
import agents.models.ImplementationContext

/**
 * Pair Navigator Agent - Quality assurance and loop detection
 * 
 * Monitors implementation behavior in real-time to prevent loops, preserve architectural intent,
 * and expand solution space using Moqui-idiomatic patterns.
 * 
 * Requirements: Cross-requirement (Quality assurance support)
 * Test Cases: Integration with all other agents
 */
class PairNavigatorAgentImpl implements PairNavigatorAgent {
    
    private static final String AGENT_ID = "pair-navigator-agent"
    private static final String AGENT_NAME = "Pair Navigator Agent"
    
    // Loop detection thresholds
    private static final int LOOP_THRESHOLD = 3
    private static final int ENTITY_CHURN_THRESHOLD = 4
    private static final int SERVICE_EXPLOSION_THRESHOLD = 5
    
    @Override
    String getAgentId() { AGENT_ID }
    
    @Override
    String getAgentName() { AGENT_NAME }
    
    @Override
    List<String> getCapabilities() {
        return [
            "loop-detection",
            "architectural-drift-detection",
            "scope-creep-guidance",
            "code-quality-review",
            "best-practice-enforcement"
        ]
    }
    
    @Override
    AgentResponse processRequest(AgentRequest request) {
        def context = request.context as ImplementationContext
        def capability = request.capability
        
        switch (capability) {
            case "loop-detection":
                return detectLoops(context)
            case "architectural-drift-detection":
                return detectArchitecturalDrift(context)
            case "scope-creep-guidance":
                return provideScopeCreepGuidance(context)
            case "code-quality-review":
                return reviewCodeQuality(context)
            case "best-practice-enforcement":
                return enforceBestPractices(context)
            default:
                return AgentResponse.error("Unknown capability: ${capability}")
        }
    }
    
    @Override
    boolean canHandle(AgentRequest request) {
        return request.capability in getCapabilities()
    }
    
    @Override
    Map<String, Object> getHealth() {
        return [
            status: "healthy",
            agentId: AGENT_ID,
            capabilities: getCapabilities().size(),
            timestamp: System.currentTimeMillis()
        ]
    }
    
    // Loop Detection
    private AgentResponse detectLoops(ImplementationContext context) {
        def loops = []
        
        // General loop detection
        if (context.iterationCount >= LOOP_THRESHOLD) {
            loops << [
                type: "general-loop",
                stopPhrase: "We are looping.",
                diagnosis: "Same approach attempted ${context.iterationCount} times without progress",
                alternative: "Re-slice the problem into smaller vertical slices"
            ]
        }
        
        // Entity churn loop
        if (context.entityChangeCount >= ENTITY_CHURN_THRESHOLD) {
            loops << [
                type: "entity-churn",
                stopPhrase: "We are churning entities.",
                diagnosis: "Entities repeatedly modified without stabilizing behavior",
                alternative: "Define service semantics first, then model entities to support them"
            ]
        }
        
        // Service explosion loop
        if (context.serviceCount >= SERVICE_EXPLOSION_THRESHOLD && context.serviceComplexity < 0.3) {
            loops << [
                type: "service-explosion",
                stopPhrase: "We are creating services to avoid making a decision.",
                diagnosis: "Too many small services wrapping CRUD without policy",
                alternative: "Consolidate services with clear policy boundaries"
            ]
        }
        
        // Screen logic leakage
        if (context.screenLogicComplexity > 0.5) {
            loops << [
                type: "screen-logic-leakage",
                stopPhrase: "Business logic is leaking into the screen layer.",
                diagnosis: "Screens contain policy work instead of orchestration",
                alternative: "Move business logic to services, keep screens for orchestration only"
            ]
        }
        
        // Domain boundary violation
        if (context.crossDomainReferences?.size() > 0) {
            loops << [
                type: "domain-boundary-violation",
                stopPhrase: "We are crossing a domain boundary.",
                diagnosis: "Entities or services cross domains casually",
                alternative: "Use service contracts or integration patterns for cross-domain communication"
            ]
        }
        
        // Framework feature overuse
        if (context.frameworkFeatureCount > 10 && context.explicitLogicRatio < 0.4) {
            loops << [
                type: "framework-overuse",
                stopPhrase: "We are leaning on the framework instead of modeling the problem.",
                diagnosis: "Excessive use of implicit framework behavior",
                alternative: "Use explicit logic and intentional framework features"
            ]
        }
        
        // Decision churn
        if (context.decisionChangeCount >= LOOP_THRESHOLD) {
            loops << [
                type: "decision-churn",
                stopPhrase: "We are stuck in decision churn.",
                diagnosis: "Too many options or micro-refactors stalling progress",
                alternative: "Collapse to two concrete options, bias toward reversibility"
            ]
        }
        
        return AgentResponse.success([
            loopsDetected: loops.size(),
            loops: loops,
            recommendation: loops.size() >= 2 ? "Reset: re-state business goal and re-slice feature" : null
        ])
    }
    
    // Architectural Drift Detection
    private AgentResponse detectArchitecturalDrift(ImplementationContext context) {
        def drifts = []
        
        // Component boundary violations
        if (context.componentDependencies?.any { it.violatesBoundary }) {
            drifts << [
                type: "component-boundary-violation",
                severity: "high",
                description: "Component dependencies violate architectural boundaries",
                recommendation: "Use durion-positivity integration layer for cross-component communication"
            ]
        }
        
        // Layering violations
        if (context.layeringViolations?.size() > 0) {
            drifts << [
                type: "layering-violation",
                severity: "high",
                description: "Code violates UI → Services → Entities layering",
                recommendation: "Refactor to respect layer boundaries"
            ]
        }
        
        // Service naming inconsistencies
        if (context.serviceNamingViolations?.size() > 0) {
            drifts << [
                type: "naming-inconsistency",
                severity: "medium",
                description: "Services don't follow {domain}.{service-type}#{Action} pattern",
                recommendation: "Rename services to follow Moqui conventions"
            ]
        }
        
        return AgentResponse.success([
            driftsDetected: drifts.size(),
            drifts: drifts,
            architecturalIntegrity: drifts.isEmpty() ? "maintained" : "compromised"
        ])
    }
    
    // Scope Creep Guidance
    private AgentResponse provideScopeCreepGuidance(ImplementationContext context) {
        def scopeIssues = []
        
        // Feature scope expansion
        if (context.featureCount > context.originalFeatureCount * 1.5) {
            scopeIssues << [
                type: "feature-expansion",
                description: "Feature count increased by ${((context.featureCount / context.originalFeatureCount - 1) * 100).round()}%",
                recommendation: "Defer non-essential features to future iterations"
            ]
        }
        
        // Speculative abstractions
        if (context.abstractionCount > context.concreteImplementationCount) {
            scopeIssues << [
                type: "speculative-abstraction",
                description: "More abstractions than concrete implementations",
                recommendation: "Remove speculative abstractions, implement only what's needed"
            ]
        }
        
        return AgentResponse.success([
            scopeIssues: scopeIssues.size(),
            issues: scopeIssues,
            scopeStatus: scopeIssues.isEmpty() ? "controlled" : "expanding"
        ])
    }
    
    // Code Quality Review
    private AgentResponse reviewCodeQuality(ImplementationContext context) {
        def qualityIssues = []
        
        // Complexity metrics
        if (context.cyclomaticComplexity > 10) {
            qualityIssues << [
                type: "high-complexity",
                severity: "medium",
                metric: "cyclomatic-complexity",
                value: context.cyclomaticComplexity,
                recommendation: "Refactor complex methods into smaller, focused functions"
            ]
        }
        
        // Code duplication
        if (context.duplicationPercentage > 5) {
            qualityIssues << [
                type: "code-duplication",
                severity: "low",
                metric: "duplication-percentage",
                value: context.duplicationPercentage,
                recommendation: "Extract common code into reusable services or utilities"
            ]
        }
        
        // Test coverage
        if (context.testCoverage < 80) {
            qualityIssues << [
                type: "low-test-coverage",
                severity: "high",
                metric: "test-coverage",
                value: context.testCoverage,
                recommendation: "Add tests to reach 80% coverage minimum"
            ]
        }
        
        return AgentResponse.success([
            qualityIssues: qualityIssues.size(),
            issues: qualityIssues,
            overallQuality: qualityIssues.isEmpty() ? "good" : "needs-improvement"
        ])
    }
    
    // Best Practice Enforcement
    private AgentResponse enforceBestPractices(ImplementationContext context) {
        def violations = []
        
        // Moqui entity best practices
        if (context.entityDefinitions?.any { !it.hasProperRelationships }) {
            violations << [
                practice: "entity-relationships",
                description: "Entities missing proper relationship definitions",
                recommendation: "Define explicit relationships using <relationship> elements"
            ]
        }
        
        // Service transaction management
        if (context.serviceDefinitions?.any { !it.hasTransactionManagement }) {
            violations << [
                practice: "transaction-management",
                description: "Services missing transaction configuration",
                recommendation: "Add require-new-transaction or use-transaction attributes"
            ]
        }
        
        // Error handling
        if (context.errorHandlingCoverage < 90) {
            violations << [
                practice: "error-handling",
                description: "Insufficient error handling coverage",
                recommendation: "Add try-catch blocks and proper error messages"
            ]
        }
        
        // Security validation
        if (context.inputValidationCoverage < 100) {
            violations << [
                practice: "input-validation",
                description: "Not all inputs are validated",
                recommendation: "Add validation for all user inputs and external data"
            ]
        }
        
        return AgentResponse.success([
            violations: violations.size(),
            bestPracticeViolations: violations,
            complianceStatus: violations.isEmpty() ? "compliant" : "non-compliant"
        ])
    }
}
