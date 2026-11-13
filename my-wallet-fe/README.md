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

All workspace apps load environment values from this directory. The API base URL targets the backend gateway:

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

The shell uses local remote entry URLs by default:

| Remote | Default remote entry |
| --- | --- |
| Authentication | `http://localhost:5174/remoteEntry.js` |
| Wallet | `http://localhost:5175/remoteEntry.js` |
| Transfers | `http://localhost:5176/remoteEntry.js` |
| Notifications | `http://localhost:5177/remoteEntry.js` |

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
  -e VITE_API_BASE_URL=http://host.docker.internal:8080/api \
  -e VITE_AUTH_MFE_REMOTE_URL=http://host.docker.internal:5174/remoteEntry.js \
  -e VITE_WALLET_MFE_REMOTE_URL=http://host.docker.internal:5175/remoteEntry.js \
  -e VITE_TRANSFER_MFE_REMOTE_URL=http://host.docker.internal:5176/remoteEntry.js \
  -e VITE_NOTIFICATION_MFE_REMOTE_URL=http://host.docker.internal:5177/remoteEntry.js \
  walletx-shell

docker run --rm -p 5174:80 -e VITE_API_BASE_URL=http://host.docker.internal:8080/api walletx-auth-mfe
docker run --rm -p 5175:80 -e VITE_API_BASE_URL=http://host.docker.internal:8080/api walletx-wallet-mfe
docker run --rm -p 5176:80 -e VITE_API_BASE_URL=http://host.docker.internal:8080/api walletx-transfer-mfe
docker run --rm -p 5177:80 -e VITE_API_BASE_URL=http://host.docker.internal:8080/api walletx-notification-mfe
```

The entrypoint rewrites the built `localhost` URLs in the static bundle at startup, so the image stays portable while the actual gateway and remote addresses remain configurable at runtime.
