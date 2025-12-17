package agents.models

/**
 * Context information for implementation-specific operations.
 * Contains data needed for domain and frontend agent processing.
 */
class ImplementationContext {
    
    /** Implementation type (entity, service, screen, component) */
    String implementationType
    
    /** Business domain (work-execution, inventory, crm, etc.) */
    String businessDomain
    
    /** Technology stack (vue, typescript, groovy, java) */
    List<String> technologyStack = []
    
    /** File paths being worked on */
    List<String> filePaths = []
    
    /** Current implementation phase (design, implementation, testing) */
    String phase
    
    /** Requirements being addressed */
    List<String> requirements = []
    
    /** Architectural constraints */
    List<String> constraints = []
    
    /** Integration points */
    List<String> integrationPoints = []
    
    /** Performance requirements */
    Map<String, Object> performanceRequirements = [:]
    
    /** Security requirements */
    List<String> securityRequirements = []
    
    /** Testing requirements */
    Map<String, Object> testingRequirements = [:]
    
    /** Additional implementation context */
    Map<String, Object> implementationSpecific = [:]
}
