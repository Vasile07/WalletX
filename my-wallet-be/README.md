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

## Docker images
Each backend module also includes a production-style Dockerfile. Build them from the backend root with the module-specific context:

```bash
cd my-wallet-be

docker build -f api-gateway/Dockerfile -t walletx/api-gateway .
docker build -f user-service/Dockerfile -t walletx/user-service .
docker build -f wallet-service/Dockerfile -t walletx/wallet-service .
docker build -f transfer-service/Dockerfile -t walletx/transfer-service .
```

The images read the same runtime settings already exposed by the Spring configuration, meaning secrets and service addresses are injected through environment variables instead of being baked into the image. Typical runtime variables include:

- `JWT_SECRET`
- `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`
- `USER_SERVICE_URL`, `WALLET_SERVICE_URL`, `TRANSFER_SERVICE_URL`
- `WALLET_INTERNAL_TRANSFER_TOKEN`
- `WALLET_LOW_BALANCE_THRESHOLD`
- `WALLET_LOW_BALANCE_NOTIFICATION_TOKEN`
- `NOTIFICATION_GATEWAY_URL`
- `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USERNAME`, `RABBITMQ_PASSWORD`
- `KAFKA_BOOTSTRAP_SERVERS`, `KAFKA_CONSUMER_GROUP`

Example:

```bash
docker run --rm -p 8081:8081 \
  -e DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/walletx \
  -e DATABASE_USERNAME=walletx \
  -e DATABASE_PASSWORD=walletx \
  -e JWT_SECRET=change-me \
  walletx/user-service
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

The API Gateway exposes an authenticated SSE endpoint at `GET /notifications` for per-user real-time updates. Clients connect with the same JWT used for the protected routes, and the stream emits event names such as `transfer_sent`, `transfer_received`, `transfer_failed`, `deposit_completed`, `low_balance`, and `system`. Each event carries a JSON payload with the user, message, and optional wallet/transfer/deposit identifiers. Low-balance events also include the currency, current balance, and configured threshold. The stream also sends a `connected` event at subscription time so reconnect logic can recover state cleanly.

The Wallet Service emits a `low_balance` SSE notification after a committed transfer crosses the configured RON threshold from at-or-above to below. The threshold defaults to `100.00` and can be changed with `WALLET_LOW_BALANCE_THRESHOLD`. For delivery, configure `WALLET_LOW_BALANCE_NOTIFICATION_TOKEN` to the same non-empty secret in the Wallet Service and API Gateway. Set `NOTIFICATION_GATEWAY_URL` in the Wallet Service when the Gateway is not reachable at `http://localhost:8080`. The authenticated notification payload includes the wallet ID, currency, current balance, and threshold.

The services do not require banking integrations or production infrastructure.
