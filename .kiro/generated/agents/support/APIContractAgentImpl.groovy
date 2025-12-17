package agents.support

import agents.interfaces.Agent
import models.AgentRequest
import models.AgentResponse

/**
 * API Contract Agent - Validates API contracts and OpenAPI specifications
 * 
 * Requirements: REQ-003 AC4, REQ-007 AC4
 * Test Cases: TC-009, TC-021
 */
class APIContractAgentImpl implements Agent {
    
    @Override
    String getAgentId() { "api-contract-agent" }
    
    @Override
    String getAgentName() { "API Contract Agent" }
    
    @Override
    List<String> getCapabilities() {
        [
            "api-contract-validation",
            "openapi-specification",
            "contract-versioning",
            "breaking-change-detection",
            "cross-project-coordination"
        ]
    }
    
    @Override
    AgentResponse processRequest(AgentRequest request) {
        def startTime = System.currentTimeMillis()
        
        try {
            def guidance = switch (request.capability) {
                case "api-contract-validation" -> validateAPIContract(request)
                case "openapi-specification" -> generateOpenAPIGuidance(request)
                case "contract-versioning" -> provideVersioningGuidance(request)
                case "breaking-change-detection" -> detectBreakingChanges(request)
                case "cross-project-coordination" -> coordinateCrossProject(request)
                default -> "Unknown capability: ${request.capability}"
            }
            
            def responseTime = System.currentTimeMillis() - startTime
            
            return new AgentResponse(
                agentId: agentId,
                success: true,
                guidance: guidance,
                responseTimeMs: responseTime,
                metadata: [capability: request.capability]
            )
        } catch (Exception e) {
            return new AgentResponse(
                agentId: agentId,
                success: false,
                error: e.message,
                responseTimeMs: System.currentTimeMillis() - startTime
            )
        }
    }
    
    @Override
    boolean canHandle(AgentRequest request) {
        capabilities.contains(request.capability)
    }
    
    @Override
    Map<String, Object> getHealth() {
        [status: "healthy", agent: agentName]
    }
    
    private String validateAPIContract(AgentRequest request) {
        """API Contract Validation:
- Validate request/response schemas match OpenAPI spec
- Check required fields present
- Validate data types and formats
- Ensure error responses follow standard format
- Verify authentication requirements

Example validation:
```groovy
def validateContract(request, spec) {
    assert request.path == spec.path
    assert request.method == spec.method
    spec.parameters.each { param ->
        if (param.required) assert request.params[param.name]
    }
}
```"""
    }
    
    private String generateOpenAPIGuidance(AgentRequest request) {
        """OpenAPI Specification Guidance:
- Use OpenAPI 3.0+ format
- Document all endpoints with descriptions
- Include request/response examples
- Define reusable schemas in components
- Specify authentication schemes

Example OpenAPI spec:
```yaml
openapi: 3.0.0
paths:
  /api/v1/orders:
    get:
      summary: List orders
      parameters:
        - name: status
          in: query
          schema:
            type: string
      responses:
        '200':
          description: Success
          content:
            application/json:
              schema:
                type: array
                items:
                  \$ref: '#/components/schemas/Order'
```"""
    }
    
    private String provideVersioningGuidance(AgentRequest request) {
        """API Contract Versioning:
- Use semantic versioning (v1, v2, etc.)
- Maintain backward compatibility within major version
- Document breaking changes clearly
- Provide migration guides for version upgrades
- Support multiple versions simultaneously

Versioning strategy:
- URL path versioning: /api/v1/resource
- Header versioning: Accept: application/vnd.api+json;version=1
- Deprecation warnings in responses
- Sunset headers for version retirement"""
    }
    
    private String detectBreakingChanges(AgentRequest request) {
        """Breaking Change Detection:
- Removing required fields
- Changing field types
- Removing endpoints
- Changing authentication requirements
- Modifying error response formats

Detection rules:
1. Compare old vs new OpenAPI specs
2. Flag removed required parameters
3. Flag type changes in existing fields
4. Flag removed endpoints
5. Generate migration report

Example check:
```groovy
def detectBreaking(oldSpec, newSpec) {
    def breaking = []
    oldSpec.paths.each { path, ops ->
        if (!newSpec.paths[path]) breaking << "Removed: \$path"
    }
    return breaking
}
```"""
    }
    
    private String coordinateCrossProject(AgentRequest request) {
        """Cross-Project Contract Coordination:
- Coordinate with durion-positivity-backend API contracts
- Ensure frontend-backend contract alignment
- Validate integration points
- Synchronize API versions across projects
- Manage shared schema definitions

Coordination pattern:
1. Define contracts in shared repository
2. Generate client/server code from contracts
3. Validate implementations against contracts
4. Run contract tests in CI/CD
5. Version contracts independently

Integration with durion-positivity:
- Use shared OpenAPI specs
- Validate REST client matches backend contract
- Test contract compliance in integration tests
- Coordinate breaking changes across projects"""
    }
}
