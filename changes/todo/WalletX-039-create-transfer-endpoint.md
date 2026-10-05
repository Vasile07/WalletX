# WalletX-039-create-transfer-endpoint

## Task

- **ID:** WalletX-039
- **Title:** Create Transfer Endpoint
- **Status:** Todo
- **Priority:** Critical
- **Created:** 2026-10-05
- **Updated:** 2026-10-05

## Description

Implement the `POST /transfers` endpoint to create a RON transfer between wallets.

## Objective

Allow an authenticated user to submit a transfer request and receive a persisted result only when the transfer succeeds.

## Scope

### In scope

- Implement `POST /transfers` using the documented request shape.
- Implement the core transfer flow, including the checks required to safely execute a valid transfer.
- Debit and credit balances safely and persist a completed transfer atomically.
- Add focused tests for successful transfers and failure/rollback scenarios.

### Out of scope

- `GET /transfers` retrieval endpoint.
- Advanced duplicate/idempotency validation.
- Notification delivery and event consumers.

## Acceptance Criteria

- [ ] A valid request debits the sender and credits the receiver.
- [ ] A completed transfer is persisted with amount, currency, wallets, and timestamp.
- [ ] Failed operations do not leave partial balance updates.
- [ ] Invalid amounts, non-RON currencies, unauthorized wallets, and insufficient balances are rejected.

## Implementation Tasks

- [ ] Define the request/response contract and implement the `POST /transfers` controller flow.
- [ ] Coordinate wallet updates and transfer persistence transactionally.
- [ ] Add tests covering success, validation failures, and rollback behavior.

## Dependencies

- WalletX-009.
- WalletX-007, WalletX-008.

## Notes

Use the documented request fields `senderWalletId`, `receiverWalletId`, `amount`, and `currency`.
WalletX-010 separately tracks additional validation hardening and duplicate-request protection.
