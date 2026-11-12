import { NavLink, Navigate, Route, Routes } from 'react-router-dom'
import { ProtectedRoute } from './ProtectedRoute'
import { AuthPage } from '../modules/auth/pages/AuthPage'
import { DashboardPage } from '../modules/dashboard/pages/DashboardPage'
import { WalletsPage } from '../modules/wallets/pages/WalletsPage'
import { TransfersPage } from '../modules/transfers/pages/TransfersPage'
import { NotificationsPage } from '../modules/notifications/pages/NotificationsPage'
import { useAppState } from '../state/appState'

const navItems = [
  { to: '/dashboard', label: 'Dashboard' },
  { to: '/auth', label: 'Authentication' },
  { to: '/wallets', label: 'Wallets' },
  { to: '/transfers', label: 'Transfers' },
  { to: '/notifications', label: 'Notifications' },
]

export function AppShell() {
  const { isAuthenticated, logout } = useAppState()

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
          <button
            type="button"
            className="ghost-button"
            onClick={() => {
              if (isAuthenticated) {
                logout()
              }
            }}
          >
            {isAuthenticated ? 'Log out' : 'Continue to sign in'}
          </button>
        </div>
      </aside>

      <main className="content-panel">
        <Routes>
          <Route path="/" element={<Navigate to={isAuthenticated ? '/dashboard' : '/auth'} replace />} />
          <Route
            path="/auth"
            element={isAuthenticated ? <Navigate to="/dashboard" replace /> : <AuthPage />}
          />
          <Route path="/dashboard" element={<ProtectedRoute><DashboardPage /></ProtectedRoute>} />
          <Route path="/wallets" element={<ProtectedRoute><WalletsPage /></ProtectedRoute>} />
          <Route path="/transfers" element={<ProtectedRoute><TransfersPage /></ProtectedRoute>} />
          <Route path="/notifications" element={<ProtectedRoute><NotificationsPage /></ProtectedRoute>} />
          <Route path="*" element={<Navigate to={isAuthenticated ? '/dashboard' : '/auth'} replace />} />
        </Routes>
      </main>
    </div>
  )
}
