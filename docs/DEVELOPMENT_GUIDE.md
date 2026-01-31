# Durion Moqui Frontend Development Guide

**Last Updated**: 2026-01-31  
**Stack**: Moqui Framework, Java 11, Groovy, Vue.js 3, Quasar, TypeScript

---

## Quick Start

### Prerequisites

- Java 11+ (for Moqui runtime)
- Node.js 18+ / npm (for frontend tooling)
- Gradle (`./gradlew`) for runtime builds
- Docker + Docker Compose (optional, for local stacks)

### Setup

```bash
cd durion-moqui-frontend

# Install UI/tooling dependencies
npm install

# Build runtime and assets (skip tests for fast iteration)
./gradlew build -x test
```

---

## Build Commands

### Gradle Builds

```bash
# Fast development build (skip tests)
./gradlew build -x test

# Full build with tests
./gradlew build

# Clean build
./gradlew clean build
```

### Frontend Tooling

```bash
# Install dependencies (also sets up git hooks)
npm install

# Run linting
npm run lint

# Auto-fix linting issues
npm run lint:fix

# Check API gateway compliance
npm run lint:api-gateway

# Run tests
npm test
```

---

## API Gateway Enforcement

### ESLint Rules

Four custom rules enforce the API gateway pattern:

1. **`no-direct-api-calls/no-direct-fetch`** — Blocks direct `fetch()` calls
2. **`no-direct-api-calls/no-direct-axios`** — Blocks axios imports
3. **`no-direct-api-calls/no-direct-authorization`** — Blocks manual auth headers
4. **`no-direct-api-calls/enforce-api-gateway`** — Encourages proper pattern

### Pre-Commit Hooks

Pre-commit hooks automatically validate code before commits:

```bash
git add .
git commit -m "Your message"
# Pre-commit hook runs automatically
```

If violations are found, the commit is rejected with clear guidance.

### Bypass (Emergency Only)

```bash
git commit --no-verify
# REQUIRES: Code review + ADR discussion
```

### Temporary Disable

```typescript
// eslint-disable-next-line no-direct-api-calls/no-direct-fetch
// TEMPORARY: Legacy code during migration. Remove by 2026-02-28
const response = await fetch('/api/legacy');
```

---

## Creating a New API Client

Follow the durion-shopmgr pattern:

### Step 1: Create API Client

```typescript
// your-component/webapp/api/YourClient.ts
import { usePositivityApiClient } from 'durion-positivity';
import type { YourData, YourFilter } from '../types';

export class YourClient {
  private apiClient = usePositivityApiClient();

  async listData(filters?: YourFilter): Promise<YourData[]> {
    return this.apiClient.request({
      service: 'pos-your-service',
      endpoint: '/list',
      method: 'GET',
      data: filters
    });
  }

  async getData(id: string): Promise<YourData> {
    return this.apiClient.request({
      service: 'pos-your-service',
      endpoint: `/${id}`,
      method: 'GET'
    });
  }

  async createData(data: Partial<YourData>): Promise<YourData> {
    return this.apiClient.request({
      service: 'pos-your-service',
      endpoint: '/',
      method: 'POST',
      data
    });
  }
}

export const yourClient = new YourClient();
```

### Step 2: Create Composable

```typescript
// your-component/webapp/composables/useYourData.ts
import { ref, onMounted, computed } from 'vue';
import { yourClient } from '../api/YourClient';

export function useYourData() {
  const data = ref<YourData[]>([]);
  const loading = ref(false);
  const error = ref<string | null>(null);

  async function fetchData() {
    loading.value = true;
    error.value = null;
    try {
      data.value = await yourClient.listData();
    } catch (err) {
      if (err instanceof Error) {
        error.value = err.message;
      }
    } finally {
      loading.value = false;
    }
  }

  onMounted(() => fetchData());

  return {
    data: computed(() => data.value),
    loading: computed(() => loading.value),
    error: computed(() => error.value),
    refresh: fetchData
  };
}
```

### Step 3: Create Pinia Store (Optional but Recommended)

```typescript
// your-component/webapp/store/yourStore.ts
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { yourClient } from '../api/YourClient';

export const useYourStore = defineStore('your-feature', () => {
  const items = ref<YourData[]>([]);
  const loading = ref(false);
  const error = ref<string | null>(null);

  const isLoading = computed(() => loading.value);
  const hasError = computed(() => error.value !== null);

  async function fetchItems() {
    loading.value = true;
    error.value = null;
    try {
      items.value = await yourClient.listData();
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Unknown error';
    } finally {
      loading.value = false;
    }
  }

  function clearState() {
    items.value = [];
    error.value = null;
  }

  return {
    items,
    loading,
    error,
    isLoading,
    hasError,
    fetchItems,
    clearState
  };
});
```

### Step 4: Add Tests

```typescript
// your-component/webapp/api/YourClient.spec.ts
import { describe, it, expect, beforeEach, vi } from 'vitest';
import { usePositivityApiClient } from 'durion-positivity';
import { YourClient } from './YourClient';

vi.mock('durion-positivity', () => ({
  usePositivityApiClient: vi.fn(() => ({
    request: vi.fn()
  }))
}));

describe('YourClient', () => {
  let client: YourClient;
  let mockApiClient: any;

  beforeEach(() => {
    mockApiClient = usePositivityApiClient();
    client = new YourClient();
  });

  it('should fetch data list', async () => {
    mockApiClient.request.mockResolvedValue([{ id: '1' }]);

    const result = await client.listData();

    expect(mockApiClient.request).toHaveBeenCalledWith({
      service: 'pos-your-service',
      endpoint: '/list',
      method: 'GET',
      data: undefined
    });
    expect(result).toHaveLength(1);
  });
});
```

---

## Vue Component Pattern

```vue
<template>
  <div class="your-component">
    <div v-if="loading" class="loading">Loading...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else class="content">
      <div v-for="item in data" :key="item.id">
        {{ item.name }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useYourData } from '../composables/useYourData';

const { data, loading, error } = useYourData();
</script>
```

---

## CI/CD Integration

Add to your CI pipeline:

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

### ESLint showing false positives

1. Check `.eslintrc.json` configuration
2. Verify `eslint-plugin-no-direct-api-calls.js` exists
3. Run: `npm run lint -- --debug`

### Can't run npm scripts

```bash
# Make sure you're in durion-moqui-frontend directory
cd /path/to/durion-moqui-frontend

# Try reinstalling
rm -rf node_modules package-lock.json
npm install
```

---

## Quick Reference

| Task | Command |
|------|---------|
| Build (fast) | `./gradlew build -x test` |
| Build (full) | `./gradlew build` |
| Install deps | `npm install` |
| Check compliance | `npm run lint` |
| Fix auto-fixable | `npm run lint:fix` |
| Check gateway rules | `npm run lint:api-gateway` |
| Run tests | `npm test` |
| Simulate pre-commit | `bash .husky/pre-commit` |

---

## References

- **Architecture Guide**: [ARCHITECTURE_GUIDE.md](./ARCHITECTURE_GUIDE.md)
- **Operations Runbook**: [OPERATIONS_RUNBOOK.md](./OPERATIONS_RUNBOOK.md)
- **ADR-0010**: Frontend Domain Responsibilities Guide
- **Example Implementation**: `runtime/component/durion-shopmgr/webapp/`
