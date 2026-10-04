# WalletX-010-implement-transfer-validation

## Task

- **ID:** WalletX-010
- **Title:** Implement Transfer Validation
- **Status:** Todo
- **Priority:** Critical
- **Created:** 2026-10-04
- **Updated:** 2026-10-04

## Description

Harden transfers with ownership, balance, currency, amount, and duplicate-request validation.

## Objective

Ensure invalid or repeated transfer requests cannot corrupt balances or create duplicate records.

## Scope

### In scope

- Sender ownership and receiver existence checks.
- Positive amount, RON currency, sufficient balance, and self-transfer rules.
- Idempotency or duplicate transfer protection.

### Out of scope

- Fraud detection and transfer cancellation.

## Acceptance Criteria

- [ ] Unauthorized wallet references are rejected.
- [ ] Insufficient funds, unsupported currency, invalid amount, and invalid wallet pairs are rejected.
- [ ] Retried duplicate requests do not create duplicate completed transfers.

## Implementation Tasks

- [ ] Add validation rules and consistent error responses.
- [ ] Add an idempotency strategy and concurrency-safe tests.

## Dependencies

- WalletX-009.

## Notes

Validation must be enforced server-side regardless of frontend behavior.
