package agents.interfaces

import agents.models.ImplementationContext

/**
 * Specialized interface for business domain-specific agents.
 * Provides expertise in specific business domains.
 */
interface DomainAgent extends Agent {
    
    /**
     * Get the business domain this agent specializes in
     */
    String getDomain()
    
    /**
     * Provide work execution domain guidance
     */
    String provideWorkExecutionGuidance(ImplementationContext context)
    
    /**
     * Provide inventory control domain guidance
     */
    String provideInventoryControlGuidance(ImplementationContext context)
    
    /**
     * Provide product & pricing domain guidance
     */
    String provideProductPricingGuidance(ImplementationContext context)
    
    /**
     * Provide CRM domain guidance
     */
    String provideCrmGuidance(ImplementationContext context)
    
    /**
     * Provide accounting domain guidance
     */
    String provideAccountingGuidance(ImplementationContext context)
}
