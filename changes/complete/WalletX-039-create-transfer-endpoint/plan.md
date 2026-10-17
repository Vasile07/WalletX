# Plan for WalletX-039: Create Transfer Endpoint

## Proposed implementation

Implement the `POST /transfers` endpoint in the Transfer Service so an authenticated user can submit a RON transfer between two wallets, while preserving transactional safety and rejecting invalid or unauthorized requests.

## Scope and affected areas

- `my-wallet-be/transfer-service`: add the controller flow, request/response contract, business logic, and persistence updates required for successful transfer creation.
- Validate the request shape and business rules before mutating balances or writing transfer history.
- Use a REST call to the Wallet Service for the atomic wallet update, and persist transfer history in the Transfer Service's PostgreSQL database after the Wallet Service confirms success.
- Add focused tests for successful transfers, validation failures, and rollback behavior.

## Implementation steps

1. Review the transfer-service skeleton and neighboring service conventions, especially the wallet service patterns for authenticated access, validation, and transactional updates.
2. Configure the Transfer Service for PostgreSQL and configure a REST client for the Wallet Service.
3. Define the transfer request/response model that matches the documented payload: `senderWalletId`, `receiverWalletId`, `amount`, and `currency`.
4. Implement `POST /transfers` in `TransferController`, resolve the authenticated user, and delegate to a dedicated transfer business service.
5. Add validation in the service layer to reject null input, non-RON currency, invalid amounts, self-transfers, unauthorized wallet ownership, and insufficient funds.
6. Have the Wallet Service lock and update the sender and receiver wallet rows in one database transaction; pass the caller's JWT and authenticate the internal call.
7. Persist transfer history in the Transfer Service after the Wallet Service confirms the wallet update.
8. Add focused tests covering: successful transfer completion, invalid amount or currency, unauthorized wallet ownership, and insufficient balance.
9. Run the transfer-service and wallet-service build/tests and adjust any issues surfaced by validation.

## Verification

- Run the Transfer Service module tests and build.
- Verify the Wallet Service debits the sender and credits the receiver atomically.
- Confirm Wallet Service failures do not create a completed transfer history record.
- Confirm the endpoint remains protected by JWT authentication and only allows access to wallets owned by the current user.

## Questions / blockers

- The project currently treats transfers as RON-only and uses wallet ownership checks similar to the wallet service.
- User, Wallet, and Transfer services currently share the `walletx` PostgreSQL database configuration, but each service accesses only its own tables. Wallet ownership and balances remain owned by Wallet Service.
- Wallet balance updates and transfer history are separate service/database transactions. A saga/idempotency improvement is deferred; a process failure after the wallet update can leave the transfer history row missing.
- WalletX-010 remains the follow-up task for hardening validation and duplicate-request protection; this task stays focused on the core creation flow.

## Branch status

- Planned branch name: `WalletX-039-create-transfer-endpoint`
- Implementation work begins from the active task package once the task is moved to active and the plan is approved.
