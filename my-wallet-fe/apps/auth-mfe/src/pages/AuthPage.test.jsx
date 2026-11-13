import React from 'react'
import { fireEvent, render, screen } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'
import { AppStateContext } from '@walletx/shared'
import { AuthPage } from './AuthPage'

describe('AuthPage', () => {
  it('validates required login fields', async () => {
    const login = vi.fn().mockResolvedValue({ name: 'WalletX User', email: 'walletx@example.com' })

    render(
      <AppStateContext.Provider
        value={{
          isAuthenticated: false,
          user: null,
          token: null,
          login,
          register: vi.fn(),
          logout: vi.fn(),
        }}
      >
        <AuthPage />
      </AppStateContext.Provider>,
    )

    fireEvent.click(screen.getByRole('button', { name: /choose sign in form/i }))
    fireEvent.click(screen.getByRole('button', { name: /^sign in$/i }))

    expect(await screen.findByText(/email is required/i)).toBeTruthy()
  })

  it('allows a user to submit a valid registration form', async () => {
    const register = vi.fn().mockResolvedValue({ name: 'Jane Doe', email: 'jane@example.com' })

    render(
      <AppStateContext.Provider
        value={{
          isAuthenticated: false,
          user: null,
          token: null,
          login: vi.fn(),
          register,
          logout: vi.fn(),
        }}
      >
        <AuthPage />
      </AppStateContext.Provider>,
    )

    fireEvent.click(screen.getByRole('button', { name: /choose registration form/i }))
    fireEvent.change(screen.getByLabelText(/full name/i), { target: { value: 'Jane Doe' } })
    fireEvent.change(screen.getByLabelText(/email/i), { target: { value: 'jane@example.com' } })
    fireEvent.change(screen.getByLabelText(/^password$/i), { target: { value: 'password123' } })
    fireEvent.change(screen.getByLabelText(/confirm password/i), { target: { value: 'password123' } })
    fireEvent.click(screen.getByRole('button', { name: /^create account$/i }))

    expect(await screen.findByText(/account created/i)).toBeTruthy()
    expect(register).toHaveBeenCalledWith({
      name: 'Jane Doe',
      email: 'jane@example.com',
      password: 'password123',
    })
  })
})
