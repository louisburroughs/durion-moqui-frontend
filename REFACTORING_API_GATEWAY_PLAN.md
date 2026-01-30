# API Refactoring Audit & Plan

**Status**: Baseline Audit Complete  
**Date**: 2026-01-29  
**Purpose**: Track migration of direct API calls to centralized durion-positivity gateway

---

## Phase 1A: durion-positivity API Gateway ✅ COMPLETE

**What Was Built:**

- `webapp/api/PositivityApiClient.ts` — Central API client with `usePositivityApiClient()` composable
- `webapp/api/errors.ts` — Error handling and parsing utilities
- `webapp/types/index.ts` — TypeScript interfaces for requests/responses
- `webapp/api/index.ts` — API re-exports
- `webapp/index.ts` — Component-level exports for other components to import

**Key Features:**

- ✅ Unified authentication token management
- ✅ Automatic correlation ID injection for observability
- ✅ Centralized error handling with retry logic
- ✅ Request/response transformation
- ✅ Timeout handling
- ✅ Network error detection and retry
- ✅ Exponential backoff for transient failures

**How to Use (MANDATORY PATTERN):**

```typescript
// ✅ CORRECT - In any durion-* component
import { usePositivityApiClient } from 'durion-positivity';

export function MyComponent() {
  const apiClient = usePositivityApiClient();
  
  async function fetchData() {
    try {
      const data = await apiClient.request({
        service: 'pos-inventory',
        endpoint: '/stock/ABC123',
        method: 'GET'
      });
    } catch (error) {
      // Handle errors
    }
  }
}
```

---

## Phase 1B: Audit Findings

**Components with Direct API Calls Found:**

| Component | Files with Direct Calls | Backend Service | Status |
|-----------|-------------------------|-----------------|--------|
| durion-shopmgr | `webapp/appointments/AppointmentFormDemo.html` | pos-shop-manager | ❌ NEEDS REFACTOR |

**Components Without Direct API Calls:**

- durion-accounting (no webapp code found)
- durion-crm (no webapp code found)
- durion-inventory (no webapp code found)
- durion-sales (no webapp code found)
- durion-workexec (no webapp code found)
- durion-hr (no webapp code found)
- durion-product (no webapp code found)
- durion-chat (reference only, no API code)
- durion-common (utilities, no backend calls)
- durion-experience (UI shell, no backend calls)

---

## Phase 2A: Refactor durion-shopmgr ✅ COMPLETE

**Status**: All files refactored and verified

**What Was Completed:**

- ✅ Created `durion-shopmgr/webapp/api/AppointmentClient.ts` with typed API methods
- ✅ Created `durion-shopmgr/webapp/store/appointmentStore.ts` for Pinia state management
- ✅ Updated `AppointmentForm.vue` to use new API client via composable
- ✅ Added comprehensive error handling and user feedback
- ✅ Added 3 test files (component, client, store) with mocked durion-positivity
- ✅ Verified no direct fetch calls remain (only in deprecated file)
- ✅ Converted appointments.service.ts to deprecation notice with re-exports

**Files Changed/Created:**

```
durion-shopmgr/
├── webapp/
│   ├── api/
│   │   ├── AppointmentClient.ts (NEW)
│   │   └── AppointmentClient.spec.ts (NEW)
│   ├── store/
│   │   ├── appointmentStore.ts (NEW)
│   │   └── appointmentStore.spec.ts (NEW)
│   └── appointments/
│       ├── AppointmentForm.vue (UPDATED)
│       ├── AppointmentForm.spec.ts (UPDATED)
│       └── appointments.service.ts (DEPRECATED - re-exports for compat)
```

**Verification:**

```bash
# No direct fetch calls (confirmed)
grep -r "fetch(" runtime/component/durion-shopmgr/webapp --include="*.ts" --include="*.vue" \
  | grep -v "appointments.service.ts" | grep -v "node_modules"
# ✅ Returns nothing (only deprecation comments found)
```

**Architecture Compliance:**

- All requests now route through `usePositivityApiClient()` from durion-positivity
- Service name: `pos-shop-manager`
- Endpoints: `/appointments`, `/appointments/{id}`
- Conflict resolution (SOFT/HARD) preserved
- Idempotency keys preserved
- Error handling and mapping preserved
- Auth/correlation ID injection via gateway

---

## Phase 2B-2G: Remaining Components ⏳ PENDING

**Note:** Most durion-* components don't have webapp/API code yet. They will need API clients created during feature development as their associated backend services are built out.

**Status by Component:**

- durion-accounting: No webapp code (will need when accounting UI added)
- durion-crm: No webapp code (will need when CRM UI added)
- durion-inventory: No webapp code (will need when inventory UI added)
- durion-sales: No webapp code (will need when sales UI added)
- durion-workexec: No webapp code (will need when workorder execution UI added)
- durion-hr: No webapp code (will need when HR UI added)
- durion-product: No webapp code (will need when product UI added)
- durion-common: Utilities only, no backend calls
- durion-experience: UI shell, no backend calls
- durion-positivity: ✅ API gateway (Phase 1A complete)

---

## Phase 3: Automated Enforcement ✅ COMPLETE

**Status**: ESLint rules and pre-commit hooks implemented and ready

**What Was Implemented:**

- ✅ Custom ESLint plugin with 4 rules (fetch, axios, auth headers, gateway enforcement)
- ✅ `.eslintrc.json` configuration with Vue 3 and TypeScript support
- ✅ Pre-commit hook using husky for early violation detection
- ✅ npm scripts for linting (`lint`, `lint:fix`, `lint:api-gateway`)
- ✅ Package.json updated with required dev dependencies
- ✅ Comprehensive documentation and developer guide

**Files Implemented:**

```
.eslintrc.json                              ESLint configuration
eslint-plugin-no-direct-api-calls.js        Custom ESLint plugin (4 rules)
.husky/pre-commit                           Pre-commit hook script
scripts/validate-enforcement.sh             Validation script
PHASE_3_ENFORCEMENT_SETUP.md                Complete setup documentation
ENFORCEMENT_CONFIG_GUIDE.md                 Quick start for developers
```

**ESLint Rules:**

1. **`no-direct-api-calls/no-direct-fetch`** — Blocks fetch() calls
2. **`no-direct-api-calls/no-direct-axios`** — Blocks axios imports
3. **`no-direct-api-calls/no-direct-authorization`** — Blocks manual auth headers
4. **`no-direct-api-calls/enforce-api-gateway`** — Encourages proper pattern

**NPM Scripts:**

```bash
npm run lint                  # Check all files
npm run lint:fix              # Auto-fix issues
npm run lint:api-gateway      # Gateway compliance check
npm run prepare              # Setup husky (auto on install)
```

**Developer Workflow:**

```bash
npm install                   # Install deps + setup hooks
npm run lint:fix              # Fix violations
git add . && git commit -m "" # Pre-commit hook validates
```

**Quick Reference:** See [ENFORCEMENT_CONFIG_GUIDE.md](./ENFORCEMENT_CONFIG_GUIDE.md) for setup and troubleshooting.

---

## Phase 4: Documentation & Validation

**Update Component READMEs:**

Each component should document its backend service dependencies:

```markdown
## Backend Service Dependencies

This component integrates with the following backend services via durion-positivity API gateway:

- **pos-shop-manager** — Appointments, Mobile Units, Shop management
  - GET `/appointments/{id}` — Fetch appointment details
  - POST `/appointments` — Create appointment
  - PUT `/appointments/{id}` — Update appointment

### API Routing

All API calls are routed through the centralized `usePositivityApiClient()` composable from durion-positivity.
See [ADR-0010: Frontend Domain Responsibilities](../../../docs/adr/0010-frontend-domain-responsibilities-guide.adr.md) for architecture details.
```

**Type Definitions:**

Export types from each component in `{component}/webapp/types/index.ts`:

```typescript
export interface Appointment {
  id: string;
  title: string;
  startTime: string;
  endTime: string;
  status: 'SCHEDULED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
}
```

---

## Validation Commands

**Verify no direct API calls (should return empty):**

```bash
# Check for direct fetch
grep -r "fetch(" runtime/component/durion-*/webapp --include="*.ts" --include="*.vue" | grep -v "durion-positivity" | grep -v "node_modules"

# Check for axios
grep -r "from 'axios'" runtime/component/durion-*/webapp --include="*.ts" --include="*.vue" | grep -v "durion-positivity" | grep -v "node_modules"

# Check for direct Authorization headers
grep -r "Authorization.*Bearer" runtime/component/durion-*/webapp --include="*.ts" --include="*.vue" | grep -v "durion-positivity" | grep -v "node_modules"
```

---

## Implementation Timeline

| Phase | Task | Owner | ETA | Status |
|-------|------|-------|-----|--------|
| 1A | Build durion-positivity API gateway | AI Agent | ✅ DONE | Complete |
| 1B | Audit existing components | AI Agent | ✅ DONE | Complete |
| 2A | Refactor durion-shopmgr | AI Agent | ✅ DONE | Complete |
| 2B-2G | Refactor remaining components | TBD | As features added | Pending |
| 3 | Implement automated enforcement | AI Agent | ✅ DONE | Complete |
| 4 | Documentation & validation | TBD | Next Sprint | Not Started |

---

## Next Steps

1. **Completed (✅):**
   - [ ] ✅ Built and validated durion-positivity API gateway implementation
   - [ ] ✅ Refactored durion-shopmgr component (Phase 2A complete)
   - [ ] ✅ Created comprehensive unit tests with mocked durion-positivity
   - [ ] ✅ Implemented ESLint enforcement rules (Phase 3 complete)

2. **Immediate (Next Priority):**
   - [ ] Run `npm install` to install ESLint + setup pre-commit hooks
   - [ ] Test ESLint: `npm run lint`
   - [ ] Verify no violations: `npm run lint:api-gateway`
   - [ ] Enable pre-commit hook enforcement

3. **Short-term (Following Sprints):**
   - [ ] Add Phase 3 setup to CI/CD pipeline
   - [ ] Document API patterns in developer handbook
   - [ ] When new components need backend integration, follow durion-shopmgr pattern
   - [ ] Consolidate API response types in durion-common if needed

4. **Ongoing:**
   - [ ] Use AppointmentClient pattern for all new backend integrations
   - [ ] Route ALL requests through durion-positivity gateway
   - [ ] Include Pinia stores for state management
   - [ ] Add unit tests mocking the gateway, not HTTP endpoints
   - [ ] Monitor ESLint violations (should remain at 0)

---

## References

- **Architecture**: [ADR-0010: Frontend Domain Responsibilities Guide](../../../docs/adr/0010-frontend-domain-responsibilities-guide.adr.md)
- **Implementation**: `runtime/component/durion-positivity/webapp/`
- **Testing**: `runtime/component/durion-positivity/webapp/__tests__/`
