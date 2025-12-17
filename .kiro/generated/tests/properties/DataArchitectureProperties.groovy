package tests.properties

import net.jqwik.api.*
import net.jqwik.api.constraints.*
import spock.lang.Specification

/**
 * Property 7: Data architecture compliance
 * 
 * Validates:
 * - Local database usage (state/cache only)
 * - No direct business data access
 * - durion-positivity backend integration for business data
 * 
 * Requirements: Data Architecture Constraints (all requirements)
 * Test Cases: Property 7 validation
 */
class DataArchitectureProperties {
    
    /**
     * Property 7.1: Local database usage restricted to state/cache
     */
    @Property
    void localDatabaseOnlyForStateAndCache(
        @ForAll("databaseOperations") DatabaseOperation operation
    ) {
        def isStateOrCache = operation.type in ['agent_state', 'session_cache', 'performance_metrics']
        def isBusinessData = operation.type in ['customer', 'order', 'invoice', 'product', 'inventory']
        
        // Local database should only be used for state/cache, never business data
        if (operation.target == 'local_db') {
            assert isStateOrCache : "Local database used for business data: ${operation.type}"
            assert !isBusinessData : "Business data stored in local database: ${operation.type}"
        }
    }
    
    /**
     * Property 7.2: Business data access only through durion-positivity backend
     */
    @Property
    void businessDataOnlyThroughPositivityBackend(
        @ForAll("businessDataRequests") BusinessDataRequest request
    ) {
        def isBusinessData = request.entityType in ['Customer', 'Order', 'Invoice', 'Product', 'Inventory', 'Vehicle']
        
        if (isBusinessData) {
            // Business data must go through positivity backend
            assert request.source == 'durion-positivity-backend' : 
                "Business data accessed directly: ${request.entityType} from ${request.source}"
            
            // Must use REST API, not direct database
            assert request.protocol == 'REST' : 
                "Business data accessed via non-REST protocol: ${request.protocol}"
        }
    }
    
    /**
     * Property 7.3: Agent state persistence uses local database only
     */
    @Property
    void agentStatePersistenceLocal(
        @ForAll("agentStateOperations") AgentStateOperation operation
    ) {
        // Agent state must be stored locally
        assert operation.target == 'local_db' : 
            "Agent state stored remotely: ${operation.target}"
        
        // Agent state should not contain business data
        assert !operation.containsBusinessData : 
            "Agent state contains business data: ${operation.stateType}"
        
        // Agent state should be in dedicated tables
        assert operation.table.startsWith('agent_') || operation.table.startsWith('session_') : 
            "Agent state in non-agent table: ${operation.table}"
    }
    
    /**
     * Property 7.4: No direct database queries for business entities
     */
    @Property
    void noDirectBusinessEntityQueries(
        @ForAll("databaseQueries") DatabaseQuery query
    ) {
        def businessTables = ['Customer', 'Order', 'Invoice', 'Product', 'Inventory', 'Vehicle', 
                              'WorkOrder', 'ServiceRecord', 'Payment', 'Ledger']
        
        if (query.tables.any { it in businessTables }) {
            // Business entity queries must fail or redirect
            assert query.source != 'direct_sql' : 
                "Direct SQL query to business table: ${query.tables}"
            
            assert query.source == 'positivity_api' : 
                "Business data queried without positivity API: ${query.source}"
        }
    }
    
    /**
     * Property 7.5: Cache invalidation does not affect business data integrity
     */
    @Property
    void cacheInvalidationSafe(
        @ForAll("cacheOperations") CacheOperation operation
    ) {
        if (operation.action == 'invalidate' || operation.action == 'clear') {
            // Cache operations should only affect local cache
            assert operation.target == 'local_cache' : 
                "Cache operation affects non-local storage: ${operation.target}"
            
            // Should not trigger business data deletion
            assert !operation.cascadesToBusinessData : 
                "Cache invalidation cascades to business data"
            
            // Should be recoverable from backend
            assert operation.recoverableFromBackend : 
                "Cache data not recoverable from backend"
        }
    }
    
    /**
     * Property 7.6: Agent guidance enforces data architecture rules
     */
    @Property
    void agentGuidanceEnforcesDataArchitecture(
        @ForAll("agentGuidanceRequests") AgentGuidanceRequest request
    ) {
        if (request.topic == 'entity_definition' || request.topic == 'database_access') {
            def guidance = request.getGuidance()
            
            // Guidance should mention positivity backend for business data
            if (request.involvesBusinessData) {
                assert guidance.contains('durion-positivity') || guidance.contains('positivity backend') : 
                    "Guidance missing positivity backend reference for business data"
            }
            
            // Guidance should warn against direct business data access
            if (request.involvesBusinessData && request.suggestsDirectAccess) {
                assert guidance.contains('warning') || guidance.contains('not recommended') : 
                    "Guidance allows direct business data access"
            }
        }
    }
    
    // Arbitraries for test data generation
    
    @Provide
    Arbitrary<DatabaseOperation> databaseOperations() {
        return Combinators.combine(
            Arbitraries.of('local_db', 'remote_db', 'cache'),
            Arbitraries.of('agent_state', 'session_cache', 'performance_metrics', 
                          'customer', 'order', 'invoice', 'product', 'inventory'),
            Arbitraries.of('read', 'write', 'update', 'delete')
        ).as { target, type, action ->
            new DatabaseOperation(target: target, type: type, action: action)
        }
    }
    
    @Provide
    Arbitrary<BusinessDataRequest> businessDataRequests() {
        return Combinators.combine(
            Arbitraries.of('Customer', 'Order', 'Invoice', 'Product', 'Inventory', 'Vehicle',
                          'AgentState', 'SessionCache'),
            Arbitraries.of('durion-positivity-backend', 'local_db', 'direct_query'),
            Arbitraries.of('REST', 'SQL', 'GraphQL')
        ).as { entityType, source, protocol ->
            new BusinessDataRequest(entityType: entityType, source: source, protocol: protocol)
        }
    }
    
    @Provide
    Arbitrary<AgentStateOperation> agentStateOperations() {
        return Combinators.combine(
            Arbitraries.of('local_db', 'remote_db'),
            Arbitraries.of('agent_state', 'agent_config', 'agent_metrics', 'session_data'),
            Arbitraries.of('agent_state', 'agent_config', 'session_cache', 'performance_metrics'),
            Arbitraries.booleans()
        ).as { target, table, stateType, containsBusinessData ->
            new AgentStateOperation(
                target: target, 
                table: table, 
                stateType: stateType, 
                containsBusinessData: containsBusinessData
            )
        }
    }
    
    @Provide
    Arbitrary<DatabaseQuery> databaseQueries() {
        return Combinators.combine(
            Arbitraries.of(['Customer'], ['Order', 'Invoice'], ['Product', 'Inventory'], 
                          ['agent_state'], ['session_cache']),
            Arbitraries.of('direct_sql', 'orm', 'positivity_api')
        ).as { tables, source ->
            new DatabaseQuery(tables: tables, source: source)
        }
    }
    
    @Provide
    Arbitrary<CacheOperation> cacheOperations() {
        return Combinators.combine(
            Arbitraries.of('invalidate', 'clear', 'refresh', 'update'),
            Arbitraries.of('local_cache', 'distributed_cache', 'database'),
            Arbitraries.booleans(),
            Arbitraries.booleans()
        ).as { action, target, cascades, recoverable ->
            new CacheOperation(
                action: action,
                target: target,
                cascadesToBusinessData: cascades,
                recoverableFromBackend: recoverable
            )
        }
    }
    
    @Provide
    Arbitrary<AgentGuidanceRequest> agentGuidanceRequests() {
        return Combinators.combine(
            Arbitraries.of('entity_definition', 'database_access', 'service_implementation', 'screen_development'),
            Arbitraries.booleans(),
            Arbitraries.booleans()
        ).as { topic, involvesBusinessData, suggestsDirectAccess ->
            new AgentGuidanceRequest(
                topic: topic,
                involvesBusinessData: involvesBusinessData,
                suggestsDirectAccess: suggestsDirectAccess
            )
        }
    }
    
    // Data classes
    
    static class DatabaseOperation {
        String target
        String type
        String action
    }
    
    static class BusinessDataRequest {
        String entityType
        String source
        String protocol
    }
    
    static class AgentStateOperation {
        String target
        String table
        String stateType
        boolean containsBusinessData
    }
    
    static class DatabaseQuery {
        List<String> tables
        String source
    }
    
    static class CacheOperation {
        String action
        String target
        boolean cascadesToBusinessData
        boolean recoverableFromBackend
    }
    
    static class AgentGuidanceRequest {
        String topic
        boolean involvesBusinessData
        boolean suggestsDirectAccess
        
        String getGuidance() {
            if (involvesBusinessData && !suggestsDirectAccess) {
                return "Use durion-positivity backend for business data access"
            } else if (involvesBusinessData && suggestsDirectAccess) {
                return "Direct access to business data is not recommended"
            } else {
                return "Local database can be used for agent state and cache"
            }
        }
    }
}
