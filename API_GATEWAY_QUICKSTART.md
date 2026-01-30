# Quick Start: Using durion-positivity API Gateway

## ✅ For Component Developers

### Import the API Client

```typescript
import { usePositivityApiClient, PositivityApiError } from 'durion-positivity';
```

### Create an API Client in Your Component

```typescript
// durion-crm/webapp/api/CustomerClient.ts
import { usePositivityApiClient } from 'durion-positivity';
import type { Customer, CustomerFilter } from '../types';

export class CustomerClient {
  private apiClient = usePositivityApiClient();

  async listCustomers(filters?: CustomerFilter): Promise<Customer[]> {
    return this.apiClient.request({
      service: 'pos-customer',
      endpoint: '/list',
      method: 'GET',
      data: filters
    });
  }

  async getCustomer(id: string): Promise<Customer> {
    return this.apiClient.request({
      service: 'pos-customer',
      endpoint: `/${id}`,
      method: 'GET'
    });
  }

  async updateCustomer(id: string, data: Partial<Customer>): Promise<Customer> {
    return this.apiClient.request({
      service: 'pos-customer',
      endpoint: `/${id}`,
      method: 'PUT',
      data
    });
  }
}

export const customerClient = new CustomerClient();
```

### Use in a Composable

```typescript
// durion-crm/webapp/composables/useCustomerList.ts
import { ref, onMounted, computed } from 'vue';
import { customerClient } from '../api/CustomerClient';

export function useCustomerList() {
  const customers = ref<Customer[]>([]);
  const loading = ref(false);
  const error = ref<string | null>(null);

  async function fetchCustomers() {
    loading.value = true;
    error.value = null;
    try {
      customers.value = await customerClient.listCustomers();
    } catch (err) {
      if (err instanceof Error) {
        error.value = err.message;
      }
    } finally {
      loading.value = false;
    }
  }

  onMounted(() => {
    fetchCustomers();
  });

  return {
    customers: computed(() => customers.value),
    loading: computed(() => loading.value),
    error: computed(() => error.value),
    refresh: fetchCustomers
  };
}
```

### Use in a Vue Component

```vue
<template>
  <div class="customer-list">
    <div v-if="loading" class="loading">Loading...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else class="content">
      <div v-for="customer in customers" :key="customer.id">
        {{ customer.name }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useCustomerList } from '../composables/useCustomerList';

const { customers, loading, error } = useCustomerList();
</script>
```

---

## ❌ WRONG - DO NOT DO THIS

```typescript
// ❌ FORBIDDEN: Direct fetch calls
const response = await fetch('/rest/api/v1/pos-customer/list', {
  method: 'GET',
  headers: { 'Authorization': `Bearer ${token}` }
});
const data = await response.json();

// ❌ FORBIDDEN: Direct axios calls
import axios from 'axios';
const { data } = await axios.get('/rest/api/v1/pos-customer/list');

// ❌ FORBIDDEN: Direct Authorization header construction
const headers = {
  'Authorization': `Bearer ${getToken()}`
};
```

---

## API Request Pattern

All requests follow this pattern:

```typescript
await apiClient.request({
  service: 'pos-{service}',        // e.g., 'pos-customer', 'pos-inventory'
  endpoint: '/path/to/endpoint',    // e.g., '/list', '/{id}', '/stock/ABC123'
  method: 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH',
  data?: { /* request body */ },    // Optional, sent with POST/PUT/DELETE
  headers?: { /* custom headers */ }, // Optional, merged with defaults
  timeoutMs?: 30000,               // Optional, default 30000ms
  idempotencyKey?: 'key-123'       // Optional, for safe retries
});
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

    // Check if error is retriable
    if (isRetriableError(error)) {
      console.log('Error is retriable - will be retried automatically');
    }

    // Get user-friendly message
    const userMessage = getErrorMessage(error);
    showNotification(userMessage);
  } else {
    console.error('Unknown error:', error);
  }
}
```

---

## Common Backend Services

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

See backend service documentation for exact endpoints.

---

## Testing

Mock the API client in your tests:

```typescript
import { describe, it, expect, beforeEach, vi } from 'vitest';
import { usePositivityApiClient } from 'durion-positivity';
import { useCustomerList } from '../useCustomerList';

vi.mock('durion-positivity', () => ({
  usePositivityApiClient: vi.fn(() => ({
    request: vi.fn()
  }))
}));

describe('useCustomerList', () => {
  it('should fetch customers', async () => {
    const mockApiClient = usePositivityApiClient();
    mockApiClient.request.mockResolvedValue([
      { id: '1', name: 'Alice' },
      { id: '2', name: 'Bob' }
    ]);

    const { customers, refresh } = useCustomerList();
    await refresh();

    expect(mockApiClient.request).toHaveBeenCalledWith({
      service: 'pos-customer',
      endpoint: '/list',
      method: 'GET'
    });
    expect(customers.value).toHaveLength(2);
  });
});
```

---

## Questions?

See [ADR-0010: Frontend Domain Responsibilities Guide](docs/adr/0010-frontend-domain-responsibilities-guide.adr.md) for detailed architecture documentation.
