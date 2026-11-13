import React from 'react'
import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { afterEach, describe, expect, it, vi } from 'vitest'
import { apiRequest, useAppState } from '@walletx/shared'
import { AppStateProvider } from './AppStateProvider'

function SignInButton() {
  const { login } = useAppState()
  return <button onClick={() => login({ email: 'jane@example.com', password: 'password123' })}>Sign in</button>
}

describe('AppStateProvider', () => {
  afterEach(() => {
    localStorage.removeItem('walletx.auth.token')
    localStorage.removeItem('walletx.user')
    vi.unstubAllGlobals()
  })

  it('stores the login JWT in the shell session and uses it for shared API calls', async () => {
    const fetchMock = vi.fn()
      .mockResolvedValueOnce({
        ok: true,
        status: 200,
        text: async () => JSON.stringify({
          token: 'issued-token',
          user: { id: 'user-1', name: 'Jane Doe', email: 'jane@example.com' },
        }),
      })
      .mockResolvedValueOnce({
        ok: true,
        status: 200,
        text: async () => '[]',
      })
    vi.stubGlobal('fetch', fetchMock)

    render(
      <AppStateProvider>
        <SignInButton />
      </AppStateProvider>,
    )

    fireEvent.click(screen.getByRole('button', { name: 'Sign in' }))

    await waitFor(() => {
      expect(localStorage.getItem('walletx.auth.token')).toBe('issued-token')
    })
    await apiRequest('/wallets')

    expect(fetchMock.mock.calls[1][1].headers.Authorization).toBe('Bearer issued-token')
  })
})
