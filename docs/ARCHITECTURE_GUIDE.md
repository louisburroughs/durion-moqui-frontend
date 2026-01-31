# Durion Moqui Frontend Architecture Guide

**Last Updated**: 2026-01-31  
**Stack**: Moqui Framework, Java 11, Groovy, Vue.js 3, Quasar, TypeScript

---

## Overview

The durion-moqui-frontend is a Moqui Framework application serving as the frontend platform for the Durion POS system. It integrates with the durion-positivity-backend microservices via a centralized API gateway pattern.

---

## API Gateway Architecture (ADR-0010)

All frontend components must route API calls through the centralized **durion-positivity** gateway. This ensures consistent authentication, observability, and error handling.

### Gateway Features

- **Centralized Authentication**: Automatic Bearer token injection
- **Correlation ID Injection**: X-Correlation-ID for distributed tracing
- **Retry Logic**: Exponential backoff with configurable retries
- **Error Handling**: Typed errors with user-friendly messages
- **Network Error Detection**: Handles timeouts and connection failures
- **TypeScript Types**: Full type safety for requests/responses

### How to Use the API Gateway

```typescript
import { usePositivityApiClient } from 'durion-positivity';

const apiClient = usePositivityApiClient();
const data = await apiClient.request({
  service: 'pos-inventory',      // Backend service name
  endpoint: '/stock/ABC123',     // API endpoint path
  method: 'GET'                  // HTTP method
});
```

### Request Pattern

```typescript
await apiClient.request({
  service: 'pos-{service}',          // e.g., 'pos-customer', 'pos-inventory'
  endpoint: '/path/to/endpoint',     // e.g., '/list', '/{id}', '/stock/ABC123'
  method: 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH',
  data?: { /* request body */ },     // Optional, sent with POST/PUT/DELETE
  headers?: { /* custom headers */ },// Optional, merged with defaults
  timeoutMs?: 30000,                 // Optional, default 30000ms
  idempotencyKey?: 'key-123'         // Optional, for safe retries
});
```

### Available Backend Services

| Service | Endpoint Examples |
|---------|------------------|
| `pos-customer` | `/list`, `/{id}`, `/{id}` (PUT) |
| `pos-inventory` | `/stock/{locationId}`, `/locations` |
| `pos-catalog` | `/products`, `/products/{id}` |
| `pos-order` | `/orders`, `/orders/{id}` |
| `pos-workorder` | `/workorders`, `/workorders/{id}` |
| `pos-people` | `/employees`, `/time-entries` |
| `pos-shop-manager` | `/appointments`, `/shops` |
| `pos-accounting` | `/transactions`, `/accounts` |

---

## Component Architecture

### Directory Structure

```
runtime/component/
├── durion-positivity/    ← API Gateway (mandatory for all API calls)
│   └── webapp/
│       ├── api/          ← PositivityApiClient.ts, errors.ts
│       ├── types/        ← TypeScript interfaces
│       └── index.ts      ← Component exports
├── durion-shopmgr/       ← Example refactored component
│   └── webapp/
│       ├── api/          ← AppointmentClient.ts
│       ├── store/        ← Pinia stores
│       └── appointments/ ← Vue components
├── durion-crm/
├── durion-inventory/
├── durion-sales/
└── ...
```

### Component Integration Pattern

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

**Step 2**: Create Pinia store (recommended)
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

---

## Forbidden Patterns

The following patterns are blocked by ESLint rules and pre-commit hooks:

### ❌ Direct fetch() calls
```typescript
// BLOCKED
const response = await fetch('/api/data');
```

### ❌ Direct axios imports
```typescript
// BLOCKED
import axios from 'axios';
const response = await axios.get('/api/data');
```

### ❌ Manual Authorization headers
```typescript
// BLOCKED
const headers = {
  'Authorization': `Bearer ${token}`,
  'Content-Type': 'application/json'
};
```

---

## Error Handling

```typescript
import { PositivityApiError, isRetriableError, getErrorMessage } from 'durion-positivity';

try {
  const data = await apiClient.request({ /* ... */ });
} catch (error) {
  if (error instanceof PositivityApiError) {
    console.error('API Error:', error.message);
    console.error('Status:', error.status);
    console.error('Service:', error.service);
    console.error('Correlation ID:', error.correlationId);

    if (isRetriableError(error)) {
      console.log('Error is retriable - will be retried automatically');
    }

    const userMessage = getErrorMessage(error);
    showNotification(userMessage);
  }
}
```

---

## Testing Pattern

Mock the gateway, not HTTP endpoints:

```typescript
import { describe, it, expect, vi } from 'vitest';
import { usePositivityApiClient } from 'durion-positivity';

vi.mock('durion-positivity', () => ({
  usePositivityApiClient: vi.fn(() => ({
    request: vi.fn()
  }))
}));

describe('YourComponent', () => {
  it('should fetch data', async () => {
    const mockApiClient = usePositivityApiClient();
    mockApiClient.request.mockResolvedValue({ id: 123 });

    // Test your component logic
  });
});
```

---

## Port Configuration

### Docker Compose (Production-like)

Edit `docker/moqui-postgres-compose.yml`:

```yaml
nginx-proxy:
  ports:
    - 80:80      # HTTP (change to 8180:80 to avoid conflicts)
    - 443:443    # HTTPS

moqui-server:
  environment:
    - webapp_http_port=80
    - webapp_https_port=443
```

### Embedded Jetty (Development)

```bash
# Default port 8080
java -jar runtime/build/libs/moqui.war

# Custom port
java -jar runtime/build/libs/moqui.war port=8180 threads=100
```

---

## References

- **ADR-0010**: Frontend Domain Responsibilities Guide
- **API Gateway**: `runtime/component/durion-positivity/webapp/`
- **Example Implementation**: `runtime/component/durion-shopmgr/webapp/`
- **Platform Runbook**: `durion/docs/OPERATIONS_RUNBOOK.md`
