# WalletX Service Architecture Baseline

## 1. Purpose

This document defines the service architecture baseline for WalletX. Its goal is to prevent duplicated business logic, unclear ownership, and mixed persistence boundaries while keeping the initial release simple and testable.

The architecture follows a modular microservice model with one public API boundary and clearly separated ownership of user data, wallet data, transfer data, and async event delivery. For the initial solo-developed version, all backend services use one shared PostgreSQL database.

## 2. Core architectural principles

- Each domain service owns its primary business data and persistence model.
- The initial deployment uses one PostgreSQL database shared by the backend services; service boundaries describe logical ownership, not separate database servers or databases.
- The frontend does not call multiple services directly; it calls the gateway/public API.
- Shared cross-cutting functions such as authentication and authorization stay centralized around JWT validation and ownership checks.
- The transfer workflow may use synchronous REST calls for state validation, but completion and downstream side effects are propagated via asynchronous messaging.
- The initial release is RON-only and supports simulated deposits only.

## 3. Component map

```text
                            +----------------------+
                            | React Frontend       |
                            | - login              |
                            | - wallets            |
                            | - transfers          |
                            | - notifications      |
                            +----------+-----------+
                                       |
                                       v
                  +--------------------+--------------------+
                  | API Gateway / Web Entry Point          |
                  | - JWT validation                       |
                  | - route enforcement                    |
                  | - request aggregation / public API     |
                  +----------+-----------------------------+
                             |
         +-------------------+-------------------+
         |                                       |
         v                                       v
+------------------+                  +----------------------+
| User Service     |                  | Wallet Service       |
| - register/login |                  | - wallet CRUD        |
| - profile        |                  | - RON balance        |
| - JWT issuance  |                  | - simulated deposits |
| - auth checks    |                  | - balance checks     |
+--------+---------+                  +----------+-----------+
         |                                         |
         |                                         v
         |                               +----------------------+
         |                               | Transfer Service     |
         |                               | - create transfer     |
         |                               | - validate funds     |
         |                               | - persist transfer   |
         |                               | - publish events      |
         |                               +----------+-----------+
         |                                          |
         |                                          v
         |                                 +----------------------+
         |                                 | RabbitMQ / Kafka     |
         |                                 | - transfer events    |
         |                                 | - notification tasks |
         |                                 +----------+-----------+
         |                                            |
         |                                            v
         +-----------------------------------------> Notification Service / Worker
                                                           - low-balance notifications
                                                           - transfer notifications
                                                           - SSE delivery to frontend
```

## 4. Service responsibilities and data ownership

| Component | Primary responsibility | Owns data | Notes |
|---|---|---|---|
| Frontend | user interactions, wallet UI, transfer flows, notification display | none directly | Calls gateway REST endpoints and consumes SSE notifications |
| API Gateway | public entry point and routing | none directly | Performs authentication routing and acts as a single public API surface |
| User Service | registration, authentication, credential validation, JWT creation, user profiles | users, credentials, auth metadata | Must not own wallet balance logic or transfer records |
| Wallet Service | wallet lifecycle, balance tracking, simulated deposit handling | wallets, wallet transactions, balance state | Owns money movement within wallet scope; does not authorize transfers to other users |
| Transfer Service | transfer execution, validation, status tracking, event publication | transfers, transfer status, transfer ledger | Coordinates validation and records transfer attempts, using the wallet service as the source of truth for balances |
| Notification Service | inbox/alert generation, low-balance checks, transfer notifications | notifications, outbox/processing state | Consumes events from the broker and pushes updates to connected clients |
| PostgreSQL | one shared database | users, wallets, transfers, and notifications | Services use logically separate tables and only their owning service writes its domain data |
| RabbitMQ / Kafka | async messaging and event streaming | broker topics/queues | RabbitMQ is for task-oriented work; Kafka is the integration/event stream |

## 5. Public API boundaries

### 5.1 Frontend to gateway / public API

The frontend calls only the gateway and never reaches service internals directly.

Public routes:

- `POST /auth/register`
- `POST /auth/login`
- `GET /users/me`
- `POST /wallets`
- `GET /wallets`
- `GET /wallets/{walletId}`
- `GET /wallets/{walletId}/balance`
- `POST /wallets/{walletId}/deposit`
- `POST /transfers`
- `GET /transfers`
- `GET /transfers/{transferId}`
- `GET /notifications` (or equivalent notification endpoint)

### 5.2 Service-to-service communication

#### Synchronous REST

- Gateway to User Service for authentication and profile checks
- Gateway to Wallet Service for wallet and balance queries
- Gateway to Transfer Service for transfer request execution
- Transfer Service to Wallet Service for balance validation when needed

#### Asynchronous messaging

- Transfer Service publishes transfer events to RabbitMQ or Kafka after successful validation.
- Notification Service consumes those events and creates notifications.
- The event stream may also support auditing or analytics consumers without adding coupling to the core transfer workflow.

## 6. Authentication and ownership model

- The User Service is responsible for registering users and issuing JWTs.
- The API Gateway validates the token and passes the authenticated principal to downstream services.
- Each protected downstream service must validate that the caller has permission to access the target resource.
- Wallet ownership checks are enforced by wallet-scoped access rules: a user may only read or mutate their own wallet or transfer history.
- The Transfer Service must not silently trust client-provided sender/receiver identifiers; it validates them against the authenticated user and authoritative wallet metadata.

This preserves a clear boundary: identity is managed by the User Service, while ownership and balances are enforced in the Wallet and Transfer services.

## 7. Data ownership rules

- User Service owns user identities and auth records.
- Wallet Service owns wallet records, balance state, and simulation of deposits.
- Transfer Service owns transfer records and transfer status transitions.
- Notification Service owns user notifications generated from broker events.
- No service should duplicate the canonical balance or user identity data of another service.

When a transfer is created, the Transfer Service should not become the source of truth for wallet balances. It may read wallet state or use a validated response from the wallet domain, but the wallet domain remains authoritative for balance calculations.

### Database and ORM

The initial implementation uses one PostgreSQL database for the application; it does not require a separate database per service. Each Spring Boot service uses Java ORM (Spring Data JPA with Hibernate) for persistence. Project Lombok is used selectively in domain classes to reduce repetitive code; setters are not generated by default so domain state changes remain explicit through constructors and domain methods. Tables remain logically owned by their domain service: a service may read or write its own tables, while cross-domain access goes through service APIs or events rather than direct writes to another service's tables. This keeps local development and deployment simple without giving up clear ownership. The database can be split later if scaling or operational needs justify it.

## 8. Async communication and event flow

```text
Frontend
  |
  v
Gateway REST API
  |
  +--> User Service (auth / profile)
  |
  +--> Wallet Service (wallets / balances / deposits)
  |
  +--> Transfer Service (transfer creation)
         |
         v
      Transfer Service
         |
         +--> Wallet validation / balance check
         |
         +--> publish TransferCompleted or TransferFailed event
                    |
                    v
               RabbitMQ / Kafka
                    |
                    +--> Notification Service
                    |       - create notifications
                    |       - push SSE updates
                    |
                    +--> Audit / analytics consumers (optional)
```

## 9. RON-only and simulated deposit constraints

These constraints are mandatory for the initial release:

- All wallets use `currency = "RON"`.
- All transfers are expressed in RON; no multi-currency or exchange-rate logic is included.
- Deposits are simulated only; they represent a local wallet top-up scenario for demonstration purposes.
- No external payment provider, bank integration, or real-money settlement flow is in scope.
- Validation rules must reject non-RON transfer payloads, invalid amounts, and insufficient balances.
- The low-balance notification rule is triggered when configured thresholds are breached, but must still be based on RON balances only.

## 10. Architectural decisions summary

1. WalletX will be built as a small set of domain-oriented services rather than a single monolith.
2. User, Wallet, Transfer, and Notification services each own their distinct domain data, stored initially in one shared PostgreSQL database.
3. The API Gateway is the only public front door for the application.
4. RabbitMQ/Kafka supports async communication and decouples notifications from the transaction flow.
5. JWT-based security remains the access-control standard across protected endpoints.
6. The initial release remains RON-only with simulated money, and it deliberately excludes real banking or cross-currency operations.

## 11. Acceptance mapping

This document satisfies the task baseline by covering:

- domain responsibilities and data boundaries
- public routes and service-to-service communication
- RON-only and simulated-deposit constraints
- ownership and authentication boundaries

## 12. Later implementation guidance

This architecture baseline is intended to guide the implementation tasks that follow. When services are created, they should align with the boundaries above and avoid introducing shared wallet logic or duplicated transaction records across service boundaries.
