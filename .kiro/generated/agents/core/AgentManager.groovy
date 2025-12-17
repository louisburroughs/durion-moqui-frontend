package agents.core

import agents.interfaces.Agent
import models.AgentRequest
import models.AgentResponse
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Agent Manager - Handles request routing, orchestration, and performance monitoring
 * Manages agent instantiation, pooling, and error recovery
 */
class AgentManager {
    
    private final AgentRegistry registry
    private final ExecutorService executorService
    private final Map<String, Long> responseTimeMetrics = [:]
    
    AgentManager(AgentRegistry registry) {
        this.registry = registry
        this.executorService = Executors.newCachedThreadPool()
    }
    
    /**
     * Process request with automatic agent selection and failover
     */
    CompletableFuture<AgentResponse> processRequest(AgentRequest request) {
        return CompletableFuture.supplyAsync({
            def startTime = System.currentTimeMillis()
            
            try {
                def agent = selectAgent(request)
                if (!agent) {
                    return createErrorResponse(request, "No suitable agent found")
                }
                
                registry.incrementRequestCount(agent.agentId)
                def response = agent.processRequest(request)
                
                recordMetrics(agent.agentId, System.currentTimeMillis() - startTime)
                return response
                
            } catch (Exception e) {
                return createErrorResponse(request, "Processing failed: ${e.message}")
            }
        }, executorService)
    }
    
    /**
     * Process request with specific agent
     */
    CompletableFuture<AgentResponse> processRequestWithAgent(String agentId, AgentRequest request) {
        return CompletableFuture.supplyAsync({
            def startTime = System.currentTimeMillis()
            
            try {
                def agent = registry.findAgent(agentId)
                if (!agent) {
                    return createErrorResponse(request, "Agent not found: ${agentId}")
                }
                
                if (!registry.isHealthy(agentId)) {
                    return createErrorResponse(request, "Agent unhealthy: ${agentId}")
                }
                
                registry.incrementRequestCount(agentId)
                def response = agent.processRequest(request)
                
                recordMetrics(agentId, System.currentTimeMillis() - startTime)
                return response
                
            } catch (Exception e) {
                return createErrorResponse(request, "Processing failed: ${e.message}")
            }
        }, executorService)
    }
    
    /**
     * Select best agent for request based on capabilities and load
     */
    private Agent selectAgent(AgentRequest request) {
        def requiredCapability = determineRequiredCapability(request)
        return registry.getBestAgent(requiredCapability)
    }
    
    /**
     * Determine required capability from request
     */
    private String determineRequiredCapability(AgentRequest request) {
        // Simple capability mapping - can be enhanced with ML
        def requestType = request.requestType?.toLowerCase()
        
        switch (requestType) {
            case 'entity': return 'moqui-entity'
            case 'service': return 'moqui-service'
            case 'screen': return 'moqui-screen'
            case 'vue': return 'vue-component'
            case 'domain': return 'domain-logic'
            case 'security': return 'security-guidance'
            case 'performance': return 'performance-optimization'
            case 'testing': return 'test-generation'
            case 'documentation': return 'documentation-generation'
            default: return 'general-guidance'
        }
    }
    
    /**
     * Create error response
     */
    private AgentResponse createErrorResponse(AgentRequest request, String error) {
        return new AgentResponse(
            requestId: request.requestId,
            agentId: 'system',
            success: false,
            error: error,
            timestamp: System.currentTimeMillis()
        )
    }
    
    /**
     * Record performance metrics
     */
    private void recordMetrics(String agentId, long responseTime) {
        responseTimeMetrics.put("${agentId}_last", responseTime)
        
        // Update rolling average (simple implementation)
        def avgKey = "${agentId}_avg"
        def currentAvg = responseTimeMetrics.get(avgKey, 0L)
        responseTimeMetrics.put(avgKey, (currentAvg + responseTime) / 2)
    }
    
    /**
     * Get performance metrics
     */
    Map<String, Object> getMetrics() {
        return [
            responseTimeMetrics: responseTimeMetrics,
            registryStats: registry.stats,
            executorStats: [
                activeThreads: executorService.toString()
            ]
        ]
    }
    
    /**
     * Shutdown manager and cleanup resources
     */
    void shutdown() {
        executorService.shutdown()
        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                executorService.shutdownNow()
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow()
            Thread.currentThread().interrupt()
        }
    }
}
