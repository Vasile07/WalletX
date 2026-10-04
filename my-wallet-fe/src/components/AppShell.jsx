import { NavLink, Navigate, Route, Routes } from 'react-router-dom'
import { AuthPage } from '../modules/auth/pages/AuthPage'
import { WalletsPage } from '../modules/wallets/pages/WalletsPage'
import { TransfersPage } from '../modules/transfers/pages/TransfersPage'
import { NotificationsPage } from '../modules/notifications/pages/NotificationsPage'
import { useAppState } from '../state/appState'

const navItems = [
  { to: '/auth', label: 'Authentication' },
  { to: '/wallets', label: 'Wallets' },
  { to: '/transfers', label: 'Transfers' },
  { to: '/notifications', label: 'Notifications' },
]

export function AppShell() {
  const { isAuthenticated, setIsAuthenticated } = useAppState()

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand-block">
          <div className="brand-mark">W</div>
          <div>
            <p className="eyebrow">Workspace</p>
            <h1>WalletX</h1>
          </div>
        </div>

        <nav className="sidebar-nav" aria-label="Main navigation">
          {navItems.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
            >
              {item.label}
            </NavLink>
          ))}
        </nav>

        <div className="sidebar-footer">
          <div className="status-pill">{isAuthenticated ? 'Signed in' : 'Demo mode'}</div>
          <button type="button" className="ghost-button" onClick={() => setIsAuthenticated((value) => !value)}>
            {isAuthenticated ? 'Log out' : 'Log in'}
          </button>
        </div>
      </aside>

      <main className="content-panel">
        <Routes>
          <Route path="/" element={<Navigate to="/auth" replace />} />
          <Route path="/auth" element={<AuthPage />} />
          <Route path="/wallets" element={<WalletsPage />} />
          <Route path="/transfers" element={<TransfersPage />} />
          <Route path="/notifications" element={<NotificationsPage />} />
          <Route path="*" element={<Navigate to="/auth" replace />} />
        </Routes>
      </main>
    </div>
  )
}
