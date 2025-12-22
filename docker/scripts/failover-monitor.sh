#!/bin/bash

# Failover Monitor Script
# Monitors primary agent instance and triggers failover to secondary within 30 seconds

set -e

PRIMARY_URL="${PRIMARY_URL:-http://moqui-agents-primary:8080}"
SECONDARY_URL="${SECONDARY_URL:-http://moqui-agents-secondary:8080}"
REDIS_URL="${REDIS_URL:-redis://redis:6379}"
FAILOVER_TIMEOUT="${FAILOVER_TIMEOUT:-30}"
CHECK_INTERVAL=10

echo "Starting failover monitor..."
echo "Primary URL: $PRIMARY_URL"
echo "Secondary URL: $SECONDARY_URL"
echo "Failover timeout: ${FAILOVER_TIMEOUT}s"

# Initialize state
CURRENT_PRIMARY="primary"
FAILOVER_START_TIME=""
CONSECUTIVE_FAILURES=0
MAX_CONSECUTIVE_FAILURES=3

# Function to check instance health
check_health() {
    local url=$1
    local timeout=5
    
    if curl -f -s --max-time $timeout "$url/rest/s1/moqui/agents/health" > /dev/null 2>&1; then
        return 0
    else
        return 1
    fi
}

# Function to promote secondary to primary
promote_secondary() {
    echo "FAILOVER: Promoting secondary to primary at $(date)"
    
    # Update Redis registry to mark secondary as primary
    redis-cli -u "$REDIS_URL" hset agent:cluster:status primary_instance secondary
    redis-cli -u "$REDIS_URL" hset agent:cluster:status failover_time "$(date -Iseconds)"
    redis-cli -u "$REDIS_URL" hset agent:cluster:status failover_reason "primary_health_check_failed"
    
    # Signal secondary to exit standby mode
    curl -X POST -f "$SECONDARY_URL/rest/s1/moqui/agents/cluster/promote" || true
    
    CURRENT_PRIMARY="secondary"
    echo "FAILOVER: Secondary promoted to primary successfully"
}

# Function to restore primary
restore_primary() {
    echo "RESTORE: Restoring primary instance at $(date)"
    
    # Update Redis registry
    redis-cli -u "$REDIS_URL" hset agent:cluster:status primary_instance primary
    redis-cli -u "$REDIS_URL" hset agent:cluster:status restore_time "$(date -Iseconds)"
    
    # Signal primary to resume active mode
    curl -X POST -f "$PRIMARY_URL/rest/s1/moqui/agents/cluster/activate" || true
    
    # Signal secondary to enter standby mode
    curl -X POST -f "$SECONDARY_URL/rest/s1/moqui/agents/cluster/standby" || true
    
    CURRENT_PRIMARY="primary"
    echo "RESTORE: Primary instance restored successfully"
}

# Main monitoring loop
while true; do
    if [ "$CURRENT_PRIMARY" = "primary" ]; then
        # Monitor primary instance
        if check_health "$PRIMARY_URL"; then
            CONSECUTIVE_FAILURES=0
            FAILOVER_START_TIME=""
        else
            CONSECUTIVE_FAILURES=$((CONSECUTIVE_FAILURES + 1))
            echo "WARNING: Primary health check failed (attempt $CONSECUTIVE_FAILURES/$MAX_CONSECUTIVE_FAILURES)"
            
            if [ -z "$FAILOVER_START_TIME" ]; then
                FAILOVER_START_TIME=$(date +%s)
                echo "ALERT: Starting failover timer at $(date)"
            fi
            
            # Check if failover timeout exceeded or max failures reached
            CURRENT_TIME=$(date +%s)
            ELAPSED_TIME=$((CURRENT_TIME - FAILOVER_START_TIME))
            
            if [ $ELAPSED_TIME -ge $FAILOVER_TIMEOUT ] || [ $CONSECUTIVE_FAILURES -ge $MAX_CONSECUTIVE_FAILURES ]; then
                # Check if secondary is healthy before failover
                if check_health "$SECONDARY_URL"; then
                    promote_secondary
                else
                    echo "ERROR: Secondary instance also unhealthy - cannot failover"
                    # Reset counters and continue monitoring
                    CONSECUTIVE_FAILURES=0
                    FAILOVER_START_TIME=""
                fi
            fi
        fi
    else
        # Monitor secondary instance (now acting as primary)
        if check_health "$SECONDARY_URL"; then
            # Check if original primary is back online
            if check_health "$PRIMARY_URL"; then
                echo "INFO: Primary instance is healthy again"
                # Wait additional time before restoring to avoid flapping
                sleep 30
                if check_health "$PRIMARY_URL"; then
                    restore_primary
                fi
            fi
        else
            echo "CRITICAL: Current primary (secondary) is unhealthy"
            # In production, this would trigger alerts to operations team
        fi
    fi
    
    # Update monitoring status in Redis
    redis-cli -u "$REDIS_URL" hset agent:cluster:monitor last_check "$(date -Iseconds)"
    redis-cli -u "$REDIS_URL" hset agent:cluster:monitor current_primary "$CURRENT_PRIMARY"
    redis-cli -u "$REDIS_URL" hset agent:cluster:monitor consecutive_failures "$CONSECUTIVE_FAILURES"
    
    sleep $CHECK_INTERVAL
done
