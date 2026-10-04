# Plan: WalletX-005 Create User Service

## Proposed implementation
- Review the backend skeleton and architecture decisions to confirm the registration route and persistence contract.
- Define the user entity, repository, registration DTOs, and validation rules needed for user creation.
- Implement `POST /auth/register` with BCrypt password hashing and no plaintext storage.
- Configure PostgreSQL persistence through application configuration, without implementing login or profile retrieval.
- Add focused Mockito-based tests for registration validation, duplicate-user handling, persistence interaction, and password hashing.
- Verify registration with the user-service module tests and the task's acceptance criteria.

## Affected project areas
- my-wallet-be/user-service/
- my-wallet-be/build.gradle
- changes/active/WalletX-005-create-user-service/
- changes/todo/WalletX-036-implement-user-profile-retrieval.md

## Verification steps
- Confirm registration rejects invalid or duplicate credentials and persists only BCrypt hashes.
- Verify the registration response contains no password or password hash.
- Check that PostgreSQL configuration and domain structure match the WalletX backend conventions.
- Run the module tests and confirm they pass with the implemented user-service behavior.

## Questions / decisions pending
- `GET /users/me` and profile retrieval are excluded from WalletX-005 and tracked as WalletX-036, dependent on registration and login/JWT authentication.
- Mockito is the requested test mocking framework; BCrypt is the required password encoder.
