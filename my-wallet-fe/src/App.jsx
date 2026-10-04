import { BrowserRouter } from 'react-router-dom'
import { useMemo, useState } from 'react'
import './App.css'
import { AppShell } from './components/AppShell'
import { AppStateContext } from './state/appState'

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false)
  const user = useMemo(
    () => ({
      name: 'WalletX User',
      email: 'user@walletx.test',
    }),
    [],
  )

  const value = useMemo(
    () => ({
      isAuthenticated,
      user,
      setIsAuthenticated,
    }),
    [isAuthenticated, user],
  )

  return (
    <BrowserRouter>
      <AppStateContext.Provider value={value}>
        <AppShell />
      </AppStateContext.Provider>
    </BrowserRouter>
  )
}

export default App
