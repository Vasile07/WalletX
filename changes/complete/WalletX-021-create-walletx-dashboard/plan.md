# Plan for WalletX-021: Create WalletX Dashboard

## Proposed implementation

1. Add a dedicated dashboard module in `my-wallet-fe/src/modules/dashboard` or a lightweight page component under the existing modules structure.
2. Build a dashboard view model that aggregates the authenticated user, wallet totals, recent transfers, and unread notifications from the existing APIs and app state.
3. Create a responsive dashboard layout with:
   - balance overview card
   - quick actions for wallet, transfer, and notifications
   - recent transactions list
   - status/loading/empty/error handling
4. Wire the dashboard into app routing and set the default authenticated landing page to the dashboard if the product decision requires it.
5. Add frontend tests covering loading, empty, and failure states alongside the happy path.

## Affected project areas

- `my-wallet-fe/src/components/AppShell.jsx`
- `my-wallet-fe/src/modules/*` (wallets, transfers, notifications)
- `my-wallet-fe/src/App.css` or matching component styling
- `my-wallet-fe/src/api/client.js` only if the dashboard needs a new fetch helper or view model transformation

## Verification steps

- Run `npm test -- --run` in `my-wallet-fe` after implementing the dashboard UI and tests.
- Run `npm run build` to confirm the React app compiles correctly.
- Manually verify the dashboard loads with signed-in state, shows empty and failure handling, and routes cleanly from the primary navigation.

## Open question for approval

- Should the dashboard replace the current authenticated landing route (`/wallets`) as the default page for signed-in users, or should it be added as an additional landing module accessible from the sidebar?

## Notes

The existing frontend already provides wallets, transfers, and notifications pages plus the authenticated app shell. The dashboard should reuse that data model and service patterns instead of introducing a second source of truth.
