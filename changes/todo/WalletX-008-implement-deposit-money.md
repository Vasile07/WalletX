# WalletX-008-implement-deposit-money

## Task

- **ID:** WalletX-008
- **Title:** Implement Deposit Money
- **Status:** Todo
- **Priority:** High
- **Created:** 2026-10-04
- **Updated:** 2026-10-04

## Description

Add simulated deposits and safe wallet balance updates.

## Objective

Allow authenticated users to add simulated RON funds while recording the resulting transaction.

## Scope

### In scope

- Deposit endpoint and amount validation.
- Atomic balance update for the wallet owner.
- Deposit record/event data needed for history and notifications.

### Out of scope

- Real payment providers or bank integrations.
- Withdrawals.

## Acceptance Criteria

- [ ] Valid positive RON deposits update the owner’s balance atomically.
- [ ] Invalid amounts, currencies, and wallet ownership are rejected.
- [ ] A completed deposit is available to transaction history consumers.

## Implementation Tasks

- [ ] Implement deposit command, validation, and persistence.
- [ ] Define the deposit result and integration event contract.

## Dependencies

- WalletX-007.

## Notes

Deposits are explicitly simulated by the project requirements.
