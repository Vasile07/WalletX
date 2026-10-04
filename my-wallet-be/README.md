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

Each service uses in-memory defaults and mock configuration so it can run without banking integrations or production infrastructure.
