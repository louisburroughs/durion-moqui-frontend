# DETSMS Frontend Agent Orchestrated Task Plan

## Purpose

Define concrete tasks for DETSMS frontend agents (Moqui + Vue) to:
- Select stories in a way that respects workspace orchestration
- Avoid unnecessary backend stubs whenever possible
- Work safely in a silo using only orchestration documents and story metadata

This plan operationalizes frontend Requirement 11 and the Workspace Story Orchestration Integration section in the frontend design.

## Inputs

- Workspace orchestration artifacts in durion:
  - durion/.github/orchestration/story-sequence.md
  - durion/.github/orchestration/frontend-coordination.md
- DETSMS requirements and design:
  - moqui_example/.kiro/specs/requirements.md
  - moqui_example/.kiro/specs/design.md
- DETSMS frontend [STORY] issues in durion-moqui-frontend.

## Outputs

- Selected set of frontend [STORY] issues ready for implementation.
- Implemented screens, flows, and Vue components aligned with documented backend contracts.
- Updated issues and Notes for Agents reflecting any discovered gaps or clarifications.

## Main Workflow

### 1. Refresh Orchestration Context

1. Read story-sequence.md to understand global ordering and classification:
   - Backend-First, Frontend-First, Parallel.
2. Read frontend-coordination.md to get the frontend-specific view:
   - Ready stories
   - Blocked stories (with blocking backend story IDs)
   - Parallel stories.
3. Verify timestamps to ensure orchestration data is recent; if stale, request workspace orchestration update.

### 2. Build a Frontend Planning Backlog

1. From frontend-coordination.md, extract all stories marked as Ready or Parallel.
2. Cross-check with DETSMS requirements/domains to group stories by:
   - Work Execution & Billing
   - Inventory Control
   - Product & Pricing
   - CRM
   - Accounting.
3. For each candidate story, open the corresponding [STORY] issue and review:
   - Acceptance criteria
   - Notes for Agents
   - Any referenced backend contracts.

### 3. Apply Stub-Avoidance Rules

1. For stories marked Blocked:
   - Do NOT select for the current sprint unless frontend-coordination.md explicitly allows a stub and describes its constraints.
2. For stories where stubs are allowed:
   - Record stub rules (behavior, response shape, error handling) directly from frontend-coordination.md and the issue.
   - Plan implementation so stub code is isolated (e.g., adapter/service layer) and trivial to replace once backend is ready.
3. For Ready and Parallel stories:
   - Prefer these over any Blocked stories when building sprint or work queues.

### 4. Implement Parallel and Ready Stories

1. For Parallel stories:
   - Use the API contracts and payload examples documented in orchestration artifacts and Notes for Agents.
   - Design Moqui services, screens, and Vue components so they assume those contracts, without needing live backend.
2. For Ready stories:
   - Validate that referenced backend work is complete or available, based on orchestration docs and linked backend issues.
   - Implement UI/UX, Moqui entities/services, and Vue components according to DETSMS design and domain rules.

### 5. Coordination via Issues (Silo-Friendly)

1. All questions, ambiguities, or discovered gaps MUST be raised as comments on the corresponding [STORY] issues.
2. When a frontend story reveals missing backend contracts:
   - Document concrete expectations (endpoint, payloads, error cases) in Notes for Agents.
   - Tag the workspace orchestration agent where applicable.
3. Do NOT rely on direct, out-of-band conversations with backend agents; expect orchestration documents to be updated instead.

### 6. Keep Orchestration in Sync

1. When a frontend story implementation starts:
   - Update the issue state and, if necessary, suggest status updates back to workspace orchestration (e.g., "In progress").
2. When a story completes:
   - Ensure acceptance criteria are met.
   - Post a concise summary and any deviations from initial assumptions.
3. If the implemented behavior differs from assumptions in orchestration docs:
   - Propose updates to story-sequence.md and frontend-coordination.md via issue comments.

## Checklist for Each Sprint Planning Cycle

1. Orchestration docs reviewed and timestamps checked.
2. Ready and Parallel stories identified and grouped by domain.
3. Blocked stories reviewed; only included if explicit stub rules exist.
4. Selected stories align with DETSMS domain priorities and capacity.
5. Any required clarifications posted on issues before implementation starts.

## Silo Assumptions

- Frontend agents operate using only orchestration docs, DETSMS requirements/design, and GitHub issues.
- No direct coordination with backend agents is assumed or required.
- All cross-team communication must be captured as structured issue comments and orchestration updates.
