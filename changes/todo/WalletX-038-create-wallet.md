# WalletX-038-create-wallet

## Task

- **ID:** WalletX-038
- **Title:** Create a Wallet
- **Status:** Todo
- **Priority:** High
- **Created:** 2026-10-04
- **Updated:** 2026-10-04

## Description

Implement wallet creation for the authenticated user.

## Objective

Allow an authenticated user to create a wallet that is automatically associated with their identity and uses the supported RON currency and initial-balance policy.

## Scope

### In scope

- Authenticated `POST /wallets` endpoint.
- Associate the new wallet with the authenticated user from the security principal.
- Enforce RON currency and the agreed initial-balance policy.
- Tests for creation, ownership assignment, currency/balance defaults, and unauthenticated access.

### Out of scope

- Wallet listing or wallet details endpoints.
- Deposits, transfers, or multi-currency support.

## Acceptance Criteria

- [ ] An authenticated user can create a wallet through `POST /wallets`.
- [ ] The new wallet is associated with the authenticated user, not an owner supplied by the request.
- [ ] The new wallet uses RON and the defined initial-balance policy.
- [ ] Unauthenticated requests are rejected.

## Implementation Tasks

- [ ] Implement wallet creation in the service and REST controller.
- [ ] Add tests for wallet creation, authenticated ownership, defaults, and authentication requirements.

## Dependencies

- WalletX-006, WalletX-007.

## Notes

Builds on the wallet domain and service classes established by WalletX-007.
