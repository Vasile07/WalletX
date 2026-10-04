# WalletX-038-create-wallet Plan

## Objective
Implement wallet creation for an authenticated user by exposing a `POST /wallets` endpoint that associates the wallet with the current principal and applies the default RON currency and initial-balance policy.

## Affected areas
- `my-wallet-be`: wallet REST controller and request/response conventions for authenticated wallet creation.
- `my-wallet-be`: wallet service logic that creates and persists the wallet for the currently authenticated user.
- `my-wallet-be`: repository/domain logic for owner assignment and default values.
- `my-wallet-be`: focused controller/service tests covering successful creation, ownership, defaults, and authentication checks.

## Proposed implementation
1. Review the existing wallet service, controller, and security patterns already established by the wallet and auth work.
2. Add the wallet-creation service flow that strips any user-supplied owner input and uses the authenticated user id from the security principal instead.
3. Enforce the agreed defaults: currency must be `RON`, and the initial balance must follow the project policy defined for wallet creation.
4. Expose the authenticated `POST /wallets` endpoint through the REST layer and ensure unauthenticated access is rejected by the current security configuration.
5. Add tests covering successful creation, proper ownership assignment, currency and balance defaults, and rejected unauthenticated requests.

## Verification
- Run the wallet service test suite focused on controller and service behavior.
- Confirm unauthenticated requests return the expected 401/forbidden response.
- Confirm authenticated requests create a wallet with the current user attached, currency set to `RON`, and the initial balance set according to the policy.
- Verify the implementation compiles cleanly with the existing backend configuration and security setup.

## Questions
- No blocking questions at this time; the task follows the existing wallet domain and authentication conventions already in the project.
- This task intentionally excludes wallet listing/details, transfers, and multi-currency support.
