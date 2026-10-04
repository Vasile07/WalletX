import { useAppState } from '../../../state/appState'

export function AuthPage() {
  const { user, isAuthenticated } = useAppState()

  return (
    <section className="page-shell">
      <header className="page-header">
        <div>
          <p className="eyebrow">Module</p>
          <h2>Authentication</h2>
        </div>
        <span className="page-badge">Auth</span>
      </header>

      <div className="module-grid">
        <article className="module-card emphasis">
          <h3>Session state</h3>
          <p>
            {isAuthenticated
              ? `Signed in as ${user.name}.`
              : 'The app is currently running in a demo session without a persisted JWT.'}
          </p>
        </article>

        <article className="module-card">
          <h3>Planned features</h3>
          <ul className="feature-list">
            <li>Register new account</li>
            <li>Authenticate with email and password</li>
            <li>Store JWT in app state</li>
            <li>Protect private routes</li>
          </ul>
        </article>
      </div>
    </section>
  )
}
