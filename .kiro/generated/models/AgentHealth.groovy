package agents.models

/**
 * Data model for agent health status.
 * Contains health and performance metrics for an agent.
 */
class AgentHealth {
    
    /** Health status (HEALTHY, DEGRADED, UNHEALTHY) */
    String status
    
    /** Last health check timestamp */
    Date lastCheck
    
    /** Response time percentiles */
    Map<String, Long> responseTimePercentiles
    
    /** Current system load */
    Double currentLoad
    
    /** Error rate percentage */
    Double errorRate
    
    /** Available memory in MB */
    Long availableMemoryMb
    
    /** CPU usage percentage */
    Double cpuUsagePercent
    
    /** Performance metrics */
    Map<String, Object> metrics
    
    /** Additional health details */
    List<String> details
    
    // Constructor
    AgentHealth() {}
    
    AgentHealth(Map<String, Object> properties) {
        properties.each { key, value ->
            if (this.hasProperty(key as String)) {
                this[key] = value
            }
        }
    }
}