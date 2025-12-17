package agents.models

/**
 * Data model for agent responses.
 * Contains the result of processing an agent request.
 */
class AgentResponse {
    
    /** Request ID this response corresponds to */
    String requestId
    
    /** Response status (SUCCESS, ERROR, TIMEOUT, etc.) */
    String status
    
    /** Response payload/content */
    Map<String, Object> payload
    
    /** Error message if status is ERROR */
    String errorMessage
    
    /** Processing time in milliseconds */
    Long processingTimeMs
    
    /** Timestamp when response was created */
    Date timestamp = new Date()
    
    /** Agent that processed the request */
    String agentId
    
    /** Confidence level (0.0 - 1.0) */
    Double confidence = 1.0
    
    /** Additional metadata */
    Map<String, Object> metadata = [:]
    
    // Convenience properties for test compatibility
    boolean getSuccess() { return status == "SUCCESS" }
    Map<String, Object> getData() { return payload }
    String getError() { return errorMessage }
    Double getAccuracy() { return confidence }
    Long getResponseTime() { return processingTimeMs }
    Long getResponseTimeMs() { return processingTimeMs }
    
    // Static factory methods
    static AgentResponse success(Map<String, Object> payload, String message = "Success") {
        return new AgentResponse(
            status: "SUCCESS",
            payload: payload,
            confidence: 1.0,
            metadata: [
                message: message,
                accuracy: 1.0,
                responseTime: 0L
            ]
        )
    }
    
    static AgentResponse error(String errorMessage) {
        return new AgentResponse(
            status: "ERROR",
            errorMessage: errorMessage,
            confidence: 0.0,
            payload: [:]
        )
    }
    
    static AgentResponse timeout(String message = "Request timed out") {
        return new AgentResponse(
            status: "TIMEOUT",
            errorMessage: message,
            confidence: 0.0,
            payload: [:]
        )
    }
}