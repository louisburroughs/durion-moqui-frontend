# Project Structure

## Root Directory Layout

```
moqui_example/
├── framework/              # Moqui Framework core
├── runtime/               # Runtime environment and components
├── docker/                # Docker configurations and scripts
├── scripts/               # Utility scripts
├── gradle/                # Gradle wrapper files
├── .kiro/                 # Kiro AI assistant configuration
├── build.gradle           # Root build configuration
├── settings.gradle        # Multi-project settings
└── MoquiInit.properties   # Runtime initialization
```

## Framework Directory

**Purpose**: Core Moqui Framework implementation
- `framework/src/` - Java/Groovy source code
- `framework/entity/` - Core entity definitions (XML)
- `framework/service/` - Core service implementations
- `framework/screen/` - Framework-level screens
- `framework/data/` - Seed data and configuration
- `framework/xsd/` - XML Schema definitions

## Runtime Directory

**Purpose**: Application runtime environment and business components

### Key Subdirectories
- `runtime/component/` - Business domain components
- `runtime/conf/` - Configuration files (Dev, Production, LoadTest)
- `runtime/db/` - Database files (H2, Derby)
- `runtime/log/` - Application logs
- `runtime/lib/` - Additional libraries
- `runtime/elasticsearch/` - Search engine installation

### Component Architecture

Each component follows standard structure:
```
runtime/component/{component-name}/
├── component.xml          # Component configuration
├── entity/               # Entity definitions
├── service/              # Service implementations  
├── screen/               # UI screens and forms
├── data/                 # Seed/demo data
├── template/             # Templates (FTL, etc)
├── src/                  # Java/Groovy source
└── build.gradle          # Component build config
```

## Business Components

### Core Components
- **mantle-udm** - Universal Data Model (foundational entities)
- **mantle-usl** - Universal Service Library (reusable services)
- **SimpleScreens** - UI components and dashboards

### Domain Components  
- **PopCommerce** - E-commerce (orders, products, inventory)
- **HiveMind** - Project management and collaboration
- **MarbleERP** - Manufacturing and resource planning
- **example** - Framework examples and tutorials

### Custom Components (Durion Suite)
- **durion-common** - Shared utilities and base functionality
- **durion-crm** - Customer relationship management
- **durion-accounting** - Financial and accounting features
- **durion-inventory** - Inventory management
- **durion-product** - Product catalog management
- **durion-workexec** - Work execution and scheduling
- **durion-experience** - User experience enhancements
- **durion-positivity** - Integration and data positivity layer
- **durion-theme** - UI theming and styling
- **durion-demo-data** - Demo data for development

## Docker Configuration

**Purpose**: Containerization and deployment configurations
- `docker/simple/` - Basic Docker setup
- `docker/certs/` - SSL certificates for development
- `docker/nginx/` - Reverse proxy configuration
- `docker/*-compose.yml` - Multi-service orchestration

## File Naming Conventions

### XML Files
- **Entities**: `{Domain}Entities.xml` (e.g., `OrderEntities.xml`)
- **Services**: `{Domain}Services.xml` 
- **Screens**: `{ScreenName}.xml`
- **Configuration**: `component.xml`, `MoquiConf.xml`

### Gradle Files
- **Root**: `build.gradle`, `settings.gradle`
- **Component**: `build.gradle` (in component root)
- **Properties**: `gradle.properties`

### Data Files
- **Seed Data**: `{Domain}Data.xml`
- **Demo Data**: `{Domain}DemoData.xml`
- **Type Data**: `{Domain}TypeData.xml`

## Configuration Hierarchy

### Build Configuration
1. `build.gradle` (root) - Master build configuration
2. `settings.gradle` - Project structure definition
3. `gradle.properties` - Build environment settings
4. Component `build.gradle` files - Component-specific builds

### Runtime Configuration
1. `MoquiInit.properties` - Initial runtime settings
2. `runtime/conf/MoquiDevConf.xml` - Development configuration
3. `runtime/conf/MoquiProductionConf.xml` - Production configuration
4. Component `component.xml` files - Component dependencies

### Component Dependencies
- Components declare dependencies in `component.xml`
- Dependency resolution follows topological order
- Core components (mantle-udm, mantle-usl) have no dependencies
- UI components (SimpleScreens) depend on core components
- Domain components depend on UI and core components

## Development Patterns

### Adding New Components
1. Create directory under `runtime/component/`
2. Add `component.xml` with dependencies
3. Follow standard directory structure
4. Add to `settings.gradle` if building separately

### Entity Development
- Define entities in `entity/` directory
- Use proper package naming: `{domain}.{subdomain}`
- Include relationships and seed data
- Follow Moqui entity conventions

### Service Development  
- Implement in `service/` directory
- Use XML service definitions
- Follow naming: `{domain}.{service-type}#{Action}`
- Include proper error handling and validation

### Screen Development
- Create in `screen/` directory
- Use XML screen definitions
- Follow UI patterns from SimpleScreens
- Include proper security and validation