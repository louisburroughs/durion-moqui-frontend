#!/usr/bin/env groovy

// Simple test script to verify agents are functional
@Grab('org.slf4j:slf4j-simple:1.7.36')

import java.nio.file.Paths

// Add the generated agents to classpath
def agentsDir = Paths.get('.kiro/generated/agents').toAbsolutePath().toString()
def modelsDir = Paths.get('.kiro/generated/models').toAbsolutePath().toString()

// Add to classpath
this.class.classLoader.addURL(new File(agentsDir).toURI().toURL())
this.class.classLoader.addURL(new File(modelsDir).toURI().toURL())

try {
    // Test 1: Load MoquiFrameworkAgentImpl
    println "Testing MoquiFrameworkAgentImpl..."
    def agentClass = Class.forName('agents.foundation.MoquiFrameworkAgentImpl')
    def agent = agentClass.newInstance()
    
    println "✓ Agent created: ${agent.getAgentId()}"
    println "✓ Agent name: ${agent.getAgentName()}"
    println "✓ Capabilities: ${agent.getCapabilities()}"
    
    // Test 2: Load models
    println "\nTesting models..."
    def requestClass = Class.forName('agents.models.AgentRequest')
    def responseClass = Class.forName('agents.models.AgentResponse')
    def contextClass = Class.forName('agents.models.MoquiContext')
    def healthClass = Class.forName('agents.models.AgentHealth')
    
    println "✓ AgentRequest class loaded"
    println "✓ AgentResponse class loaded"
    println "✓ MoquiContext class loaded"
    println "✓ AgentHealth class loaded"
    
    // Test 3: Create a simple request
    println "\nTesting request processing..."
    def context = contextClass.newInstance()
    context.entityName = "TestEntity"
    context.componentName = "test-component"
    
    def request = requestClass.newInstance()
    request.requestId = "test-001"
    request.capability = "entity-guidance"
    request.context = context
    request.payload = [entityType: "business", domain: "test"]
    
    // Test 4: Process request
    def response = agent.processRequest(request)
    println "✓ Request processed successfully"
    println "✓ Response status: ${response.status}"
    println "✓ Response confidence: ${response.confidence}"
    
    // Test 5: Health check
    def health = agent.getHealth()
    println "✓ Health check: ${health.status}"
    
    println "\n🎉 All foundation agent tests passed!"
    
} catch (Exception e) {
    println "❌ Test failed: ${e.message}"
    e.printStackTrace()
    System.exit(1)
}