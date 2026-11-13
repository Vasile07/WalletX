import React from 'react'
import { render, screen } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { AppStateContext, apiRequest } from '@walletx/shared'
import { DashboardPage } from './DashboardPage'

vi.mock('@walletx/shared', async (importOriginal) => ({
  ...await importOriginal(),
  apiRequest: vi.fn(),
}))

describe('DashboardPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('loads the wallet summary and recent transfer activity', async () => {
    apiRequest.mockImplementation((path) => {
      if (path === '/wallets') {
        return Promise.resolve([
          { id: 'wallet-1', userId: 'user-1', currency: 'RON', balance: 425.5 },
          { id: 'wallet-2', userId: 'user-1', currency: 'RON', balance: 75 },
        ])
      }

      if (path === '/transfers') {
        return Promise.resolve([
          { id: 'transfer-1', senderWalletId: 'wallet-1', receiverWalletId: 'wallet-2', amount: 125, currency: 'RON', direction: 'SENT', createdAt: '2026-10-10T12:00:00Z' },
          { id: 'transfer-2', senderWalletId: 'wallet-2', receiverWalletId: 'wallet-1', amount: 35, currency: 'RON', direction: 'RECEIVED', createdAt: '2026-10-09T09:30:00Z' },
        ])
      }

      return Promise.resolve(null)
    })

    render(
      <MemoryRouter>
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
          <DashboardPage />
        </AppStateContext.Provider>
      </MemoryRouter>,
    )

    expect(await screen.findByText(/current balance/i)).toBeTruthy()
    expect(await screen.findByRole('link', { name: /send transfer/i })).toBeTruthy()
    expect(screen.getByText(/latest activity/i)).toBeTruthy()
  })

  it('handles an empty wallet state', async () => {
    apiRequest.mockImplementation((path) => {
      if (path === '/wallets') {
        return Promise.resolve([])
      }

      if (path === '/transfers') {
        return Promise.resolve([])
      }

      return Promise.resolve(null)
    })

    render(
      <MemoryRouter>
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
          <DashboardPage />
        </AppStateContext.Provider>
      </MemoryRouter>,
    )

    expect(await screen.findByText(/no wallets added yet/i)).toBeTruthy()
  })

  it('shows dashboard errors when data loading fails', async () => {
    apiRequest.mockRejectedValue(new Error('API is temporarily unavailable'))

    render(
      <MemoryRouter>
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
          <DashboardPage />
        </AppStateContext.Provider>
      </MemoryRouter>,
    )

    expect(await screen.findByText(/api is temporarily unavailable/i)).toBeTruthy()
  })
})
