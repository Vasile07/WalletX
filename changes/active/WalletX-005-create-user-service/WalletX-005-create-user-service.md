# WalletX-005-create-user-service

## Task

- **ID:** WalletX-005
- **Title:** Create User Service
- **Status:** Active
- **Priority:** Critical
- **Created:** 2026-10-04
- **Updated:** 2026-10-04

## Description

Implement user registration and user persistence with secure password hashing.

## Objective

Provide secure user creation that can support the later authentication and ownership flows.

## Scope

### In scope

- User registration at `POST /auth/register`.
- Secure password hashing and input validation.
- PostgreSQL persistence and validation for user data.

### Out of scope

- Login, JWT issuance, and authenticated profile retrieval.
- Wallet and transfer behavior.

## Acceptance Criteria

- [x] Users can register with validated unique credentials at `POST /auth/register`.
- [x] Passwords are never persisted in plaintext.
- [x] Registration responses never expose the stored password hash.

## Implementation Tasks

- [x] Define the user entity, repository, registration service, and `POST /auth/register` endpoint.
- [x] Add input validation, BCrypt password hashing, and persistence configuration.
- [x] Add Mockito-based tests for registration behavior and password hashing.

## Dependencies

- WalletX-002, WalletX-004.

## Notes

`GET /users/me` is intentionally excluded and tracked separately in WalletX-036. Use Mockito for tests and BCrypt for password hashing.
