import { createContext, createElement, useContext, useMemo, useState } from 'react'
import { apiRequest, apiRoutes } from '../api/client'

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

const defaultAuthState = {
  isAuthenticated: false,
  user: null,
  token: null,
  login: async () => {
    throw new Error('Auth login is not configured.')
  },
  register: async () => {
    throw new Error('Auth registration is not configured.')
  },
  logout: () => {},
}

export const AppStateContext = createContext(defaultAuthState)

export function AppStateProvider({ children }) {
  const [token, setToken] = useState(() => getStoredToken())
  const [user, setUser] = useState(() => getStoredUser())

  const persistSession = (nextToken, nextUser) => {
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
  }

  const logout = () => {
    persistSession(null, null)
  }

  const login = async ({ email, password }) => {
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
  }

  const register = async ({ name, email, password }) => {
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
  }

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

  return createElement(
    AppStateContext.Provider,
    { value },
    children,
  )
}

export function useAppState() {
  return useContext(AppStateContext)
}
