import { createContext, useContext } from 'react'

export const defaultAuthState = {
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

export function useAppState() {
  return useContext(AppStateContext)
}
