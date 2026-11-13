# WalletX-025-create-docker-compose-configuration Plan

## Objective
Define a single local Docker Compose stack that starts the WalletX application services, infrastructure dependencies, and supporting components in a reproducible way.

## Affected areas
- `infra/docker-compose.yml`: complete the compose topology with services, networks, volumes, health checks, and wiring.
- `my-wallet-be`: confirm each service can connect by Compose service names and expected ports.
- `my-wallet-fe`: verify the frontend is exposed and uses the correct API gateway host/port in the local stack.
- `docs/`: document the local startup flow, persistence behavior, and reset procedure.

## Proposed implementation
1. Review the existing Dockerfiles and runtime configuration for the frontend, gateway, user-service, wallet-service, transfer-service, and any function dependencies to confirm names, ports, and env requirements.
2. Extend the compose definition to include all required application services and supporting infrastructure, including PostgreSQL, RabbitMQ, and Kafka, with consistent naming and port mapping.
3. Add networks and named volumes, then wire `depends_on` and health checks so the stack starts reliably and services wait for dependencies to become ready.
4. Set environment values to use Compose service names instead of localhost and keep the default local deployment straightforward for `docker compose up`.
5. Validate the stack with Docker Compose config checks and a smoke flow against the app endpoints to confirm the environment works end-to-end.

## Verification
- `docker compose config` succeeds without errors.
- `docker compose up --build` brings the stack up with active healthchecks and no startup loops.
- Application services can reach PostgreSQL, RabbitMQ, and Kafka using their configured names and ports.
- The app can complete a basic end-to-end flow through the composed environment.
- Persistence and reset behavior are documented for local development use.

## Questions
- The current repository already contains the RabbitMQ and Kafka definitions; the planned work assumes the remaining WalletX services and PostgreSQL instances will be added to the same compose file.
- No default profile preference has been specified, so the initial plan assumes all required parts of the stack start together unless a follow-up decision is needed for resource-constrained local machines.
