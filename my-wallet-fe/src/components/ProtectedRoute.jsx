import { Navigate, useLocation } from 'react-router-dom'
import { useAppState } from '../state/appState'

export function ProtectedRoute({ children }) {
  const { isAuthenticated } = useAppState()
  const location = useLocation()

  if (!isAuthenticated) {
    return <Navigate to="/auth" replace state={{ from: location.pathname }} />
  }

  return children
}
