# WalletX-009-create-transfer-service

## Task

- **ID:** WalletX-009
- **Title:** Create Transfer Service
- **Status:** Active
- **Priority:** Critical
- **Created:** 2026-10-04
- **Updated:** 2026-10-05

## Description

Create the structural skeleton for the Transfer Service so subsequent tasks can implement transfer creation and retrieval independently.

## Objective

Establish a buildable, testable foundation for transfer functionality without implementing transfer endpoint behavior.

## Scope

### In scope

- Transfer-service application structure and minimal class/interface scaffolding needed by later endpoint work.
- Empty `TransferController` registered at `/transfers` as a placeholder for endpoint tasks.
- Build/startup wiring and baseline tests for the skeleton.

### Out of scope

- `POST /transfers` implementation (tracked separately).
- `GET /transfers` implementation (tracked separately).
- Transfer validation, wallet balance updates, persistence behavior, and cross-service coordination.
- Advanced duplicate/idempotency validation, notification delivery, and event consumers.

## Acceptance Criteria

- [x] The Transfer Service has the minimal package and type structure needed for follow-up implementation tasks.
- [x] The service builds and starts successfully with no transfer endpoint behavior implemented.
- [x] Existing service behavior, including health checks, remains available.

## Implementation Tasks

- [x] Define the minimum transfer domain, persistence, business contracts, and empty controller placeholder required by follow-up tasks.
- [x] Add or update baseline tests to verify application startup and existing health behavior.

## Dependencies

- WalletX-007, WalletX-008.

## Notes

The documented `POST /transfers` request shape and RON-only policy apply to the follow-up endpoint tasks; this task does not implement either endpoint.
