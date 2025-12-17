package tests.properties

import net.jqwik.api.*
import net.jqwik.api.constraints.*
import agents.infrastructure.SecurityAgentImpl
import models.AgentRequest
import models.SecurityContext
import models.MoquiContext

/**
 * Property-based tests for SecurityAgentImpl using jqwik
 * Validates Property 5: Security constraint enforcement
 * 
 * Requirements: REQ-004 (Security Agent), REQ-011 (Security Requirements)
 * Properties: JWT authentication, role-based authorization, TLS 1.3, audit trails, threat detection
 */
class SecurityProperties {

    SecurityAgentImpl securityAgent = new SecurityAgentImpl()

    /**
     * Property 5.1: JWT Authentication Required
     * All authentication requests must enforce JWT token validation
     */
    @Property
    @Report(Reporting.GENERATED)
    def "JWT authentication must be enforced for all requests"(
        @ForAll @AlphaChars @StringLength(min = 5, max = 20) String userId,
        @ForAll @Size(min = 1, max = 5) List<@AlphaChars String> roles,
        @ForAll @AlphaChars @StringLength(min = 10, max = 50) String sessionId
    ) {
        given: "security context and authentication request"
        def securityContext = new SecurityContext(
            userId: userId,
            roles: roles,
            sessionId: sessionId
        )
        def request = new AgentRequest(
            type: "AUTHENTICATION_GUIDANCE",
            context: securityContext,
            parameters: [authenticationType: "JWT"]
        )

        when: "processing authentication request"
        def response = securityAgent.processRequest(request)

        then: "JWT authentication is enforced"
        assert response.success
        assert response.guidance.contains("JWT")
        assert response.guidance.contains("token validation")
        assert response.compliance == 100.0
        assert response.securityConstraints.jwtRequired == true
    }

    /**
     * Property 5.2: Role-Based Authorization
     * All authorization decisions must be based on user roles and permissions
     */
    @Property
    @Report(Reporting.GENERATED)
    def "role-based authorization must be enforced consistently"(
        @ForAll @AlphaChars @StringLength(min = 5, max = 20) String serviceName,
        @ForAll @Size(min = 1, max = 3) List<@AlphaChars String> requiredRoles,
        @ForAll @Size(min = 1, max = 5) List<@AlphaChars String> userRoles
    ) {
        given: "service security request with roles"
        def securityContext = new SecurityContext(
            userId: "testUser",
            roles: userRoles
        )
        def request = new AgentRequest(
            type: "SERVICE_SECURITY_GUIDANCE",
            context: securityContext,
            parameters: [
                serviceName: serviceName,
                requiredRoles: requiredRoles
            ]
        )

        when: "processing service security request"
        def response = securityAgent.processRequest(request)

        then: "role-based authorization is enforced"
        assert response.success
        assert response.guidance.contains("require-authentication")
        assert response.guidance.contains("role")
        assert response.compliance == 100.0
        
        // Check if user has required roles
        def hasRequiredRole = requiredRoles.any { role -> userRoles.contains(role) }
        if (hasRequiredRole) {
            assert response.authorization.granted == true
        } else {
            assert response.authorization.granted == false
            assert response.guidance.contains("insufficient privileges")
        }
    }

    /**
     * Property 5.3: TLS 1.3 Encryption Required
     * All external communications must use TLS 1.3 encryption
     */
    @Property
    @Report(Reporting.GENERATED)
    def "TLS 1.3 encryption must be enforced for external communications"(
        @ForAll @AlphaChars @StringLength(min = 5, max = 30) String externalSystem,
        @ForAll("httpsMethods") String protocol,
        @ForAll @AlphaChars @StringLength(min = 5, max = 20) String endpoint
    ) {
        given: "external integration security request"
        def securityContext = new SecurityContext(
            userId: "testUser",
            roles: ["ADMIN"]
        )
        def request = new AgentRequest(
            type: "EXTERNAL_INTEGRATION_SECURITY_GUIDANCE",
            context: securityContext,
            parameters: [
                externalSystem: externalSystem,
                protocol: protocol,
                endpoint: endpoint
            ]
        )

        when: "processing external integration request"
        def response = securityAgent.processRequest(request)

        then: "TLS 1.3 encryption is enforced"
        assert response.success
        assert response.guidance.contains("TLS 1.3")
        assert response.guidance.contains("certificate validation")
        assert response.compliance >= 99.0
        assert response.securityConstraints.tlsRequired == true
        assert response.securityConstraints.minTlsVersion == "1.3"
    }

    @Provide
    def httpsMethods() {
        return Arbitraries.of("HTTPS", "WSS", "FTPS")
    }

    /**
     * Property 5.4: Audit Trail Completeness
     * All security-related operations must generate complete audit trails
     */
    @Property
    @Report(Reporting.GENERATED)
    def "audit trails must be complete for all security operations"(
        @ForAll("securityOperationTypes") String operationType,
        @ForAll @AlphaChars @StringLength(min = 5, max = 20) String userId,
        @ForAll @AlphaChars @StringLength(min = 5, max = 30) String resourceName
    ) {
        given: "security operation request"
        def securityContext = new SecurityContext(
            userId: userId,
            roles: ["USER"]
        )
        def request = new AgentRequest(
            type: operationType,
            context: securityContext,
            parameters: [
                resourceName: resourceName,
                auditRequired: true
            ]
        )

        when: "processing security operation"
        def response = securityAgent.processRequest(request)

        then: "complete audit trail is generated"
        assert response.success
        assert response.auditTrail != null
        assert response.auditTrail.userId == userId
        assert response.auditTrail.operation == operationType
        assert response.auditTrail.resourceName == resourceName
        assert response.auditTrail.timestamp != null
        assert response.auditTrail.success != null
        assert response.guidance.contains("audit")
    }

    @Provide
    def securityOperationTypes() {
        return Arbitraries.of(
            "AUTHENTICATION_GUIDANCE",
            "ENTITY_SECURITY_GUIDANCE", 
            "SERVICE_SECURITY_GUIDANCE",
            "SCREEN_SECURITY_GUIDANCE",
            "EXTERNAL_INTEGRATION_SECURITY_GUIDANCE"
        )
    }

    /**
     * Property 5.5: Threat Detection Within 5 Seconds
     * Security threats must be detected and reported within 5 seconds
     */
    @Property
    @Report(Reporting.GENERATED)
    def "threat detection must complete within 5 seconds"(
        @ForAll("threatTypes") String threatType,
        @ForAll @AlphaChars @StringLength(min = 5, max = 50) String threatData,
        @ForAll @IntRange(min = 1, max = 10) int severity
    ) {
        given: "threat detection request"
        def securityContext = new SecurityContext(
            userId: "securityAnalyst",
            roles: ["SECURITY_ADMIN"]
        )
        def request = new AgentRequest(
            type: "THREAT_DETECTION",
            context: securityContext,
            parameters: [
                threatType: threatType,
                threatData: threatData,
                severity: severity
            ]
        )

        when: "processing threat detection"
        long startTime = System.currentTimeMillis()
        def response = securityAgent.processRequest(request)
        long endTime = System.currentTimeMillis()
        long responseTime = endTime - startTime

        then: "threat detection completes within 5 seconds"
        assert response.success
        assert responseTime <= 5000 // 5 seconds
        assert response.threatDetection != null
        assert response.threatDetection.threatType == threatType
        assert response.threatDetection.severity == severity
        assert response.threatDetection.detected != null
        
        if (response.threatDetection.detected) {
            assert response.guidance.contains("threat detected")
            assert response.guidance.contains("mitigation")
        }
    }

    @Provide
    def threatTypes() {
        return Arbitraries.of(
            "SQL_INJECTION",
            "XSS_ATTACK",
            "CSRF_ATTACK", 
            "BRUTE_FORCE",
            "PRIVILEGE_ESCALATION",
            "DATA_EXFILTRATION",
            "MALICIOUS_INPUT"
        )
    }

    /**
     * Property 5.6: Input Validation Consistency
     * All input validation must be consistent across different input types
     */
    @Property
    @Report(Reporting.GENERATED)
    def "input validation must be consistent across all input types"(
        @ForAll("inputTypes") String inputType,
        @ForAll @StringLength(min = 1, max = 100) String inputValue,
        @ForAll @AlphaChars @StringLength(min = 5, max = 20) String fieldName
    ) {
        given: "input validation request"
        def securityContext = new SecurityContext(
            userId: "developer",
            roles: ["DEVELOPER"]
        )
        def request = new AgentRequest(
            type: "SERVICE_SECURITY_GUIDANCE",
            context: securityContext,
            parameters: [
                inputValidation: true,
                inputType: inputType,
                inputValue: inputValue,
                fieldName: fieldName
            ]
        )

        when: "processing input validation request"
        def response = securityAgent.processRequest(request)

        then: "consistent validation patterns are provided"
        assert response.success
        assert response.guidance.contains("input validation")
        assert response.guidance.contains("XSS prevention")
        assert response.inputValidation != null
        assert response.inputValidation.fieldName == fieldName
        assert response.inputValidation.inputType == inputType
        assert response.inputValidation.validationRules != null
        assert response.inputValidation.validationRules.size() > 0
    }

    @Provide
    def inputTypes() {
        return Arbitraries.of(
            "String", "Integer", "Long", "BigDecimal", 
            "Date", "Timestamp", "Boolean", "Email", 
            "Phone", "URL", "JSON", "XML"
        )
    }

    /**
     * Property 5.7: Security Configuration Idempotency
     * Security configuration operations must be idempotent
     */
    @Property
    @Report(Reporting.GENERATED)
    def "security configuration must be idempotent"(
        @ForAll @AlphaChars @StringLength(min = 5, max = 20) String componentName,
        @ForAll("securityLevels") String securityLevel,
        @ForAll @Size(min = 1, max = 5) List<@AlphaChars String> securityFeatures
    ) {
        given: "security configuration request"
        def securityContext = new SecurityContext(
            userId: "admin",
            roles: ["ADMIN"]
        )
        def request = new AgentRequest(
            type: "SECURITY_CONFIGURATION",
            context: securityContext,
            parameters: [
                componentName: componentName,
                securityLevel: securityLevel,
                securityFeatures: securityFeatures
            ]
        )

        when: "applying security configuration multiple times"
        def response1 = securityAgent.processRequest(request)
        def response2 = securityAgent.processRequest(request)
        def response3 = securityAgent.processRequest(request)

        then: "configuration results are idempotent"
        assert response1.success && response2.success && response3.success
        assert response1.guidance == response2.guidance
        assert response2.guidance == response3.guidance
        assert response1.securityConfiguration == response2.securityConfiguration
        assert response2.securityConfiguration == response3.securityConfiguration
    }

    @Provide
    def securityLevels() {
        return Arbitraries.of("LOW", "MEDIUM", "HIGH", "CRITICAL")
    }

    /**
     * Property 5.8: Cross-Domain Security Consistency
     * Security policies must be consistent across different Moqui domains
     */
    @Property
    @Report(Reporting.GENERATED)
    def "security policies must be consistent across domains"(
        @ForAll("moquiDomains") String domain1,
        @ForAll("moquiDomains") String domain2,
        @ForAll @AlphaChars @StringLength(min = 5, max = 20) String securityPolicy
    ) {
        given: "security policy requests for different domains"
        def securityContext = new SecurityContext(
            userId: "architect",
            roles: ["ARCHITECT"]
        )
        def request1 = new AgentRequest(
            type: "SECURITY_POLICY_GUIDANCE",
            context: securityContext,
            parameters: [
                domain: domain1,
                securityPolicy: securityPolicy
            ]
        )
        def request2 = new AgentRequest(
            type: "SECURITY_POLICY_GUIDANCE", 
            context: securityContext,
            parameters: [
                domain: domain2,
                securityPolicy: securityPolicy
            ]
        )

        when: "requesting security policies for both domains"
        def response1 = securityAgent.processRequest(request1)
        def response2 = securityAgent.processRequest(request2)

        then: "security policies are consistent across domains"
        assert response1.success && response2.success
        
        // Core security principles should be consistent
        assert response1.securityPrinciples.authentication == response2.securityPrinciples.authentication
        assert response1.securityPrinciples.authorization == response2.securityPrinciples.authorization
        assert response1.securityPrinciples.encryption == response2.securityPrinciples.encryption
        assert response1.securityPrinciples.auditTrail == response2.securityPrinciples.auditTrail
        
        // Both should enforce the same security policy
        assert response1.guidance.contains(securityPolicy)
        assert response2.guidance.contains(securityPolicy)
    }

    @Provide
    def moquiDomains() {
        return Arbitraries.of(
            "durion-crm", "durion-inventory", "durion-accounting",
            "durion-workexec", "durion-product", "PopCommerce", 
            "HiveMind", "MarbleERP"
        )
    }
}
