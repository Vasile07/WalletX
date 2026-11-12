import React from 'react'
import { fireEvent, render, screen } from '@testing-library/react'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { AppStateContext } from '../../../state/appState'
import { NotificationsPage } from './NotificationsPage'

function renderNotificationsPage() {
  render(
    <AppStateContext.Provider
      value={{
        isAuthenticated: true,
        user: { id: 'user-1', name: 'Jane Doe', email: 'jane@example.com' },
        token: 'demo-token',
        login: vi.fn(),
        register: vi.fn(),
        logout: vi.fn(),
      }}
    >
      <NotificationsPage />
    </AppStateContext.Provider>,
  )
}

describe('NotificationsPage', () => {
  beforeEach(() => {
    vi.unstubAllGlobals()
  })

  afterEach(() => {
    vi.unstubAllGlobals()
  })

  it('establishes the notification stream and renders live low-balance alerts', async () => {
    const stream = new ReadableStream({
      start(controller) {
        controller.enqueue(new TextEncoder().encode(
          'event: low_balance\n' +
            'data: {"id":"notification-1","type":"LOW_BALANCE","userId":"user-1","walletId":"wallet-1","message":"Wallet balance is below the configured threshold","currency":"RON","balance":95,"threshold":100,"createdAt":"2026-11-12T09:00:00Z"}\n\n',
        ))
        controller.close()
      },
    })

    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({ ok: true, body: stream }))

    renderNotificationsPage()

    expect(await screen.findByText(/low balance/i)).toBeTruthy()
    expect(screen.getByText(/wallet balance is below the configured threshold/i)).toBeTruthy()
    expect(global.fetch).toHaveBeenCalledWith(
      expect.stringContaining('/notifications'),
      expect.objectContaining({
        headers: expect.objectContaining({
          Authorization: 'Bearer demo-token',
          'X-User-Id': 'user-1',
        }),
      }),
    )
  })

  it('marks unread notifications as read', async () => {
    const stream = new ReadableStream({
      start(controller) {
        controller.enqueue(new TextEncoder().encode(
          'event: connected\n' +
            'data: {"id":"connected-1","type":"SYSTEM","message":"Connected to WalletX notifications","createdAt":"2026-11-12T09:10:00Z","connected":true}\n\n',
        ))
        controller.close()
      },
    })

    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({ ok: true, body: stream }))

    renderNotificationsPage()

    expect(await screen.findByText(/connected to walletx notifications/i)).toBeTruthy()
    fireEvent.click(screen.getByRole('button', { name: /mark all read/i }))
    expect(screen.getByText(/0 unread/i)).toBeTruthy()
  })
})
