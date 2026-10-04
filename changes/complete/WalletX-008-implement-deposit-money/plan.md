# WalletX-008-implement-deposit-money Plan

## Objective
Implement the simulated deposit flow for authenticated wallet owners, ensuring deposits are validated, persisted, and safe to consume in transaction history or notifications.

## Affected areas
- `my-wallet-be/wallet-service`: add deposit request/response contracts and controller endpoint logic.
- `my-wallet-be/wallet-service`: extend the service and repository to validate ownership, currency, and positive amounts before updating the wallet balance.
- `my-wallet-be/wallet-service`: add focused tests for valid deposits, invalid inputs, ownership checks, and authenticated access rules.

## Proposed implementation
1. Review the established wallet service conventions for authentication, repository access, and response DTO mapping.
2. Define the deposit command contract and validation rules: only positive decimals, RON-only currency, wallet ownership check, and atomic balance update.
3. Extend the wallet service and repository to locate the wallet owned by the authenticated user and persist the updated balance.
4. Expose the deposit endpoint using the current security principal and return the updated wallet payload.
5. Add integration-focused controller tests and service/domain assertions covering success and rejection cases.

## Verification
- Run the wallet-service test suite focused on the deposit flow and existing wallet controller tests.
- Confirm authenticated valid deposits increase the balance by the exact amount with 2-decimal precision.
- Confirm invalid amounts, unsupported currencies, or unauthorized wallet ownership are rejected with the expected HTTP or domain-level failures.
- Validate that the updated wallet remains available for downstream history/notification consumers.

## Questions
- The task inherits the existing WalletX-007 wallet domain, repository, and security conventions.
- No withdrawal or multi-currency support is included in this task. The initial scope remains RON-only and simulated deposits only.
