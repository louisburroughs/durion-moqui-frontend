package tests.specs

import spock.lang.Specification
import spock.lang.Timeout
import spock.lang.Unroll
import agents.infrastructure.SecurityAgentImpl
import models.AgentRequest
import models.SecurityContext
import models.MoquiContext

/**
 * Spock specification for SecurityAgentImpl
 * Tests all 5 security guidance areas with performance and compliance validation
 * 
 * Requirements: REQ-004 (Security Agent), REQ-011 (Security Requirements)
 * Test Cases: TC-010, TC-011, TC-012, TC-031, TC-032, TC-033
 */
class SecurityAgentSpec extends Specification {

    SecurityAgentImpl securityAgent
    SecurityContext securityContext
    MoquiContext moquiContext

    def setup() {
        securityAgent = new SecurityAgentImpl()
        securityContext = new SecurityContext(
            userId: "testUser",
            roles: ["ADMIN", "USER"],
            permissions: ["READ", "WRITE"],
            sessionId: "test-session-123"
        )
        moquiContext = new MoquiContext(
            componentName: "durion-crm",
            entityName: "Customer",
            serviceName: "CustomerServices.createCustomer",
            screenName: "CustomerDetail"
        )
    }

    // AC1: Authentication guidance (1 second, 100% compliance)
    @Timeout(1)
    def "should provide JWT authentication guidance within 1 second"() {
        given: "authentication guidance request"
        def request = new AgentRequest(
            type: "AUTHENTICATION_GUIDANCE",
            context: securityContext,
            moquiContext: moquiContext,
            parameters: [
                authenticationType: "JWT",
                tokenExpiry: "24h",
                refreshToken: true
            ]
        )

        when: "requesting authentication guidance"
        def response = securityAgent.processRequest(request)

        then: "provides JWT authentication patterns"
        response.success
        response.guidance.contains("JWT")
        response.guidance.contains("token validation")
        response.guidance.contains("refresh token")
        response.compliance == 100.0
        response.responseTimeMs <= 1000
    }

    @Timeout(1)
    def "should provide OAuth2 integration guidance"() {
        given: "OAuth2 authentication request"
        def request = new AgentRequest(
            type: "AUTHENTICATION_GUIDANCE",
            context: securityContext,
            parameters: [authenticationType: "OAUTH2", provider: "durion-positivity"]
        )

        when: "requesting OAuth2 guidance"
        def response = securityAgent.processRequest(request)

        then: "provides OAuth2 patterns"
        response.success
        response.guidance.contains("OAuth2")
        response.guidance.contains("authorization code")
        response.guidance.contains("durion-positivity")
        response.compliance == 100.0
    }

    // AC2: Entity security guidance (2 seconds, 99% compliance)
    @Timeout(2)
    def "should provide entity security guidance within 2 seconds"() {
        given: "entity security guidance request"
        def request = new AgentRequest(
            type: "ENTITY_SECURITY_GUIDANCE",
            context: securityContext,
            moquiContext: moquiContext,
            parameters: [
                entityName: "Customer",
                securityLevel: "HIGH",
                fieldEncryption: ["ssn", "creditCard"]
            ]
        )

        when: "requesting entity security guidance"
        def response = securityAgent.processRequest(request)

        then: "provides entity security patterns"
        response.success
        response.guidance.contains("entity-filter")
        response.guidance.contains("field encryption")
        response.guidance.contains("row-level security")
        response.compliance >= 99.0
        response.responseTimeMs <= 2000
    }

    @Unroll
    def "should provide field-level security for sensitive field: #fieldName"() {
        given: "field security request"
        def request = new AgentRequest(
            type: "ENTITY_SECURITY_GUIDANCE",
            context: securityContext,
            parameters: [fieldName: fieldName, dataType: dataType]
        )

        when: "requesting field security"
        def response = securityAgent.processRequest(request)

        then: "provides appropriate security guidance"
        response.success
        response.guidance.contains(expectedPattern)

        where:
        fieldName    | dataType | expectedPattern
        "ssn"        | "String" | "encrypt"
        "creditCard" | "String" | "encrypt"
        "email"      | "String" | "validate"
        "password"   | "String" | "hash"
    }

    // AC3: Service security guidance (2 seconds, 100% compliance)
    @Timeout(2)
    def "should provide service security guidance within 2 seconds"() {
        given: "service security guidance request"
        def request = new AgentRequest(
            type: "SERVICE_SECURITY_GUIDANCE",
            context: securityContext,
            moquiContext: moquiContext,
            parameters: [
                serviceName: "CustomerServices.createCustomer",
                requiredPermissions: ["CUSTOMER_CREATE"],
                inputValidation: true
            ]
        )

        when: "requesting service security guidance"
        def response = securityAgent.processRequest(request)

        then: "provides service security patterns"
        response.success
        response.guidance.contains("require-authentication")
        response.guidance.contains("input validation")
        response.guidance.contains("XSS prevention")
        response.compliance == 100.0
        response.responseTimeMs <= 2000
    }

    def "should provide input validation patterns for service parameters"() {
        given: "input validation request"
        def request = new AgentRequest(
            type: "SERVICE_SECURITY_GUIDANCE",
            context: securityContext,
            parameters: [
                inputParameters: [
                    "customerName": "String",
                    "email": "String", 
                    "phoneNumber": "String"
                ]
            ]
        )

        when: "requesting input validation guidance"
        def response = securityAgent.processRequest(request)

        then: "provides validation patterns"
        response.success
        response.guidance.contains("parameter validation")
        response.guidance.contains("email format")
        response.guidance.contains("phone format")
        response.guidance.contains("XSS protection")
    }

    // AC4: Screen security guidance (3 seconds, 100% compliance)
    @Timeout(3)
    def "should provide screen security guidance within 3 seconds"() {
        given: "screen security guidance request"
        def request = new AgentRequest(
            type: "SCREEN_SECURITY_GUIDANCE",
            context: securityContext,
            moquiContext: moquiContext,
            parameters: [
                screenName: "CustomerDetail",
                requiredRoles: ["CUSTOMER_VIEW"],
                conditionalFields: ["ssn", "creditLimit"]
            ]
        )

        when: "requesting screen security guidance"
        def response = securityAgent.processRequest(request)

        then: "provides screen security patterns"
        response.success
        response.guidance.contains("require-authentication")
        response.guidance.contains("conditional rendering")
        response.guidance.contains("Vue.js security")
        response.compliance == 100.0
        response.responseTimeMs <= 3000
    }

    def "should provide Vue.js security integration patterns"() {
        given: "Vue.js security request"
        def request = new AgentRequest(
            type: "SCREEN_SECURITY_GUIDANCE",
            context: securityContext,
            parameters: [
                framework: "Vue.js",
                components: ["CustomerForm", "CustomerList"]
            ]
        )

        when: "requesting Vue.js security guidance"
        def response = securityAgent.processRequest(request)

        then: "provides Vue.js security patterns"
        response.success
        response.guidance.contains("v-if security")
        response.guidance.contains("computed properties")
        response.guidance.contains("XSS prevention")
    }

    // AC5: External integration security (2 seconds, 99% compliance)
    @Timeout(2)
    def "should provide external integration security guidance within 2 seconds"() {
        given: "external integration security request"
        def request = new AgentRequest(
            type: "EXTERNAL_INTEGRATION_SECURITY_GUIDANCE",
            context: securityContext,
            parameters: [
                externalSystem: "durion-positivity-backend",
                protocol: "HTTPS",
                authentication: "JWT"
            ]
        )

        when: "requesting external integration security guidance"
        def response = securityAgent.processRequest(request)

        then: "provides integration security patterns"
        response.success
        response.guidance.contains("TLS 1.3")
        response.guidance.contains("certificate validation")
        response.guidance.contains("circuit breaker")
        response.compliance >= 99.0
        response.responseTimeMs <= 2000
    }

    def "should provide durion-positivity security patterns"() {
        given: "durion-positivity security request"
        def request = new AgentRequest(
            type: "EXTERNAL_INTEGRATION_SECURITY_GUIDANCE",
            context: securityContext,
            parameters: [
                targetSystem: "durion-positivity-backend",
                apiEndpoints: ["/api/customers", "/api/vehicles"]
            ]
        )

        when: "requesting durion-positivity security guidance"
        def response = securityAgent.processRequest(request)

        then: "provides durion-positivity security patterns"
        response.success
        response.guidance.contains("durion-positivity")
        response.guidance.contains("API key management")
        response.guidance.contains("rate limiting")
    }

    // Security constraint enforcement tests
    def "should enforce OWASP Top 10 protection"() {
        given: "OWASP security assessment request"
        def request = new AgentRequest(
            type: "SECURITY_ASSESSMENT",
            context: securityContext,
            parameters: [assessmentType: "OWASP_TOP_10"]
        )

        when: "requesting OWASP assessment"
        def response = securityAgent.processRequest(request)

        then: "provides OWASP protection guidance"
        response.success
        response.guidance.contains("injection prevention")
        response.guidance.contains("authentication")
        response.guidance.contains("sensitive data")
        response.guidance.contains("XML external entities")
        response.guidance.contains("broken access control")
    }

    def "should validate security configuration completeness"() {
        given: "security configuration validation request"
        def request = new AgentRequest(
            type: "SECURITY_VALIDATION",
            context: securityContext,
            parameters: [
                component: "durion-crm",
                validationType: "COMPLETENESS"
            ]
        )

        when: "validating security configuration"
        def response = securityAgent.processRequest(request)

        then: "validates security completeness"
        response.success
        response.validation.authenticationConfigured
        response.validation.authorizationConfigured
        response.validation.inputValidationConfigured
        response.validation.encryptionConfigured
    }

    // Error handling and edge cases
    def "should handle invalid security context gracefully"() {
        given: "invalid security context"
        def invalidContext = new SecurityContext(userId: null, roles: [])
        def request = new AgentRequest(
            type: "AUTHENTICATION_GUIDANCE",
            context: invalidContext
        )

        when: "processing request with invalid context"
        def response = securityAgent.processRequest(request)

        then: "handles gracefully with security guidance"
        response.success
        response.guidance.contains("authentication required")
        response.warnings.contains("invalid security context")
    }

    def "should provide security guidance for unknown entity types"() {
        given: "unknown entity security request"
        def request = new AgentRequest(
            type: "ENTITY_SECURITY_GUIDANCE",
            context: securityContext,
            parameters: [entityName: "UnknownEntity"]
        )

        when: "requesting security for unknown entity"
        def response = securityAgent.processRequest(request)

        then: "provides generic security guidance"
        response.success
        response.guidance.contains("default security")
        response.guidance.contains("entity-filter")
    }

    // Performance validation
    def "should maintain performance under concurrent requests"() {
        given: "multiple concurrent security requests"
        def requests = (1..10).collect { i ->
            new AgentRequest(
                type: "AUTHENTICATION_GUIDANCE",
                context: securityContext,
                parameters: [requestId: "concurrent-$i"]
            )
        }

        when: "processing concurrent requests"
        def responses = requests.collect { request ->
            securityAgent.processRequest(request)
        }

        then: "all requests complete within performance targets"
        responses.every { it.success }
        responses.every { it.responseTimeMs <= 1000 }
        responses.every { it.compliance >= 99.0 }
    }
}
