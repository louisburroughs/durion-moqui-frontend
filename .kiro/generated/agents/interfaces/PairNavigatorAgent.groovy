package agents.interfaces

import agents.models.ImplementationContext

/**
 * Specialized interface for pair navigator agents.
 * Provides loop detection, quality assurance, and architectural guidance.
 */
interface PairNavigatorAgent extends Agent {
    
    /**
     * Detect implementation loops and provide guidance
     */
    String detectImplementationLoops(ImplementationContext context)
    
    /**
     * Detect architectural drift and provide corrections
     */
    String detectArchitecturalDrift(ImplementationContext context)
    
    /**
     * Provide scope creep guidance
     */
    String provideScopeGuidance(ImplementationContext context)
    
    /**
     * Review code quality and provide recommendations
     */
    String reviewCodeQuality(ImplementationContext context)
    
    /**
     * Enforce best practices
     */
    String enforceBestPractices(ImplementationContext context)
}
