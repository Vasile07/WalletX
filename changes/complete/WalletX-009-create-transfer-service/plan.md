# Plan for WalletX-009: Create Transfer Service Skeleton

## Proposed implementation

Create only the structural foundation for the Transfer Service. The `POST /transfers` and `GET /transfers` endpoints will be implemented as separate follow-up tasks.

### Scope and affected areas

- `my-wallet-be/transfer-service`: establish minimal domain, persistence, and business contracts, plus an empty `TransferController` mapped to `/transfers`.
- Keep the existing health endpoint and current application wiring intact.
- No wallet-service integration or changes are required for this skeleton task.
- Do not add controller endpoint methods; route behavior belongs to the separate endpoint tasks.

### Implementation steps

1. Review the Transfer Service module and conventions in neighboring backend services.
2. Add only the minimal transfer model, repository/service contracts, and empty `/transfers` controller needed to make follow-up work straightforward; do not implement transfer endpoint behavior, validation, balance mutation, or persistence workflows.
3. Preserve the existing health endpoint and service startup behavior.
4. Add or update focused tests for startup and health behavior, then run the transfer-service test/build checks.

### Verification

- Run the Transfer Service module's tests and build.
- Confirm the application starts and the existing health endpoint continues to work.
- Confirm the controller is registered but adds no `POST /transfers` or `GET /transfers` endpoint implementation.

### Questions / blockers

- The core `POST /transfers` flow and the `GET /transfers` endpoint are tracked separately as WalletX-039 and WalletX-040.
- WalletX-010 remains the follow-up task for transfer validation hardening and duplicate-request protection; WalletX-011 remains the combined deposit/transfer transaction history task.
- No other blocking questions at the moment.

### Branch status

- Planned branch name: `WalletX-009-create-transfer-service`
- Branch creation will happen only after the plan is approved.
