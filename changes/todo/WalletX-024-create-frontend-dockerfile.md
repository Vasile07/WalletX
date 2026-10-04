# WalletX-024-create-frontend-dockerfile

## Task

- **ID:** WalletX-024
- **Title:** Create Frontend Dockerfile
- **Status:** Todo
- **Priority:** High
- **Created:** 2026-10-04
- **Updated:** 2026-10-04

## Description

Containerize the React application for local and deployment use.

## Objective

Produce a small, reproducible frontend image serving the built application.

## Scope

### In scope

- Production frontend build stage and static server runtime.
- Runtime configuration for the gateway URL.
- Container health check and local verification.

### Out of scope

- Nginx load-balancer routing.
- Backend containers.

## Acceptance Criteria

- [ ] The frontend image builds and serves the application.
- [ ] Gateway configuration is environment-driven.
- [ ] The container can communicate with the local gateway.

## Implementation Tasks

- [ ] Create multi-stage frontend Dockerfile.
- [ ] Verify production build and document usage.

## Dependencies

- WalletX-003, WalletX-022.

## Notes

Keep static serving concerns separate from the external load balancer.
