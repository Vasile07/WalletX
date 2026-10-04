# WalletX-012-create-api-gateway

## Task

- **ID:** WalletX-012
- **Title:** Create API Gateway
- **Status:** Todo
- **Priority:** High
- **Created:** 2026-10-04
- **Updated:** 2026-10-04

## Description

Create a Spring Cloud Gateway as the single public entry point for WalletX REST APIs.

## Objective

Route frontend requests to the correct services while centralizing cross-cutting API concerns.

## Scope

### In scope

- Routes for user, wallet, and transfer services.
- CORS, request correlation, and gateway-level error handling.
- Local service discovery/configuration suitable for Docker Compose.

### Out of scope

- Business logic duplication in the gateway.
- Nginx load balancing.

## Acceptance Criteria

- [ ] Public API routes reach the correct backend service.
- [ ] Protected routes preserve JWT identity and reject malformed access consistently.
- [ ] Gateway configuration works in local containerized development.

## Implementation Tasks

- [ ] Create gateway project and route configuration.
- [ ] Add cross-cutting filters and integration tests.

## Dependencies

- WalletX-004, WalletX-005, WalletX-007, WalletX-009.

## Notes

The gateway is the public REST boundary behind Nginx.
