# Plan: WalletX-006 Implement Login and JWT Authentication

## Proposed implementation

- Review the existing User Service registration and password-hashing implementation, plus the project API conventions.
- Add a validated login request and a response class containing only `accessToken` and `tokenType`, using Lombok for boilerplate. Normalize the email, compare the supplied password with the stored BCrypt hash, and return a generic unauthorized response for unknown users and incorrect passwords.
- Add JWT issuance and validation using a signing secret and expiration configured through environment-backed application properties. Include the user's stable ID and identity claims needed by downstream services.
- Configure stateless Spring Security bearer-token authentication. Keep registration, login, and health checks public; require valid authentication for other protected User Service routes. Expose the authenticated identity as a typed principal for future secured operations.
- Follow the project Java convention: use classes rather than records and Lombok for boilerplate accessors and constructors where appropriate.
- Add focused unit and MVC/security tests for successful login, invalid credentials, token signature and expiry validation, authenticated identity propagation, and rejected unauthenticated requests.
- Keep current-user profile retrieval out of this task; it is tracked separately by WalletX-036.

## Affected project areas

- `my-wallet-be/user-service/build.gradle`
- `my-wallet-be/user-service/src/main/java/com/walletx/userservice/`
- `my-wallet-be/user-service/src/main/resources/application.yml`
- `my-wallet-be/user-service/src/test/java/com/walletx/userservice/`
- `changes/active/WalletX-006-implement-login-and-jwt-authentication/`
- Related API/authentication documentation, if existing docs need clarification after implementation.

## Verification steps

- Test that valid credentials produce a signed JWT with the expected subject and expiry.
- Test that unknown users and incorrect passwords receive the same unauthorized response.
- Test that JWTs with invalid signatures or expired timestamps are rejected.
- Test that protected requests require a valid bearer token and that the authenticated principal exposes the expected user identity.
- Test that registration, login, and health endpoints remain accessible without a token.
- Run the User Service test suite and confirm existing registration behavior remains intact.

## Decisions confirmed

- Login response shape: only `accessToken` and `tokenType`; no user profile fields or separate ID token. The access token carries the user's ID as its subject.
- JWT signing secrets are configured using `JWT_SECRET`; there is no default secret, and the User Service fails to start without a valid key of at least 32 bytes.
- Current-user profile endpoints are excluded from WalletX-006 and remain tracked by WalletX-036.

## Branch

Created and active: `WalletX-006-implement-login-and-jwt-authentication`.
