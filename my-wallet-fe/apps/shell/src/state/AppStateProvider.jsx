import React, { useCallback, useMemo, useState } from 'react'
import { AppStateContext, apiRequest, apiRoutes } from '@walletx/shared'

const STORAGE_KEYS = {
  token: 'walletx.auth.token',
  user: 'walletx.user',
}

function readStoredValue(key) {
  try {
    const value = localStorage.getItem(key)
    return value ? JSON.parse(value) : null
  } catch {
    return null
  }
}

function getStoredToken() {
  const storedToken = localStorage.getItem(STORAGE_KEYS.token)
  if (!storedToken) {
    return null
  }

  try {
    const parsedToken = JSON.parse(storedToken)
    return typeof parsedToken === 'string' ? parsedToken : storedToken
  } catch {
    return storedToken
  }
}

function getStoredUser() {
  return (
    readStoredValue(STORAGE_KEYS.user) ??
    readStoredValue('walletx_user') ??
    { name: 'WalletX User', email: 'user@walletx.test' }
  )
}

export function AppStateProvider({ children }) {
  const [token, setToken] = useState(() => getStoredToken())
  const [user, setUser] = useState(() => getStoredUser())

  const persistSession = useCallback((nextToken, nextUser) => {
    setToken(nextToken)
    setUser(nextUser)

    if (nextToken) {
      localStorage.setItem(STORAGE_KEYS.token, nextToken)
    } else {
      localStorage.removeItem(STORAGE_KEYS.token)
    }

    if (nextUser) {
      localStorage.setItem(STORAGE_KEYS.user, JSON.stringify(nextUser))
    } else {
      localStorage.removeItem(STORAGE_KEYS.user)
    }
  }, [])

  const logout = useCallback(() => {
    persistSession(null, null)
  }, [persistSession])

  const login = useCallback(async ({ email, password }) => {
    const response = await apiRequest(apiRoutes.auth.login, {
      method: 'POST',
      body: JSON.stringify({ email, password }),
    })

    const nextToken = response?.token || response?.accessToken || response?.jwt
    const nextUser = response?.user || {
      name: response?.name || email.split('@')[0].replace(/[._-]/g, ' '),
      email,
    }

    if (!nextToken) {
      throw new Error('Authentication response did not include a JWT.')
    }

    persistSession(nextToken, nextUser)
    return nextUser
  }, [persistSession])

  const register = useCallback(async ({ name, email, password }) => {
    const response = await apiRequest(apiRoutes.auth.register, {
      method: 'POST',
      body: JSON.stringify({ name, email, password }),
    })

    const nextToken = response?.token || response?.accessToken || response?.jwt
    const nextUser = response?.user || {
      name: response?.name || name,
      email: response?.email || email,
    }

    if (nextToken) {
      persistSession(nextToken, nextUser)
    }

    return { ...nextUser, token: nextToken }
  }, [persistSession])

  const value = useMemo(
    () => ({
      isAuthenticated: Boolean(token),
      user,
      token,
      login,
      register,
      logout,
    }),
    [token, user, login, register, logout],
  )

  return <AppStateContext.Provider value={value}>{children}</AppStateContext.Provider>
}
