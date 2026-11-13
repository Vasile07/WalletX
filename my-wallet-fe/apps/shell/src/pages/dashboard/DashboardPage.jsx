import React, { useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import { apiRequest, apiRoutes, useAppState } from '@walletx/shared'

const emptyStatus = { type: '', message: '' }

function formatRON(amount, currency = 'RON') {
  const value = Number(amount ?? 0)
  return new Intl.NumberFormat('ro-RO', {
    style: 'currency',
    currency: currency || 'RON',
    minimumFractionDigits: 2,
  }).format(value)
}

function formatActivityDate(value) {
  if (!value) {
    return 'Recently'
  }

  const date = new Date(value)
  if (Number.isNaN(date.getTime())) {
    return 'Recently'
  }

  return new Intl.DateTimeFormat('ro-RO', {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(date)
}

function getTransferLabel(transfer) {
  if (transfer?.direction === 'RECEIVED') {
    return 'Received transfer'
  }

  if (transfer?.direction === 'SENT') {
    return 'Sent transfer'
  }

  return 'Transfer'
}

function getTransferAmount(transfer, defaultCurrency = 'RON') {
  const amount = Number(transfer?.amount ?? 0)
  const currency = transfer?.currency || defaultCurrency
  const prefix = transfer?.direction === 'RECEIVED' ? '+' : '-'
  return `${prefix}${formatRON(amount, currency)}`
}

export function DashboardPage() {
  const { user } = useAppState()
  const [wallets, setWallets] = useState([])
  const [transfers, setTransfers] = useState([])
  const [status, setStatus] = useState(emptyStatus)
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    if (!user?.id) {
      setWallets([])
      setTransfers([])
      setIsLoading(false)
      return
    }

    let isCurrent = true

    const loadDashboard = async () => {
      setIsLoading(true)
      setStatus(emptyStatus)

      try {
        const [walletResponse, transferResponse] = await Promise.all([
          apiRequest(apiRoutes.wallets),
          apiRequest(apiRoutes.transfers),
        ])

        if (!isCurrent) {
          return
        }

        const visibleWallets = Array.isArray(walletResponse) ? walletResponse : []
        const visibleTransfers = Array.isArray(transferResponse) ? transferResponse : []

        setWallets(visibleWallets.filter((wallet) => !wallet.userId || wallet.userId === user.id))
        setTransfers(visibleTransfers)
      } catch (error) {
        if (!isCurrent) {
          return
        }

        setWallets([])
        setTransfers([])
        setStatus({
          type: 'error',
          message: error.message || 'Unable to load your WalletX dashboard.',
        })
      } finally {
        if (isCurrent) {
          setIsLoading(false)
        }
      }
    }

    void loadDashboard()

    return () => {
      isCurrent = false
    }
  }, [user?.id])

  const totalBalance = useMemo(
    () => wallets.reduce((sum, wallet) => sum + Number(wallet.balance ?? 0), 0),
    [wallets],
  )

  const recentTransfers = useMemo(
    () =>
      [...transfers]
        .sort((left, right) => {
          const leftEpoch = Date.parse(left?.createdAt || left?.timestamp || 0)
          const rightEpoch = Date.parse(right?.createdAt || right?.timestamp || 0)

          if (Number.isNaN(leftEpoch) && Number.isNaN(rightEpoch)) {
            return 0
          }

          if (Number.isNaN(leftEpoch)) {
            return 1
          }

          if (Number.isNaN(rightEpoch)) {
            return -1
          }

          return rightEpoch - leftEpoch
        })
        .slice(0, 4),
    [transfers],
  )

  const notifications = useMemo(() => {
    const items = []

    const lowBalanceWallets = wallets.filter((wallet) => Number(wallet.balance ?? 0) < 100)
    if (lowBalanceWallets.length > 0) {
      items.push({
        title: 'Low balance alert',
        message: `${lowBalanceWallets.length} wallet${lowBalanceWallets.length > 1 ? 's are' : ' is'} below the 100 RON threshold.`,
      })
    }

    if (recentTransfers.length > 0) {
      const latestTransfer = recentTransfers[0]
      items.push({
        title: 'Latest activity',
        message: `${getTransferLabel(latestTransfer)} • ${getTransferAmount(latestTransfer, latestTransfer?.currency || 'RON')}`,
      })
    }

    if (items.length === 0) {
      items.push({
        title: 'WalletX ready',
        message: 'No recent issues detected. Start a transfer or deposit to build momentum.',
      })
    }

    if (user?.name && items.length < 3) {
      items.push({
        title: 'Welcome back',
        message: `${user.name}, your accounts are ready for the next move.`,
      })
    }

    return items.slice(0, 3)
  }, [recentTransfers, user?.name, wallets])

  return (
    <section className="page-shell dashboard-shell">
      <header className="page-header">
        <div>
          <p className="eyebrow">Overview</p>
          <h2>Dashboard</h2>
        </div>
        <span className="page-badge">WalletX summary</span>
      </header>

      {status.message ? <div className={`status-banner ${status.type}`}>{status.message}</div> : null}

      {isLoading ? (
        <div className="empty-state">
          <p>Loading your dashboard...</p>
        </div>
      ) : wallets.length === 0 ? (
        <div className="empty-state">
          <p>No wallets added yet.</p>
          <span>Open Wallets to create one and start managing your balance.</span>
        </div>
      ) : (
        <>
          <div className="module-grid dashboard-summary-grid">
            <article className="module-card emphasis">
              <h3>Current balance</h3>
              <p className="summary-total">{formatRON(totalBalance)}</p>
              <p>{wallets.length} wallet{wallets.length === 1 ? '' : 's'} tracked for this account.</p>
            </article>

            <article className="module-card">
              <h3>Quick actions</h3>
              <div className="dashboard-actions">
                <Link className="primary-button dashboard-link" to="/wallets">
                  Manage wallets
                </Link>
                <Link className="primary-button dashboard-link secondary" to="/transfers">
                  Send transfer
                </Link>
                <Link className="primary-button dashboard-link secondary" to="/notifications">
                  View notifications
                </Link>
              </div>
            </article>
          </div>

          <div className="module-grid dashboard-grid">
            <article className="module-card">
              <h3>Recent transactions</h3>
              {recentTransfers.length === 0 ? (
                <p className="empty-state-copy">No transfers yet. Use the transfer workflow to move funds.</p>
              ) : (
                <ul className="transaction-list" aria-live="polite">
                  {recentTransfers.map((transfer, index) => (
                    <li key={transfer.id || `${transfer.senderWalletId}-${transfer.receiverWalletId}-${index}`} className="transaction-item">
                      <div>
                        <span className="transaction-title">{getTransferLabel(transfer)}</span>
                        <small>
                          {transfer.counterpartyWalletId || transfer.receiverWalletId || transfer.senderWalletId || 'Wallet transfer'}
                        </small>
                      </div>

                      <div className="transaction-meta">
                        <strong className={transfer.direction === 'RECEIVED' ? 'positive' : 'negative'}>
                          {getTransferAmount(transfer, transfer?.currency || 'RON')}
                        </strong>
                        <time>{formatActivityDate(transfer.createdAt || transfer.timestamp)}</time>
                      </div>
                    </li>
                  ))}
                </ul>
              )}
            </article>

            <article className="module-card">
              <h3>Notifications</h3>
              <ul className="notification-preview-list">
                {notifications.map((notification) => (
                  <li key={`${notification.title}-${notification.message}`} className="notification-preview-item">
                    <span className="notification-pill">{notification.title}</span>
                    <p>{notification.message}</p>
                  </li>
                ))}
              </ul>
            </article>
          </div>
        </>
      )}
    </section>
  )
}
