# WalletX-037-retrieve-authenticated-user-wallets Plan

## Objective
Implement a wallet-listing endpoint that resolves the authenticated principal and returns only the wallets owned by that user.

## Affected areas
- `my-wallet-be/wallet-service`: add validation and endpoint logic for authenticated wallet listing.
- `my-wallet-be/wallet-service`: extend repository/service behavior to filter wallets by the authenticated user id.
- `my-wallet-be/wallet-service`: add focused controller and service tests for authorized and unauthorized behavior.

## Proposed implementation
1. Review the Wallet Service domain, repository, and controller conventions already established for the wallet module.
2. Add a service method that returns wallets for a specific user id, ensuring repository queries are owner-scoped.
3. Implement the authenticated `/wallets` endpoint using the current security principal to resolve the current user id.
4. Add tests covering successful listing, empty results, unauthorized access, and ownership isolation.

## Verification
- Run the Wallet Service test suite for the controller, repository, and domain tests.
- Confirm unauthenticated requests return 401 and authenticated requests return only the matching wallets.
- Validate the wallet service still compiles with the new security configuration and owner-scoped query behavior.

## Questions
- The task inherits the existing wallet domain model and user principal conventions established by the wallet and auth work.
- No additional wallet creation or transfer behavior is included in this task.
