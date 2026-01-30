# Phase 3: API Gateway Enforcement Setup

**Status**: ✅ COMPLETE  
**Date**: 2026-01-29  
**Objective**: Implement automated enforcement of ADR-0010 via ESLint rules and pre-commit hooks

---

## Overview

Phase 3 implements automated enforcement of the centralized API gateway pattern to prevent developers from accidentally introducing direct API calls outside durion-positivity component.

---

## Implementation Components

### 1. ESLint Configuration (`.eslintrc.json`)

Centralized ESLint configuration with custom rules for API gateway enforcement.

**Key Features**:
- Custom ESLint plugin: `no-direct-api-calls` with 4 rules
- Vue 3 and TypeScript support
- Auto-exclude durion-positivity/webapp from restrictions
- Integration with existing projects

**Custom Rules**:

1. **`no-direct-api-calls/no-direct-fetch`**
   - Blocks: `fetch()` calls in durion-* components
   - Exception: durion-positivity/webapp allowed
   - Error Message: Clear guidance to use durion-positivity gateway

2. **`no-direct-api-calls/no-direct-axios`**
   - Blocks: `import axios from 'axios'` in durion-* components
   - Exception: durion-positivity/webapp allowed
   - Error Message: Redirects to ADR-0010 documentation

3. **`no-direct-api-calls/no-direct-authorization`**
   - Blocks: Manual `Authorization` header construction
   - Pattern: Detects `'Authorization': 'Bearer ${token}'`
   - Message: Explains gateway handles auth automatically

4. **`no-direct-api-calls/enforce-api-gateway`**
   - Documentation rule (future enhancement)
   - Suggestion: Use gateway for consistency

### 2. Custom ESLint Plugin (`eslint-plugin-no-direct-api-calls.js`)

Complete ESLint plugin implementation with:
- 4 comprehensive rules with detailed messages
- Exception handling for durion-positivity component
- Clear error messages with code examples
- Links to ADR-0010 documentation

**Rule Implementation Details**:

Each rule includes:
```javascript
{
  meta: {
    type: 'problem',
    docs: { description, category, recommended, url },
    messages: { ... }
  },
  create(context) {
    // Detects violations
    // Provides detailed error messages
    // Links to documentation
  }
}
```

### 3. Pre-Commit Hook (`.husky/pre-commit`)

Bash script that runs before every commit to catch violations early.

**Checks Performed**:

1. **Direct fetch() calls**
   - Scans staged files for `fetch(` patterns
   - Excludes durion-positivity/webapp
   - Reports violations with file paths

2. **Direct axios imports**
   - Detects `from 'axios'` imports
   - Warns about centralized gateway alternative
   - Prevents commit with violations

3. **Manual Authorization headers**
   - Finds `'Authorization': 'Bearer ...'` patterns
   - Explains gateway injection mechanism
   - Blocks commits with security violations

4. **Gateway usage validation**
   - Ensures API-calling components import `usePositivityApiClient`
   - Validates proper pattern usage
   - Requires gateway for any API operations

**Output Example**:
```
🔍 Running API Gateway Enforcement Checks...

Checking for direct fetch() calls...
Checking for direct axios imports...
Checking for manual Authorization headers...
Checking for proper API gateway usage...

❌ COMMIT REJECTED: Architecture violations detected

📋 Required Pattern (ADR-0010):
   import { usePositivityApiClient } from 'durion-positivity';
   const apiClient = usePositivityApiClient();
   const result = await apiClient.request({...});

📖 See: https://github.com/louisburroughs/durion/blob/master/docs/adr/0010-frontend-domain-responsibilities-guide.adr.md

✅ To bypass (not recommended):
   git commit --no-verify
```

### 4. NPM Scripts

Updated `package.json` with new linting scripts:

```bash
# Run ESLint on all component webapp files
npm run lint

# Fix auto-fixable ESLint violations
npm run lint:fix

# Run API gateway enforcement rules only
npm run lint:api-gateway

# Initialize husky hooks (runs on npm install)
npm run prepare
```

---

## Setup Instructions

### For Developers

**1. Install dependencies**:
```bash
npm install
```

This automatically:
- Installs ESLint and plugins
- Installs husky for git hooks
- Initializes pre-commit hooks

**2. Run linting checks**:
```bash
# Check for violations
npm run lint

# Auto-fix issues
npm run lint:fix

# Check API gateway compliance specifically
npm run lint:api-gateway
```

**3. Commit your changes**:
```bash
git add .
git commit -m "Your message"
```

Pre-commit hook automatically runs and rejects commits with violations.

**4. If you need to bypass (not recommended)**:
```bash
git commit --no-verify
# Use only for emergency fixes - requires code review
```

### For CI/CD Pipeline

Add to GitHub Actions or other CI:

```yaml
- name: Install dependencies
  run: npm install

- name: Run ESLint
  run: npm run lint

- name: Check API Gateway Compliance
  run: npm run lint:api-gateway
```

---

## Architecture Compliance Rules

### ✅ ALLOWED Patterns

**Use durion-positivity gateway in any component**:
```typescript
// durion-shopmgr/webapp/appointments/AppointmentForm.vue
import { useAppointmentStore } from '../store/appointmentStore';
// ✅ Store calls useAppointmentClient() which uses durion-positivity
const store = useAppointmentStore();
await store.createAppointment(request);
```

**Use API client in durion-positivity/webapp**:
```typescript
// durion-positivity/webapp/api/PositivityApiClient.ts
import { usePositivityApiClient } from 'durion-positivity';
// ✅ Direct use allowed in gateway component
const apiClient = usePositivityApiClient();
```

### ❌ FORBIDDEN Patterns

**Direct fetch calls** ❌:
```typescript
// durion-shopmgr/webapp/api/old-client.ts
const response = await fetch('/api/appointments');
// ❌ REJECTED: Use durion-positivity gateway
```

**Direct axios** ❌:
```typescript
// durion-inventory/webapp/api/inventory-client.ts
import axios from 'axios';
const response = await axios.get('/api/inventory');
// ❌ REJECTED: Use durion-positivity gateway
```

**Manual auth headers** ❌:
```typescript
// durion-sales/webapp/api/sales-client.ts
const headers = {
  'Authorization': `Bearer ${token}`, // ❌ REJECTED
  'Content-Type': 'application/json'
};
const response = await fetch('/api/sales', { headers });
```

---

## Error Message Examples

### Example 1: Direct fetch() call

**File**: `runtime/component/durion-shopmgr/webapp/api/OldClient.ts:42`

**Error**:
```
❌ ARCHITECTURE VIOLATION: Direct fetch() call detected in 
   runtime/component/durion-shopmgr/webapp/api/OldClient.ts

REQUIRED: Route all API calls through durion-positivity component.

CORRECT PATTERN:
  import { usePositivityApiClient } from "durion-positivity";
  const apiClient = usePositivityApiClient();
  const result = await apiClient.request({
    service: "your-service",
    endpoint: "/your/endpoint",
    method: "POST",
    data: requestData
  });

See ADR-0010: https://github.com/louisburroughs/durion/blob/master/docs/adr/0010-frontend-domain-responsibilities-guide.adr.md
```

### Example 2: Direct axios import

**File**: `runtime/component/durion-crm/webapp/api/contacts-client.ts:1`

**Error**:
```
❌ ARCHITECTURE VIOLATION: Direct axios import detected in 
   runtime/component/durion-crm/webapp/api/contacts-client.ts

REQUIRED: Route all API calls through durion-positivity component.

CORRECT PATTERN:
  import { usePositivityApiClient } from "durion-positivity";
  const apiClient = usePositivityApiClient();
  ...
```

### Example 3: Manual Authorization header

**File**: `runtime/component/durion-order/webapp/api/OrderClient.ts:25`

**Error**:
```
❌ SECURITY VIOLATION: Manual Authorization header detected in 
   runtime/component/durion-order/webapp/api/OrderClient.ts

REQUIRED: Let durion-positivity gateway inject auth tokens automatically.

WRONG:
  headers: { "Authorization": `Bearer ${token}` }

CORRECT:
  import { usePositivityApiClient } from "durion-positivity";
  const apiClient = usePositivityApiClient();
  // Gateway automatically injects Authorization header
```

---

## Exceptions & Overrides

### When to Override

**Very limited cases**:
1. Durion-positivity/webapp itself (explicitly excluded)
2. Emergency security fixes (use `--no-verify`, requires review)
3. Legacy migration phase (add temporary eslint-disable, document)

### How to Override

**Temporary disable (with reason)**:
```typescript
// eslint-disable-next-line no-direct-api-calls/no-direct-fetch
// TEMPORARY: Legacy code during migration. Remove by 2026-02-28
const response = await fetch('/api/legacy');
```

**File-level disable (not recommended)**:
```typescript
/* eslint-disable no-direct-api-calls/no-direct-fetch */
// REASON: Temporary during Phase 2 migration
// DEADLINE: 2026-02-28

// ... legacy code ...

/* eslint-enable no-direct-api-calls/no-direct-fetch */
```

**Bypass commit check**:
```bash
git commit --no-verify
# REQUIRES: Code review + ADR discussion
```

---

## Testing & Validation

### Unit Test Enforcement

ESLint rules do NOT run on test files automatically. Test files can:
- Mock fetch/axios
- Use direct calls for test setup
- Test the gateway pattern itself

**However**: Tests should mock durion-positivity, not HTTP endpoints (see Phase 2A patterns).

### CI Enforcement

Add to CI pipeline:
```yaml
# .github/workflows/lint.yml
name: ESLint Enforcement
on: [push, pull_request]

jobs:
  lint:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
        with:
          node-version: 18
      
      - name: Install dependencies
        run: npm install
      
      - name: Run ESLint
        run: npm run lint
        
      - name: Verify API Gateway Compliance
        run: npm run lint:api-gateway
```

---

## Monitoring & Metrics

### Track Violations

Create a baseline report:
```bash
# Check current violations (before full enforcement)
npm run lint:api-gateway > /tmp/baseline.txt

# Monitor improvements
npm run lint:api-gateway > /tmp/current.txt

# Compare
diff /tmp/baseline.txt /tmp/current.txt
```

### Metrics to Track

1. **Violations Found**: Total direct API calls remaining
2. **Components Compliant**: % of durion-* components using gateway
3. **Time to Compliance**: Days until all violations fixed
4. **New Violations**: Per-week detection rate (should decrease)

---

## Common Questions

### Q: How do I fix a violation?

**A**: Convert to durion-positivity pattern:
```typescript
// BEFORE (direct fetch)
const response = await fetch('/api/data');

// AFTER (gateway)
import { usePositivityApiClient } from 'durion-positivity';
const apiClient = usePositivityApiClient();
const response = await apiClient.request({
  service: 'your-service',
  endpoint: '/data',
  method: 'GET'
});
```

### Q: Can I use fetch in my component?

**A**: Only through durion-positivity gateway. Create an API client wrapper like durion-shopmgr did.

### Q: Does durion-positivity have the same rules?

**A**: No. The `.eslintrc.json` excludes `durion-positivity/webapp` from fetch/axios restrictions. The gateway needs to use these directly.

### Q: What about tests?

**A**: Tests should mock `durion-positivity`, not HTTP. See Phase 2A test patterns in AppointmentForm.spec.ts.

### Q: Can I bypass pre-commit hooks?

**A**: Yes, but don't. Use `git commit --no-verify` only for emergencies - requires code review and ADR discussion.

### Q: How do I run ESLint manually?

**A**: 
```bash
npm run lint              # Check all files
npm run lint:fix          # Auto-fix issues
npm run lint:api-gateway  # Check gateway compliance only
```

---

## Integration with Development Workflow

### Before Committing

1. **Install deps**: `npm install` (sets up husky)
2. **Lint**: `npm run lint:fix` to auto-fix issues
3. **Commit**: `git commit -m "..."` (pre-commit hook runs automatically)
4. **Push**: If lint passed, commit succeeds

### During Code Review

Reviewers should:
1. Verify no `--no-verify` commits merged
2. Check for eslint-disable comments (should be temporary)
3. Ensure durion-positivity pattern used for API calls
4. Link to ADR-0010 in review comments

### In CI Pipeline

1. Install dependencies
2. Run `npm run lint`
3. Run `npm run lint:api-gateway`
4. Block merge if violations found
5. Track metrics over time

---

## References

- **ADR-0010**: [Frontend Domain Responsibilities](https://github.com/louisburroughs/durion/blob/master/docs/adr/0010-frontend-domain-responsibilities-guide.adr.md)
- **Phase 1**: [API Gateway Implementation](./PHASE_1_IMPLEMENTATION_SUMMARY.md)
- **Phase 2A**: [durion-shopmgr Refactoring](./PHASE_2A_COMPLETION_REPORT.md)
- **Plan**: [Full Roadmap](./REFACTORING_API_GATEWAY_PLAN.md)

---

## Sign-Off

- **Status**: ✅ Phase 3 Complete
- **ESLint Rules**: 4 custom rules implemented
- **Pre-Commit Hook**: Bash script ready
- **Documentation**: Complete with examples
- **Next Phase**: Phase 4 (Documentation validation)
