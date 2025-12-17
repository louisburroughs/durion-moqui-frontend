package agents.models

/**
 * Context information specific to Moqui Framework operations.
 * Contains Moqui-specific data needed for agent processing.
 */
class MoquiContext {
    
    /** Component name (e.g., durion-crm, PopCommerce) */
    String componentName
    
    /** Entity name if applicable */
    String entityName
    
    /** Service name if applicable */
    String serviceName
    
    /** Screen path if applicable */
    String screenPath
    
    /** Screen name if applicable */
    String screenName
    
    /** Domain context */
    String domainContext
    
    /** Integration point */
    String integrationPoint
    
    /** Moqui Framework version */
    String moquiVersion = "3.x"
    
    /** Database type (PostgreSQL, MySQL, H2) */
    String databaseType
    
    /** Current user context */
    String userId
    
    /** Tenant ID for multi-tenant operations */
    String tenantId
    
    /** Component dependencies */
    List<String> dependencies = []
    
    /** Entity relationships */
    Map<String, List<String>> entityRelationships = [:]
    
    /** Service parameters */
    Map<String, Object> serviceParameters = [:]
    
    /** Screen parameters */
    Map<String, Object> screenParameters = [:]
    
    /** Additional Moqui-specific context */
    Map<String, Object> moquiSpecific = [:]
    
    // Constructor that accepts a Map
    MoquiContext() {}
    
    MoquiContext(Map<String, Object> properties) {
        properties.each { key, value ->
            if (this.hasProperty(key as String)) {
                this[key] = value
            }
        }
    }
}
