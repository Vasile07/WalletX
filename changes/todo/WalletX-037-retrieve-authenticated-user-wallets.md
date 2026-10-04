# WalletX-037-retrieve-authenticated-user-wallets

## Task

- **ID:** WalletX-037
- **Title:** Retrieve Authenticated User Wallets
- **Status:** Todo
- **Priority:** High
- **Created:** 2026-10-04
- **Updated:** 2026-10-04

## Description

Implement retrieval of all wallets owned by the authenticated user.

## Objective

Allow an authenticated user to list their wallets without exposing wallets owned by other users.

## Scope

### In scope

- Authenticated `GET /wallets` endpoint.
- Resolve the authenticated user's identity from the security principal.
- Return only wallets belonging to that user.
- Tests for authenticated listing, ownership isolation, and unauthenticated access.

### Out of scope

- Wallet creation.
- Wallet details or balance endpoints.
- Deposits, transfers, or multi-currency support.

## Acceptance Criteria

- [ ] An authenticated user can retrieve their wallet list through `GET /wallets`.
- [ ] The result contains only wallets owned by the authenticated user.
- [ ] Unauthenticated requests are rejected.
- [ ] Tests cover listing behavior and owner isolation.

## Implementation Tasks

- [ ] Implement owner-scoped wallet listing in the service and REST controller.
- [ ] Add tests for successful listing, empty results, ownership isolation, and authentication requirements.

## Dependencies

- WalletX-006, WalletX-007.

## Notes

Builds on the wallet domain and repository classes established by WalletX-007.
