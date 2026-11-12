# Plan

1. Add a secure live notification stream that authenticates with the JWT and user id headers.
2. Parse incoming SSE events into structured notification items for transfer, deposit, and low-balance updates.
3. Render a read/unread notification list with reusable filters and visible connection state.
4. Test the live stream and read-state interactions to confirm reconnect and UI behavior remain stable.
