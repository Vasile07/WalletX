import { createContext, useContext } from 'react'

export const AppStateContext = createContext({
  isAuthenticated: false,
  user: { name: 'WalletX User', email: 'user@walletx.test' },
  setIsAuthenticated: () => {},
})

export function useAppState() {
  return useContext(AppStateContext)
}
