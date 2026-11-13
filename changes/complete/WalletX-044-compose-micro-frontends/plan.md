# WalletX-044 Compose Micro-Frontends Plan

## Goal

Add the micro-frontend shell and the independently deployed frontend apps to the local Docker Compose environment so the WalletX assessment can be exercised end-to-end through the secure gateway and the browser-facing shell.

## Current state

- The backend stack is already defined in `infra/docker-compose.yml` for PostgreSQL, RabbitMQ, Kafka, the wallet service, transfer service, and the API gateway.
- The frontend container definitions for the auth, wallet, transfer, and notification micro frontends already exist in the same Compose file, but they still need to be validated as part of the overall shell experience.
- The shell and remote apps are expected to be runtime-composed via Module Federation and should remain independently deployable while being served from the local container network.

## Affected project areas

- `infra/docker-compose.yml` — add or finalize the micro-frontend shell service and ensure all frontend containers resolve correctly from the Compose network.
- `my-wallet-fe/apps/*` — validate vite runtime settings, remote entry points, and any gateway URLs used by the shell and remotes.
- `my-wallet-fe/docker/` and `Dockerfile` entries — confirm static serving and runtime environment-variable wiring.
- Optional documentation updates if needed for the local compose smoke-test workflow.

## Proposed implementation

1. Review the shell and micro-frontend runtime configuration together with the existing Docker images from WalletX-043.
2. Extend the Compose deployment with the shell container and any missing environment variables required for runtime module loading and API access.
3. Ensure the shell can discover each remote container by HTTP endpoint while keeping the remote apps independently buildable and deployable.
4. Confirm the API calls from the shell and micro frontends are routed through `http://api-gateway:8080` or the browser-accessible `localhost` equivalent used by the assessment flow.
5. Validate the authenticated flow with a local smoke test that starts the stack and checks shell + remote loading and gateway reachability.

## Verification plan

- Run `docker compose config` to validate Compose wiring and environment variables.
- Start the full stack with `docker compose up --build -d` from `infra/`.
- Check container health with `docker compose ps` and `curl`/`wget` against the shell and frontend endpoints.
- Exercise the login and wallet/transfer flow through the shell to confirm the app loads modules and reaches the gateway successfully.
- Capture any issues in the task notes and complete the final review before moving to completion.

## Questions / blockers

- No open design questions at this stage. The implementation can proceed with the existing shell + remote architecture once the Compose integration is reviewed against the runtime config for the frontend apps.

## Work status

- Task file moved to `changes/active/WalletX-044-compose-micro-frontends/`.
- Plan drafted and awaiting approval before implementation begins.
