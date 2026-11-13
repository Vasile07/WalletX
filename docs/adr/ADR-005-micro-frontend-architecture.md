# ADR-005: Define WalletX micro-frontend architecture

- Status: Accepted
- Date: 2026-11-13

## Context

The existing React frontend already groups the product by business area through modules such as authentication, wallets, transfers, and notifications. The assessment requires the application to be split into independently developed and deployable micro frontends, but the project still needs to preserve the current secure API contract and existing user experience.

The current app is a single Vite React application with a shell-like layout, route guards, and domain-specific modules. Although this structure is already modular, it is not yet deployable as independent frontends. We need an architecture that supports separate ownership, isolated builds, and clear boundaries without creating auth or API access drift.

## Decision

We adopt a shell-plus-remotes micro-frontend architecture for WalletX using React + Vite with runtime composition via Module Federation.

### Architectural model

- The `shell` application owns the global layout, route orchestration, auth session state, shared navigation, and app-level protection.
- Each business area becomes a separate remote micro frontend:
  - `auth-mfe` for login and registration
  - `wallet-mfe` for wallet listing, wallet creation, balances, and deposits
  - `transfer-mfe` for transfer creation and history
  - `notification-mfe` for unread notifications and in-app alerts
- The shell loads remote modules at runtime and renders them inside its routing tree.
- The remote apps remain independently buildable, but they are integrated only through the shell and a small shared contract layer.

### Why this approach

This is the best fit for the current project because:

- The current frontend is already organized by domain modules, so the migration is incremental rather than a rewrite.
- Vite and the current React setup can support a host + remote model through Module Federation without introducing a completely different frontend stack.
- The shell keeps the user experience consistent while each remote remains independently owned and deployed.
- Independent builds reduce coupling and align with the assessment requirement for independently developed and deployable micro frontends.

### Alternatives considered

#### 1. Full monorepo package split without runtime composition

This would reduce duplication, but it would not provide independent deployment boundaries or a clean runtime composition model. It also makes it harder to show the required micro-frontend separation during assessment.

#### 2. Separate standalone SPA applications with manual routing and shared auth via cookie/session broker

This would satisfy independent deployment but creates a weaker developer experience and more coupling between apps. It is also harder to maintain a coherent shell and consistent global navigation.

#### 3. Server-side composition or iframe-based composition

This is operationally simple for some cases, but it does not fit the current React architecture well and introduces stronger isolation and navigation friction than needed for WalletX.

## Responsibilities and interfaces

### Shell responsibilities

- Owns the application layout and top-level navigation.
- Maintains the `auth` context and JWT/session storage.
- Redirects unauthenticated users to the auth route.
- Provides a unified shell service for API access and shared error handling.
- Resolves which remote should render at each route and handles cross-module navigation.
- Aggregates page-level shell metadata such as breadcrumb, page title, and error boundaries.

### Auth micro frontend responsibilities

- Owns the login and registration experience.
- Calls the same gateway endpoints used by the current app (`/auth/login`, `/auth/register`).
- Returns a JWT or session token to the shell.
- Exposes a minimal contract for `login`, `register`, and `logout` operations.

### Wallet micro frontend responsibilities

- Owns wallet listing, wallet creation, balance reads, and deposit flows.
- Reads authenticated wallet data from the gateway and ensures wallet ownership checks remain enforced on the backend.
- Exposes wallet navigation and selection state to the shell via a shared contract.

### Transfer micro frontend responsibilities

- Owns transfer creation, transfer history, and validation state.
- Calls the gateway transfer endpoints and surfaces transfer results in the shell.
- Exposes transfer success/failure feedback to the shell and other remotes through a common event channel.

### Notification micro frontend responsibilities

- Owns notification listing and unread-state presentation.
- Reads the authenticated notification endpoints and shows in-app updates.
- Consumes the same async update model used by the backend notification flow.

## Shared contracts and cross-module behavior

### Authentication state

- The shell remains the single source of truth for the authenticated user and JWT.
- Authenticated state is stored in a shell-owned session store and exposed to remotes through a shared API or context contract.
- Each remote must never independently create a second trust boundary for user identity.
- The shell is responsible for attaching the bearer token to all protected requests made from remotes.

### API access

- The frontend continues to call the public API gateway, never service internals directly.
- A shared API client wrapper is created in the shell and reused by remotes through a package or shared library contract.
- All `Authorization` headers are injected centrally, so the same token handling is used across every remote.
- Remote apps must not bypass gateway routing, auth validation, or ownership checks.

### Routing

- The shell owns the route table for top-level navigation.
- Each remote exposes route segments assigned to its domain, such as `/auth`, `/wallets`, `/transfers`, and `/notifications`.
- The shell decides which remote handles a route and delegates rendering while retaining global navigation state.
- Deep links remain supported as long as the shell knows the route mapping.

### Shared dependencies

- React, React Router, and the common client utilities are treated as shared dependencies.
- Version pinning is required so remotes do not accidentally load multiple React copies.
- Shared UI primitives or auth helpers live in a small library package consumed by the shell and remotes.
- Domain-specific logic stays inside the owning remote instead of being duplicated into the shell.

### Cross-module navigation

- The shell owns cross-feature navigation and page transitions.
- Shared events are used only for lightweight state notifications, such as "wallet selected" or "transfer created".
- The shell remains the coordinator for app-wide actions that require navigation or session awareness.

## Local development workflow

1. Start the shell application in local development mode.
2. Start each remote application on a separate port.
3. Use a local host mapping or dev-server remote configuration so the shell resolves the remote entry points.
4. Use a shared development environment variable for the API base URL so all remotes target the same gateway.
5. Keep the auth token in a single shared session source to avoid inconsistent user state across modules.

This preserves a realistic development flow while still supporting isolated remote build pipelines.

## Deployment boundaries

- Each micro frontend is built and deployed independently.
- The shell is deployed as a host application that knows the remote locations and route map.
- Environmental configuration decides which remote bundle versions the shell loads at runtime.
- The gateway remains the same public backend API for all remotes, so the backend contract does not change across frontend deployment boundaries.

## Migration boundaries

### Phase 1

- Keep the current app as the shell and extract the auth experience into the first remote.

### Phase 2

- Extract wallet, transfer, and notification domains into separate remotes.

### Phase 3

- Consolidate the shared client and auth contract layer and finalize a strict route map for the shell.

This sequence minimizes behavioral risk and allows the team to preserve all current features while moving to independent frontend ownership.

## Consequences

### Positive

- Independent frontend ownership becomes possible without rewriting the backend.
- The shell keeps the developer experience coherent while each domain remains autonomous.
- The secure API gateway remains the single backend boundary for all modules.
- Route and session management stay centralized and easier to reason about.
- The architecture aligns with the assessment requirement to support independently developed and deployable frontends.

### Negative

- The shell and remotes must share strict contracts to avoid drift.
- Module Federation adds some operational complexity during local development.
- Shared dependency versioning must be disciplined to prevent duplicate React or router copies.
- Cross-module communication is intentionally limited so the architecture remains predictable.

## Related constraints

- The frontend must continue to call the existing API Gateway rather than service internals.
- Authentication and authorization decisions remain centralized in the shell and backend.
- The current behavior and secure API contracts must remain intact during migration.
- The architecture should minimize disruption to the current project while enabling future micro-frontend deployment.
