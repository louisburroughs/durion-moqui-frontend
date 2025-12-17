package durion.agents.tests.specs

import spock.lang.Specification
import spock.lang.Timeout
import spock.lang.Unroll
import durion.agents.implementation.DomainAgentImpl
import durion.agents.models.AgentRequest
import durion.agents.models.AgentResponse
import durion.agents.models.ImplementationContext

/**
 * Spock specification for Domain Agent testing
 * Tests all 5 domain specializations with accuracy and performance targets
 * 
 * Requirements: REQ-002 (all 5 acceptance criteria)
 * Test Cases: TC-004, TC-005, TC-006
 */
class DomainAgentSpec extends Specification {

    DomainAgentImpl domainAgent
    
    def setup() {
        domainAgent = new DomainAgentImpl()
    }

    @Timeout(3)
    def "should provide Work Execution domain guidance with 95% accuracy"() {
        given: "a Work Execution domain request"
        def context = new ImplementationContext(
            domain: "work-execution",
            component: "durion-workexec",
            entityTypes: ["WorkOrder", "ServiceRecord", "TechnicianSchedule"]
        )
        def request = new AgentRequest(
            requestType: "domain-guidance",
            context: context,
            query: "estimate-to-payment workflow patterns"
        )

        when: "requesting Work Execution guidance"
        def response = domainAgent.processRequest(request)

        then: "should provide accurate guidance within 3 seconds"
        response.success
        response.accuracy >= 0.95
        response.responseTime <= 3000
        response.content.contains("WorkOrder")
        response.content.contains("estimate-to-payment")
        response.content.contains("durion-positivity")
    }

    @Timeout(2)
    def "should provide Inventory Control domain guidance with 92% accuracy"() {
        given: "an Inventory Control domain request"
        def context = new ImplementationContext(
            domain: "inventory",
            component: "durion-inventory", 
            entityTypes: ["StockLevel", "Transfer", "Adjustment"]
        )
        def request = new AgentRequest(
            requestType: "domain-guidance",
            context: context,
            query: "inventory tracking and reservation patterns"
        )

        when: "requesting Inventory Control guidance"
        def response = domainAgent.processRequest(request)

        then: "should provide accurate guidance within 2 seconds"
        response.success
        response.accuracy >= 0.92
        response.responseTime <= 2000
        response.content.contains("StockLevel")
        response.content.contains("reservation")
        response.content.contains("availability")
    }

    @Timeout(3)
    def "should provide Product & Pricing domain guidance with 99% accuracy"() {
        given: "a Product & Pricing domain request"
        def context = new ImplementationContext(
            domain: "product",
            component: "durion-product",
            entityTypes: ["Product", "PriceList", "Promotion"]
        )
        def request = new AgentRequest(
            requestType: "domain-guidance",
            context: context,
            query: "dynamic pricing and catalog management"
        )

        when: "requesting Product & Pricing guidance"
        def response = domainAgent.processRequest(request)

        then: "should provide accurate guidance within 3 seconds"
        response.success
        response.accuracy >= 0.99
        response.responseTime <= 3000
        response.content.contains("PriceList")
        response.content.contains("dynamic pricing")
        response.content.contains("catalog")
    }

    @Timeout(3)
    def "should provide CRM domain guidance with 97% accuracy"() {
        given: "a CRM domain request"
        def context = new ImplementationContext(
            domain: "crm",
            component: "durion-crm",
            entityTypes: ["Customer", "Contact", "Vehicle"]
        )
        def request = new AgentRequest(
            requestType: "domain-guidance",
            context: context,
            query: "customer and vehicle management patterns"
        )

        when: "requesting CRM guidance"
        def response = domainAgent.processRequest(request)

        then: "should provide accurate guidance within 3 seconds"
        response.success
        response.accuracy >= 0.97
        response.responseTime <= 3000
        response.content.contains("Customer")
        response.content.contains("Vehicle")
        response.content.contains("service history")
    }

    @Timeout(3)
    def "should provide Accounting domain guidance with 98% accuracy"() {
        given: "an Accounting domain request"
        def context = new ImplementationContext(
            domain: "accounting",
            component: "durion-accounting",
            entityTypes: ["Invoice", "Payment", "Ledger"]
        )
        def request = new AgentRequest(
            requestType: "domain-guidance",
            context: context,
            query: "accounts receivable and payment processing"
        )

        when: "requesting Accounting guidance"
        def response = domainAgent.processRequest(request)

        then: "should provide accurate guidance within 3 seconds"
        response.success
        response.accuracy >= 0.98
        response.responseTime <= 3000
        response.content.contains("Invoice")
        response.content.contains("Payment")
        response.content.contains("receivable")
    }

    @Unroll
    def "should enforce domain boundaries for #domain"() {
        given: "a cross-domain request"
        def context = new ImplementationContext(
            domain: domain,
            component: component,
            crossDomainAccess: true
        )
        def request = new AgentRequest(
            requestType: "boundary-validation",
            context: context,
            query: "access entities from other domains"
        )

        when: "validating domain boundaries"
        def response = domainAgent.processRequest(request)

        then: "should enforce proper boundaries"
        response.success
        response.content.contains("domain boundary")
        response.content.contains("durion-positivity")

        where:
        domain           | component
        "work-execution" | "durion-workexec"
        "inventory"      | "durion-inventory"
        "product"        | "durion-product"
        "crm"           | "durion-crm"
        "accounting"    | "durion-accounting"
    }

    def "should validate business data access rules"() {
        given: "a request for direct business data access"
        def context = new ImplementationContext(
            domain: "inventory",
            component: "durion-inventory",
            dataAccessType: "direct-business-data"
        )
        def request = new AgentRequest(
            requestType: "data-access-validation",
            context: context,
            query: "direct database access to business entities"
        )

        when: "validating data access patterns"
        def response = domainAgent.processRequest(request)

        then: "should enforce data architecture constraints"
        response.success
        response.content.contains("durion-positivity")
        response.content.contains("local database")
        response.content.contains("state and cache only")
        !response.content.contains("direct business data")
    }

    def "should provide integration guidance for all domains"() {
        given: "integration requests for each domain"
        def domains = ["work-execution", "inventory", "product", "crm", "accounting"]

        when: "requesting integration guidance for each domain"
        def responses = domains.collect { domain ->
            def context = new ImplementationContext(
                domain: domain,
                component: "durion-${domain}",
                integrationType: "durion-positivity"
            )
            def request = new AgentRequest(
                requestType: "integration-guidance",
                context: context,
                query: "durion-positivity backend integration"
            )
            domainAgent.processRequest(request)
        }

        then: "all domains should provide integration guidance"
        responses.every { it.success }
        responses.every { it.content.contains("durion-positivity") }
        responses.every { it.content.contains("REST API") }
    }

    def "should handle unknown domain gracefully"() {
        given: "a request for unknown domain"
        def context = new ImplementationContext(
            domain: "unknown-domain",
            component: "unknown-component"
        )
        def request = new AgentRequest(
            requestType: "domain-guidance",
            context: context,
            query: "guidance for unknown domain"
        )

        when: "requesting guidance for unknown domain"
        def response = domainAgent.processRequest(request)

        then: "should handle gracefully with appropriate message"
        !response.success
        response.content.contains("unknown domain")
        response.content.contains("supported domains")
    }

    def "should provide performance optimization guidance"() {
        given: "a performance optimization request"
        def context = new ImplementationContext(
            domain: "inventory",
            component: "durion-inventory",
            performanceContext: true
        )
        def request = new AgentRequest(
            requestType: "performance-guidance",
            context: context,
            query: "optimize inventory queries and caching"
        )

        when: "requesting performance guidance"
        def response = domainAgent.processRequest(request)

        then: "should provide optimization patterns"
        response.success
        response.content.contains("caching")
        response.content.contains("query optimization")
        response.content.contains("performance")
    }
}
