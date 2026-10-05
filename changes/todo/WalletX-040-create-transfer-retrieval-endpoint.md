# WalletX-040-create-transfer-retrieval-endpoint

## Task

- **ID:** WalletX-040
- **Title:** Create Transfer Retrieval Endpoint
- **Status:** Todo
- **Priority:** High
- **Created:** 2026-10-05
- **Updated:** 2026-10-05

## Description

Implement `GET /transfers` to retrieve transfer history for the authenticated user.

## Objective

Allow users to view transfer records they are authorized to access.

## Scope

### In scope

- Implement `GET /transfers` for retrieving relevant transfer history.
- Apply authenticated-user access rules to returned transfer records.
- Add focused tests for retrieval and access control.

### Out of scope

- `POST /transfers` creation endpoint.
- `GET /transfers/{id}` unless separately specified.
- Pagination/filtering enhancements beyond existing project conventions.

## Acceptance Criteria

- [ ] The endpoint returns transfer history for the authenticated user according to the API contract.
- [ ] A user cannot retrieve transfer history belonging only to another user.
- [ ] Empty history is returned successfully.
- [ ] Tests cover normal retrieval, empty history, and access boundaries.

## Implementation Tasks

- [ ] Add the authenticated transfer-history retrieval operation to the controller and service.
- [ ] Implement repository queries for the authorized user's transfer records.
- [ ] Add tests for retrieval and authorization behavior.

## Dependencies

- WalletX-009.

## Notes

This endpoint can be implemented independently of WalletX-039 once the transfer-service skeleton and persistence types are in place. The current project documentation lists `GET /transfers` without query parameters. Revisit pagination if the API contract changes.
