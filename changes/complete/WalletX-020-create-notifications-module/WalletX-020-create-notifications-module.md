# WalletX-020-create-notifications-module

## Task

- **ID:** WalletX-020
- **Title:** Create Notifications Module
- **Status:** Active
- **Priority:** Medium
- **Created:** 2026-10-04
- **Updated:** 2026-11-12

## Description

Display real-time server notifications in the React application.

## Objective

Make transfer, deposit, and low-balance events visible to the correct user as they occur.

## Scope

### In scope

- SSE client lifecycle and reconnect handling.
- Notification list/toast presentation and read/display state.
- Handling of transfer and low-balance notification types.

### Out of scope

- Backend SSE implementation.
- Email/push notifications.

## Acceptance Criteria

- [x] Authenticated users receive and display their own SSE notifications.
- [x] Disconnects and reconnects are handled without duplicate subscriptions.
- [x] Notification rendering supports documented event types.

## Implementation Tasks

- [x] Implement SSE client and notification state management.
- [x] Build notification UI and tests.

## Dependencies

- WalletX-015, WalletX-017.

## Notes

Keep connection state visible enough for useful failure feedback.
