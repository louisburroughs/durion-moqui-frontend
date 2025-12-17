package agents.models

/**
 * Data model for agent requests.
 * Contains all information needed for an agent to process a request.
 */
class AgentRequest {
    
    /** Unique request identifier */
    String requestId
    
    /** Type of request (entity, service, screen, etc.) */
    String requestType
    
    /** Request payload/content */
    Map<String, Object> payload
    
    /** Request parameters (alternative to payload) */
    Map<String, Object> parameters
    
    /** Context information */
    Object context
    
    /** Priority level (1-5, 1 = highest) */
    Integer priority = 3
    
    /** Timestamp when request was created */
    Date timestamp = new Date()
    
    /** Source agent or system making the request */
    String source
    
    /** Target capabilities required */
    List<String> requiredCapabilities = []
    
    /** Specific capability being requested */
    String capability
    
    /** Timeout in milliseconds */
    Long timeoutMs = 5000L
}
