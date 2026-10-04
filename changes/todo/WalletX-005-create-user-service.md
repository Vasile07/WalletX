# WalletX-005-create-user-service

## Task

- **ID:** WalletX-005
- **Title:** Create User Service
- **Status:** Todo
- **Priority:** Critical
- **Created:** 2026-10-04
- **Updated:** 2026-10-04

## Description

Implement user registration, profile management, password hashing, and user persistence.

## Objective

Provide a secure user domain that can support authentication and ownership checks.

## Scope

### In scope

- Registration and profile retrieval/update behavior.
- Secure password hashing and validation.
- PostgreSQL persistence and validation for user data.

### Out of scope

- JWT login/token issuance.
- Wallet and transfer behavior.

## Acceptance Criteria

- [ ] Users can register with validated unique credentials.
- [ ] Passwords are never persisted in plaintext.
- [ ] An authenticated user can retrieve their profile.

## Implementation Tasks

- [ ] Define user entity, repository, service, and REST endpoints.
- [ ] Add validation, password hashing, and persistence configuration.

## Dependencies

- WalletX-002, WalletX-004.

## Notes

Use the documented `/auth/register` and `/users/me` API conventions.
