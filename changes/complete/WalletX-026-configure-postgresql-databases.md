# WalletX-026-configure-postgresql-databases

## Task

- **ID:** WalletX-026
- **Title:** Configure PostgreSQL Databases
- **Status:** Todo
- **Priority:** Critical
- **Created:** 2026-10-04
- **Updated:** 2026-10-04

## Description

Define persistence and database configuration for the WalletX services.

## Objective

Give each service clear ownership of durable data while keeping local setup reproducible.

## Scope

### In scope

- PostgreSQL schemas/databases for user, wallet, transfer, and notification data as appropriate.
- Migrations, connection settings, indexes, and transaction configuration.
- Local credentials and volume configuration through environment variables.

### Out of scope

- Data warehouse or analytics storage.
- Production backup operations.

## Acceptance Criteria

- [ ] Services initialize and migrate their databases successfully.
- [ ] Data ownership boundaries prevent accidental cross-service writes.
- [ ] Local database state persists and can be reset as documented.

## Implementation Tasks

- [ ] Define schemas, migrations, indexes, and service connections.
- [ ] Verify persistence through create, deposit, transfer, and history flows.

## Dependencies

- WalletX-005, WalletX-007, WalletX-009, WalletX-025.

## Notes

Prefer separate database boundaries per service where practical.
