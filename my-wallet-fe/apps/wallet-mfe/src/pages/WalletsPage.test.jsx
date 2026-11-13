import React from 'react'
import { fireEvent, render, screen } from '@testing-library/react'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { AppStateContext, apiRequest } from '@walletx/shared'
import { WalletsPage } from './WalletsPage'

vi.mock('@walletx/shared', async (importOriginal) => ({
  ...await importOriginal(),
  apiRequest: vi.fn(),
}))

describe('WalletsPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('loads wallet data and supports a valid deposit', async () => {
    apiRequest.mockImplementation((path, options = {}) => {
      if (path === '/wallets' && !options.method) {
        return Promise.resolve([
          { id: 'wallet-1', userId: 'user-1', currency: 'RON', balance: 100 },
        ])
      }

      if (path === '/wallets/wallet-1/deposit') {
        return Promise.resolve({ id: 'deposit-1', balance: 150.25 })
      }

      return Promise.resolve(null)
    })

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
        <WalletsPage />
      </AppStateContext.Provider>,
    )

    expect(await screen.findByText(/wallet overview/i)).toBeTruthy()
    fireEvent.change(screen.getByLabelText(/deposit amount/i), { target: { value: '50.25' } })
    fireEvent.click(screen.getByRole('button', { name: /submit deposit/i }))

    expect(await screen.findByText(/deposit received/i)).toBeTruthy()
  })

  it('creates a wallet for the signed-in user', async () => {
    apiRequest.mockImplementation((path, options = {}) => {
      if (path === '/wallets' && !options.method) {
        return Promise.resolve([])
      }

      if (path === '/wallets' && options.method === 'POST') {
        return Promise.resolve({ id: 'wallet-2', userId: 'user-1', currency: 'RON', balance: 0 })
      }

      return Promise.resolve(null)
    })

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
        <WalletsPage />
      </AppStateContext.Provider>,
    )

    fireEvent.click(screen.getByRole('button', { name: /create wallet/i }))

    expect(await screen.findByText(/new wallet created successfully/i)).toBeTruthy()
  })
})
