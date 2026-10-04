# WalletX-004-define-service-architecture

## Task

- **ID:** WalletX-004
- **Title:** Define Service Architecture
- **Status:** Todo
- **Priority:** Critical
- **Created:** 2026-10-04
- **Updated:** 2026-10-04

## Description

Define responsibilities, API boundaries, data ownership, and communication paths for WalletX components.

## Objective

Produce an architecture baseline that prevents duplicated business logic and unclear service ownership.

## Scope

### In scope

- Define User, Wallet, Transfer, gateway, database, broker, notification, and frontend boundaries.
- Define synchronous REST and asynchronous RabbitMQ/Kafka interactions.
- Define authentication and ownership responsibilities.

### Out of scope

- Implementing services or infrastructure.

## Acceptance Criteria

- [ ] Each domain responsibility and data boundary is documented.
- [ ] Public routes and service-to-service communication are identified.
- [ ] RON-only and simulated-deposit constraints are documented.

## Implementation Tasks

- [ ] Create an architecture decision record and service boundary map.
- [ ] Review boundaries against all project requirements.

## Dependencies

- WalletX-001.

## Notes

This task guides implementation and the later C4/UML documentation tasks.
