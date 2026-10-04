# WalletX-009-create-transfer-service

## Task

- **ID:** WalletX-009
- **Title:** Create Transfer Service
- **Status:** Todo
- **Priority:** Critical
- **Created:** 2026-10-04
- **Updated:** 2026-10-04

## Description

Implement money transfers between wallets and persist transfer records.

## Objective

Provide the core transfer workflow for moving RON between users safely.

## Scope

### In scope

- Transfer creation and retrieval endpoints.
- Sender/receiver balance updates and transfer persistence.
- Service integration with wallet ownership and balance data.

### Out of scope

- Advanced duplicate/idempotency validation.
- Notification delivery and event consumers.

## Acceptance Criteria

- [ ] A valid transfer debits the sender and credits the receiver.
- [ ] A completed transfer is persisted with amount, currency, wallets, and timestamp.
- [ ] Failed operations do not leave partial balance updates.

## Implementation Tasks

- [ ] Define transfer model, repository, API, and application service.
- [ ] Implement transactional coordination with the Wallet Service.

## Dependencies

- WalletX-007, WalletX-008.

## Notes

Use the documented `POST /transfers` request shape and RON-only policy.
