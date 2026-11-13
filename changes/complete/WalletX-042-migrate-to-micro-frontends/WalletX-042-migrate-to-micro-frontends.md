# WalletX-042-migrate-to-micro-frontends

## Task

- **ID:** WalletX-042
- **Title:** Migrate Frontend to Micro-Frontends
- **Status:** Active
- **Priority:** High
- **Created:** 2026-11-12
- **Updated:** 2026-11-13

## Description

Refactor the current single React application into a shell and independently buildable Authentication, Wallet, Transfers, and Notifications micro frontends.

## Objective

Deliver the micro-frontend architecture defined for the WalletX assessment while preserving the existing user flows and secured API integration.

## Scope

### In scope

- Implement the shell and the micro frontends according to the architecture decision.
- Move existing feature pages and their tests into the appropriate micro frontends.
- Preserve authentication, bearer-token handling, navigation, and API error behavior across module boundaries.
- Provide a local development workflow for running the shell and micro frontends together.

### Out of scope

- Dockerfiles and Docker Compose orchestration.
- New backend business capabilities.

## Acceptance Criteria

- [x] The shell integrates the independently buildable micro frontends using the selected approach.
- [x] Authentication, wallet, transfer, and notification flows work through the shell.
- [x] Protected API calls continue to include the authenticated user's bearer token.
- [x] Automated tests cover the shell integration and the migrated feature behavior.

## Implementation Tasks

- [x] Create the shell and micro-frontend application structure.
- [x] Migrate existing modules and adapt shared authentication and API integration.
- [x] Add and run tests for integrated user flows.

## Dependencies

- WalletX-041.

## Notes

Use [ADR-005: Micro-Frontend Architecture](../../docs/adr/ADR-005-micro-frontend-architecture.md) as the implementation reference. Avoid duplicating session state or API client behavior across micro frontends.
