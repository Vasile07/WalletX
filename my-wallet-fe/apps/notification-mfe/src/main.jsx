import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import { AppStateContext, defaultAuthState } from '@walletx/shared'
import NotificationsPage from './Page.jsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <BrowserRouter>
      <AppStateContext.Provider value={defaultAuthState}>
        <NotificationsPage />
      </AppStateContext.Provider>
    </BrowserRouter>
  </StrictMode>,
)
