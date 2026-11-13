# WalletX Frontend

The frontend is an npm workspace containing a Module Federation shell, four independently buildable React remotes, and a shared auth/API contract package.

## Local setup

1. Install dependencies:
   ```bash
   npm install
   ```
2. Copy the environment template when local overrides are needed:
   ```bash
   copy .env.example .env
   ```
3. Start the shell and all remotes:
   ```bash
   npm run dev
   ```
4. Open the shell at `http://localhost:5173`.

## Environment configuration

All workspace apps load environment values from this directory. The API base URL targets the public Nginx entry point:

```env
VITE_API_BASE_URL=http://localhost/api
```

The shell uses public remote entry URLs by default:

| Remote | Default remote entry |
| --- | --- |
| Authentication | `http://localhost/auth/remoteEntry.js` |
| Wallet | `http://localhost/wallet/remoteEntry.js` |
| Transfers | `http://localhost/transfer/remoteEntry.js` |
| Notifications | `http://localhost/notification/remoteEntry.js` |

Set `VITE_AUTH_MFE_REMOTE_URL`, `VITE_WALLET_MFE_REMOTE_URL`, `VITE_TRANSFER_MFE_REMOTE_URL`, or `VITE_NOTIFICATION_MFE_REMOTE_URL` to override a remote location for a shell build.

## Project structure

- `apps/shell` owns layout, top-level routes, route protection, and the sole auth/session provider.
- `apps/auth-mfe` owns login and registration.
- `apps/wallet-mfe` owns wallet listing, creation, and deposits.
- `apps/transfer-mfe` owns transfer creation and history.
- `apps/notification-mfe` owns the authenticated notification stream and feed.
- `packages/shared` provides the shared auth context contract and gateway API client.

## Available routes

- `/dashboard` — shell-owned dashboard
- `/auth` — authentication remote
- `/wallets` — wallet remote
- `/transfers` — transfer remote
- `/notifications` — notification remote

## Verification

The root commands run across all workspaces:

```bash
npm test
npm run lint
npm run build
```

Each app can also be built independently with `npm run build --workspace @walletx/<app-name>`. Remotes are exposed through Module Federation and share React, React Router, and `@walletx/shared` with the shell.

## Containerized builds

Each frontend app is also packaged as a production static-server image. The images build from the workspace root and expose a health endpoint at `/healthz`.

```bash
cd my-wallet-fe

docker build -f apps/shell/Dockerfile -t walletx-shell .
docker build -f apps/auth-mfe/Dockerfile -t walletx-auth-mfe .
docker build -f apps/wallet-mfe/Dockerfile -t walletx-wallet-mfe .
docker build -f apps/transfer-mfe/Dockerfile -t walletx-transfer-mfe .
docker build -f apps/notification-mfe/Dockerfile -t walletx-notification-mfe .
```

Run the images with runtime gateway and remote settings injected at startup:

```bash
docker run --rm -p 5173:80 \
  -e VITE_API_BASE_URL=http://host.docker.internal/api \
  -e VITE_AUTH_MFE_REMOTE_URL=http://host.docker.internal/auth/remoteEntry.js \
  -e VITE_WALLET_MFE_REMOTE_URL=http://host.docker.internal/wallet/remoteEntry.js \
  -e VITE_TRANSFER_MFE_REMOTE_URL=http://host.docker.internal/transfer/remoteEntry.js \
  -e VITE_NOTIFICATION_MFE_REMOTE_URL=http://host.docker.internal/notification/remoteEntry.js \
  walletx-shell

docker run --rm -p 5174:80 -e VITE_API_BASE_URL=http://host.docker.internal/api walletx-auth-mfe
docker run --rm -p 5175:80 -e VITE_API_BASE_URL=http://host.docker.internal/api walletx-wallet-mfe
docker run --rm -p 5176:80 -e VITE_API_BASE_URL=http://host.docker.internal/api walletx-transfer-mfe
docker run --rm -p 5177:80 -e VITE_API_BASE_URL=http://host.docker.internal/api walletx-notification-mfe
```

The entrypoint rewrites the built `localhost` URLs in the static bundle at startup, so the image stays portable while the actual gateway and remote addresses remain configurable at runtime.

## Compose with Nginx load balancer

The local Docker Compose stack exposes a single public entry point through Nginx on `http://localhost`. Browser requests are routed to the shell at `/`, API traffic is forwarded to `/api`, and each remote is served under its own path (`/auth`, `/wallet`, `/transfer`, `/notification`).

```bash
cd infra
docker compose up --build
docker compose up --scale api-gateway=2
```

Verify the public entry point and health checks:

```bash
curl -fsS http://localhost/healthz
curl -fsS http://localhost/api/actuator/health
```

The gateway can still be reached directly on `http://localhost:8080` for debugging, while the public path remains balanced through Nginx.
