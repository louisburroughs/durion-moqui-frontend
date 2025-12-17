package tests.properties

import net.jqwik.api.*
import net.jqwik.api.constraints.*
import net.jqwik.api.statistics.Statistics
import agents.foundation.*
import agents.implementation.*
import agents.infrastructure.*
import agents.quality.*
import models.*
import java.util.concurrent.*

/**
 * Property-based tests for agent performance requirements.
 * 
 * Requirements:
 * - REQ-009: Performance Requirements
 * - Property 1: Response time bounds (all agents)
 * - Property 6: Performance scalability (50 concurrent users, < 10% degradation)
 */
class AgentPerformanceProperties {
    
    // Property 1: Response time bounds for all agent types
    @Property
    @Label("Property 1: All agents respond within specified time bounds")
    void allAgentsRespondWithinTimeBounds(
        @ForAll("agentRequests") AgentRequest request
    ) {
        def agent = createAgentForRequest(request)
        def startTime = System.currentTimeMillis()
        
        def response = agent.processRequest(request)
        
        def elapsedTime = System.currentTimeMillis() - startTime
        def maxTime = getMaxResponseTime(agent.class)
        
        Statistics.collect(agent.class.simpleName, elapsedTime)
        
        assert elapsedTime <= maxTime : 
            "${agent.class.simpleName} exceeded ${maxTime}ms limit: ${elapsedTime}ms"
        assert response != null
        assert response.success || response.errorMessage != null
    }
    
    // Property 6: Performance scalability under concurrent load
    @Property(tries = 10)
    @Label("Property 6: Performance degrades < 10% under 50 concurrent users")
    void performanceScalesUnderConcurrentLoad(
        @ForAll("agentRequests") @Size(50) List<AgentRequest> requests
    ) {
        def agent = new MoquiFrameworkAgentImpl()
        
        // Baseline: single request
        def baselineTime = measureResponseTime(agent, requests[0])
        
        // Concurrent: 50 requests
        def executor = Executors.newFixedThreadPool(50)
        def startTime = System.currentTimeMillis()
        
        def futures = requests.collect { request ->
            executor.submit({ agent.processRequest(request) } as Callable)
        }
        
        futures.each { it.get() }
        
        def totalTime = System.currentTimeMillis() - startTime
        def avgConcurrentTime = totalTime / requests.size()
        
        executor.shutdown()
        
        def degradation = ((avgConcurrentTime - baselineTime) / baselineTime) * 100
        
        Statistics.collect("Concurrent degradation %", degradation)
        
        assert degradation < 10.0 : 
            "Performance degraded ${degradation}% under concurrent load (limit: 10%)"
    }
    
    @Provide
    Arbitrary<AgentRequest> agentRequests() {
        return Arbitraries.of(
            new AgentRequest(
                requestId: UUID.randomUUID().toString(),
                agentType: "MoquiFramework",
                operation: "entity-guidance",
                context: new MoquiContext(
                    entityName: "TestEntity",
                    componentName: "test-component"
                )
            ),
            new AgentRequest(
                requestId: UUID.randomUUID().toString(),
                agentType: "Domain",
                operation: "work-execution-guidance",
                context: new ImplementationContext(
                    domain: "WorkExecution",
                    feature: "estimate-to-payment"
                )
            ),
            new AgentRequest(
                requestId: UUID.randomUUID().toString(),
                agentType: "Security",
                operation: "authentication-guidance",
                context: new ArchitecturalContext(
                    securityLevel: "high",
                    authenticationType: "JWT"
                )
            )
        )
    }
    
    private def createAgentForRequest(AgentRequest request) {
        switch (request.agentType) {
            case "MoquiFramework": return new MoquiFrameworkAgentImpl()
            case "Domain": return new DomainAgentImpl()
            case "Security": return new SecurityAgentImpl()
            case "DevOps": return new DevOpsAgentImpl()
            case "Testing": return new TestingAgentImpl()
            case "Performance": return new PerformanceAgentImpl()
            default: return new MoquiFrameworkAgentImpl()
        }
    }
    
    private long getMaxResponseTime(Class agentClass) {
        def timeouts = [
            (MoquiFrameworkAgentImpl): 2000L,
            (DomainAgentImpl): 3000L,
            (ExperienceLayerAgentImpl): 3000L,
            (SecurityAgentImpl): 3000L,
            (DevOpsAgentImpl): 5000L,
            (DatabaseAgentImpl): 2000L,
            (TestingAgentImpl): 4000L,
            (PerformanceAgentImpl): 4000L,
            (PairNavigatorAgentImpl): 2000L
        ]
        return timeouts[agentClass] ?: 3000L
    }
    
    private long measureResponseTime(agent, AgentRequest request) {
        def startTime = System.currentTimeMillis()
        agent.processRequest(request)
        return System.currentTimeMillis() - startTime
    }
}
