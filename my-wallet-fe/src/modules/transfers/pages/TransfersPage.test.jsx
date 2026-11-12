import React from 'react'
import { fireEvent, render, screen } from '@testing-library/react'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { AppStateContext } from '../../../state/appState'
import { apiRequest } from '../../../api/client'
import { TransfersPage } from './TransfersPage'

vi.mock('../../../api/client', () => ({
  apiRequest: vi.fn(),
  apiRoutes: {
    wallets: '/wallets',
    transfers: '/transfers',
  },
}))

describe('TransfersPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('loads wallet and transfer data, then submits a valid transfer', async () => {
    apiRequest.mockImplementation((path, options = {}) => {
      if (path === '/wallets') {
        return Promise.resolve([
          { id: 'wallet-1', userId: 'user-1', currency: 'RON', balance: 250 },
          { id: 'wallet-2', userId: 'user-1', currency: 'RON', balance: 75 },
        ])
      }

      if (path === '/transfers' && !options.method) {
        return Promise.resolve([
          {
            id: 'transfer-1',
            direction: 'SENT',
            senderWalletId: 'wallet-1',
            receiverWalletId: 'wallet-2',
            counterpartyWalletId: 'wallet-2',
            amount: 25,
            currency: 'RON',
            completedAt: '2026-10-10T12:00:00Z',
          },
        ])
      }

      if (path === '/transfers' && options.method === 'POST') {
        return Promise.resolve({
          id: 'transfer-2',
          senderWalletId: 'wallet-1',
          receiverWalletId: 'wallet-2',
          amount: 45,
          currency: 'RON',
        })
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
        <TransfersPage />
      </AppStateContext.Provider>,
    )

    expect(await screen.findByText(/transfer overview/i)).toBeTruthy()

    fireEvent.change(screen.getByLabelText(/receiver wallet id/i), { target: { value: 'wallet-2' } })
    fireEvent.change(screen.getByLabelText(/amount/i), { target: { value: '45.00' } })
    fireEvent.click(screen.getByRole('button', { name: /send transfer/i }))

    expect(await screen.findByText(/transfer sent successfully/i)).toBeTruthy()
  })

  it('blocks transfers that exceed the sender balance', async () => {
    apiRequest.mockImplementation((path, options = {}) => {
      if (path === '/wallets') {
        return Promise.resolve([{ id: 'wallet-1', userId: 'user-1', currency: 'RON', balance: 50 }])
      }

      if (path === '/transfers' && !options.method) {
        return Promise.resolve([])
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
        <TransfersPage />
      </AppStateContext.Provider>,
    )

    expect(await screen.findByText(/transfer overview/i)).toBeTruthy()

    fireEvent.change(screen.getByLabelText(/receiver wallet id/i), { target: { value: 'wallet-2' } })
    fireEvent.change(screen.getByLabelText(/amount/i), { target: { value: '75.00' } })
    fireEvent.click(screen.getByRole('button', { name: /send transfer/i }))

    expect(await screen.findByText(/insufficient funds/i)).toBeTruthy()
  })
})
