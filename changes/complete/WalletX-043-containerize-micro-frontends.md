# WalletX-043-containerize-micro-frontends

## Task

- **ID:** WalletX-043
- **Title:** Containerize Micro-Frontends
- **Status:** Active
- **Priority:** High
- **Created:** 2026-11-12
- **Updated:** 2026-11-13

## Description

Build production-ready container images for the micro-frontend shell and independently deployed frontend applications.

## Objective

Make the migrated frontend applications reproducibly buildable and runnable as containers with gateway configuration supplied at runtime.

## Scope

### In scope

- Create production build and static-server container configurations for the shell and micro frontends.
- Configure runtime routing and gateway URL settings without baking environment-specific values into images.
- Add health checks and document how to build and run each frontend image.

### Out of scope

- Docker Compose orchestration.
- External Nginx load-balancer configuration.

## Acceptance Criteria

- [ ] Each shell and micro-frontend image builds successfully.
- [ ] Runtime configuration supports the required gateway URL and module entry points.
- [ ] The containers serve the application and pass their health checks.

## Implementation Tasks

- [ ] Create and verify container build configurations for the shell and micro frontends.
- [ ] Document required runtime settings and local image verification.

## Dependencies

- WalletX-042.

## Notes

Coordinate the image layout and runtime configuration with the integration approach selected in WalletX-041.
