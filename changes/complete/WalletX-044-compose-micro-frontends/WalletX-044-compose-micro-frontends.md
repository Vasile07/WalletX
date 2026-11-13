# WalletX-044-compose-micro-frontends

## Task

- **ID:** WalletX-044
- **Title:** Integrate Micro-Frontends with Docker Compose
- **Status:** Active
- **Priority:** High
- **Created:** 2026-11-12
- **Updated:** 2026-11-13

## Description

Extend the local Compose deployment to run the micro-frontend shell and its independently deployed frontend applications with the WalletX services.

## Objective

Allow the assessment's micro-frontend user experience to run end-to-end in the local containerized environment.

## Scope

### In scope

- Add the shell and micro-frontend containers to the Compose deployment.
- Configure service discovery, frontend entry points, and gateway connectivity.
- Verify the authenticated user flow through the composed environment.

### Out of scope

- Kubernetes deployment.
- Production secrets management or external load-balancer design.

## Acceptance Criteria

- [ ] Compose starts the shell and all required micro-frontends alongside the existing application services.
- [ ] The shell can load each micro frontend and frontend API calls reach the gateway.
- [ ] The authenticated user flow can be smoke-tested through the composed environment.

## Implementation Tasks

- [ ] Update the Compose configuration with frontend services, dependencies, and runtime settings.
- [ ] Run and document a local end-to-end smoke test.

## Dependencies

- WalletX-025, WalletX-043.

## Notes

Keep the shell as the user-facing entry point and preserve independently deployable micro-frontend boundaries.
