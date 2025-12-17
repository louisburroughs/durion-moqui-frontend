package agents.core

import agents.interfaces.Agent
import models.AgentHealth
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * Agent Registry - Manages agent discovery, registration, and health monitoring
 * Provides load balancing with health-aware routing
 */
class AgentRegistry {
    
    private final Map<String, Agent> agents = new ConcurrentHashMap<>()
    private final Map<String, AgentHealth> healthStatus = new ConcurrentHashMap<>()
    private final Map<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>()
    
    /**
     * Register an agent with the registry
     */
    void registerAgent(Agent agent) {
        if (agent?.agentId) {
            agents.put(agent.agentId, agent)
            healthStatus.put(agent.agentId, agent.health)
            requestCounts.put(agent.agentId, new AtomicInteger(0))
        }
    }
    
    /**
     * Unregister an agent from the registry
     */
    void unregisterAgent(String agentId) {
        agents.remove(agentId)
        healthStatus.remove(agentId)
        requestCounts.remove(agentId)
    }
    
    /**
     * Find agent by ID
     */
    Agent findAgent(String agentId) {
        return agents.get(agentId)
    }
    
    /**
     * Find agents by capability with health-aware load balancing
     */
    List<Agent> findAgentsByCapability(String capability) {
        return agents.values()
            .findAll { it.capabilities.contains(capability) }
            .findAll { isHealthy(it.agentId) }
            .sort { requestCounts.get(it.agentId).get() }
    }
    
    /**
     * Get the best agent for a capability (lowest load, healthy)
     */
    Agent getBestAgent(String capability) {
        def candidates = findAgentsByCapability(capability)
        return candidates.isEmpty() ? null : candidates.first()
    }
    
    /**
     * Update agent health status
     */
    void updateHealth(String agentId, AgentHealth health) {
        if (agents.containsKey(agentId)) {
            healthStatus.put(agentId, health)
        }
    }
    
    /**
     * Check if agent is healthy
     */
    boolean isHealthy(String agentId) {
        def health = healthStatus.get(agentId)
        return health?.status == 'HEALTHY'
    }
    
    /**
     * Increment request count for load balancing
     */
    void incrementRequestCount(String agentId) {
        requestCounts.get(agentId)?.incrementAndGet()
    }
    
    /**
     * Get all registered agents
     */
    Collection<Agent> getAllAgents() {
        return agents.values()
    }
    
    /**
     * Get registry statistics
     */
    Map<String, Object> getStats() {
        return [
            totalAgents: agents.size(),
            healthyAgents: agents.keySet().count { isHealthy(it) },
            totalRequests: requestCounts.values().sum { it.get() } ?: 0
        ]
    }
}
