# WalletX Frontend

This frontend application provides the React shell for the WalletX digital wallet platform. It includes a shared layout and route placeholders for the planned authentication, wallet, transfer, and notification modules.

## Local setup

1. Install dependencies:
   ```bash
   npm install
   ```
2. Copy the environment template:
   ```bash
   copy .env.example .env
   ```
3. Start the app:
   ```bash
   npm run dev -- --host 0.0.0.0
   ```
4. Open the Vite dev server URL displayed in the terminal.

## Environment configuration

The app expects a base API URL for the backend gateway. The default value is:

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

## Project structure

- `src/components/AppShell.jsx` contains the shared sidebar layout and routing shell.
- `src/modules/*` stores module-specific placeholder pages for each feature area.
- `src/api/client.js` defines the shared API conventions and request helper.
- `src/state/appState.js` defines the app state context used by the shell and modules.

## Available routes

- `/auth` – authentication shell and session status placeholder
- `/wallets` – wallet management module placeholder
- `/transfers` – transfer workflow placeholder
- `/notifications` – notification feed placeholder

## Verification

Run the production build to check that the app compiles successfully:

```bash
npm run build
```
