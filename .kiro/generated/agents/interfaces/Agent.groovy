package agents.interfaces

import agents.models.AgentRequest
import agents.models.AgentResponse
import agents.models.AgentHealth

/**
 * Base interface for all agents in the Moqui agent structure system.
 * Defines core capabilities and lifecycle methods.
 */
interface Agent {
    
    /**
     * Unique identifier for this agent instance
     */
    String getAgentId()
    
    /**
     * Human-readable name for this agent
     */
    String getAgentName()
    
    /**
     * Set of capabilities this agent provides
     */
    Set<String> getCapabilities()
    
    /**
     * Process a request and return a response
     */
    AgentResponse processRequest(AgentRequest request)
    
    /**
     * Check if this agent can handle the given request
     */
    boolean canHandle(AgentRequest request)
    
    /**
     * Get current health status of this agent
     */
    AgentHealth getHealth()
}
