package tests.properties

import net.jqwik.api.*
import net.jqwik.api.constraints.*
import net.jqwik.api.statistics.Statistics
import agents.support.UsabilityManager

/**
 * Property 8: Usability Targets
 * 
 * Validates REQ-012 acceptance criteria:
 * - AC1: 80% productivity in 2 hours training
 * - AC2: 95% guidance relevance
 * - AC3: 90% intent recognition
 * 
 * Test Cases: TC-034, TC-035, TC-036
 */
class UsabilityProperties {
    
    private UsabilityManager usabilityManager = new UsabilityManager()
    
    /**
     * Property 8.1: Onboarding Productivity Target
     * 
     * Validates: REQ-012 AC1
     * Target: 80% productivity after 2-hour training
     * Test Case: TC-034
     */
    @Property
    @Label("Property 8.1: Onboarding achieves 80% productivity in 2 hours")
    void onboardingProductivityTarget(
        @ForAll @IntRange(min = 1, max = 100) int developerId
    ) {
        // Given: A developer starting onboarding
        def request = [
            capability: 'onboarding-workflow',
            developerId: developerId,
            startTime: System.currentTimeMillis()
        ]
        
        // When: Developer completes 2-hour onboarding
        def response = usabilityManager.processRequest(request)
        
        // Then: Productivity should be >= 80%
        assert response.success
        assert response.onboardingComplete
        assert response.phases.size() == 4
        
        // Validate phase durations sum to 120 minutes
        def totalMinutes = response.phases.sum { it.durationMinutes }
        assert totalMinutes == 120
        
        // Validate productivity target
        assert response.productivityScore >= 80.0
        
        // Statistics for analysis
        Statistics.collect(response.productivityScore)
    }
    
    /**
     * Property 8.2: Context-Aware Guidance Relevance
     * 
     * Validates: REQ-012 AC2
     * Target: 95% guidance relevance
     * Test Case: TC-035
     */
    @Property
    @Label("Property 8.2: Context-aware guidance achieves 95% relevance")
    void contextAwareGuidanceRelevance(
        @ForAll("activeFiles") List<String> activeFiles,
        @ForAll("guidanceTypes") String guidanceType
    ) {
        // Given: Active files in workspace
        def request = [
            capability: 'context-aware-guidance',
            activeFiles: activeFiles,
            guidanceType: guidanceType
        ]
        
        // When: Requesting context-aware guidance
        def response = usabilityManager.processRequest(request)
        
        // Then: Relevance should be >= 95%
        assert response.success
        assert response.relevanceScore >= 95.0
        assert response.contextDetected
        assert response.guidanceProvided
        
        // Validate context detection
        assert response.detectedContext in ['entity', 'service', 'screen', 'integration', 'test']
        
        // Statistics for analysis
        Statistics.collect(response.relevanceScore)
    }
    
    /**
     * Property 8.3: Natural Language Query Intent Recognition
     * 
     * Validates: REQ-012 AC3
     * Target: 90% intent recognition accuracy
     * Test Case: TC-036
     */
    @Property
    @Label("Property 8.3: NLP query achieves 90% intent recognition")
    void naturalLanguageIntentRecognition(
        @ForAll("naturalLanguageQueries") String query
    ) {
        // Given: A natural language query
        def request = [
            capability: 'natural-language-query',
            query: query
        ]
        
        // When: Processing the query
        def response = usabilityManager.processRequest(request)
        
        // Then: Intent recognition should be >= 90%
        assert response.success
        assert response.intentRecognized
        assert response.recognitionAccuracy >= 90.0
        
        // Validate intent classification
        assert response.intent in ['create', 'modify', 'query', 'debug', 'optimize', 'document']
        
        // Validate entity extraction
        assert response.entities != null
        assert response.entities.size() >= 0
        
        // Statistics for analysis
        Statistics.collect(response.recognitionAccuracy)
    }
    
    /**
     * Property 8.4: UI Pattern Consistency
     * 
     * Validates: REQ-012 AC4
     * Target: Consistent UI patterns across all guidance
     */
    @Property
    @Label("Property 8.4: UI patterns are consistent across guidance types")
    void uiPatternConsistency(
        @ForAll("uiPatternTypes") String patternType
    ) {
        // Given: A UI pattern type
        def request = [
            capability: 'ui-pattern-consistency',
            patternType: patternType
        ]
        
        // When: Requesting UI pattern guidance
        def response = usabilityManager.processRequest(request)
        
        // Then: Pattern should be standardized
        assert response.success
        assert response.pattern != null
        assert response.pattern.structure != null
        assert response.pattern.progressiveDisclosure == true
        assert response.pattern.visualConsistency == true
        
        // Validate pattern structure
        assert response.pattern.structure.containsKey('header')
        assert response.pattern.structure.containsKey('content')
        assert response.pattern.structure.containsKey('actions')
    }
    
    /**
     * Property 8.5: Help Documentation Accessibility
     * 
     * Validates: REQ-012 AC5
     * Target: 2-click access to all documentation
     */
    @Property
    @Label("Property 8.5: Help documentation accessible within 2 clicks")
    void helpDocumentationAccessibility(
        @ForAll("helpTopics") String topic
    ) {
        // Given: A help topic
        def request = [
            capability: 'help-documentation',
            topic: topic,
            contextSensitive: true
        ]
        
        // When: Requesting help documentation
        def response = usabilityManager.processRequest(request)
        
        // Then: Documentation should be accessible
        assert response.success
        assert response.clicksRequired <= 2
        assert response.documentationFound
        assert response.f1KeySupported == true
        
        // Validate search capability
        assert response.fullTextSearchAvailable == true
    }
    
    /**
     * Property 8.6: Onboarding Phase Progression
     * 
     * Validates: REQ-012 AC1
     * Ensures phases progress correctly
     */
    @Property
    @Label("Property 8.6: Onboarding phases progress in correct order")
    void onboardingPhaseProgression() {
        // Given: Onboarding workflow
        def request = [
            capability: 'onboarding-workflow',
            developerId: 1
        ]
        
        // When: Retrieving onboarding phases
        def response = usabilityManager.processRequest(request)
        
        // Then: Phases should be in correct order
        assert response.success
        assert response.phases.size() == 4
        
        def phases = response.phases
        assert phases[0].name == 'Introduction'
        assert phases[0].durationMinutes == 15
        
        assert phases[1].name == 'Hands-on Basics'
        assert phases[1].durationMinutes == 30
        
        assert phases[2].name == 'Domain Context'
        assert phases[2].durationMinutes == 30
        
        assert phases[3].name == 'Advanced Patterns'
        assert phases[3].durationMinutes == 45
    }
    
    // Arbitraries for test data generation
    
    @Provide
    Arbitrary<List<String>> activeFiles() {
        return Arbitraries.of(
            ['entity/OrderEntities.xml'],
            ['service/OrderServices.xml'],
            ['screen/OrderScreen.xml'],
            ['entity/ProductEntities.xml', 'service/ProductServices.xml'],
            ['screen/DashboardScreen.xml', 'service/DashboardServices.xml']
        )
    }
    
    @Provide
    Arbitrary<String> guidanceTypes() {
        return Arbitraries.of(
            'entity-definition',
            'service-implementation',
            'screen-development',
            'integration-pattern',
            'testing-strategy'
        )
    }
    
    @Provide
    Arbitrary<String> naturalLanguageQueries() {
        return Arbitraries.of(
            'How do I create a new entity for orders?',
            'Show me service patterns for payment processing',
            'What is the best way to implement a dashboard screen?',
            'How do I integrate with durion-positivity backend?',
            'Debug performance issue in product catalog service',
            'Optimize database queries for inventory management'
        )
    }
    
    @Provide
    Arbitrary<String> uiPatternTypes() {
        return Arbitraries.of(
            'entity-form',
            'list-view',
            'dashboard',
            'wizard',
            'detail-view'
        )
    }
    
    @Provide
    Arbitrary<String> helpTopics() {
        return Arbitraries.of(
            'entity-definition',
            'service-implementation',
            'screen-development',
            'integration-patterns',
            'testing-strategies',
            'performance-optimization'
        )
    }
}
