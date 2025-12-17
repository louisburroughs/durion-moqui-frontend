package agents.models

/**
 * Context information for architectural decisions and guidance.
 * Contains architectural constraints and design patterns.
 */
class ArchitecturalContext {
    
    /** Architectural pattern being applied */
    String architecturalPattern
    
    /** Domain boundaries */
    Map<String, List<String>> domainBoundaries = [:]
    
    /** Component dependencies */
    Map<String, List<String>> componentDependencies = [:]
    
    /** Integration patterns */
    List<String> integrationPatterns = []
    
    /** Quality attributes (performance, security, scalability) */
    Map<String, Object> qualityAttributes = [:]
    
    /** Architectural constraints */
    List<String> architecturalConstraints = []
    
    /** Design decisions */
    List<Map<String, Object>> designDecisions = []
    
    /** Technology choices */
    Map<String, String> technologyChoices = [:]
    
    /** Cross-cutting concerns */
    List<String> crossCuttingConcerns = []
    
    /** Architectural debt */
    List<Map<String, Object>> architecturalDebt = []
    
    /** Additional architectural context */
    Map<String, Object> architecturalSpecific = [:]
}
