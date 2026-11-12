import React, { useEffect, useMemo, useState } from 'react'
import { apiRequest, apiRoutes } from '../../../api/client'
import { useAppState } from '../../../state/appState'

const emptyStatus = { type: '', message: '' }

function formatRON(amount) {
  const value = Number(amount ?? 0)
  return new Intl.NumberFormat('ro-RO', {
    style: 'currency',
    currency: 'RON',
    minimumFractionDigits: 2,
  }).format(value)
}

export function WalletsPage() {
  const { user } = useAppState()
  const [wallets, setWallets] = useState([])
  const [selectedWalletId, setSelectedWalletId] = useState('')
  const [depositAmount, setDepositAmount] = useState('')
  const [status, setStatus] = useState(emptyStatus)
  const [isLoading, setIsLoading] = useState(true)
  const [isSubmitting, setIsSubmitting] = useState(false)

  const refreshWallets = async (quiet = false) => {
    if (!quiet) {
      setStatus(emptyStatus)
    }

    try {
      const response = await apiRequest(apiRoutes.wallets)
      const walletList = Array.isArray(response) ? response : []
      const visibleWallets = user?.id
        ? walletList.filter((wallet) => !wallet.userId || wallet.userId === user.id)
        : walletList

      setWallets(visibleWallets)
      setSelectedWalletId((currentSelection) => {
        if (visibleWallets.some((wallet) => wallet.id === currentSelection)) {
          return currentSelection
        }

        return visibleWallets[0]?.id ?? ''
      })
    } catch (error) {
      setWallets([])
      setSelectedWalletId('')
      setStatus({ type: 'error', message: error.message || 'Unable to load wallets.' })
    } finally {
      setIsLoading(false)
    }
  }

  useEffect(() => {
    setIsLoading(true)
    void refreshWallets(false)
  }, [user?.id])

  const selectedWallet = useMemo(
    () => wallets.find((wallet) => wallet.id === selectedWalletId) ?? wallets[0] ?? null,
    [wallets, selectedWalletId],
  )

  const totalBalance = wallets.reduce((sum, wallet) => sum + Number(wallet.balance ?? 0), 0)

  const handleCreateWallet = async () => {
    setStatus(emptyStatus)
    setIsSubmitting(true)

    try {
      const nextWallet = await apiRequest(apiRoutes.wallets, { method: 'POST' })
      setStatus({
        type: 'success',
        message: `New wallet created successfully. ${formatRON(nextWallet?.balance ?? 0)} available.`,
      })
      await refreshWallets(true)
      setSelectedWalletId(nextWallet?.id ?? '')
    } catch (error) {
      setStatus({ type: 'error', message: error.message || 'Unable to create a wallet.' })
    } finally {
      setIsSubmitting(false)
    }
  }

  const handleDepositSubmit = async (event) => {
    event.preventDefault()

    if (!selectedWallet) {
      setStatus({ type: 'error', message: 'Create a wallet before submitting a deposit.' })
      return
    }

    const amount = Number(depositAmount)
    if (!Number.isFinite(amount) || amount <= 0) {
      setStatus({ type: 'error', message: 'Enter a valid deposit amount greater than zero.' })
      return
    }

    setStatus(emptyStatus)
    setIsSubmitting(true)

    try {
      const result = await apiRequest(`${apiRoutes.wallets}/${selectedWallet.id}/deposit`, {
        method: 'POST',
        body: JSON.stringify({ amount: amount.toFixed(2), currency: selectedWallet.currency || 'RON' }),
      })

      setDepositAmount('')
      setStatus({
        type: 'success',
        message: `Deposit received. Current balance: ${formatRON(result?.balance ?? selectedWallet.balance ?? 0)}.`,
      })
      await refreshWallets(true)
    } catch (error) {
      setStatus({ type: 'error', message: error.message || 'Unable to complete the deposit.' })
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <section className="page-shell wallet-shell">
      <header className="page-header">
        <div>
          <p className="eyebrow">Module</p>
          <h2>Wallets</h2>
        </div>
        <span className="page-badge">RON balances</span>
      </header>

      <div className="module-grid wallet-summary-grid">
        <article className="module-card emphasis">
          <h3>Wallet overview</h3>
          {isLoading ? (
            <p>Loading your wallets...</p>
          ) : wallets.length === 0 ? (
            <p>You do not have any wallets yet. Create one to begin depositing simulated funds.</p>
          ) : (
            <>
              <p className="summary-total">{formatRON(totalBalance)}</p>
              <p>{wallets.length} wallet{wallets.length === 1 ? '' : 's'} tracked for this account.</p>
            </>
          )}
        </article>

        <article className="module-card">
          <h3>Quick actions</h3>
          <button type="button" className="primary-button" onClick={handleCreateWallet} disabled={isSubmitting}>
            {isSubmitting ? 'Working...' : 'Create wallet'}
          </button>
          {selectedWallet && (
            <div className="selected-wallet-box">
              <span className="muted-label">Selected wallet</span>
              <strong>{selectedWallet.currency || 'RON'}</strong>
              <span>{formatRON(selectedWallet.balance ?? 0)}</span>
            </div>
          )}
        </article>
      </div>

      <div className="module-grid wallet-grid">
        <article className="module-card">
          <h3>Your wallets</h3>
          {wallets.length === 0 ? (
            <p className="empty-state">No wallets available.</p>
          ) : (
            <ul className="wallet-list" aria-live="polite">
              {wallets.map((wallet) => (
                <li
                  key={wallet.id}
                  className={wallet.id === selectedWallet?.id ? 'wallet-item active' : 'wallet-item'}
                  onClick={() => setSelectedWalletId(wallet.id)}
                  onKeyDown={(event) => {
                    if (event.key === 'Enter' || event.key === ' ') {
                      event.preventDefault()
                      setSelectedWalletId(wallet.id)
                    }
                  }}
                  tabIndex={0}
                  role="button"
                  aria-label={`Select wallet ${wallet.id}`}
                >
                  <div>
                    <span className="wallet-label">{wallet.currency || 'RON'}</span>
                    <small>{wallet.id}</small>
                  </div>
                  <strong>{formatRON(wallet.balance ?? 0)}</strong>
                </li>
              ))}
            </ul>
          )}
        </article>

        <article className="module-card emphasis">
          <h3>Simulated deposit</h3>
          <form className="wallet-form" onSubmit={handleDepositSubmit} noValidate>
            <label className="field-group">
              <span>Wallet</span>
              <select
                value={selectedWallet?.id ?? ''}
                onChange={(event) => setSelectedWalletId(event.target.value)}
                disabled={wallets.length === 0}
              >
                {wallets.length === 0 ? (
                  <option value="">No wallets available</option>
                ) : (
                  wallets.map((wallet) => (
                    <option key={wallet.id} value={wallet.id}>
                      {wallet.currency || 'RON'} • {formatRON(wallet.balance ?? 0)}
                    </option>
                  ))
                )}
              </select>
            </label>

            <label className="field-group">
              <span>Deposit amount</span>
              <input
                type="number"
                min="0.01"
                step="0.01"
                value={depositAmount}
                onChange={(event) => setDepositAmount(event.target.value)}
                placeholder="250.00"
              />
            </label>

            {status.message && (
              <div className={`status-banner ${status.type === 'error' ? 'error' : 'success'}`}>
                {status.message}
              </div>
            )}

            <button type="submit" className="primary-button" disabled={isSubmitting || wallets.length === 0}>
              {isSubmitting ? 'Submitting...' : 'Submit deposit'}
            </button>
          </form>
        </article>
      </div>
    </section>
  )
}
