# WalletX-007-create-wallet-service

## Task

- **ID:** WalletX-007
- **Title:** Create Wallet Service Classes
- **Status:** Active
- **Priority:** Critical
- **Created:** 2026-10-04
- **Updated:** 2026-10-04

## Description

Create the foundational classes for the Wallet Service without implementing wallet retrieval or creation behavior.

## Objective

Establish the wallet domain and service-layer structure that subsequent tasks can use to add authenticated wallet operations.

## Scope

### In scope

- Wallet entity and persistence repository.
- Wallet service class foundation and the DTOs needed by the service layer.
- RON as the supported wallet currency and the documented initial-balance policy in the model.

### Out of scope

- HTTP endpoints for creating or retrieving wallets.
- Owner-scoped wallet operations.
- Deposits, transfers, multi-currency support, wallet details, or balance retrieval endpoints.

## Acceptance Criteria

- [x] Wallet domain and persistence classes are defined using existing backend conventions.
- [x] The wallet model represents its owner, RON currency, and initial balance policy.
- [x] Wallet service classes compile without implementing create/list endpoint behavior.

## Implementation Tasks

- [x] Review backend persistence, security principal, and service conventions.
- [x] Define wallet entity, currency enum, repository, and service class.
- [x] Add focused tests for the foundational wallet model and persistence mapping where appropriate.

## Dependencies

- WalletX-002, WalletX-004, WalletX-006.

## Notes

Authenticated wallet listing and wallet creation are tracked separately by WalletX-037 and WalletX-038.
New wallets start with a zero RON balance.
