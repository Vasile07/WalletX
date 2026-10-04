# WalletX Backend

This backend workspace contains the initial Spring Boot service skeletons for WalletX.

## Included services
- `user-service`
- `wallet-service`
- `transfer-service`

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
./gradlew :user-service:bootRun
./gradlew :wallet-service:bootRun
./gradlew :transfer-service:bootRun
```

## Health endpoints
Each service exposes a basic readiness endpoint:
- `/api/health`
- `/actuator/health`

The user service provides registration at `POST /auth/register`. It accepts a JSON body containing `name`, `email`, and `password`, and returns `201 Created` with the new user's `id`, `name`, and `email`. Invalid input returns `400 Bad Request`; an email that is already registered returns `409 Conflict`. Passwords are stored as BCrypt hashes and are not included in the response.

The user and wallet services use PostgreSQL. Configure their connection with `DATABASE_URL`, `DATABASE_USERNAME`, and `DATABASE_PASSWORD`; local defaults point to `jdbc:postgresql://localhost:5432/walletx`.

The services do not require banking integrations or production infrastructure.
