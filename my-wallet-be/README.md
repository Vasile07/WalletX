# WalletX Backend

This backend workspace contains the Spring Boot services and shared security library for WalletX.

## Included services
- `user-service`
- `wallet-service`
- `transfer-service`
- `api-gateway`
- `security-common` (shared JWT authentication filter)

## Module structure
Each service follows the same package layout:
- `controller`
- `business`
- `persistence`
- `domain`
- `config`

## Run locally
From the repository root:

```bash
cd my-wallet-be
./gradlew build
./gradlew :api-gateway:bootRun
./gradlew :user-service:bootRun
./gradlew :wallet-service:bootRun
./gradlew :transfer-service:bootRun
```

## Health endpoints
Each service exposes a basic readiness endpoint:
- `/api/health`
- `/actuator/health`

The user service provides registration at `POST /auth/register`. It accepts a JSON body containing `name`, `email`, and `password`, and returns `201 Created` with the new user's `id`, `name`, and `email`. Invalid input returns `400 Bad Request`; an email that is already registered returns `409 Conflict`. Passwords are stored as BCrypt hashes and are not included in the response.

All backend services use PostgreSQL. Configure their connection with `DATABASE_URL`, `DATABASE_USERNAME`, and `DATABASE_PASSWORD`; local defaults point to `jdbc:postgresql://localhost:5432/walletx`.

Set `JWT_SECRET` to the same secret (at least 32 bytes) for the User, Wallet, and Transfer services. The Transfer Service calls the Wallet Service over REST at `WALLET_SERVICE_URL` (default `http://localhost:8082`). Configure `WALLET_INTERNAL_TRANSFER_TOKEN` to the same non-empty secret in both services; the Wallet Service requires this token as well as the caller's JWT on its internal transfer operation.

The Wallet Service owns wallet records and atomically applies the debit and credit in its own database transaction. The Transfer Service records history after Wallet Service confirms success. These operations span two services and are not one distributed transaction; a failure after the wallet update but before transfer-history persistence can leave the balances changed without a history row. A later idempotency/outbox or saga step is needed to close that failure window.

RabbitMQ is used for transfer completion work items. Start the broker locally from the repository root with `docker compose -f infra/docker-compose.yml up -d rabbitmq`, then configure `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USERNAME`, `RABBITMQ_PASSWORD`, and `RABBITMQ_AUTO_STARTUP` as needed. The Transfer Service publishes transfer-completion messages to the `walletx.transfer.exchange` exchange on the `transfer.completed` routing key and consumes them from the `walletx.transfer.completed.queue` with retry and dead-letter queues.

Kafka is used for event streaming. Start the broker locally with `docker compose -f infra/docker-compose.yml up -d kafka`, then configure `KAFKA_BOOTSTRAP_SERVERS` (default `localhost:9092`) and optionally `KAFKA_CONSUMER_GROUP` for the audit consumer. The Transfer Service emits versioned `transfer.completed` events to the `money-transfers` topic. The built-in `TransferEventConsumer` logs each event to provide a basic consumer verification path for independent downstream consumers.

The services do not require banking integrations or production infrastructure.
