package agents.core

import agents.interfaces.Agent
import models.AgentRequest
import models.AgentResponse
import java.util.concurrent.CompletableFuture

/**
 * Collaboration Controller - Orchestrates multi-agent workflows
 * Handles conflict detection, resolution, and consensus building
 */
class CollaborationController {
    
    private final AgentManager agentManager
    private final Map<String, List<String>> workflowDefinitions = [:]
    
    CollaborationController(AgentManager agentManager) {
        this.agentManager = agentManager
    }
    
    /**
     * Execute multi-agent workflow
     */
    CompletableFuture<AgentResponse> executeWorkflow(String workflowId, AgentRequest initialRequest) {
        def agentSequence = workflowDefinitions.get(workflowId)
        if (!agentSequence) {
            return CompletableFuture.completedFuture(
                createErrorResponse(initialRequest, "Workflow not found: ${workflowId}")
            )
        }
        
        return executeSequence(agentSequence, initialRequest)
    }
    
    /**
     * Execute agent sequence with context passing
     */
    private CompletableFuture<AgentResponse> executeSequence(List<String> agentIds, AgentRequest request) {
        CompletableFuture<AgentResponse> result = CompletableFuture.completedFuture(null)
        
        for (String agentId : agentIds) {
            result = result.thenCompose { previousResponse ->
                def nextRequest = buildNextRequest(request, previousResponse)
                return agentManager.processRequestWithAgent(agentId, nextRequest)
            }
        }
        
        return result
    }
    
    /**
     * Execute agents in parallel and merge results
     */
    CompletableFuture<AgentResponse> executeParallel(List<String> agentIds, AgentRequest request) {
        def futures = agentIds.collect { agentId ->
            agentManager.processRequestWithAgent(agentId, request)
        }
        
        return CompletableFuture.allOf(futures as CompletableFuture[])
            .thenApply { _ ->
                def responses = futures.collect { it.join() }
                return mergeResponses(request, responses)
            }
    }
    
    /**
     * Detect conflicts between agent responses
     */
    List<String> detectConflicts(List<AgentResponse> responses) {
        def conflicts = []
        
        // Check for contradictory guidance
        for (int i = 0; i < responses.size(); i++) {
            for (int j = i + 1; j < responses.size(); j++) {
                if (hasConflict(responses[i], responses[j])) {
                    conflicts.add("Conflict between ${responses[i].agentId} and ${responses[j].agentId}")
                }
            }
        }
        
        return conflicts
    }
    
    /**
     * Resolve conflicts using priority-based strategy
     */
    AgentResponse resolveConflicts(List<AgentResponse> responses, Map<String, Integer> agentPriorities) {
        // Sort by priority (higher priority wins)
        def sorted = responses.sort { response ->
            -(agentPriorities.get(response.agentId, 0))
        }
        
        return sorted.first()
    }
    
    /**
     * Build consensus from multiple agent responses
     */
    AgentResponse buildConsensus(AgentRequest request, List<AgentResponse> responses) {
        def conflicts = detectConflicts(responses)
        
        if (conflicts.isEmpty()) {
            return mergeResponses(request, responses)
        }
        
        // Use voting mechanism for consensus
        def consensusData = responses.collectEntries { response ->
            [(response.agentId): response.data]
        }
        
        return new AgentResponse(
            requestId: request.requestId,
            agentId: 'collaboration-controller',
            success: true,
            data: consensusData,
            metadata: [conflicts: conflicts],
            timestamp: System.currentTimeMillis()
        )
    }
    
    /**
     * Register workflow definition
     */
    void registerWorkflow(String workflowId, List<String> agentSequence) {
        workflowDefinitions.put(workflowId, agentSequence)
    }
    
    /**
     * Build next request from previous response
     */
    private AgentRequest buildNextRequest(AgentRequest original, AgentResponse previous) {
        return new AgentRequest(
            requestId: original.requestId,
            requestType: original.requestType,
            context: original.context + (previous?.data ?: [:]),
            timestamp: System.currentTimeMillis()
        )
    }
    
    /**
     * Merge multiple responses into single response
     */
    private AgentResponse mergeResponses(AgentRequest request, List<AgentResponse> responses) {
        def mergedData = responses.collectEntries { response ->
            [(response.agentId): response.data]
        }
        
        def allSuccessful = responses.every { it.success }
        
        return new AgentResponse(
            requestId: request.requestId,
            agentId: 'collaboration-controller',
            success: allSuccessful,
            data: mergedData,
            timestamp: System.currentTimeMillis()
        )
    }
    
    /**
     * Check if two responses conflict
     */
    private boolean hasConflict(AgentResponse r1, AgentResponse r2) {
        // Simple conflict detection - can be enhanced
        return r1.data && r2.data && 
               r1.data.toString() != r2.data.toString() &&
               r1.success && r2.success
    }
    
    /**
     * Create error response
     */
    private AgentResponse createErrorResponse(AgentRequest request, String error) {
        return new AgentResponse(
            requestId: request.requestId,
            agentId: 'collaboration-controller',
            success: false,
            error: error,
            timestamp: System.currentTimeMillis()
        )
    }
}
