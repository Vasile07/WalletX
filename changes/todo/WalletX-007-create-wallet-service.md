# WalletX-007-create-wallet-service

## Task

- **ID:** WalletX-007
- **Title:** Create Wallet Service
- **Status:** Todo
- **Priority:** Critical
- **Created:** 2026-10-04
- **Updated:** 2026-10-04

## Description

Implement wallet creation, ownership, RON currency, and balance retrieval.

## Objective

Provide a persistent wallet domain with secure owner-scoped access.

## Scope

### In scope

- Wallet creation and listing for the authenticated user.
- Wallet details and balance endpoints.
- PostgreSQL persistence and RON currency enforcement.

### Out of scope

- Deposits and transfer balance mutations.
- Multi-currency support.

## Acceptance Criteria

- [ ] Authenticated users can create and list their wallets.
- [ ] Wallet details and balances are owner-scoped.
- [ ] New wallets use RON and a deterministic initial balance policy.

## Implementation Tasks

- [ ] Define wallet model, repository, service, and REST endpoints.
- [ ] Add ownership and currency validation.

## Dependencies

- WalletX-002, WalletX-004, WalletX-006.

## Notes

Follow the documented `/wallets` endpoint family.
