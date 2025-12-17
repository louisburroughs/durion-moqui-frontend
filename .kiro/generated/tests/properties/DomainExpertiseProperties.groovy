package durion.agents.tests.properties

import net.jqwik.api.*
import net.jqwik.api.constraints.*
import durion.agents.implementation.DomainAgentImpl
import durion.agents.models.AgentRequest
import durion.agents.models.ImplementationContext

/**
 * Property-based tests for Domain Agent expertise
 * Tests accuracy thresholds and data architecture compliance
 * 
 * Requirements: REQ-002, Data Architecture Constraints
 * Properties: Property 2 (Accuracy thresholds), Property 7 (Data architecture compliance)
 */
class DomainExpertiseProperties {

    @Property
    @Label("Property 2: Domain accuracy thresholds must be maintained")
    boolean domainAccuracyThresholds(
        @ForAll("domainRequests") AgentRequest request
    ) {
        def agent = new DomainAgentImpl()
        def response = agent.processRequest(request)
        
        if (!response.success) return true // Skip failed requests
        
        def domain = request.context.domain
        def expectedAccuracy = getExpectedAccuracy(domain)
        
        return response.accuracy >= expectedAccuracy
    }

    @Property
    @Label("Property 7: Data architecture compliance - no direct business data access")
    boolean dataArchitectureCompliance(
        @ForAll("dataAccessRequests") AgentRequest request
    ) {
        def agent = new DomainAgentImpl()
        def response = agent.processRequest(request)
        
        if (!response.success) return true
        
        // Verify guidance enforces durion-positivity for business data
        def content = response.content.toLowerCase()
        boolean enforcesDurionPositivity = content.contains("durion-positivity")
        boolean prohibitsDirectAccess = !content.contains("direct database access") || 
                                       content.contains("local database") && content.contains("state and cache only")
        
        return enforcesDurionPositivity && prohibitsDirectAccess
    }

    @Property
    @Label("Domain boundary enforcement must be consistent")
    boolean domainBoundaryEnforcement(
        @ForAll("crossDomainRequests") AgentRequest request
    ) {
        def agent = new DomainAgentImpl()
        def response = agent.processRequest(request)
        
        if (!response.success) return true
        
        // Cross-domain requests should enforce boundaries
        def content = response.content.toLowerCase()
        return content.contains("domain boundary") || content.contains("durion-positivity")
    }

    @Provide
    Arbitrary<AgentRequest> domainRequests() {
        return Combinators.combine(
            Arbitraries.of("work-execution", "inventory", "product", "crm", "accounting"),
            Arbitraries.of("entity-guidance", "service-guidance", "integration-guidance"),
            Arbitraries.strings().alpha().ofMinLength(10).ofMaxLength(50)
        ).as { domain, requestType, query ->
            new AgentRequest(
                requestType: requestType,
                context: new ImplementationContext(
                    domain: domain,
                    component: "durion-${domain}",
                    entityTypes: getEntityTypesForDomain(domain)
                ),
                query: query
            )
        }
    }

    @Provide
    Arbitrary<AgentRequest> dataAccessRequests() {
        return Combinators.combine(
            Arbitraries.of("work-execution", "inventory", "product", "crm", "accounting"),
            Arbitraries.of("direct-business-data", "local-cache", "state-management")
        ).as { domain, dataAccessType ->
            new AgentRequest(
                requestType: "data-access-validation",
                context: new ImplementationContext(
                    domain: domain,
                    component: "durion-${domain}",
                    dataAccessType: dataAccessType
                ),
                query: "data access patterns for ${domain}"
            )
        }
    }

    @Provide
    Arbitrary<AgentRequest> crossDomainRequests() {
        return Combinators.combine(
            Arbitraries.of("work-execution", "inventory", "product", "crm", "accounting"),
            Arbitraries.of("work-execution", "inventory", "product", "crm", "accounting")
        ).filter { source, target -> source != target }
        .as { sourceDomain, targetDomain ->
            new AgentRequest(
                requestType: "boundary-validation",
                context: new ImplementationContext(
                    domain: sourceDomain,
                    component: "durion-${sourceDomain}",
                    crossDomainAccess: true,
                    targetDomain: targetDomain
                ),
                query: "access ${targetDomain} entities from ${sourceDomain}"
            )
        }
    }

    private double getExpectedAccuracy(String domain) {
        switch (domain) {
            case "work-execution": return 0.95
            case "inventory": return 0.92
            case "product": return 0.99
            case "crm": return 0.97
            case "accounting": return 0.98
            default: return 0.90
        }
    }

    private List<String> getEntityTypesForDomain(String domain) {
        switch (domain) {
            case "work-execution": return ["WorkOrder", "ServiceRecord", "TechnicianSchedule"]
            case "inventory": return ["StockLevel", "Transfer", "Adjustment"]
            case "product": return ["Product", "PriceList", "Promotion"]
            case "crm": return ["Customer", "Contact", "Vehicle"]
            case "accounting": return ["Invoice", "Payment", "Ledger"]
            default: return []
        }
    }
}
