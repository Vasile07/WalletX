# WalletX-036-implement-user-profile-retrieval

## Task

- **ID:** WalletX-036
- **Title:** Implement User Profile Retrieval
- **Status:** Todo
- **Priority:** High
- **Created:** 2026-10-04
- **Updated:** 2026-10-04

## Description

Implement authenticated retrieval of the current user's profile through `GET /users/me`.

## Objective

Allow an authenticated user to view their own profile without exposing credentials or another user's data.

## Scope

### In scope

- `GET /users/me` in the User Service.
- Resolving the authenticated principal to its user record.
- Returning only safe profile fields.
- Tests for authorized retrieval and unauthorized access.

### Out of scope

- User registration and profile updates.
- Login or JWT issuance and validation.
- Wallet and transfer behavior.

## Acceptance Criteria

- [ ] An authenticated user can retrieve their own profile using `GET /users/me`.
- [ ] The response excludes password hashes and other credential data.
- [ ] Unauthenticated requests are rejected.

## Implementation Tasks

- [ ] Implement the authenticated profile endpoint and response DTO.
- [ ] Add tests for profile ownership, response fields, and authentication requirements.

## Dependencies

- WalletX-005, WalletX-006.

## Notes

This task was split from WalletX-005 so registration can be implemented independently. The endpoint follows the documented `/users/me` API convention.
