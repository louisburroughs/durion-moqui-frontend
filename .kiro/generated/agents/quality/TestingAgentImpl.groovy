package agents.quality

import agents.interfaces.Agent
import agents.models.AgentRequest
import agents.models.AgentResponse
import agents.models.MoquiContext
import agents.models.ImplementationContext

/**
 * Testing Agent Implementation
 * 
 * Provides comprehensive testing guidance for Moqui Framework applications:
 * - Entity testing with Spock specifications
 * - Service testing with mock data and transactions
 * - Screen testing with Jest for Vue.js components
 * - Workflow testing for cross-domain integration
 * - External integration testing with circuit breakers
 * 
 * Requirements: REQ-005 (all 5 acceptance criteria)
 * Performance Targets: 2-4 second response time, 90-100% coverage
 * Test Cases: TC-013, TC-014, TC-015
 */
class TestingAgentImpl implements Agent {
    
    private static final String AGENT_ID = "testing-agent"
    private static final String AGENT_NAME = "Testing Agent"
    private static final List<String> CAPABILITIES = [
        "entity-testing-guidance",
        "service-testing-guidance", 
        "screen-testing-guidance",
        "workflow-testing-guidance",
        "integration-testing-guidance",
        "spock-framework-patterns",
        "jest-component-testing",
        "mock-data-generation",
        "test-coverage-analysis"
    ]
    
    @Override
    String getAgentId() { return AGENT_ID }
    
    @Override
    String getAgentName() { return AGENT_NAME }
    
    @Override
    List<String> getCapabilities() { return CAPABILITIES }
    
    @Override
    boolean canHandle(AgentRequest request) {
        return request.capability in CAPABILITIES ||
               request.context?.contains("testing") ||
               request.context?.contains("test") ||
               request.context?.contains("spec") ||
               request.context?.contains("junit") ||
               request.context?.contains("spock") ||
               request.context?.contains("jest")
    }
    
    @Override
    AgentResponse processRequest(AgentRequest request) {
        long startTime = System.currentTimeMillis()
        
        try {
            String guidance = generateTestingGuidance(request)
            long responseTime = System.currentTimeMillis() - startTime
            
            return new AgentResponse(
                agentId: AGENT_ID,
                success: true,
                content: guidance,
                responseTimeMs: responseTime,
                metadata: [
                    testingType: determineTestingType(request),
                    framework: determineTestFramework(request),
                    coverageTarget: determineCoverageTarget(request)
                ]
            )
        } catch (Exception e) {
            return new AgentResponse(
                agentId: AGENT_ID,
                success: false,
                error: "Testing guidance generation failed: ${e.message}",
                responseTimeMs: System.currentTimeMillis() - startTime
            )
        }
    }
    
    @Override
    Map<String, Object> getHealth() {
        return [
            status: "healthy",
            capabilities: CAPABILITIES.size(),
            lastCheck: new Date(),
            testFrameworks: ["spock", "jest", "junit"],
            coverageTargets: [
                entity: "95%",
                service: "98%", 
                screen: "90%",
                workflow: "92%",
                integration: "100%"
            ]
        ]
    }
    
    // Core guidance generation
    private String generateTestingGuidance(AgentRequest request) {
        String testingType = determineTestingType(request)
        
        switch (testingType) {
            case "entity":
                return generateEntityTestingGuidance(request)
            case "service":
                return generateServiceTestingGuidance(request)
            case "screen":
                return generateScreenTestingGuidance(request)
            case "workflow":
                return generateWorkflowTestingGuidance(request)
            case "integration":
                return generateIntegrationTestingGuidance(request)
            default:
                return generateGeneralTestingGuidance(request)
        }
    }
    
    private String determineTestingType(AgentRequest request) {
        String context = request.context?.toLowerCase() ?: ""
        String capability = request.capability?.toLowerCase() ?: ""
        
        if (context.contains("entity") || capability.contains("entity")) return "entity"
        if (context.contains("service") || capability.contains("service")) return "service"
        if (context.contains("screen") || context.contains("vue") || capability.contains("screen")) return "screen"
        if (context.contains("workflow") || context.contains("cross-domain")) return "workflow"
        if (context.contains("integration") || context.contains("external")) return "integration"
        
        return "general"
    }
    
    private String determineTestFramework(AgentRequest request) {
        String context = request.context?.toLowerCase() ?: ""
        
        if (context.contains("vue") || context.contains("screen") || context.contains("component")) return "jest"
        if (context.contains("entity") || context.contains("service") || context.contains("groovy")) return "spock"
        
        return "spock" // Default for Moqui
    }
    
    private String determineCoverageTarget(AgentRequest request) {
        String testingType = determineTestingType(request)
        
        switch (testingType) {
            case "entity": return "95%"
            case "service": return "98%"
            case "screen": return "90%"
            case "workflow": return "92%"
            case "integration": return "100%"
            default: return "90%"
        }
    }
    
    // AC1: Entity testing guidance (3 seconds, 95% coverage)
    private String generateEntityTestingGuidance(AgentRequest request) {
        return """
# Entity Testing Guidance - Spock Framework

## Entity Test Structure
```groovy
package moqui.entity

import org.moqui.Moqui
import org.moqui.context.ExecutionContext
import org.moqui.entity.EntityFacade
import org.moqui.entity.EntityValue
import spock.lang.Specification
import spock.lang.Shared

class ${extractEntityName(request)}Spec extends Specification {
    
    @Shared ExecutionContext ec
    @Shared EntityFacade ef
    
    def setupSpec() {
        ec = Moqui.getExecutionContext()
        ef = ec.entity
    }
    
    def cleanupSpec() {
        ec?.destroy()
    }
    
    def setup() {
        ec.transaction.begin(null)
    }
    
    def cleanup() {
        ec.transaction.rollback()
    }
}
```

## Entity CRUD Testing Patterns
```groovy
def "should create entity with valid data"() {
    given: "valid entity data"
    Map entityData = [
        ${generateEntityFields(request)}
    ]
    
    when: "creating entity"
    EntityValue entity = ef.makeValue("${extractEntityName(request)}")
    entity.setAll(entityData)
    entity.create()
    
    then: "entity is created successfully"
    entity.${extractPrimaryKey(request)} != null
    
    and: "entity can be retrieved"
    EntityValue retrieved = ef.find("${extractEntityName(request)}")
        .condition(entity.getPrimaryKeys())
        .one()
    retrieved != null
    retrieved.${extractPrimaryKey(request)} == entity.${extractPrimaryKey(request)}
}

def "should validate required fields"() {
    given: "entity with missing required field"
    EntityValue entity = ef.makeValue("${extractEntityName(request)}")
    
    when: "attempting to create without required fields"
    entity.create()
    
    then: "validation error is thrown"
    thrown(org.moqui.entity.EntityException)
}

def "should update entity fields"() {
    given: "existing entity"
    EntityValue entity = createTestEntity()
    String originalValue = entity.${extractUpdateableField(request)}
    String newValue = "Updated Value"
    
    when: "updating entity"
    entity.${extractUpdateableField(request)} = newValue
    entity.update()
    
    then: "entity is updated"
    entity.${extractUpdateableField(request)} == newValue
    
    and: "change is persisted"
    EntityValue retrieved = ef.find("${extractEntityName(request)}")
        .condition(entity.getPrimaryKeys())
        .one()
    retrieved.${extractUpdateableField(request)} == newValue
}

def "should delete entity"() {
    given: "existing entity"
    EntityValue entity = createTestEntity()
    Map primaryKeys = entity.getPrimaryKeys()
    
    when: "deleting entity"
    entity.delete()
    
    then: "entity is removed"
    EntityValue retrieved = ef.find("${extractEntityName(request)}")
        .condition(primaryKeys)
        .one()
    retrieved == null
}
```

## Entity Relationship Testing
```groovy
def "should maintain referential integrity"() {
    given: "parent and child entities"
    EntityValue parent = createTestParent()
    EntityValue child = ef.makeValue("${extractChildEntity(request)}")
    child.${extractForeignKey(request)} = parent.${extractPrimaryKey(request)}
    
    when: "creating child with valid parent reference"
    child.create()
    
    then: "child is created successfully"
    child.${extractPrimaryKey(request)} != null
    
    when: "attempting to delete parent with existing child"
    parent.delete()
    
    then: "referential integrity constraint prevents deletion"
    thrown(org.moqui.entity.EntityException)
}
```

## Entity Validation Testing
```groovy
def "should validate field constraints"() {
    given: "entity with invalid field values"
    EntityValue entity = ef.makeValue("${extractEntityName(request)}")
    
    when: "setting field with invalid length"
    entity.${extractStringField(request)} = "x" * 300 // Exceeds max length
    entity.create()
    
    then: "validation error is thrown"
    thrown(org.moqui.entity.EntityException)
}

def "should validate date ranges"() {
    given: "entity with date fields"
    EntityValue entity = ef.makeValue("${extractEntityName(request)}")
    entity.fromDate = new Date() + 1
    entity.thruDate = new Date()
    
    when: "creating with invalid date range"
    entity.create()
    
    then: "validation error is thrown"
    thrown(org.moqui.entity.EntityException)
}
```

## Test Data Helpers
```groovy
private EntityValue createTestEntity() {
    EntityValue entity = ef.makeValue("${extractEntityName(request)}")
    entity.setAll([
        ${generateTestEntityData(request)}
    ])
    entity.create()
    return entity
}

private void cleanupTestData() {
    ef.find("${extractEntityName(request)}")
        .condition("${extractTestDataCondition(request)}")
        .deleteAll()
}
```

## Coverage Requirements
- **Target Coverage**: 95%
- **Required Tests**: CRUD operations, validation, relationships, constraints
- **Performance**: Complete test suite in < 3 seconds
- **Data Validation**: All field types, constraints, and business rules

## Best Practices
1. Use transactions for test isolation
2. Clean up test data in cleanup methods
3. Test both positive and negative scenarios
4. Validate all entity constraints
5. Test relationship integrity
6. Use meaningful test data
7. Group related tests logically
8. Test edge cases and boundary conditions
"""
    }
    
    // AC2: Service testing guidance (2 seconds, 98% coverage)
    private String generateServiceTestingGuidance(AgentRequest request) {
        return """
# Service Testing Guidance - Spock Framework

## Service Test Structure
```groovy
package moqui.service

import org.moqui.Moqui
import org.moqui.context.ExecutionContext
import org.moqui.service.ServiceFacade
import spock.lang.Specification
import spock.lang.Shared

class ${extractServiceName(request)}Spec extends Specification {
    
    @Shared ExecutionContext ec
    @Shared ServiceFacade sf
    
    def setupSpec() {
        ec = Moqui.getExecutionContext()
        sf = ec.service
    }
    
    def cleanupSpec() {
        ec?.destroy()
    }
    
    def setup() {
        ec.transaction.begin(null)
    }
    
    def cleanup() {
        ec.transaction.rollback()
    }
}
```

## Service Call Testing Patterns
```groovy
def "should execute service with valid parameters"() {
    given: "valid service parameters"
    Map serviceParams = [
        ${generateServiceParams(request)}
    ]
    
    when: "calling service"
    Map result = sf.sync().name("${extractServiceName(request)}")
        .parameters(serviceParams)
        .call()
    
    then: "service executes successfully"
    result.success == true
    result.${extractExpectedResult(request)} != null
    
    and: "expected side effects occur"
    verifyExpectedSideEffects(result)
}

def "should validate required parameters"() {
    given: "service parameters missing required field"
    Map serviceParams = [
        // Missing required parameter
        ${generateIncompleteParams(request)}
    ]
    
    when: "calling service with missing parameters"
    sf.sync().name("${extractServiceName(request)}")
        .parameters(serviceParams)
        .call()
    
    then: "parameter validation error is thrown"
    thrown(org.moqui.service.ServiceException)
}

def "should handle invalid parameter types"() {
    given: "service parameters with wrong types"
    Map serviceParams = [
        ${generateInvalidTypeParams(request)}
    ]
    
    when: "calling service with invalid types"
    sf.sync().name("${extractServiceName(request)}")
        .parameters(serviceParams)
        .call()
    
    then: "type validation error is thrown"
    thrown(org.moqui.service.ServiceException)
}
```

## Transaction Testing
```groovy
def "should handle transaction rollback on error"() {
    given: "service parameters that will cause error"
    Map serviceParams = [
        ${generateErrorCausingParams(request)}
    ]
    
    and: "initial database state"
    int initialCount = getEntityCount("${extractRelatedEntity(request)}")
    
    when: "calling service that fails"
    sf.sync().name("${extractServiceName(request)}")
        .parameters(serviceParams)
        .call()
    
    then: "service exception is thrown"
    thrown(org.moqui.service.ServiceException)
    
    and: "database state is unchanged"
    getEntityCount("${extractRelatedEntity(request)}") == initialCount
}

def "should commit transaction on success"() {
    given: "valid service parameters"
    Map serviceParams = [
        ${generateValidParams(request)}
    ]
    
    when: "calling service successfully"
    Map result = sf.sync().name("${extractServiceName(request)}")
        .parameters(serviceParams)
        .call()
    
    then: "service completes successfully"
    result.success == true
    
    and: "changes are committed"
    verifyDataPersisted(result)
}
```

## Mock Data and Dependency Testing
```groovy
def "should handle external service dependencies"() {
    given: "mocked external service"
    mockExternalService("${extractExternalDependency(request)}")
    
    and: "service parameters"
    Map serviceParams = [
        ${generateServiceParams(request)}
    ]
    
    when: "calling service with mocked dependency"
    Map result = sf.sync().name("${extractServiceName(request)}")
        .parameters(serviceParams)
        .call()
    
    then: "service uses mocked data"
    result.success == true
    verifyMockInteraction("${extractExternalDependency(request)}")
}

def "should handle database unavailability"() {
    given: "database connection issues simulated"
    simulateDatabaseError()
    
    and: "service parameters"
    Map serviceParams = [
        ${generateServiceParams(request)}
    ]
    
    when: "calling service with database issues"
    sf.sync().name("${extractServiceName(request)}")
        .parameters(serviceParams)
        .call()
    
    then: "appropriate error handling occurs"
    thrown(org.moqui.entity.EntityException)
    
    cleanup:
    restoreDatabaseConnection()
}
```

## Business Logic Testing
```groovy
def "should apply business rules correctly"() {
    given: "business rule test scenario"
    Map testScenario = setupBusinessRuleScenario()
    
    when: "executing service with business logic"
    Map result = sf.sync().name("${extractServiceName(request)}")
        .parameters(testScenario.params)
        .call()
    
    then: "business rules are applied"
    result.${extractBusinessRuleResult(request)} == testScenario.expectedResult
    
    and: "side effects match business requirements"
    verifyBusinessRuleSideEffects(result, testScenario)
}

def "should handle edge cases in business logic"() {
    given: "edge case parameters"
    Map edgeCaseParams = [
        ${generateEdgeCaseParams(request)}
    ]
    
    when: "executing service with edge case"
    Map result = sf.sync().name("${extractServiceName(request)}")
        .parameters(edgeCaseParams)
        .call()
    
    then: "edge case is handled gracefully"
    result.success == true
    result.${extractEdgeCaseResult(request)} != null
}
```

## Performance Testing
```groovy
def "should complete within performance requirements"() {
    given: "service parameters"
    Map serviceParams = [
        ${generateServiceParams(request)}
    ]
    
    when: "measuring service execution time"
    long startTime = System.currentTimeMillis()
    Map result = sf.sync().name("${extractServiceName(request)}")
        .parameters(serviceParams)
        .call()
    long executionTime = System.currentTimeMillis() - startTime
    
    then: "service completes within time limit"
    executionTime < 2000 // 2 seconds max
    result.success == true
}
```

## Test Helpers and Utilities
```groovy
private void mockExternalService(String serviceName) {
    // Mock external service calls
    ec.service.registerMock(serviceName) { Map params ->
        return [success: true, mockData: "test"]
    }
}

private void verifyExpectedSideEffects(Map result) {
    // Verify database changes, notifications, etc.
    assert getEntityCount("${extractRelatedEntity(request)}") > 0
}

private int getEntityCount(String entityName) {
    return ec.entity.find(entityName).count()
}

private Map setupBusinessRuleScenario() {
    return [
        params: [${generateBusinessRuleParams(request)}],
        expectedResult: "${extractExpectedBusinessResult(request)}"
    ]
}
```

## Coverage Requirements
- **Target Coverage**: 98%
- **Required Tests**: Parameter validation, business logic, transactions, error handling
- **Performance**: Complete test suite in < 2 seconds
- **Mock Testing**: External dependencies, database failures, service interactions

## Best Practices
1. Test all service parameters and return values
2. Use transactions for test isolation
3. Mock external dependencies
4. Test error conditions and edge cases
5. Validate business rule implementation
6. Test performance requirements
7. Use meaningful test data
8. Test concurrent access scenarios
9. Validate security constraints
10. Test service composition patterns
"""
    }
    
    // AC3: Screen testing guidance (4 seconds, 90% coverage)
    private String generateScreenTestingGuidance(AgentRequest request) {
        return """
# Screen Testing Guidance - Jest + Vue Test Utils

## Vue Component Test Structure
```typescript
import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount, VueWrapper } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import { Quasar } from 'quasar'
import ${extractComponentName(request)} from '@/components/${extractComponentName(request)}.vue'

describe('${extractComponentName(request)}', () => {
  let wrapper: VueWrapper<any>
  
  beforeEach(() => {
    setActivePinia(createPinia())
    
    wrapper = mount(${extractComponentName(request)}, {
      global: {
        plugins: [Quasar],
        stubs: ['router-link', 'router-view']
      },
      props: {
        ${generateComponentProps(request)}
      }
    })
  })
  
  afterEach(() => {
    wrapper.unmount()
  })
})
```

## Component Rendering Tests
```typescript
it('should render component with correct structure', () => {
  // Test component mounting
  expect(wrapper.exists()).toBe(true)
  
  // Test key elements are present
  expect(wrapper.find('[data-testid="${extractTestId(request)}"]').exists()).toBe(true)
  expect(wrapper.find('.${extractCssClass(request)}').exists()).toBe(true)
  
  // Test initial state
  expect(wrapper.vm.${extractDataProperty(request)}).toBe(${extractInitialValue(request)})
})

it('should render with props correctly', () => {
  const testProps = {
    ${generateTestProps(request)}
  }
  
  wrapper = mount(${extractComponentName(request)}, {
    global: { plugins: [Quasar] },
    props: testProps
  })
  
  // Verify props are used correctly
  expect(wrapper.text()).toContain(testProps.${extractPropName(request)})
  expect(wrapper.vm.${extractPropName(request)}).toBe(testProps.${extractPropName(request)})
})

it('should handle missing optional props gracefully', () => {
  wrapper = mount(${extractComponentName(request)}, {
    global: { plugins: [Quasar] }
    // No props provided
  })
  
  expect(wrapper.exists()).toBe(true)
  expect(wrapper.vm.${extractOptionalProp(request)}).toBe(${extractDefaultValue(request)})
})
```

## User Interaction Testing
```typescript
it('should handle button clicks correctly', async () => {
  const button = wrapper.find('[data-testid="${extractButtonTestId(request)}"]')
  
  await button.trigger('click')
  
  // Verify click handler was called
  expect(wrapper.emitted('${extractEmittedEvent(request)}')).toBeTruthy()
  expect(wrapper.vm.${extractClickHandler(request)}).toHaveBeenCalled()
})

it('should handle form input changes', async () => {
  const input = wrapper.find('input[data-testid="${extractInputTestId(request)}"]')
  const testValue = '${extractTestInputValue(request)}'
  
  await input.setValue(testValue)
  
  // Verify v-model binding
  expect(wrapper.vm.${extractModelProperty(request)}).toBe(testValue)
  
  // Verify input validation
  expect(wrapper.find('.error-message').exists()).toBe(false)
})

it('should validate form inputs', async () => {
  const form = wrapper.find('form')
  const input = wrapper.find('input[required]')
  
  // Submit form with invalid data
  await input.setValue('')
  await form.trigger('submit')
  
  // Verify validation errors
  expect(wrapper.find('.error-message').exists()).toBe(true)
  expect(wrapper.emitted('submit')).toBeFalsy()
})
```

## Quasar Component Integration Testing
```typescript
it('should integrate with Quasar components correctly', () => {
  // Test QBtn integration
  const qBtn = wrapper.findComponent({ name: 'QBtn' })
  expect(qBtn.exists()).toBe(true)
  expect(qBtn.props('label')).toBe('${extractButtonLabel(request)}')
  
  // Test QTable integration
  const qTable = wrapper.findComponent({ name: 'QTable' })
  if (qTable.exists()) {
    expect(qTable.props('rows')).toEqual(wrapper.vm.${extractTableData(request)})
    expect(qTable.props('columns')).toEqual(wrapper.vm.${extractTableColumns(request)})
  }
})

it('should handle Quasar dialog interactions', async () => {
  const dialogTrigger = wrapper.find('[data-testid="open-dialog"]')
  
  await dialogTrigger.trigger('click')
  
  // Verify dialog is opened
  expect(wrapper.vm.${extractDialogProperty(request)}).toBe(true)
  
  // Test dialog content
  const dialog = wrapper.findComponent({ name: 'QDialog' })
  expect(dialog.exists()).toBe(true)
})
```

## State Management Testing (Pinia)
```typescript
import { useTestStore } from '@/stores/testStore'

it('should interact with Pinia store correctly', () => {
  const store = useTestStore()
  
  // Test store integration
  expect(wrapper.vm.${extractStoreProperty(request)}).toBe(store.${extractStoreProperty(request)})
  
  // Test store actions
  wrapper.vm.${extractStoreAction(request)}()
  expect(store.${extractStoreState(request)}).toBe(${extractExpectedStoreValue(request)})
})

it('should react to store state changes', async () => {
  const store = useTestStore()
  
  // Change store state
  store.${extractStoreProperty(request)} = '${extractNewStoreValue(request)}'
  
  await wrapper.vm.\$nextTick()
  
  // Verify component reacts to store changes
  expect(wrapper.text()).toContain('${extractNewStoreValue(request)}')
})
```

## API Integration Testing
```typescript
it('should handle API calls correctly', async () => {
  // Mock API call
  const mockApiResponse = {
    ${generateMockApiResponse(request)}
  }
  
  vi.spyOn(wrapper.vm, '${extractApiMethod(request)}')
    .mockResolvedValue(mockApiResponse)
  
  // Trigger API call
  await wrapper.vm.${extractApiTrigger(request)}()
  
  // Verify API integration
  expect(wrapper.vm.${extractApiMethod(request)}).toHaveBeenCalled()
  expect(wrapper.vm.${extractApiData(request)}).toEqual(mockApiResponse)
})

it('should handle API errors gracefully', async () => {
  // Mock API error
  vi.spyOn(wrapper.vm, '${extractApiMethod(request)}')
    .mockRejectedValue(new Error('API Error'))
  
  // Trigger API call
  await wrapper.vm.${extractApiTrigger(request)}()
  
  // Verify error handling
  expect(wrapper.find('.error-message').exists()).toBe(true)
  expect(wrapper.vm.${extractErrorState(request)}).toBe(true)
})
```

## Accessibility Testing
```typescript
it('should meet accessibility requirements', () => {
  // Test ARIA attributes
  const button = wrapper.find('button')
  expect(button.attributes('aria-label')).toBeDefined()
  
  // Test keyboard navigation
  const focusableElements = wrapper.findAll('[tabindex], button, input, select, textarea')
  expect(focusableElements.length).toBeGreaterThan(0)
  
  // Test semantic HTML
  expect(wrapper.find('main, section, article, nav').exists()).toBe(true)
})

it('should support keyboard navigation', async () => {
  const firstInput = wrapper.find('input')
  
  await firstInput.trigger('keydown', { key: 'Tab' })
  
  // Verify focus management
  expect(document.activeElement).toBe(firstInput.element)
})
```

## Performance Testing
```typescript
it('should render within performance requirements', () => {
  const startTime = performance.now()
  
  wrapper = mount(${extractComponentName(request)}, {
    global: { plugins: [Quasar] },
    props: { ${generateLargeDataProps(request)} }
  })
  
  const renderTime = performance.now() - startTime
  
  // Should render large datasets quickly
  expect(renderTime).toBeLessThan(100) // 100ms max
})

it('should handle large data sets efficiently', async () => {
  const largeDataSet = Array.from({ length: 1000 }, (_, i) => ({
    id: i,
    name: \`Item \${i}\`
  }))
  
  await wrapper.setProps({ ${extractDataProp(request)}: largeDataSet })
  
  // Component should handle large data without performance issues
  expect(wrapper.exists()).toBe(true)
  expect(wrapper.findAll('.data-item').length).toBeLessThanOrEqual(50) // Virtual scrolling
})
```

## Test Utilities and Helpers
```typescript
// Test helper functions
function createMockRouter() {
  return {
    push: vi.fn(),
    replace: vi.fn(),
    go: vi.fn(),
    back: vi.fn(),
    forward: vi.fn()
  }
}

function createMockStore() {
  return {
    ${generateMockStoreState(request)}
  }
}

function waitForAsyncOperation() {
  return new Promise(resolve => setTimeout(resolve, 0))
}
```

## Coverage Requirements
- **Target Coverage**: 90%
- **Required Tests**: Rendering, interactions, state management, API integration
- **Performance**: Complete test suite in < 4 seconds
- **Accessibility**: WCAG 2.1 AA compliance testing

## Best Practices
1. Use data-testid attributes for reliable element selection
2. Test user interactions, not implementation details
3. Mock external dependencies (APIs, stores)
4. Test accessibility and keyboard navigation
5. Use async/await for asynchronous operations
6. Test error states and edge cases
7. Verify Quasar component integration
8. Test responsive behavior
9. Use meaningful test descriptions
10. Group related tests logically
"""
    }
    
    // AC4: Workflow testing guidance (4 seconds, 92% coverage)
    private String generateWorkflowTestingGuidance(AgentRequest request) {
        return """
# Workflow Testing Guidance - Cross-Domain Integration

## Workflow Test Structure
```groovy
package moqui.workflow

import org.moqui.Moqui
import org.moqui.context.ExecutionContext
import spock.lang.Specification
import spock.lang.Shared

class ${extractWorkflowName(request)}WorkflowSpec extends Specification {
    
    @Shared ExecutionContext ec
    
    def setupSpec() {
        ec = Moqui.getExecutionContext()
    }
    
    def cleanupSpec() {
        ec?.destroy()
    }
    
    def setup() {
        ec.transaction.begin(null)
        setupWorkflowTestData()
    }
    
    def cleanup() {
        cleanupWorkflowTestData()
        ec.transaction.rollback()
    }
}
```

## End-to-End Workflow Testing
```groovy
def "should complete full ${extractWorkflowName(request)} workflow"() {
    given: "initial workflow state"
    Map workflowContext = initializeWorkflowContext()
    
    when: "executing workflow step 1 - ${extractStep1Name(request)}"
    Map step1Result = ec.service.sync()
        .name("${extractStep1Service(request)}")
        .parameters(workflowContext)
        .call()
    
    then: "step 1 completes successfully"
    step1Result.success == true
    step1Result.${extractStep1Output(request)} != null
    
    when: "executing workflow step 2 - ${extractStep2Name(request)}"
    Map step2Context = step1Result + [additionalParam: "value"]
    Map step2Result = ec.service.sync()
        .name("${extractStep2Service(request)}")
        .parameters(step2Context)
        .call()
    
    then: "step 2 completes successfully"
    step2Result.success == true
    step2Result.${extractStep2Output(request)} != null
    
    when: "executing final workflow step - ${extractFinalStepName(request)}"
    Map finalContext = step2Result + workflowContext
    Map finalResult = ec.service.sync()
        .name("${extractFinalStepService(request)}")
        .parameters(finalContext)
        .call()
    
    then: "workflow completes successfully"
    finalResult.success == true
    finalResult.${extractFinalOutput(request)} != null
    
    and: "workflow state is properly updated"
    verifyWorkflowCompletion(finalResult)
}
```

## Cross-Domain Integration Testing
```groovy
def "should integrate across ${extractDomain1(request)} and ${extractDomain2(request)} domains"() {
    given: "data in ${extractDomain1(request)} domain"
    Map domain1Data = createDomain1TestData()
    
    when: "triggering cross-domain operation"
    Map result = ec.service.sync()
        .name("${extractCrossDomainService(request)}")
        .parameters([
            domain1Id: domain1Data.${extractDomain1Id(request)},
            operation: "${extractCrossDomainOperation(request)}"
        ])
        .call()
    
    then: "operation succeeds"
    result.success == true
    
    and: "${extractDomain2(request)} domain is updated"
    verifyDomain2Update(result.${extractDomain2Id(request)})
    
    and: "cross-domain consistency is maintained"
    verifyCrossDomainConsistency(domain1Data, result)
}
```

## Workflow State Management Testing
```groovy
def "should handle workflow state transitions correctly"() {
    given: "workflow in initial state"
    String workflowId = createWorkflowInstance()
    
    expect: "initial state is correct"
    getWorkflowState(workflowId) == "${extractInitialState(request)}"
    
    when: "transitioning to next state"
    Map transitionResult = ec.service.sync()
        .name("${extractTransitionService(request)}")
        .parameters([
            workflowId: workflowId,
            action: "${extractTransitionAction(request)}"
        ])
        .call()
    
    then: "state transition succeeds"
    transitionResult.success == true
    getWorkflowState(workflowId) == "${extractNextState(request)}"
    
    and: "state change is audited"
    verifyStateChangeAudit(workflowId, "${extractInitialState(request)}", "${extractNextState(request)}")
}

def "should prevent invalid state transitions"() {
    given: "workflow in specific state"
    String workflowId = createWorkflowInstance()
    setWorkflowState(workflowId, "${extractCurrentState(request)}")
    
    when: "attempting invalid transition"
    ec.service.sync()
        .name("${extractTransitionService(request)}")
        .parameters([
            workflowId: workflowId,
            action: "${extractInvalidAction(request)}"
        ])
        .call()
    
    then: "transition is rejected"
    thrown(org.moqui.service.ServiceException)
    
    and: "state remains unchanged"
    getWorkflowState(workflowId) == "${extractCurrentState(request)}"
}
```

## Workflow Error Handling Testing
```groovy
def "should handle workflow step failures gracefully"() {
    given: "workflow context that will cause step failure"
    Map workflowContext = createFailureScenarioContext()
    
    when: "executing workflow with failure scenario"
    Map result = ec.service.sync()
        .name("${extractWorkflowService(request)}")
        .parameters(workflowContext)
        .call()
    
    then: "workflow handles failure appropriately"
    result.success == false
    result.errorMessage != null
    
    and: "workflow state reflects failure"
    getWorkflowState(workflowContext.workflowId) == "${extractFailureState(request)}"
    
    and: "compensating actions are triggered"
    verifyCompensatingActions(workflowContext.workflowId)
}
```

## Performance and Scalability Testing
```groovy
def "should handle concurrent workflow executions"() {
    given: "multiple workflow instances"
    List<String> workflowIds = (1..10).collect { createWorkflowInstance() }
    
    when: "executing workflows concurrently"
    List<Future> futures = workflowIds.collect { workflowId ->
        ec.service.async()
            .name("${extractWorkflowService(request)}")
            .parameters([workflowId: workflowId])
            .call()
    }
    
    then: "all workflows complete successfully"
    futures.each { future ->
        Map result = future.get()
        assert result.success == true
    }
    
    and: "no data corruption occurs"
    workflowIds.each { workflowId ->
        verifyWorkflowDataIntegrity(workflowId)
    }
}
```

## Coverage Requirements
- **Target Coverage**: 92%
- **Required Tests**: End-to-end workflows, cross-domain integration, state management
- **Performance**: Complete test suite in < 4 seconds
- **Concurrency**: Multi-user workflow testing

## Best Practices
1. Test complete end-to-end workflows
2. Verify cross-domain data consistency
3. Test state transition logic
4. Handle workflow failures gracefully
5. Test concurrent workflow execution
6. Validate business rule enforcement
7. Test workflow performance under load
8. Verify audit trail completeness
"""
    }
    
    // AC5: External integration testing (3 seconds, 100% coverage)
    private String generateIntegrationTestingGuidance(AgentRequest request) {
        return """
# Integration Testing Guidance - External Systems

## Integration Test Structure
```groovy
package moqui.integration

import org.moqui.Moqui
import org.moqui.context.ExecutionContext
import spock.lang.Specification
import spock.lang.Shared

class ${extractIntegrationName(request)}IntegrationSpec extends Specification {
    
    @Shared ExecutionContext ec
    
    def setupSpec() {
        ec = Moqui.getExecutionContext()
        setupIntegrationMocks()
    }
    
    def cleanupSpec() {
        cleanupIntegrationMocks()
        ec?.destroy()
    }
    
    def setup() {
        ec.transaction.begin(null)
        resetIntegrationState()
    }
    
    def cleanup() {
        ec.transaction.rollback()
    }
}
```

## durion-positivity Integration Testing
```groovy
def "should integrate with durion-positivity backend successfully"() {
    given: "integration request data"
    Map integrationData = [
        ${generatePositivityIntegrationData(request)}
    ]
    
    when: "calling durion-positivity service"
    Map result = ec.service.sync()
        .name("durion.positivity.${extractPositivityService(request)}")
        .parameters(integrationData)
        .call()
    
    then: "integration succeeds"
    result.success == true
    result.${extractPositivityResponse(request)} != null
    
    and: "response data is valid"
    validatePositivityResponse(result)
    
    and: "local cache is updated"
    verifyLocalCacheUpdate(result.${extractPositivityResponse(request)})
}

def "should handle durion-positivity service unavailability"() {
    given: "durion-positivity service is unavailable"
    simulatePositivityServiceDown()
    
    and: "integration request"
    Map integrationData = [
        ${generatePositivityIntegrationData(request)}
    ]
    
    when: "attempting integration call"
    Map result = ec.service.sync()
        .name("durion.positivity.${extractPositivityService(request)}")
        .parameters(integrationData)
        .call()
    
    then: "circuit breaker activates"
    result.success == false
    result.errorType == "CIRCUIT_BREAKER_OPEN"
    
    and: "fallback mechanism is used"
    verifyFallbackMechanism(integrationData)
    
    cleanup:
    restorePositivityService()
}
```

## Circuit Breaker Testing
```groovy
def "should implement circuit breaker pattern correctly"() {
    given: "circuit breaker configuration"
    Map circuitConfig = [
        failureThreshold: 3,
        timeout: 5000,
        resetTimeout: 30000
    ]
    
    when: "multiple failures occur"
    (1..3).each {
        simulateServiceFailure()
        callExternalService()
    }
    
    then: "circuit breaker opens"
    getCircuitBreakerState() == "OPEN"
    
    when: "attempting call with open circuit"
    Map result = callExternalService()
    
    then: "call is rejected immediately"
    result.success == false
    result.errorType == "CIRCUIT_BREAKER_OPEN"
    
    when: "waiting for reset timeout"
    Thread.sleep(circuitConfig.resetTimeout + 1000)
    
    and: "service is restored"
    restoreExternalService()
    
    and: "making successful call"
    result = callExternalService()
    
    then: "circuit breaker closes"
    result.success == true
    getCircuitBreakerState() == "CLOSED"
}
```

## API Contract Testing
```groovy
def "should validate API contract compliance"() {
    given: "API contract specification"
    Map contractSpec = loadApiContract("${extractApiContract(request)}")
    
    when: "making API call"
    Map result = ec.service.sync()
        .name("${extractApiService(request)}")
        .parameters([${generateApiParams(request)}])
        .call()
    
    then: "response matches contract"
    validateResponseContract(result, contractSpec)
    
    and: "required fields are present"
    contractSpec.requiredFields.each { field ->
        assert result[field] != null
    }
    
    and: "data types are correct"
    validateResponseDataTypes(result, contractSpec)
}

def "should handle API version compatibility"() {
    given: "different API versions"
    List<String> apiVersions = ["v1", "v2", "v3"]
    
    expect: "all versions are supported"
    apiVersions.each { version ->
        Map result = callApiWithVersion(version)
        assert result.success == true
        assert result.apiVersion == version
    }
}
```

## Data Synchronization Testing
```groovy
def "should synchronize data with external systems correctly"() {
    given: "local data changes"
    Map localChanges = createLocalDataChanges()
    
    when: "triggering synchronization"
    Map syncResult = ec.service.sync()
        .name("${extractSyncService(request)}")
        .parameters([changes: localChanges])
        .call()
    
    then: "synchronization succeeds"
    syncResult.success == true
    syncResult.syncedRecords == localChanges.size()
    
    and: "external system is updated"
    verifyExternalSystemUpdate(localChanges)
    
    and: "sync status is tracked"
    verifySyncStatusTracking(syncResult.syncId)
}

def "should handle data conflicts during synchronization"() {
    given: "conflicting data changes"
    Map localChanges = createConflictingChanges()
    Map externalChanges = createExternalConflictingChanges()
    
    when: "attempting synchronization"
    Map syncResult = ec.service.sync()
        .name("${extractSyncService(request)}")
        .parameters([changes: localChanges])
        .call()
    
    then: "conflicts are detected"
    syncResult.success == false
    syncResult.conflicts != null
    syncResult.conflicts.size() > 0
    
    and: "conflict resolution is triggered"
    verifyConflictResolutionProcess(syncResult.conflicts)
}
```

## Security Integration Testing
```groovy
def "should handle authentication with external systems"() {
    given: "authentication credentials"
    Map credentials = [
        ${generateAuthCredentials(request)}
    ]
    
    when: "authenticating with external system"
    Map authResult = ec.service.sync()
        .name("${extractAuthService(request)}")
        .parameters(credentials)
        .call()
    
    then: "authentication succeeds"
    authResult.success == true
    authResult.accessToken != null
    authResult.tokenExpiry != null
    
    and: "token is stored securely"
    verifySecureTokenStorage(authResult.accessToken)
}

def "should handle token refresh automatically"() {
    given: "expired authentication token"
    setExpiredToken()
    
    when: "making API call with expired token"
    Map result = callProtectedApi()
    
    then: "token is refreshed automatically"
    result.success == true
    verifyTokenRefresh()
    
    and: "API call succeeds with new token"
    verifyApiCallSuccess(result)
}
```

## Performance Integration Testing
```groovy
def "should meet integration performance requirements"() {
    given: "performance test data"
    Map testData = generatePerformanceTestData()
    
    when: "measuring integration performance"
    long startTime = System.currentTimeMillis()
    Map result = ec.service.sync()
        .name("${extractIntegrationService(request)}")
        .parameters(testData)
        .call()
    long executionTime = System.currentTimeMillis() - startTime
    
    then: "integration completes within time limit"
    executionTime < 3000 // 3 seconds max
    result.success == true
    
    and: "throughput meets requirements"
    verifyThroughputRequirements(result)
}
```

## Test Utilities
```groovy
private void simulatePositivityServiceDown() {
    // Mock service unavailability
    ec.service.registerMock("durion.positivity.*") { Map params ->
        throw new RuntimeException("Service unavailable")
    }
}

private void validatePositivityResponse(Map response) {
    assert response.timestamp != null
    assert response.data != null
    assert response.status == "SUCCESS"
}

private Map callExternalService() {
    return ec.service.sync()
        .name("${extractExternalService(request)}")
        .call()
}
```

## Coverage Requirements
- **Target Coverage**: 100%
- **Required Tests**: API integration, circuit breakers, data sync, security
- **Performance**: Complete test suite in < 3 seconds
- **Reliability**: All integration failure scenarios covered

## Best Practices
1. Test all external service integrations
2. Implement circuit breaker patterns
3. Validate API contracts and versions
4. Test authentication and authorization
5. Handle network failures gracefully
6. Test data synchronization scenarios
7. Validate security token management
8. Test performance under load
9. Mock external dependencies appropriately
10. Test error recovery mechanisms
"""
    }
    
    // General testing guidance
    private String generateGeneralTestingGuidance(AgentRequest request) {
        return """
# General Testing Guidance - Moqui Framework

## Testing Strategy Overview
This guidance provides comprehensive testing patterns for Moqui Framework applications, covering:
- Entity testing with Spock Framework (95% coverage target)
- Service testing with mock data and transactions (98% coverage target)
- Screen testing with Jest and Vue Test Utils (90% coverage target)
- Workflow testing for cross-domain integration (92% coverage target)
- External integration testing with circuit breakers (100% coverage target)

## Framework Selection
- **Spock Framework**: For Groovy-based entity and service testing
- **Jest + Vue Test Utils**: For Vue.js component and screen testing
- **jqwik**: For property-based testing and edge case discovery
- **Testcontainers**: For integration testing with real databases

## Test Organization
```
src/test/groovy/
├── entity/          # Entity CRUD and validation tests
├── service/         # Service logic and transaction tests
├── integration/     # Cross-domain and external integration tests
└── performance/     # Load and performance tests

src/test/javascript/
├── components/      # Vue.js component tests
├── screens/         # Screen interaction tests
└── e2e/            # End-to-end user journey tests
```

## Performance Targets
- Entity tests: Complete in < 3 seconds with 95% coverage
- Service tests: Complete in < 2 seconds with 98% coverage
- Screen tests: Complete in < 4 seconds with 90% coverage
- Workflow tests: Complete in < 4 seconds with 92% coverage
- Integration tests: Complete in < 3 seconds with 100% coverage

## Best Practices Summary
1. Use transactions for test isolation
2. Mock external dependencies appropriately
3. Test both positive and negative scenarios
4. Validate business rules and constraints
5. Test error handling and recovery
6. Use meaningful test data and descriptions
7. Group related tests logically
8. Test performance requirements
9. Validate security constraints
10. Maintain high test coverage standards

For specific testing guidance, request help with:
- "entity-testing-guidance" for entity CRUD and validation testing
- "service-testing-guidance" for service logic and transaction testing
- "screen-testing-guidance" for Vue.js component and UI testing
- "workflow-testing-guidance" for cross-domain integration testing
- "integration-testing-guidance" for external system integration testing
"""
    }
    
    // Helper methods for extracting context-specific information
    private String extractEntityName(AgentRequest request) {
        // Extract entity name from context or use default
        String context = request.context ?: ""
        if (context.contains("Entity")) {
            return context.replaceAll(".*?([A-Z][a-zA-Z]+)Entity.*", "\$1")
        }
        return "TestEntity"
    }
    
    private String extractServiceName(AgentRequest request) {
        String context = request.context ?: ""
        if (context.contains("Service")) {
            return context.replaceAll(".*?([A-Z][a-zA-Z]+)Service.*", "\$1")
        }
        return "TestService"
    }
    
    private String extractComponentName(AgentRequest request) {
        String context = request.context ?: ""
        if (context.contains("Component")) {
            return context.replaceAll(".*?([A-Z][a-zA-Z]+)Component.*", "\$1")
        }
        return "TestComponent"
    }
    
    private String extractWorkflowName(AgentRequest request) {
        String context = request.context ?: ""
        if (context.contains("Workflow")) {
            return context.replaceAll(".*?([A-Z][a-zA-Z]+)Workflow.*", "\$1")
        }
        return "TestWorkflow"
    }
    
    private String extractIntegrationName(AgentRequest request) {
        String context = request.context ?: ""
        if (context.contains("Integration")) {
            return context.replaceAll(".*?([A-Z][a-zA-Z]+)Integration.*", "\$1")
        }
        return "TestIntegration"
    }
    
    // Additional helper methods for generating context-specific test data
    private String generateEntityFields(AgentRequest request) {
        return """entityId: "TEST_001",
        name: "Test Entity",
        description: "Test entity description",
        fromDate: new Date(),
        thruDate: null"""
    }
    
    private String generateServiceParams(AgentRequest request) {
        return """serviceParam1: "testValue1",
        serviceParam2: 123,
        serviceParam3: true"""
    }
    
    private String generateComponentProps(AgentRequest request) {
        return """title: "Test Component",
        visible: true,
        data: []"""
    }
    
    // More helper methods would be added based on specific context requirements
    private String extractPrimaryKey(AgentRequest request) { return "entityId" }
    private String extractUpdateableField(AgentRequest request) { return "description" }
    private String extractChildEntity(AgentRequest request) { return "ChildEntity" }
    private String extractForeignKey(AgentRequest request) { return "parentEntityId" }
    private String extractStringField(AgentRequest request) { return "description" }
    private String extractTestDataCondition(AgentRequest request) { return "entityId", "TEST_%"  }
    
    // Additional extraction methods for comprehensive test generation...
}
