# API Gateway Enforcement Configuration Guide

**Quick Setup**: 5 minutes  
**Reference**: Phase 3 of API Refactoring  
**Learn More**: [PHASE_3_ENFORCEMENT_SETUP.md](./PHASE_3_ENFORCEMENT_SETUP.md)

---

## What Is This?

Phase 3 implements automated enforcement of ADR-0010 (Frontend Domain Responsibilities) via:
- **ESLint rules**: Catch violations during development
- **Pre-commit hooks**: Prevent committing violations
- **CI checks**: Verify all code meets standards

**Goal**: Prevent direct API calls outside durion-positivity component.

---

## Quick Start (5 minutes)

### Step 1: Install

```bash
# From durion-moqui-frontend root
npm install

# This automatically:
# - Installs ESLint, TypeScript, Vue plugins
# - Installs husky for git hooks
# - Sets up pre-commit enforcement
```

### Step 2: Check Compliance

```bash
# Run linting check
npm run lint

# This reports all violations with clear error messages
# and links to documentation
```

### Step 3: Fix Issues

```bash
# Auto-fix auto-fixable violations
npm run lint:fix

# Manual fixes required for some violations
# (pre-commit hook shows what needs fixing)
```

### Step 4: Commit

```bash
git add .
git commit -m "Fix API gateway violations"

# Pre-commit hook automatically:
# - Checks for direct fetch() calls
# - Checks for direct axios imports  
# - Checks for manual auth headers
# - Verifies gateway usage
# 
# Commit rejected if violations found
```

---

## What Gets Blocked?

### ❌ Direct fetch() calls

```typescript
// BLOCKED ❌
const response = await fetch('/api/data');

// ALLOWED ✅
import { usePositivityApiClient } from 'durion-positivity';
const apiClient = usePositivityApiClient();
const response = await apiClient.request({
  service: 'pos-service',
  endpoint: '/data',
  method: 'GET'
});
```

### ❌ Direct axios imports

```typescript
// BLOCKED ❌
import axios from 'axios';
const response = await axios.get('/api/data');

// ALLOWED ✅
// Use durion-positivity gateway (see above)
```

### ❌ Manual Authorization headers

```typescript
// BLOCKED ❌
const headers = {
  'Authorization': `Bearer ${token}`,
  'Content-Type': 'application/json'
};
const response = await fetch('/api/data', { headers });

// ALLOWED ✅
// Gateway injects auth automatically
const apiClient = usePositivityApiClient();
const response = await apiClient.request({...});
```

---

## NPM Scripts

### Linting

```bash
# Check all files
npm run lint

# Auto-fix issues
npm run lint:fix

# Check API gateway compliance only
npm run lint:api-gateway
```

### Running Tests

```bash
# Tests don't trigger API gateway rules
npm test

# Tests should mock durion-positivity, not HTTP
# See Phase 2A patterns for examples
```

---

## Common Tasks

### I have a violation. How do I fix it?

1. **Read the error message** - It explains the issue and solution
2. **Check the example** - See correct pattern in error message
3. **Look at Phase 2A** - durion-shopmgr shows complete working example
4. **Run lint:fix** - Auto-fix what can be fixed automatically

Example error:
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
```

### I need to add a new API endpoint

Follow the durion-shopmgr pattern:

**Step 1**: Create API client wrapper
```typescript
// your-component/webapp/api/YourClient.ts
import { usePositivityApiClient } from 'durion-positivity';

export function useYourClient() {
  const apiClient = usePositivityApiClient();
  
  async function fetchData(id: string) {
    return await apiClient.request({
      service: 'pos-your-service',
      endpoint: `/data/${id}`,
      method: 'GET'
    });
  }
  
  return { fetchData };
}
```

**Step 2**: Create Pinia store (optional but recommended)
```typescript
// your-component/webapp/store/yourStore.ts
import { defineStore } from 'pinia';
import { useYourClient } from '../api/YourClient';

export const useYourStore = defineStore('your-feature', () => {
  const client = useYourClient();
  // State management, actions, etc.
});
```

**Step 3**: Use in component
```typescript
// your-component/webapp/YourComponent.vue
const store = useYourStore();
await store.doSomething();
```

**Step 4**: Add tests
```typescript
// your-component/webapp/YourComponent.spec.ts
jest.mock('durion-positivity', () => ({
  usePositivityApiClient: () => ({
    request: jest.fn()
  })
}));
```

See [Phase 2A Completion Report](./PHASE_2A_COMPLETION_REPORT.md) for full example.

### I get violations pre-commit. How do I proceed?

```bash
# Option 1: Fix the violations (recommended)
npm run lint:fix
git add .
git commit -m "Fix API gateway violations"

# Option 2: Skip hook (only for emergencies)
git commit --no-verify
# Requires code review + ADR discussion
```

### How do I test if my code complies?

```bash
# Run full lint check
npm run lint

# Run API gateway check specifically
npm run lint:api-gateway

# Simulate pre-commit (before committing)
bash .husky/pre-commit
```

### Can I disable a rule?

Only as last resort. All disables must be temporary + documented.

```typescript
// ESLint disable with reason (temporary)
// eslint-disable-next-line no-direct-api-calls/no-direct-fetch
// TODO: Migrate to gateway API by 2026-02-28 (ADR-0010)
const response = await fetch('/api/legacy');
```

### What about tests?

Tests are excluded from enforcement rules. However, they should mock `durion-positivity`, not HTTP:

```typescript
// ✅ CORRECT: Mock the gateway
jest.mock('durion-positivity', () => ({
  usePositivityApiClient: () => ({
    request: jest.fn().mockResolvedValue({ id: 123 })
  })
}));

// ❌ WRONG: Mock HTTP endpoint
jest.mock('fetch', () => jest.fn());
```

---

## Troubleshooting

### ESLint not installed

```bash
npm install
# Reinstalls all dependencies
```

### Pre-commit hook not running

```bash
# Setup husky manually
npx husky install

# Or re-install everything
npm install
```

### Can't run npm scripts

```bash
# Make sure you're in durion-moqui-frontend directory
cd /path/to/durion-moqui-frontend

# Try reinstalling
rm -rf node_modules package-lock.json
npm install
```

### ESLint showing false positives

1. Check `.eslintrc.json` configuration
2. Verify `eslint-plugin-no-direct-api-calls.js` exists
3. Run: `npm run lint -- --debug`
4. See [PHASE_3_ENFORCEMENT_SETUP.md](./PHASE_3_ENFORCEMENT_SETUP.md) for details

### Pre-commit hook has wrong behavior

```bash
# Check hook script
cat .husky/pre-commit

# Make executable
chmod +x .husky/pre-commit

# Test manually
bash .husky/pre-commit
```

---

## Documentation

- **Full Setup Guide**: [PHASE_3_ENFORCEMENT_SETUP.md](./PHASE_3_ENFORCEMENT_SETUP.md)
- **Architecture Decision**: [ADR-0010](../docs/adr/0010-frontend-domain-responsibilities-guide.adr.md)
- **Phase 1 (Gateway)**: [PHASE_1_IMPLEMENTATION_SUMMARY.md](./PHASE_1_IMPLEMENTATION_SUMMARY.md)
- **Phase 2A (Refactoring)**: [PHASE_2A_COMPLETION_REPORT.md](./PHASE_2A_COMPLETION_REPORT.md)
- **Full Roadmap**: [REFACTORING_API_GATEWAY_PLAN.md](./REFACTORING_API_GATEWAY_PLAN.md)

---

## Quick Reference

| Task | Command |
|------|---------|
| Check compliance | `npm run lint` |
| Fix auto-fixable | `npm run lint:fix` |
| Check gateway rules | `npm run lint:api-gateway` |
| Simulate pre-commit | `bash .husky/pre-commit` |
| Install/setup | `npm install` |
| Run tests | `npm test` |

---

## Support

For questions or issues:
1. Check [PHASE_3_ENFORCEMENT_SETUP.md](./PHASE_3_ENFORCEMENT_SETUP.md) FAQ
2. See error message link to ADR-0010
3. Review Phase 2A example in durion-shopmgr
4. Consult team lead if unsure

Remember: The goal is to improve consistency and observability, not to be restrictive. When in doubt, ask!
