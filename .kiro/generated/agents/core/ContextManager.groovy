package agents.core

import models.MoquiContext
import models.ImplementationContext
import models.ArchitecturalContext
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * Context Manager - Manages session context storage, sharing, and validation
 * Provides context cleanup and cross-agent context coordination
 */
class ContextManager {
    
    private final Map<String, Map<String, Object>> sessionContexts = new ConcurrentHashMap<>()
    private final Map<String, Long> contextTimestamps = new ConcurrentHashMap<>()
    private final ScheduledExecutorService cleanupExecutor = Executors.newScheduledThreadPool(1)
    
    // Context TTL in milliseconds (30 minutes)
    private static final long CONTEXT_TTL = 30 * 60 * 1000L
    
    ContextManager() {
        // Schedule cleanup every 5 minutes
        cleanupExecutor.scheduleAtFixedRate(this::cleanupExpiredContexts, 5, 5, TimeUnit.MINUTES)
    }
    
    /**
     * Store context for session
     */
    void storeContext(String sessionId, String contextKey, Object contextData) {
        sessionContexts.computeIfAbsent(sessionId, { [:] }).put(contextKey, contextData)
        contextTimestamps.put("${sessionId}:${contextKey}", System.currentTimeMillis())
    }
    
    /**
     * Retrieve context for session
     */
    Object getContext(String sessionId, String contextKey) {
        def sessionData = sessionContexts.get(sessionId)
        return sessionData?.get(contextKey)
    }
    
    /**
     * Get all context for session
     */
    Map<String, Object> getSessionContext(String sessionId) {
        return sessionContexts.get(sessionId) ?: [:]
    }
    
    /**
     * Store Moqui-specific context
     */
    void storeMoquiContext(String sessionId, MoquiContext context) {
        storeContext(sessionId, 'moqui', context)
    }
    
    /**
     * Retrieve Moqui-specific context
     */
    MoquiContext getMoquiContext(String sessionId) {
        return getContext(sessionId, 'moqui') as MoquiContext
    }
    
    /**
     * Store implementation context
     */
    void storeImplementationContext(String sessionId, ImplementationContext context) {
        storeContext(sessionId, 'implementation', context)
    }
    
    /**
     * Retrieve implementation context
     */
    ImplementationContext getImplementationContext(String sessionId) {
        return getContext(sessionId, 'implementation') as ImplementationContext
    }
    
    /**
     * Store architectural context
     */
    void storeArchitecturalContext(String sessionId, ArchitecturalContext context) {
        storeContext(sessionId, 'architectural', context)
    }
    
    /**
     * Retrieve architectural context
     */
    ArchitecturalContext getArchitecturalContext(String sessionId) {
        return getContext(sessionId, 'architectural') as ArchitecturalContext
    }
    
    /**
     * Share context between sessions
     */
    void shareContext(String fromSessionId, String toSessionId, String contextKey) {
        def contextData = getContext(fromSessionId, contextKey)
        if (contextData) {
            storeContext(toSessionId, contextKey, contextData)
        }
    }
    
    /**
     * Validate context integrity
     */
    boolean validateContext(String sessionId) {
        def sessionData = sessionContexts.get(sessionId)
        if (!sessionData) return true
        
        // Check for required context consistency
        def moquiContext = sessionData.get('moqui') as MoquiContext
        def implContext = sessionData.get('implementation') as ImplementationContext
        
        if (moquiContext && implContext) {
            // Validate component consistency
            return moquiContext.componentName == implContext.componentName
        }
        
        return true
    }
    
    /**
     * Clear session context
     */
    void clearSession(String sessionId) {
        sessionContexts.remove(sessionId)
        
        // Remove timestamps for this session
        def keysToRemove = contextTimestamps.keySet().findAll { it.startsWith("${sessionId}:") }
        keysToRemove.each { contextTimestamps.remove(it) }
    }
    
    /**
     * Clear specific context
     */
    void clearContext(String sessionId, String contextKey) {
        def sessionData = sessionContexts.get(sessionId)
        sessionData?.remove(contextKey)
        contextTimestamps.remove("${sessionId}:${contextKey}")
    }
    
    /**
     * Get context statistics
     */
    Map<String, Object> getStats() {
        return [
            activeSessions: sessionContexts.size(),
            totalContexts: contextTimestamps.size(),
            oldestContext: contextTimestamps.values().min(),
            newestContext: contextTimestamps.values().max()
        ]
    }
    
    /**
     * Cleanup expired contexts
     */
    private void cleanupExpiredContexts() {
        def now = System.currentTimeMillis()
        def expiredKeys = []
        
        contextTimestamps.each { key, timestamp ->
            if (now - timestamp > CONTEXT_TTL) {
                expiredKeys.add(key)
            }
        }
        
        expiredKeys.each { key ->
            def parts = key.split(':')
            if (parts.length == 2) {
                clearContext(parts[0], parts[1])
            }
        }
        
        // Remove empty sessions
        def emptySessions = sessionContexts.findAll { _, contexts -> contexts.isEmpty() }.keySet()
        emptySessions.each { sessionContexts.remove(it) }
    }
    
    /**
     * Shutdown context manager
     */
    void shutdown() {
        cleanupExecutor.shutdown()
        try {
            if (!cleanupExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                cleanupExecutor.shutdownNow()
            }
        } catch (InterruptedException e) {
            cleanupExecutor.shutdownNow()
            Thread.currentThread().interrupt()
        }
    }
}
