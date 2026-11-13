# WalletX-042 Implementation Plan

## Objective

Migrate the current Vite/React frontend into a runtime-composed shell and four independently buildable micro frontends, preserving existing route behavior, authentication, secured gateway access, and feature tests.

## Affected project areas

- `my-wallet-fe/` — convert the existing single app into the shell and independently buildable auth, wallet, transfer, and notification apps.
- `changes/active/WalletX-042-migrate-to-micro-frontends/` — retain this task and its implementation plan throughout active work.

## Proposed implementation

1. Establish a frontend workspace structure with separate Vite applications for the shell and each business-area remote, using the existing app as the migration source.
2. Configure Module Federation for runtime composition, following ADR-005. Keep React and React Router shared and version-aligned, expose each remote's route/page entry point, and configure local remote URLs and ports.
3. Keep the shell responsible for layout, top-level routing, protected-route behavior, and the sole authentication/session state. Extract a small shared contract/API client boundary so remotes receive the shell-owned token and use the same gateway URL, bearer-header injection, and API error handling.
4. Move existing feature pages and their tests into the appropriate remotes. Keep dashboard and global navigation behavior in the shell, and preserve the existing deep links and unauthenticated redirects.
5. Add a root development workflow that starts the shell and all remotes together, document per-app build/run commands and required environment configuration, and update the frontend README.
6. Add focused integration tests for shell route composition and shared authentication/API behavior; retain and adapt the existing feature tests for migrated pages.

## Verification

- Run the frontend lint and test commands after migration, including the shell/remote integration and existing migrated feature tests.
- Build the shell and every remote independently to verify their deployment boundaries.
- Start the documented local combined workflow and verify that the shell resolves all remotes.
- Verify protected calls receive the shell-owned bearer token, API failures remain surfaced as before, deep links render the expected remote, and unauthenticated routes retain their redirect behavior.

## Questions requiring user input

- None. ADR-005 specifies Module Federation and the shell-owned auth/API boundaries; implementation details will follow the existing frontend conventions.

## Status

Plan approved; implementation is complete and ready for user review on `WalletX-042-migrate-to-micro-frontends`.
