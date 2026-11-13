import React, { useState } from 'react'
import { useAppState } from '@walletx/shared'

const initialForm = {
  name: '',
  email: '',
  password: '',
  confirmPassword: '',
}

export function AuthPage() {
  const { user, isAuthenticated, login, register } = useAppState()
  const [mode, setMode] = useState('login')
  const [form, setForm] = useState(initialForm)
  const [status, setStatus] = useState({ type: '', message: '' })
  const [isSubmitting, setIsSubmitting] = useState(false)

  const updateField = (event) => {
    const { name, value } = event.target
    setForm((current) => ({ ...current, [name]: value }))
    setStatus({ type: '', message: '' })
  }

  const handleSubmit = async (event) => {
    event.preventDefault()
    setStatus({ type: '', message: '' })

    if (mode === 'register') {
      if (!form.name.trim()) {
        setStatus({ type: 'error', message: 'Full name is required.' })
        return
      }

      if (!form.email.trim()) {
        setStatus({ type: 'error', message: 'Email is required.' })
        return
      }

      if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
        setStatus({ type: 'error', message: 'Use a valid email address.' })
        return
      }

      if (form.password.length < 8) {
        setStatus({ type: 'error', message: 'Password must be at least 8 characters long.' })
        return
      }

      if (form.password !== form.confirmPassword) {
        setStatus({ type: 'error', message: 'Passwords do not match.' })
        return
      }
    } else {
      if (!form.email.trim()) {
        setStatus({ type: 'error', message: 'Email is required.' })
        return
      }

      if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
        setStatus({ type: 'error', message: 'Use a valid email address.' })
        return
      }

      if (!form.password) {
        setStatus({ type: 'error', message: 'Password is required.' })
        return
      }
    }

    setIsSubmitting(true)

    try {
      if (mode === 'login') {
        await login({ email: form.email.trim(), password: form.password })
        setStatus({ type: 'success', message: 'Welcome back! Your session is active.' })
      } else {
        await register({
          name: form.name.trim(),
          email: form.email.trim(),
          password: form.password,
        })
        setStatus({ type: 'success', message: 'Account created. You can now sign in.' })
        setMode('login')
        setForm({ ...initialForm, email: form.email.trim() })
      }
    } catch (error) {
      setStatus({ type: 'error', message: error.message || 'Authentication failed. Please try again.' })
    } finally {
      setIsSubmitting(false)
    }
  }

  if (isAuthenticated) {
    return (
      <section className="page-shell">
        <header className="page-header">
          <div>
            <p className="eyebrow">Session</p>
            <h2>Authenticated</h2>
          </div>
          <span className="page-badge">Active</span>
        </header>

        <div className="module-grid">
          <article className="module-card emphasis">
            <h3>Welcome back</h3>
            <p>{user?.name || 'WalletX User'} is signed in with a valid JWT session.</p>
          </article>

          <article className="module-card">
            <h3>Session details</h3>
            <ul className="feature-list">
              <li>{user?.email || 'user@walletx.test'}</li>
              <li>Protected routes are enabled.</li>
              <li>Bearer token is attached to private API requests.</li>
            </ul>
          </article>
        </div>
      </section>
    )
  }

  return (
    <section className="page-shell auth-shell">
      <header className="page-header">
        <div>
          <p className="eyebrow">Module</p>
          <h2>Authentication</h2>
        </div>
        <span className="page-badge">Auth</span>
      </header>

      <div className="module-grid auth-grid">
        <article className="module-card emphasis auth-card">
          <div className="auth-toggle" role="tablist" aria-label="Authentication mode">
            <button
              type="button"
              aria-label="Choose sign in form"
              className={mode === 'login' ? 'tab-button active' : 'tab-button'}
              onClick={() => setMode('login')}
            >
              Sign in
            </button>
            <button
              type="button"
              aria-label="Choose registration form"
              className={mode === 'register' ? 'tab-button active' : 'tab-button'}
              onClick={() => setMode('register')}
            >
              Register
            </button>
          </div>

          <form className="auth-form" onSubmit={handleSubmit} noValidate>
            {mode === 'register' && (
              <label className="field-group">
                <span>Full name</span>
                <input
                  type="text"
                  name="name"
                  value={form.name}
                  onChange={updateField}
                  placeholder="John Doe"
                  autoComplete="name"
                />
              </label>
            )}

            <label className="field-group">
              <span>Email</span>
              <input
                type="email"
                name="email"
                value={form.email}
                onChange={updateField}
                placeholder="name@example.com"
                autoComplete="email"
              />
            </label>

            <label className="field-group">
              <span>Password</span>
              <input
                type="password"
                name="password"
                value={form.password}
                onChange={updateField}
                placeholder={mode === 'login' ? 'Enter your password' : 'Choose a strong password'}
                autoComplete={mode === 'login' ? 'current-password' : 'new-password'}
              />
            </label>

            {mode === 'register' && (
              <label className="field-group">
                <span>Confirm password</span>
                <input
                  type="password"
                  name="confirmPassword"
                  value={form.confirmPassword}
                  onChange={updateField}
                  placeholder="Repeat the password"
                  autoComplete="new-password"
                />
              </label>
            )}

            {status.message && (
              <div className={`status-banner ${status.type === 'error' ? 'error' : 'success'}`}>
                {status.message}
              </div>
            )}

            <button type="submit" className="primary-button" disabled={isSubmitting}>
              {isSubmitting ? 'Please wait...' : mode === 'login' ? 'Sign in' : 'Create account'}
            </button>
          </form>
        </article>

        <article className="module-card">
          <h3>What this module provides</h3>
          <ul className="feature-list">
            <li>Register a new WalletX account.</li>
            <li>Sign in with JWT-based session state.</li>
            <li>Protect private routes and API requests.</li>
            <li>Display friendly authentication feedback.</li>
          </ul>
        </article>
      </div>
    </section>
  )
}
