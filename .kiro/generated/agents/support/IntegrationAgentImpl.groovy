package agents.support

import agents.interfaces.Agent
import models.AgentRequest
import models.AgentResponse
import groovy.transform.CompileStatic

/**
 * Integration Agent Implementation
 * 
 * Provides guidance for:
 * - durion-positivity integration patterns
 * - Circuit breaker guidance
 * - Error handling and retry logic
 * - Integration testing patterns
 * - Integration failure handling (REQ-014)
 * 
 * Requirements: REQ-003 AC4, REQ-013, REQ-014
 * Test Cases: TC-009, TC-037, TC-038, TC-039, TC-040, TC-041, TC-042
 */
@CompileStatic
class IntegrationAgentImpl implements Agent {
    
    private static final String AGENT_ID = "integration-agent"
    private static final String AGENT_NAME = "Integration Agent"
    private static final List<String> CAPABILITIES = [
        "durion-positivity-integration",
        "circuit-breaker-patterns",
        "error-handling",
        "retry-logic",
        "integration-testing",
        "failure-handling"
    ]
    
    @Override
    String getAgentId() { AGENT_ID }
    
    @Override
    String getAgentName() { AGENT_NAME }
    
    @Override
    List<String> getCapabilities() { CAPABILITIES }
    
    @Override
    boolean canHandle(AgentRequest request) {
        request.capability in CAPABILITIES
    }
    
    @Override
    AgentResponse processRequest(AgentRequest request) {
        long startTime = System.currentTimeMillis()
        
        String guidance = switch(request.capability) {
            case "durion-positivity-integration" -> provideDurionPositivityIntegration(request)
            case "circuit-breaker-patterns" -> provideCircuitBreakerGuidance(request)
            case "error-handling" -> provideErrorHandlingGuidance(request)
            case "retry-logic" -> provideRetryLogicGuidance(request)
            case "integration-testing" -> provideIntegrationTestingGuidance(request)
            case "failure-handling" -> provideFailureHandlingGuidance(request)
            default -> "Unknown capability: ${request.capability}"
        }
        
        long responseTime = System.currentTimeMillis() - startTime
        
        return new AgentResponse(
            agentId: AGENT_ID,
            requestId: request.requestId,
            guidance: guidance,
            responseTimeMs: responseTime,
            success: true
        )
    }
    
    @Override
    Map<String, Object> getHealth() {
        [
            status: "healthy",
            agentId: AGENT_ID,
            capabilities: CAPABILITIES.size(),
            timestamp: System.currentTimeMillis()
        ]
    }
    
    /**
     * Provide durion-positivity integration patterns
     * REQ-003 AC4, REQ-013
     */
    private String provideDurionPositivityIntegration(AgentRequest request) {
        return """
durion-positivity Integration Patterns:

1. Component Structure:
   - Location: runtime/component/durion-positivity/
   - Purpose: Integration layer with durion-positivity-backend services
   - Pattern: REST client wrapper with circuit breaker

2. Service Integration Pattern:
   <service verb="get" noun="VehicleData" type="interface">
       <description>Fetch vehicle data from durion-positivity-backend</description>
       <in-parameters>
           <parameter name="vin" required="true"/>
       </in-parameters>
       <out-parameters>
           <parameter name="vehicleData" type="Map"/>
       </out-parameters>
       <actions>
           <script><![CDATA[
               import org.moqui.util.RestClient
               
               def client = ec.service.rest()
                   .uri("http://positivity-backend:8080/api/v1/vehicles/\${vin}")
                   .method("GET")
                   .addHeader("Authorization", "Bearer \${ec.user.token}")
                   .timeout(3000)
               
               def response = client.call()
               if (response.statusCode == 200) {
                   vehicleData = response.jsonObject()
               } else {
                   ec.message.addError("Failed to fetch vehicle data: \${response.statusCode}")
               }
           ]]></script>
       </actions>
   </service>

3. Circuit Breaker Configuration:
   - Use Resilience4j or similar pattern
   - Failure threshold: 50% over 10 requests
   - Open circuit duration: 30 seconds
   - Half-open test requests: 3

4. Error Handling:
   - Timeout: 3 seconds for API calls
   - Retry: 3 attempts with exponential backoff
   - Fallback: Return cached data or graceful degradation

5. Integration Testing:
   - Mock durion-positivity-backend responses
   - Test circuit breaker behavior
   - Validate error handling and retry logic
"""
    }
    
    /**
     * Provide circuit breaker guidance
     * REQ-013
     */
    private String provideCircuitBreakerGuidance(AgentRequest request) {
        return """
Circuit Breaker Pattern Guidance:

1. Circuit States:
   - CLOSED: Normal operation, requests pass through
   - OPEN: Failure threshold exceeded, requests fail fast
   - HALF_OPEN: Testing if service recovered

2. Configuration:
   failureRateThreshold: 50
   slowCallRateThreshold: 100
   slowCallDurationThreshold: 3000ms
   waitDurationInOpenState: 30s
   permittedNumberOfCallsInHalfOpenState: 3
   slidingWindowSize: 10
   minimumNumberOfCalls: 5

3. Implementation Pattern:
   <script><![CDATA[
       import io.github.resilience4j.circuitbreaker.CircuitBreaker
       import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
       
       def config = CircuitBreakerConfig.custom()
           .failureRateThreshold(50)
           .waitDurationInOpenState(Duration.ofSeconds(30))
           .build()
       
       def circuitBreaker = CircuitBreaker.of("positivity-backend", config)
       
       def result = circuitBreaker.executeSupplier(() -> {
           // Call durion-positivity-backend
           return ec.service.rest()
               .uri("http://positivity-backend:8080/api/v1/resource")
               .call()
       })
   ]]></script>

4. Monitoring:
   - Track circuit state transitions
   - Log failure rates and slow calls
   - Alert on circuit open events

5. Fallback Strategy:
   - Return cached data
   - Return default values
   - Queue request for later processing
"""
    }
    
    /**
     * Provide error handling guidance
     * REQ-013, REQ-014
     */
    private String provideErrorHandlingGuidance(AgentRequest request) {
        return """
Error Handling Guidance:

1. Error Categories:
   - Transient: Network timeouts, temporary unavailability
   - Permanent: Invalid credentials, resource not found
   - Degraded: Partial service availability

2. Handling Strategy:
   <script><![CDATA[
       try {
           def response = ec.service.rest()
               .uri("http://positivity-backend:8080/api/v1/resource")
               .timeout(3000)
               .call()
           
           if (response.statusCode == 200) {
               return response.jsonObject()
           } else if (response.statusCode >= 500) {
               // Transient error - retry
               throw new TransientException("Service unavailable")
           } else if (response.statusCode == 404) {
               // Permanent error - no retry
               ec.message.addError("Resource not found")
               return null
           }
       } catch (SocketTimeoutException e) {
           // Transient - retry with backoff
           ec.logger.warn("Timeout calling positivity-backend", e)
           throw new TransientException("Timeout", e)
       } catch (Exception e) {
           // Unknown error - log and fail
           ec.logger.error("Unexpected error", e)
           throw e
       }
   ]]></script>

3. Error Response Format:
   {
       "error": {
           "code": "INTEGRATION_FAILURE",
           "message": "Failed to fetch data from durion-positivity-backend",
           "details": "Connection timeout after 3000ms",
           "retryable": true
       }
   }

4. Logging:
   - Log all integration errors with context
   - Include request ID for tracing
   - Track error rates and patterns
"""
    }
    
    /**
     * Provide retry logic guidance
     * REQ-013
     */
    private String provideRetryLogicGuidance(AgentRequest request) {
        return """
Retry Logic Guidance:

1. Retry Strategy:
   - Max attempts: 3
   - Backoff: Exponential (1s, 2s, 4s)
   - Jitter: ±20% to prevent thundering herd

2. Implementation:
   <script><![CDATA[
       import java.util.concurrent.TimeUnit
       
       def maxAttempts = 3
       def baseDelayMs = 1000
       def attempt = 0
       
       while (attempt < maxAttempts) {
           try {
               def response = ec.service.rest()
                   .uri("http://positivity-backend:8080/api/v1/resource")
                   .call()
               
               if (response.statusCode == 200) {
                   return response.jsonObject()
               } else if (response.statusCode >= 500) {
                   attempt++
                   if (attempt < maxAttempts) {
                       def delayMs = baseDelayMs * Math.pow(2, attempt - 1)
                       def jitter = (Math.random() * 0.4 - 0.2) * delayMs
                       TimeUnit.MILLISECONDS.sleep((long)(delayMs + jitter))
                   }
               } else {
                   // Non-retryable error
                   break
               }
           } catch (SocketTimeoutException e) {
               attempt++
               if (attempt < maxAttempts) {
                   def delayMs = baseDelayMs * Math.pow(2, attempt - 1)
                   TimeUnit.MILLISECONDS.sleep((long)delayMs)
               }
           }
       }
       
       ec.message.addError("Failed after \${maxAttempts} attempts")
       return null
   ]]></script>

3. Retry Conditions:
   - Retry: 5xx errors, timeouts, connection failures
   - No Retry: 4xx errors (except 429 rate limit), authentication failures

4. Monitoring:
   - Track retry attempts and success rates
   - Alert on high retry rates
"""
    }
    
    /**
     * Provide integration testing guidance
     * REQ-013
     */
    private String provideIntegrationTestingGuidance(AgentRequest request) {
        return """
Integration Testing Guidance:

1. Test Structure (Spock):
   class PositivityIntegrationSpec extends Specification {
       
       def "should fetch vehicle data successfully"() {
           given: "Mock durion-positivity-backend response"
           def mockServer = new MockRestServer()
           mockServer.expect(requestTo("http://positivity-backend:8080/api/v1/vehicles/VIN123"))
               .andRespond(withSuccess('{"vin":"VIN123","make":"Toyota"}', MediaType.APPLICATION_JSON))
           
           when: "Calling integration service"
           def result = ec.service.sync().name("durion.positivity.VehicleServices.get#VehicleData")
               .parameter("vin", "VIN123")
               .call()
           
           then: "Vehicle data is returned"
           result.vehicleData.vin == "VIN123"
           result.vehicleData.make == "Toyota"
       }
       
       def "should handle timeout with retry"() {
           given: "Mock server with timeout"
           def mockServer = new MockRestServer()
           mockServer.expect(times(3), requestTo("http://positivity-backend:8080/api/v1/vehicles/VIN123"))
               .andRespond(withTimeout())
           
           when: "Calling integration service"
           def result = ec.service.sync().name("durion.positivity.VehicleServices.get#VehicleData")
               .parameter("vin", "VIN123")
               .call()
           
           then: "Error is returned after retries"
           result.vehicleData == null
           ec.message.hasError()
       }
       
       def "should use circuit breaker on repeated failures"() {
           given: "Mock server with repeated failures"
           def mockServer = new MockRestServer()
           mockServer.expect(times(10), requestTo("http://positivity-backend:8080/api/v1/vehicles/VIN123"))
               .andRespond(withServerError())
           
           when: "Calling integration service multiple times"
           10.times {
               ec.service.sync().name("durion.positivity.VehicleServices.get#VehicleData")
                   .parameter("vin", "VIN123")
                   .call()
           }
           
           then: "Circuit breaker opens"
           circuitBreaker.state == CircuitBreaker.State.OPEN
       }
   }

2. Test Coverage:
   - Success scenarios
   - Timeout and retry scenarios
   - Circuit breaker behavior
   - Error handling and fallback
"""
    }
    
    /**
     * Provide failure handling guidance (REQ-014)
     * AC1: Framework version conflicts (10 sec, 90% resolution)
     * AC2: Dependency conflicts (5 sec, 95% accuracy)
     * AC3: External system failures (3 sec, 85% workaround success)
     * AC4: Workspace communication failures (maintain 80% capability)
     * AC5: Database connectivity failures (2 sec, 100% data protection)
     */
    private String provideFailureHandlingGuidance(AgentRequest request) {
        String failureType = request.context?.failureType ?: "general"
        
        return switch(failureType) {
            case "framework-version-conflict" -> handleFrameworkVersionConflict()
            case "dependency-conflict" -> handleDependencyConflict()
            case "external-system-failure" -> handleExternalSystemFailure()
            case "workspace-communication-failure" -> handleWorkspaceCommunicationFailure()
            case "database-connectivity-failure" -> handleDatabaseConnectivityFailure()
            default -> provideGeneralFailureHandling()
        }
    }
    
    private String handleFrameworkVersionConflict() {
        return """
Framework Version Conflict Resolution (10 sec, 90% resolution):

1. Detection:
   - Check Moqui Framework version in build.gradle
   - Validate component compatibility
   - Identify conflicting dependencies

2. Resolution Strategy:
   a) Check component.xml for version requirements:
      <depends-on name="mantle-udm" version="3.0.0"/>
   
   b) Update build.gradle to align versions:
      dependencies {
          implementation "org.moqui:moqui-framework:3.0.0"
          implementation "org.moqui.mantle:mantle-udm:3.0.0"
      }
   
   c) Run dependency resolution:
      ./gradlew dependencies --configuration runtimeClasspath

3. Fallback:
   - Use compatibility layer if available
   - Isolate conflicting components
   - Document version constraints

4. Response Time Target: 10 seconds
5. Resolution Success Rate: 90%
"""
    }
    
    private String handleDependencyConflict() {
        return """
Dependency Conflict Resolution (5 sec, 95% accuracy):

1. Detection:
   - Run: ./gradlew dependencies
   - Identify conflicting versions
   - Check for transitive dependency issues

2. Resolution:
   a) Force specific version:
      configurations.all {
          resolutionStrategy {
              force 'org.apache.commons:commons-lang3:3.12.0'
          }
      }
   
   b) Exclude transitive dependency:
      implementation('org.moqui:component') {
          exclude group: 'org.apache.commons', module: 'commons-lang3'
      }
   
   c) Use dependency constraints:
      dependencies {
          constraints {
              implementation('org.apache.commons:commons-lang3:3.12.0')
          }
      }

3. Validation:
   - Run: ./gradlew build
   - Verify no conflicts remain
   - Test affected functionality

4. Response Time Target: 5 seconds
5. Accuracy: 95%
"""
    }
    
    private String handleExternalSystemFailure() {
        return """
External System Failure Handling (3 sec, 85% workaround success):

1. Detection:
   - Monitor durion-positivity-backend health
   - Track response times and error rates
   - Alert on service unavailability

2. Workaround Strategy:
   a) Use cached data:
      def cachedData = ec.cache.get("vehicle-data", vin)
      if (cachedData) return cachedData
   
   b) Fallback to local data:
      def localData = ec.entity.find("Vehicle")
          .condition("vin", vin)
          .one()
   
   c) Queue request for later:
      ec.service.async().name("durion.positivity.QueuedRequest.create#Request")
          .parameter("requestType", "vehicle-data")
          .parameter("requestData", [vin: vin])
          .call()
   
   d) Return partial data:
      return [
          vin: vin,
          status: "partial",
          message: "External system unavailable, showing cached data"
      ]

3. Recovery:
   - Retry with exponential backoff
   - Process queued requests when service recovers
   - Update cache with fresh data

4. Response Time Target: 3 seconds
5. Workaround Success Rate: 85%
"""
    }
    
    private String handleWorkspaceCommunicationFailure() {
        return """
Workspace Communication Failure Handling (maintain 80% capability):

1. Detection:
   - Monitor workspace agent connectivity
   - Track cross-project API calls
   - Alert on communication failures

2. Local Operation Mode:
   a) Use local agent capabilities:
      - Moqui Framework Agent (entity, service, screen guidance)
      - Domain Agent (business logic patterns)
      - Security Agent (authentication, authorization)
      - Testing Agent (local test generation)
   
   b) Queue cross-project requests:
      def queuedRequest = [
          targetWorkspace: "durion-positivity-backend",
          requestType: "api-contract-validation",
          requestData: apiContract,
          timestamp: System.currentTimeMillis()
      ]
      ec.cache.put("queued-workspace-requests", requestId, queuedRequest)
   
   c) Use cached workspace data:
      def cachedContract = ec.cache.get("api-contracts", contractId)
      if (cachedContract) return cachedContract

3. Capability Retention:
   - 100%: Local Moqui development (entities, services, screens)
   - 100%: Domain-specific guidance
   - 80%: Cross-project integration (cached contracts)
   - 60%: API contract validation (local validation only)
   - 0%: Real-time workspace coordination

4. Recovery:
   - Process queued requests when connectivity restored
   - Sync cached data with workspace agents
   - Validate queued operations

5. Target: Maintain 80% capability during failure
"""
    }
    
    private String handleDatabaseConnectivityFailure() {
        return """
Database Connectivity Failure Handling (2 sec, 100% data protection):

1. Detection:
   - Monitor database connection pool
   - Track query timeouts and failures
   - Alert on connectivity loss

2. Data Protection Strategy:
   a) Transaction rollback:
      try {
          ec.transaction.begin()
          // Database operations
          ec.transaction.commit()
      } catch (Exception e) {
          ec.transaction.rollback()
          ec.logger.error("Database operation failed", e)
      }
   
   b) Queue write operations:
      def queuedWrite = [
          entityName: "Vehicle",
          operation: "create",
          data: vehicleData,
          timestamp: System.currentTimeMillis()
      ]
      ec.cache.put("queued-writes", writeId, queuedWrite)
   
   c) Use in-memory cache for reads:
      def cachedEntity = ec.cache.get("entity-cache", entityId)
      if (cachedEntity) return cachedEntity

3. Recovery:
   a) Reconnect to database:
      - Retry connection with exponential backoff
      - Validate connection health
   
   b) Process queued writes:
      - Replay queued operations in order
      - Validate data consistency
      - Handle conflicts
   
   c) Refresh cache:
      - Invalidate stale cache entries
      - Reload critical data

4. Response Time Target: 2 seconds
5. Data Protection: 100% (no data loss)
"""
    }
    
    private String provideGeneralFailureHandling() {
        return """
General Integration Failure Handling:

1. Failure Types:
   - Framework version conflicts (REQ-014 AC1)
   - Dependency conflicts (REQ-014 AC2)
   - External system failures (REQ-014 AC3)
   - Workspace communication failures (REQ-014 AC4)
   - Database connectivity failures (REQ-014 AC5)

2. Common Patterns:
   - Detect failures quickly (< 5 seconds)
   - Provide workarounds or fallbacks
   - Queue operations for later processing
   - Maintain data consistency
   - Log all failures with context

3. Recovery Strategy:
   - Automatic retry with exponential backoff
   - Manual intervention for persistent failures
   - Graceful degradation of functionality
   - Clear user communication

4. Monitoring:
   - Track failure rates by type
   - Alert on critical failures
   - Generate failure reports

For specific failure types, provide failureType in request context:
- framework-version-conflict
- dependency-conflict
- external-system-failure
- workspace-communication-failure
- database-connectivity-failure
"""
    }
}
