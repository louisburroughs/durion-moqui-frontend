package agents.interfaces

import agents.models.ImplementationContext

/**
 * Specialized interface for Vue.js frontend development agents.
 */
interface VueAgent extends Agent {
    
    /**
     * Provide Vue.js 3 Composition API guidance
     */
    String provideCompositionApiGuidance(ImplementationContext context)
    
    /**
     * Provide TypeScript 5.x integration guidance
     */
    String provideTypeScriptGuidance(ImplementationContext context)
    
    /**
     * Provide state management (Pinia) guidance
     */
    String provideStateManagementGuidance(ImplementationContext context)
    
    /**
     * Provide Quasar v2 component guidance
     */
    String provideQuasarGuidance(ImplementationContext context)
    
    /**
     * Provide Moqui screen integration guidance
     */
    String provideMoquiIntegrationGuidance(ImplementationContext context)
}
