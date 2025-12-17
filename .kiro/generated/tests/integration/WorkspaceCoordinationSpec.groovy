package tests.integration

import agents.core.WorkspaceCoordination
import spock.lang.Specification
import spock.lang.Timeout

/**
 * Workspace Coordination Integration Tests
 * 
 * Tests cross-project agent communication and fallback behavior.
 * 
 * Requirements: REQ-014 AC4 (Workspace communication failures)
 * Test Cases: TC-042
 */
class WorkspaceCoordinationSpec extends Specification {
    
    WorkspaceCoordination coordination
    
    def setup() {
        coordination = new WorkspaceCoordination()
    }
    
    def cleanup() {
        coordination.shutdown()
    }
    
    // TC-042: Workspace communication failure handling
    
    def "should coordinate with Requirements Decomposition Agent"() {
        given: "a requirement to decompose"
        def requirement = [
            id: 'REQ-001',
            description: 'Implement user authentication',
            priority: 'high'
        ]
        
        when: "coordinating requirement decomposition"
        def result = coordination.coordinateRequirementDecomposition(requirement)
        
        then: "should return successful decomposition"
        result.success == true
        result.analysis != null
    }
    
    def "should coordinate with Workspace Architecture Agent"() {
        given: "an architecture design to validate"
        def design = [
            component: 'durion-crm',
            entities: ['Customer', 'Contact'],
            services: ['CustomerService']
        ]
        
        when: "coordinating architecture validation"
        def result = coordination.coordinateArchitectureValidation(design)
        
        then: "should return validation result"
        result.success == true
        result.validation != null
    }
    
    def "should coordinate with Unified Security Agent"() {
        given: "a security context to validate"
        def securityContext = [
            authentication: 'JWT',
            authorization: 'RBAC',
            encryption: 'TLS 1.3'
        ]
        
        when: "coordinating security validation"
        def result = coordination.coordinateSecurityValidation(securityContext)
        
        then: "should return security validation"
        result.success == true
        result.validation != null
    }
    
    def "should coordinate with API Contract Agent"() {
        given: "an API contract to validate"
        def contract = [
            endpoint: '/api/customers',
            method: 'POST',
            requestBody: [type: 'Customer'],
            responseBody: [type: 'CustomerResponse']
        ]
        
        when: "coordinating API contract validation"
        def result = coordination.coordinateAPIContractValidation(contract)
        
        then: "should return contract validation"
        result.success == true
        result.validation != null
    }
    
    def "should coordinate with Frontend-Backend Bridge Agent"() {
        given: "an integration request"
        def integrationRequest = [
            frontend: 'Vue.js 3',
            backend: 'Moqui REST API',
            dataMapping: ['customer' : 'Customer']
        ]
        
        when: "coordinating frontend-backend integration"
        def result = coordination.coordinateFrontendBackendIntegration(integrationRequest)
        
        then: "should return integration guidance"
        result.success == true
        result.guidance != null
    }
    
    def "should coordinate with End-to-End Testing Agent"() {
        given: "an E2E test request"
        def testRequest = [
            workflow: 'customer-registration',
            steps: ['navigate', 'fill-form', 'submit', 'verify']
        ]
        
        when: "coordinating E2E test generation"
        def result = coordination.coordinateE2ETesting(testRequest)
        
        then: "should return test generation result"
        result.success == true
        result.tests != null
    }
    
    // Fallback behavior tests (80% capability retention)
    
    def "should fallback to local requirement analysis when workspace unavailable"() {
        given: "workspace is unavailable"
        coordination.setWorkspaceAvailable(false)
        
        and: "a requirement to decompose"
        def requirement = [
            id: 'REQ-002',
            description: 'Implement payment processing'
        ]
        
        when: "coordinating requirement decomposition"
        def result = coordination.coordinateRequirementAnalysis(requirement)
        
        then: "should use local fallback"
        result.success == true
        result.fallback == true
        result.capability == 0.80
        result.analysis.notes.contains('Workspace unavailable')
    }
    
    def "should fallback to local architecture validation when workspace unavailable"() {
        given: "workspace is unavailable"
        coordination.setWorkspaceAvailable(false)
        
        and: "an architecture design"
        def design = [component: 'durion-accounting']
        
        when: "coordinating architecture validation"
        def result = coordination.coordinateArchitectureValidation(design)
        
        then: "should use local fallback"
        result.success == true
        result.fallback == true
        result.capability == 0.80
    }
    
    def "should fallback to local security validation when workspace unavailable"() {
        given: "workspace is unavailable"
        coordination.setWorkspaceAvailable(false)
        
        and: "a security context"
        def securityContext = [authentication: 'OAuth2']
        
        when: "coordinating security validation"
        def result = coordination.coordinateSecurityValidation(securityContext)
        
        then: "should use local fallback"
        result.success == true
        result.fallback == true
        result.capability == 0.80
    }
    
    def "should fallback to local contract validation when workspace unavailable"() {
        given: "workspace is unavailable"
        coordination.setWorkspaceAvailable(false)
        
        and: "an API contract"
        def contract = [endpoint: '/api/orders']
        
        when: "coordinating API contract validation"
        def result = coordination.coordinateAPIContractValidation(contract)
        
        then: "should use local fallback"
        result.success == true
        result.fallback == true
        result.capability == 0.80
    }
    
    def "should fallback to local integration guidance when workspace unavailable"() {
        given: "workspace is unavailable"
        coordination.setWorkspaceAvailable(false)
        
        and: "an integration request"
        def integrationRequest = [frontend: 'Vue.js', backend: 'Moqui']
        
        when: "coordinating frontend-backend integration"
        def result = coordination.coordinateFrontendBackendIntegration(integrationRequest)
        
        then: "should use local fallback"
        result.success == true
        result.fallback == true
        result.capability == 0.80
    }
    
    def "should fallback to local E2E test generation when workspace unavailable"() {
        given: "workspace is unavailable"
        coordination.setWorkspaceAvailable(false)
        
        and: "a test request"
        def testRequest = [workflow: 'order-processing']
        
        when: "coordinating E2E test generation"
        def result = coordination.coordinateE2ETesting(testRequest)
        
        then: "should use local fallback"
        result.success == true
        result.fallback == true
        result.capability == 0.80
    }
    
    // Request queueing tests
    
    def "should queue requests when workspace unavailable"() {
        given: "workspace is unavailable"
        coordination.setWorkspaceAvailable(false)
        
        when: "queueing a request"
        coordination.queueRequestForLater('requirements-decomposition', [id: 'REQ-003'])
        
        then: "request should be queued"
        coordination.getQueuedRequestCount() == 1
    }
    
    def "should process queued requests when workspace becomes available"() {
        given: "workspace is unavailable with queued requests"
        coordination.setWorkspaceAvailable(false)
        coordination.queueRequestForLater('requirements-decomposition', [id: 'REQ-004'])
        coordination.queueRequestForLater('workspace-architecture', [id: 'ARCH-001'])
        
        when: "workspace becomes available"
        coordination.setWorkspaceAvailable(true)
        coordination.processQueuedRequests()
        
        then: "queued requests should be processed"
        coordination.getQueuedRequestCount() == 0
    }
    
    // Workspace availability tests
    
    def "should report workspace availability status"() {
        when: "checking workspace availability"
        def available = coordination.isWorkspaceAvailable()
        
        then: "should return current status"
        available == true
    }
    
    def "should update workspace availability status"() {
        when: "setting workspace unavailable"
        coordination.setWorkspaceAvailable(false)
        
        then: "status should be updated"
        coordination.isWorkspaceAvailable() == false
        
        when: "setting workspace available"
        coordination.setWorkspaceAvailable(true)
        
        then: "status should be updated"
        coordination.isWorkspaceAvailable() == true
    }
    
    // Cross-project coordination patterns
    
    def "should coordinate cross-project API contracts"() {
        given: "API contracts from multiple projects"
        def frontendContract = [
            project: 'durion-moqui-frontend',
            endpoint: '/api/customers',
            version: 'v1'
        ]
        def backendContract = [
            project: 'durion-positivity-backend',
            endpoint: '/customers',
            version: 'v1'
        ]
        
        when: "coordinating API contract validation"
        def frontendResult = coordination.coordinateAPIContractValidation(frontendContract)
        def backendResult = coordination.coordinateAPIContractValidation(backendContract)
        
        then: "both contracts should be validated"
        frontendResult.success == true
        backendResult.success == true
    }
    
    def "should coordinate workspace-level security policies"() {
        given: "security policies from workspace"
        def securityPolicy = [
            workspace: 'durion',
            authentication: 'JWT',
            authorization: 'RBAC',
            encryption: 'TLS 1.3'
        ]
        
        when: "coordinating security validation"
        def result = coordination.coordinateSecurityValidation(securityPolicy)
        
        then: "workspace security policy should be validated"
        result.success == true
        result.validation != null
    }
    
    def "should coordinate workspace-level architecture decisions"() {
        given: "architecture decisions from workspace"
        def architectureDecision = [
            workspace: 'durion',
            decision: 'Use durion-positivity for all external integrations',
            projects: ['durion-moqui-frontend', 'durion-positivity-backend']
        ]
        
        when: "coordinating architecture validation"
        def result = coordination.coordinateArchitectureValidation(architectureDecision)
        
        then: "workspace architecture decision should be validated"
        result.success == true
        result.validation != null
    }
}
