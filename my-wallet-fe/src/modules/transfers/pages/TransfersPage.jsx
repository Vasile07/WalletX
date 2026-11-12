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

function describeTransferDirection(transfer) {
  if (!transfer) {
    return 'Transfer'
  }

  if (transfer.direction === 'SENT') {
    return 'Sent'
  }

  if (transfer.direction === 'RECEIVED') {
    return 'Received'
  }

  return 'Transfer'
}

export function TransfersPage() {
  const { user } = useAppState()
  const [wallets, setWallets] = useState([])
  const [transfers, setTransfers] = useState([])
  const [senderWalletId, setSenderWalletId] = useState('')
  const [receiverWalletId, setReceiverWalletId] = useState('')
  const [amount, setAmount] = useState('')
  const [currency, setCurrency] = useState('RON')
  const [status, setStatus] = useState(emptyStatus)
  const [isLoading, setIsLoading] = useState(true)
  const [isSubmitting, setIsSubmitting] = useState(false)

  const selectedWallet = useMemo(
    () => wallets.find((wallet) => wallet.id === senderWalletId) ?? wallets[0] ?? null,
    [wallets, senderWalletId],
  )

  useEffect(() => {
    if (!user?.id) {
      setWallets([])
      setTransfers([])
      setSenderWalletId('')
      setReceiverWalletId('')
      setAmount('')
      setCurrency('RON')
      setIsLoading(false)
      return
    }

    const loadTransferData = async () => {
      setIsLoading(true)
      setStatus(emptyStatus)

      try {
        const [walletResponse, transferResponse] = await Promise.all([
          apiRequest(apiRoutes.wallets),
          apiRequest(apiRoutes.transfers),
        ])

        const visibleWallets = Array.isArray(walletResponse) ? walletResponse : []
        const visibleTransfers = Array.isArray(transferResponse) ? transferResponse : []

        setWallets(visibleWallets)
        setTransfers(visibleTransfers)
        setSenderWalletId((currentSelection) => {
          if (visibleWallets.some((wallet) => wallet.id === currentSelection)) {
            return currentSelection
          }

          return visibleWallets[0]?.id ?? ''
        })

        if (visibleWallets[0]) {
          setCurrency(visibleWallets[0].currency || 'RON')
        }
      } catch (error) {
        setWallets([])
        setTransfers([])
        setSenderWalletId('')
        setStatus({ type: 'error', message: error.message || 'Unable to load transfer details.' })
      } finally {
        setIsLoading(false)
      }
    }

    void loadTransferData()
  }, [user?.id])

  useEffect(() => {
    if (!selectedWallet) {
      return
    }

    setCurrency((currentCurrency) => currentCurrency || selectedWallet.currency || 'RON')
  }, [selectedWallet])

  const refreshTransferState = async () => {
    const [walletResponse, transferResponse] = await Promise.all([
      apiRequest(apiRoutes.wallets),
      apiRequest(apiRoutes.transfers),
    ])

    const visibleWallets = Array.isArray(walletResponse) ? walletResponse : []
    const visibleTransfers = Array.isArray(transferResponse) ? transferResponse : []

    setWallets(visibleWallets)
    setTransfers(visibleTransfers)
    setSenderWalletId((currentSelection) => {
      if (visibleWallets.some((wallet) => wallet.id === currentSelection)) {
        return currentSelection
      }

      return visibleWallets[0]?.id ?? ''
    })
  }

  const handleTransferSubmit = async (event) => {
    event.preventDefault()

    if (!selectedWallet) {
      setStatus({ type: 'error', message: 'Create a wallet before attempting a transfer.' })
      return
    }

    const trimmedReceiverId = receiverWalletId.trim()
    const nextAmount = Number(amount)

    if (!trimmedReceiverId) {
      setStatus({ type: 'error', message: 'Select or enter the destination wallet ID.' })
      return
    }

    if (trimmedReceiverId === selectedWallet.id) {
      setStatus({ type: 'error', message: 'The sender and receiver wallet must be different.' })
      return
    }

    if (!Number.isFinite(nextAmount) || nextAmount <= 0) {
      setStatus({ type: 'error', message: 'Enter a transfer amount greater than zero.' })
      return
    }

    const availableBalance = Number(selectedWallet.balance ?? 0)
    if (nextAmount > availableBalance) {
      setStatus({
        type: 'error',
        message: `Insufficient funds. ${formatRON(availableBalance)} available in the selected wallet.`,
      })
      return
    }

    setStatus(emptyStatus)
    setIsSubmitting(true)

    try {
      const transferCurrency = currency || selectedWallet.currency || 'RON'
      const result = await apiRequest(apiRoutes.transfers, {
        method: 'POST',
        body: JSON.stringify({
          senderWalletId: selectedWallet.id,
          receiverWalletId: trimmedReceiverId,
          amount: nextAmount.toFixed(2),
          currency: transferCurrency,
        }),
      })

      setAmount('')
      setReceiverWalletId('')
      setStatus({
        type: 'success',
        message: `Transfer sent successfully. ${formatRON(result?.amount ?? nextAmount)} transferred.`,
      })
      await refreshTransferState()
    } catch (error) {
      setStatus({ type: 'error', message: error.message || 'Unable to complete the transfer.' })
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <section className="page-shell">
      <header className="page-header">
        <div>
          <p className="eyebrow">Module</p>
          <h2>Transfers</h2>
        </div>
        <span className="page-badge">RON transfers</span>
      </header>

      <div className="module-grid wallet-summary-grid">
        <article className="module-card emphasis">
          <h3>Transfer overview</h3>
          {isLoading ? (
            <p>Loading transfer data...</p>
          ) : wallets.length === 0 ? (
            <p>Create a wallet first to start sending funds.</p>
          ) : (
            <>
              <p className="summary-total">{formatRON(selectedWallet?.balance ?? 0)}</p>
              <p>Available in {selectedWallet ? 'the selected wallet' : 'your wallets'}.</p>
            </>
          )}
        </article>

        <article className="module-card">
          <h3>Transfer summary</h3>
          <div className="selected-wallet-box">
            <span className="muted-label">Sender wallet</span>
            <strong>{selectedWallet ? (selectedWallet.currency || 'RON') : 'No wallet selected'}</strong>
            <span>{selectedWallet ? formatRON(selectedWallet.balance ?? 0) : 'Create a wallet to continue'}</span>
          </div>
          <div className="selected-wallet-box compact-box">
            <span className="muted-label">Recent transfers</span>
            <strong>{transfers.length}</strong>
          </div>
        </article>
      </div>

      <div className="module-grid wallet-grid">
        <article className="module-card emphasis">
          <h3>Send funds</h3>
          <form className="wallet-form" onSubmit={handleTransferSubmit} noValidate>
            <label className="field-group">
              <span>Sender wallet</span>
              <select
                value={selectedWallet?.id ?? ''}
                onChange={(event) => setSenderWalletId(event.target.value)}
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
              <span>Receiver wallet ID</span>
              <input
                type="text"
                value={receiverWalletId}
                onChange={(event) => setReceiverWalletId(event.target.value)}
                placeholder="xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
              />
            </label>

            <label className="field-group">
              <span>Amount</span>
              <input
                type="number"
                min="0.01"
                step="0.01"
                value={amount}
                onChange={(event) => setAmount(event.target.value)}
                placeholder="150.00"
              />
            </label>

            <label className="field-group">
              <span>Currency</span>
              <select value={currency} onChange={(event) => setCurrency(event.target.value)}>
                <option value="RON">RON</option>
              </select>
            </label>

            {status.message && (
              <div className={`status-banner ${status.type === 'error' ? 'error' : 'success'}`}>
                {status.message}
              </div>
            )}

            <button type="submit" className="primary-button" disabled={wallets.length === 0 || isSubmitting}>
              {isSubmitting ? 'Sending...' : 'Send transfer'}
            </button>
          </form>
        </article>

        <article className="module-card">
          <h3>Recent transfer history</h3>
          {transfers.length === 0 ? (
            <p className="empty-state">No transfers have been recorded yet.</p>
          ) : (
            <ul className="wallet-list transfer-list" aria-live="polite">
              {transfers.slice(0, 5).map((transfer) => {
                const direction = describeTransferDirection(transfer)
                const isOutgoing = transfer.direction === 'SENT'
                const counterpartyId = isOutgoing
                  ? transfer.counterpartyWalletId ?? transfer.receiverWalletId
                  : transfer.counterpartyWalletId ?? transfer.senderWalletId

                return (
                  <li key={transfer.id} className="transfer-item">
                    <div className="transfer-details">
                      <span className="wallet-label">{direction}</span>
                      <small>{transfer.completedAt ? new Date(transfer.completedAt).toLocaleString() : 'Transfer completed'}</small>
                      <small>{counterpartyId ? `Counterparty: ${counterpartyId}` : 'Pending review'}</small>
                    </div>
                    <strong className={isOutgoing ? 'negative-amount' : 'positive-amount'}>
                      {isOutgoing ? '-' : '+'}
                      {formatRON(transfer.amount ?? 0)}
                    </strong>
                  </li>
                )
              })}
            </ul>
          )}
        </article>
      </div>
    </section>
  )
}
