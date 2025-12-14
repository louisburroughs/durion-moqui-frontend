# Durion Enterprise Tire Service Management System (DETSMS) Requirements

## Introduction

The Durion Enterprise Tire Service Management System (DETSMS) is a comprehensive tire service management system built on Moqui Framework. The system provides enterprise-grade business functionality across multiple domains including Work Execution, Inventory Control, Product & Pricing, Customer Relationship Management, and Accounting integration, with integrated Experience layer, UI/Mobile interfaces, and MCP conversational capabilities.

## Glossary

- **DETSMS**: Durion Enterprise Tire Service Management System for tire service operations
- **Work Execution Domain**: Business domain managing the estimate to payment workflow
- **Experience Layer**: Orchestration services providing task-oriented APIs for UI/Mobile/MCP integration
- **MCP**: Model Context Protocol for conversational AI integration
- **Durion-Positivity**: Experience API component providing API layer to expose business logic via APIs
- **Durion-Theme**: Component responsible for UI look and feel and common UI components
- **Positivity Backend**: Spring Boot microservices system providing business logic and data persistence
- **Service Writer**: User role responsible for creating estimates and managing customer service requests
- **Service Advisor**: User role responsible for customer interaction and service coordination
- **Mechanic**: User role responsible for executing work orders and updating job status
- **Shop Manager**: User role responsible for business operations and system administration

## Requirements

### Requirement 1

**User Story:** As a service writer, I want to create estimates and convert them to workorders, so that I can manage customer service requests efficiently.

#### Acceptance Criteria

1. WHEN a Service Writer creates an estimate for a customer vehicle THEN the DETSMS SHALL store estimate details with proper customer and vehicle linkage
2. WHEN an estimate is approved THEN the DETSMS SHALL convert the estimate to a workorder with all line items preserved
3. WHEN a workorder is created THEN the DETSMS SHALL assign the workorder to available mechanics and service bays
4. WHEN a workorder is completed THEN the DETSMS SHALL generate an invoice with accurate pricing calculations
5. WHERE warranty service is required THEN the DETSMS SHALL track warranty cases and link them to original workorders

### Requirement 2

**User Story:** As a mechanic, I want to check parts availability and consume inventory for workorders, so that I can complete jobs without delays.

#### Acceptance Criteria

1. WHEN a Mechanic checks part availability THEN the DETSMS SHALL display current stock levels by location
2. WHEN parts are added to a workorder THEN the DETSMS SHALL reserve inventory to prevent overselling
3. WHEN a workorder is completed THEN the DETSMS SHALL consume reserved inventory and update stock levels
4. WHEN parts are received THEN the DETSMS SHALL update inventory levels and make parts available for workorders
5. WHERE parts are not available THEN the DETSMS SHALL suggest alternatives or allow backorder processing

### Requirement 3

**User Story:** As a service advisor, I want to manage customer information and vehicle details, so that I can create accurate estimates and track service history.

#### Acceptance Criteria

1. WHEN a customer calls for service THEN the DETSMS SHALL allow lookup by phone number, name, or vehicle information
2. WHEN a new customer is created THEN the DETSMS SHALL capture essential contact information and preferences
3. WHEN a vehicle is added THEN the DETSMS SHALL store VIN, make, model, year, and service history linkage
4. WHERE fleet customers exist THEN the DETSMS SHALL manage multiple vehicles under a single account
5. WHEN customer information changes THEN the DETSMS SHALL maintain an audit trail of modifications

### Requirement 4

**User Story:** As a shop manager, I want to maintain product catalogs and pricing structures, so that estimates and invoices reflect current pricing.

#### Acceptance Criteria

1. WHEN products are added to the catalog THEN the DETSMS SHALL store essential product information and categorization
2. WHEN service operations are defined THEN the DETSMS SHALL link service operations to required skills and labor categories
3. WHEN pricing is updated THEN the DETSMS SHALL apply changes to new estimates while preserving existing workorder pricing
4. WHERE customer-specific pricing exists THEN the DETSMS SHALL apply appropriate discounts and overrides
5. WHEN pricing lookups occur THEN the DETSMS SHALL return effective prices based on customer and date context

### Requirement 5

**User Story:** As a mechanic, I want to access and update workorder information from mobile devices, so that I can work efficiently in the field or shop floor.

#### Acceptance Criteria

1. WHEN a Mechanic logs in THEN the DETSMS SHALL display assigned workorders with priority and status
2. WHEN viewing a workorder THEN the DETSMS SHALL show customer details, vehicle information, and required services
3. WHEN adding findings THEN the DETSMS SHALL allow text notes and photo capture with automatic attachment
4. WHEN updating workorder status THEN the DETSMS SHALL reflect changes immediately in the main system
5. WHERE mobile connectivity is limited THEN the DETSMS SHALL support offline mode with synchronization capabilities

### Requirement 6

**User Story:** As a service advisor, I want to interact with the system through natural language, so that I can quickly access information and perform common tasks.

#### Acceptance Criteria

1. WHEN a Service Advisor asks about customer information THEN the DETSMS SHALL provide relevant details and service history
2. WHEN a Service Advisor requests to create an estimate THEN the DETSMS SHALL guide the user through the process conversationally
3. WHEN a Service Advisor inquires about parts availability THEN the DETSMS SHALL check inventory and provide current status
4. WHEN a Service Advisor asks for workorder updates THEN the DETSMS SHALL summarize current status and any blocking issues
5. WHERE complex requests are made THEN the DETSMS SHALL break requests down into manageable steps

### Requirement 7

**User Story:** As a shop manager, I want to track invoices and payments, so that I can monitor accounts receivable.

#### Acceptance Criteria

1. WHEN a workorder is completed THEN the DETSMS SHALL generate an invoice with accurate line items and pricing
2. WHEN payments are received THEN the DETSMS SHALL record payments against the appropriate invoice
3. WHEN viewing customer accounts THEN the DETSMS SHALL display current balance and payment history
4. WHERE invoices are posted THEN the DETSMS SHALL maintain simple accounts receivable records
5. WHEN payment discrepancies occur THEN the DETSMS SHALL flag discrepancies for manual review

### Requirement 8

**User Story:** As a system architect, I want to ensure proper integration with positivity backend APIs, so that business logic and data persistence are handled correctly.

#### Acceptance Criteria

1. WHEN implementing business logic THEN the DETSMS SHALL use Positivity Backend APIs for all business data persistence and processing
2. WHEN requiring data persistence THEN the DETSMS SHALL use local database only for state maintenance and caching purposes
3. WHEN a required API does not exist in Positivity Backend THEN the DETSMS SHALL create specifications for missing APIs
4. WHEN integrating with Positivity Backend THEN the DETSMS SHALL use JWT-based authentication for all API communications
5. WHEN handling cross-domain communication THEN the DETSMS SHALL use durion-positivity experience API patterns

### Requirement 9

**User Story:** As a system architect, I want to ensure proper component structure and dependencies, so that the system maintains clear architectural boundaries.

#### Acceptance Criteria

1. WHEN creating durion components THEN the DETSMS SHALL follow proper component dependency hierarchy
2. WHEN implementing UI components THEN the DETSMS SHALL use durion-theme for look and feel and common UI components
3. WHEN requiring shared functionality THEN the DETSMS SHALL use durion-common for utilities and base functionality
4. WHEN integrating domains THEN the DETSMS SHALL respect domain boundaries and use proper integration patterns
5. WHEN building screens THEN the DETSMS SHALL use Moqui screen widgets, forms, and UI patterns consistently

### Requirement 10

**User Story:** As a system administrator, I want comprehensive security and performance capabilities, so that the system operates securely and efficiently.

#### Acceptance Criteria

1. WHEN implementing authentication THEN the DETSMS SHALL use JWT-based authentication with proper token management
2. WHEN securing entities THEN the DETSMS SHALL implement entity-level security constraints and field-level access control
3. WHEN optimizing performance THEN the DETSMS SHALL implement efficient database queries with proper indexing
4. WHEN implementing caching THEN the DETSMS SHALL use appropriate caching strategies with proper invalidation
5. WHEN processing requests THEN the DETSMS SHALL implement asynchronous processing where appropriate


