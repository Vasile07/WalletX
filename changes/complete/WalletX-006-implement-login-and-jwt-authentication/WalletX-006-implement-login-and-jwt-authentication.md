# WalletX-006-implement-login-and-jwt-authentication

## Task

- **ID:** WalletX-006
- **Title:** Implement Login and JWT Authentication
- **Status:** Completed
- **Priority:** Critical
- **Created:** 2026-10-04
- **Updated:** 2026-10-04

## Description

Add login, JWT generation and validation, and bearer-token authentication for the User Service.

## Objective

Provide secure login that returns an access token and enforce authenticated access to protected User Service routes.

## Scope

### In scope

- Credential validation and JWT issuance.
- JWT signature, expiry, and claim validation.
- Spring Security integration for protected endpoints.
- A login response containing only `accessToken` and `tokenType`.
- Authenticated user identity propagation through the Spring Security principal.

### Out of scope

- Frontend authentication state.
- OAuth providers or two-factor authentication.
- Current-user profile endpoints, tracked by WalletX-036.

## Acceptance Criteria

- [x] Valid credentials return an access JWT and token type without profile fields.
- [x] Invalid credentials are rejected with an authentication error.
- [x] Invalid-signature or expired tokens cannot access protected endpoints.
- [x] Protected requests receive a typed principal with the authenticated user's ID and identity claims.

## Implementation Tasks

- [x] Implement login and environment-configured token service configuration.
- [x] Add stateless security filters, authorization rules, and unauthorized responses.
- [x] Add tests for login, token validation, and protected routes.

## Dependencies

- WalletX-005.

## Decisions

- Login returns only `accessToken` and `tokenType`; the JWT subject identifies the user. No separate ID token is issued.
- Configure a signing key of at least 32 bytes through `JWT_SECRET`; there is no insecure default.
- Current-user profile retrieval remains tracked by WalletX-036.

## Notes

Send access tokens with the HTTP Authorization header using the Bearer scheme. Keep the JWT signing key in environment configuration.
