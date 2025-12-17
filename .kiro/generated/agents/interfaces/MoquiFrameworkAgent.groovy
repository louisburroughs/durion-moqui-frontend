package agents.interfaces

import agents.models.MoquiContext

/**
 * Specialized interface for Moqui Framework-specific agents.
 * Extends base Agent with Moqui-specific capabilities.
 */
interface MoquiFrameworkAgent extends Agent {
    
    /**
     * Provide entity definition guidance
     */
    String provideEntityGuidance(MoquiContext context)
    
    /**
     * Provide service implementation guidance
     */
    String provideServiceGuidance(MoquiContext context)
    
    /**
     * Provide screen development guidance
     */
    String provideScreenGuidance(MoquiContext context)
    
    /**
     * Provide durion-positivity integration guidance
     */
    String providePositivityIntegrationGuidance(MoquiContext context)
    
    /**
     * Provide architecture compliance guidance
     */
    String provideArchitectureGuidance(MoquiContext context)
}
