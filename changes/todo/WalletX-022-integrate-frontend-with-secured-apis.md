# WalletX-022-integrate-frontend-with-secured-apis

## Task

- **ID:** WalletX-022
- **Title:** Integrate Frontend with Secured APIs
- **Status:** Todo
- **Priority:** Critical
- **Created:** 2026-10-04
- **Updated:** 2026-10-04

## Description

Connect all React modules to the gateway and JWT-protected backend APIs.

## Objective

Deliver an end-to-end authenticated user flow across registration, wallets, deposits, transfers, history, and notifications.

## Scope

### In scope

- Centralized API client and bearer-token handling.
- Gateway base URL and environment configuration.
- Consistent error, expiry, retry, and logout behavior.

### Out of scope

- New backend business capabilities.
- Production hosting.

## Acceptance Criteria

- [ ] A user can complete the primary flow through the frontend against local services.
- [ ] Protected requests include valid JWTs and expire safely.
- [ ] API errors are surfaced consistently across modules.

## Implementation Tasks

- [ ] Configure API clients and environment values.
- [ ] Run and fix end-to-end integration flows.

## Dependencies

- WalletX-012, WalletX-017, WalletX-018, WalletX-019, WalletX-020, WalletX-021.

## Notes

This is the frontend integration milestone before containerized deployment.
