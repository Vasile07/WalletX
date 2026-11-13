import React from 'react'
import { render, screen } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { describe, expect, it, vi } from 'vitest'
import { AppStateContext } from '@walletx/shared'
import { AppShell } from './AppShell'

function renderShell(path, authState) {
  render(
    <MemoryRouter initialEntries={[path]}>
      <AppStateContext.Provider value={authState}>
        <AppShell />
      </AppStateContext.Provider>
    </MemoryRouter>,
  )
}

describe('shell remote integration', () => {
  it('renders a remote inside the protected shell route with shell-owned auth state', async () => {
    renderShell('/wallets', {
      isAuthenticated: true,
      user: { name: 'Jane Doe' },
      token: 'shell-token',
      login: vi.fn(),
      register: vi.fn(),
      logout: vi.fn(),
    })

    expect(await screen.findByText(/remote rendered at \/wallets for jane doe: shell-token/i)).toBeTruthy()
  })

  it('redirects an unauthenticated protected route to the auth remote', async () => {
    renderShell('/wallets', {
      isAuthenticated: false,
      user: null,
      token: null,
      login: vi.fn(),
      register: vi.fn(),
      logout: vi.fn(),
    })

    expect(await screen.findByText(/remote rendered at \/auth for anonymous: no token/i)).toBeTruthy()
  })
})
