package tests.specs

import spock.lang.Specification
import spock.lang.Timeout
import spock.lang.Unroll
import agents.foundation.MoquiFrameworkAgentImpl
import agents.models.AgentRequest
import agents.models.MoquiContext

/**
 * Spock specification for MoquiFrameworkAgent
 * Tests all 5 acceptance criteria with performance validation
 * Requirements: REQ-001, REQ-009 (Performance)
 */
class MoquiFrameworkAgentSpec extends Specification {

    MoquiFrameworkAgentImpl agent
    
    def setup() {
        agent = new MoquiFrameworkAgentImpl()
    }

    @Timeout(2)
    def "should provide entity guidance within 2 seconds with 95% accuracy"() {
        given: "an entity guidance request"
        def context = new MoquiContext()
        context.entityName = "Customer"
        context.componentName = "durion-crm"
        
        def request = new AgentRequest()
        request.requestId = "test-entity-001"
        request.requestType = "entity_guidance"
        request.capability = "entity-guidance"
        request.context = context
        request.payload = [
            entityType: "business",
            domain: "CRM",
            relationships: ["Contact", "Vehicle"]
        ]

        when: "processing the request"
        def startTime = System.currentTimeMillis()
        def response = agent.processRequest(request)
        def endTime = System.currentTimeMillis()

        then: "response is provided within 2 seconds"
        (endTime - startTime) <= 2000

        and: "response contains entity guidance"
        response.status == "SUCCESS"
        response.payload != null
        response.payload.entityDefinition != null
        response.payload.relationships != null

        and: "accuracy metrics are tracked"
        response.confidence >= 0.95
    }

    @Timeout(2)
    def "should provide service guidance within 2 seconds with 98% accuracy"() {
        given: "a service guidance request"
        def request = new AgentRequest(
            requestId: "test-service-001",
            requestType: "service_guidance",
            context: new MoquiContext(
                serviceName: "CustomerService#createCustomer",
                domainContext: "CRM"
            ),
            parameters: [
                serviceType: "entity-auto",
                transactionRequired: true
            ]
        )

        when: "processing the request"
        def startTime = System.currentTimeMillis()
        def response = agent.processRequest(request)
        def endTime = System.currentTimeMillis()

        then: "response is provided within 2 seconds"
        (endTime - startTime) <= 2000

        and: "response contains service guidance"
        response.success
        response.data.guidance.contains("service")
        response.data.guidance.contains("transaction")
        response.data.xmlExample != null

        and: "accuracy meets 98% threshold"
        response.metadata.accuracy >= 0.98
        response.metadata.responseTime <= 2000
    }

    @Timeout(2)
    def "should provide screen guidance within 2 seconds with 92% accuracy"() {
        given: "a screen guidance request"
        def request = new AgentRequest(
            requestId: "test-screen-001",
            requestType: "screen_guidance",
            context: new MoquiContext(
                screenName: "CustomerDetail",
                domainContext: "CRM"
            ),
            parameters: [
                screenType: "form",
                includeTransitions: true
            ]
        )

        when: "processing the request"
        def startTime = System.currentTimeMillis()
        def response = agent.processRequest(request)
        def endTime = System.currentTimeMillis()

        then: "response is provided within 2 seconds"
        (endTime - startTime) <= 2000

        and: "response contains screen guidance"
        response.success
        response.data.guidance.contains("screen")
        response.data.guidance.contains("form")
        response.data.xmlExample != null

        and: "accuracy meets 92% threshold"
        response.metadata.accuracy >= 0.92
        response.metadata.responseTime <= 2000
    }

    @Timeout(3)
    def "should provide positivity integration guidance within 3 seconds with 95% accuracy"() {
        given: "a positivity integration request"
        def request = new AgentRequest(
            requestId: "test-positivity-001",
            requestType: "positivity_integration",
            context: new MoquiContext(
                integrationPoint: "durion-positivity",
                domainContext: "Integration"
            ),
            parameters: [
                integrationType: "rest_service",
                circuitBreakerRequired: true
            ]
        )

        when: "processing the request"
        def startTime = System.currentTimeMillis()
        def response = agent.processRequest(request)
        def endTime = System.currentTimeMillis()

        then: "response is provided within 3 seconds"
        (endTime - startTime) <= 3000

        and: "response contains positivity integration guidance"
        response.success
        response.data.guidance.contains("durion-positivity")
        response.data.guidance.contains("circuit breaker")
        response.data.xmlExample != null

        and: "accuracy meets 95% threshold"
        response.metadata.accuracy >= 0.95
        response.metadata.responseTime <= 3000
    }

    @Timeout(2)
    def "should provide architecture guidance within 2 seconds with 100% compliance"() {
        given: "an architecture guidance request"
        def request = new AgentRequest(
            requestId: "test-architecture-001",
            requestType: "architecture_guidance",
            context: new MoquiContext(
                componentName: "durion-crm",
                domainContext: "Architecture"
            ),
            parameters: [
                guidanceType: "component_structure",
                validateCompliance: true
            ]
        )

        when: "processing the request"
        def startTime = System.currentTimeMillis()
        def response = agent.processRequest(request)
        def endTime = System.currentTimeMillis()

        then: "response is provided within 2 seconds"
        (endTime - startTime) <= 2000

        and: "response contains architecture guidance"
        response.success
        response.data.guidance.contains("component")
        response.data.guidance.contains("structure")
        response.data.xmlExample != null

        and: "compliance is 100%"
        response.metadata.accuracy >= 1.0
        response.metadata.responseTime <= 2000
    }

    @Unroll
    def "should handle invalid requests gracefully for request type: #requestType"() {
        given: "an invalid request"
        def request = new AgentRequest(
            requestId: "test-invalid-${requestType}",
            requestType: requestType,
            context: null,
            parameters: [:]
        )

        when: "processing the request"
        def response = agent.processRequest(request)

        then: "response indicates failure with helpful message"
        !response.success
        response.error != null
        response.error.contains("Invalid") || response.error.contains("Missing")

        where:
        requestType << ["invalid_type", "", null]
    }

    def "should track performance metrics correctly"() {
        given: "multiple requests"
        def requests = (1..5).collect { i ->
            new AgentRequest(
                requestId: "perf-test-${i}",
                requestType: "entity_guidance",
                context: new MoquiContext(
                    entityName: "TestEntity${i}",
                    domainContext: "Test"
                ),
                parameters: [entityType: "business"]
            )
        }

        when: "processing all requests"
        def responses = requests.collect { agent.processRequest(it) }

        then: "all responses are successful"
        responses.every { it.success }

        and: "performance metrics are tracked"
        def health = agent.getHealth()
        health.status == "healthy"
        health.metrics.totalRequests >= 5
        health.metrics.averageResponseTime > 0
        health.metrics.successRate >= 0.8
    }

    def "should maintain capability accuracy thresholds"() {
        when: "checking agent capabilities"
        def capabilities = agent.getCapabilitiesDetailed()

        then: "all capabilities have defined accuracy thresholds"
        capabilities.entity_guidance.accuracyThreshold == 0.95
        capabilities.service_guidance.accuracyThreshold == 0.98
        capabilities.screen_guidance.accuracyThreshold == 0.92
        capabilities.positivity_integration.accuracyThreshold == 0.95
        capabilities.architecture_guidance.accuracyThreshold == 1.0

        and: "all capabilities have response time limits"
        capabilities.entity_guidance.responseTimeLimit <= 2000
        capabilities.service_guidance.responseTimeLimit <= 2000
        capabilities.screen_guidance.responseTimeLimit <= 2000
        capabilities.positivity_integration.responseTimeLimit <= 3000
        capabilities.architecture_guidance.responseTimeLimit <= 2000
    }
}
