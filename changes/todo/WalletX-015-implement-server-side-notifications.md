# WalletX-015-implement-server-side-notifications

## Task

- **ID:** WalletX-015
- **Title:** Implement Server-Side Notifications
- **Status:** Todo
- **Priority:** High
- **Created:** 2026-10-04
- **Updated:** 2026-10-04

## Description

Deliver transfer and deposit notifications from the backend to connected web clients through SSE.

## Objective

Provide real-time, user-scoped updates for important wallet events.

## Scope

### In scope

- SSE connection endpoint and authenticated user channel management.
- Notifications for sent/received transfers, failed transfers, and completed deposits.
- Notification persistence or replay policy for reconnecting clients.

### Out of scope

- Browser push notifications.
- Email or SMS delivery.

## Acceptance Criteria

- [ ] Authenticated clients receive only their own notifications.
- [ ] Relevant events produce the documented notification types.
- [ ] Reconnect and disconnect behavior does not leak resources.

## Implementation Tasks

- [ ] Implement SSE endpoint, publisher, and user subscription registry.
- [ ] Connect event/message consumers to notification creation and delivery.

## Dependencies

- WalletX-008, WalletX-013, WalletX-014.

## Notes

SSE is the selected real-time notification technology.
