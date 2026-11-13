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
