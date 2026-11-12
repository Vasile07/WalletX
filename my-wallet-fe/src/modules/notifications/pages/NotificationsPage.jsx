import React, { useCallback, useEffect, useMemo, useRef, useState } from 'react'
import { API_BASE_URL, apiRoutes } from '../../../api/client'
import { useAppState } from '../../../state/appState'

const emptyStatus = { type: '', message: '' }
const eventLabels = {
  connected: 'Connected',
  system: 'System',
  transfer_sent: 'Transfer sent',
  transfer_received: 'Transfer received',
  transfer_failed: 'Transfer failed',
  deposit_completed: 'Deposit completed',
  low_balance: 'Low balance',
}

function toEventKey(value) {
  return String(value ?? 'system')
    .trim()
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, '_')
    .replace(/^_+|_+$/g, '') || 'system'
}

function formatCurrency(value, currency = 'RON') {
  const numberValue = Number(value)
  if (!Number.isFinite(numberValue)) {
    return currency ? `${currency}` : 'N/A'
  }

  return new Intl.NumberFormat('ro-RO', {
    style: 'currency',
    currency: currency || 'RON',
    minimumFractionDigits: 2,
  }).format(numberValue)
}

function formatTimestamp(value) {
  if (!value) {
    return 'Just now'
  }

  const date = new Date(value)
  if (Number.isNaN(date.getTime())) {
    return 'Just now'
  }

  return new Intl.DateTimeFormat('ro-RO', {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(date)
}

function parseSsePayload(payload) {
  const lines = payload.split(/\r?\n/)
  let eventName = 'message'
  let data = ''

  for (const line of lines) {
    if (!line) {
      continue
    }

    if (line.startsWith('event:')) {
      eventName = line.replace(/^event:\s*/i, '').trim() || 'message'
      continue
    }

    if (line.startsWith('data:')) {
      const lineValue = line.replace(/^data:\s*/i, '').trim()
      data = data ? `${data}\n${lineValue}` : lineValue
    }
  }

  if (!data) {
    return null
  }

  try {
    return { event: eventName, data: JSON.parse(data) }
  } catch {
    return { event: eventName, data: { message: data } }
  }
}

function buildNotification(raw) {
  const eventKey = toEventKey(raw?.type || raw?.event || 'system')
  const type = eventKey.toUpperCase().replace(/[^A-Z0-9]+/g, '_').replace(/^_+|_+$/g, '') || 'SYSTEM'
  const message = raw?.message || eventLabels[eventKey] || 'WalletX notification'
  const createdAt = raw?.createdAt || new Date().toISOString()

  return {
    id: raw?.id || `${type}-${createdAt}-${Math.random().toString(16).slice(2)}`,
    type,
    title: eventLabels[eventKey] || 'System',
    message,
    walletId: raw?.walletId || null,
    currency: raw?.currency || null,
    balance: raw?.balance ?? null,
    threshold: raw?.threshold ?? null,
    connected: Boolean(raw?.connected),
    createdAt,
    read: Boolean(raw?.connected),
  }
}

function getFilterSummary(filter) {
  if (filter === 'unread') {
    return 'Unread only'
  }

  if (filter === 'read') {
    return 'Read only'
  }

  return 'All notifications'
}

export function NotificationsPage() {
  const { user, token } = useAppState()
  const [notifications, setNotifications] = useState([])
  const [filter, setFilter] = useState('all')
  const [connectionState, setConnectionState] = useState('offline')
  const [status, setStatus] = useState(emptyStatus)
  const reconnectTimerRef = useRef(null)
  const abortControllerRef = useRef(null)

  const connectToStream = useCallback(() => {
    if (!user?.id || !token) {
      setConnectionState('offline')
      setStatus({ type: 'error', message: 'Authentication is required to receive live notifications.' })
      return
    }

    const controller = new AbortController()
    abortControllerRef.current = controller
    setConnectionState('connecting')
    setStatus({ type: 'info', message: 'Connecting to the WalletX notification stream…' })

    const requestUrl = `${API_BASE_URL}${apiRoutes.notifications}`

    fetch(requestUrl, {
      method: 'GET',
      signal: controller.signal,
      headers: {
        Accept: 'text/event-stream',
        Authorization: `Bearer ${token}`,
        'X-User-Id': String(user.id),
      },
      credentials: 'include',
    })
      .then(async (response) => {
        if (!response.ok) {
          throw new Error(`Unable to connect to notifications (${response.status}).`)
        }

        if (!response.body) {
          throw new Error('The notification stream is unavailable in this browser.')
        }

        setConnectionState('connected')
        setStatus({ type: 'success', message: 'Connected to live WalletX events.' })

        const reader = response.body.getReader()
        const decoder = new TextDecoder()
        let buffer = ''

        while (true) {
          const { done, value } = await reader.read()
          if (done) {
            break
          }

          buffer += decoder.decode(value, { stream: true })

          let boundary = buffer.indexOf('\n\n')
          while (boundary >= 0) {
            const messageBlock = buffer.slice(0, boundary)
            buffer = buffer.slice(boundary + 2)
            const parsed = parseSsePayload(messageBlock)

            if (!parsed) {
              boundary = buffer.indexOf('\n\n')
              continue
            }

            const notification = buildNotification(parsed.data)
            if (parsed.event === 'connected') {
              notification.title = 'Connected'
              notification.message = notification.message || 'Connected to WalletX notifications'
              notification.read = true
            }

            setNotifications((current) => {
              const existingIndex = current.findIndex((item) => item.id === notification.id)
              if (existingIndex >= 0) {
                const next = [...current]
                next[existingIndex] = { ...next[existingIndex], ...notification }
                return next
              }

              return [notification, ...current].slice(0, 40)
            })

            boundary = buffer.indexOf('\n\n')
          }
        }
      })
      .catch((error) => {
        if (error.name === 'AbortError') {
          return
        }

        setConnectionState('reconnecting')
        setStatus({ type: 'error', message: 'Disconnected from notifications. Retrying…' })
        reconnectTimerRef.current = window.setTimeout(() => {
          connectToStream()
        }, 2500)
      })
  }, [token, user?.id])

  useEffect(() => {
    if (!user?.id || !token) {
      setNotifications([])
      setConnectionState('offline')
      setStatus({ type: 'error', message: 'Sign in to receive secure wallet alerts.' })
      return undefined
    }

    connectToStream()

    return () => {
      if (abortControllerRef.current) {
        abortControllerRef.current.abort()
      }

      if (reconnectTimerRef.current) {
        window.clearTimeout(reconnectTimerRef.current)
      }
    }
  }, [connectToStream, token, user?.id])

  const unreadCount = notifications.filter((notification) => !notification.read).length

  const filteredNotifications = useMemo(
    () => notifications.filter((notification) => {
      if (filter === 'all') {
        return true
      }

      if (filter === 'unread') {
        return !notification.read
      }

      return notification.read
    }),
    [filter, notifications],
  )

  const markNotificationAsRead = (notificationId) => {
    setNotifications((current) =>
      current.map((notification) =>
        notification.id === notificationId ? { ...notification, read: true } : notification,
      ),
    )
  }

  const markAllRead = () => {
    setNotifications((current) => current.map((notification) => ({ ...notification, read: true })))
  }

  return (
    <section className="page-shell">
      <header className="page-header">
        <div>
          <p className="eyebrow">Module</p>
          <h2>Notifications</h2>
        </div>

        <div className="notification-header-actions">
          <span className={`connection-indicator ${connectionState}`}>
            {connectionState === 'connected' ? 'Connected' : connectionState === 'connecting' ? 'Connecting' : connectionState === 'reconnecting' ? 'Reconnecting' : 'Offline'}
          </span>
          <button type="button" className="ghost-button" onClick={markAllRead}>
            Mark all read
          </button>
        </div>
      </header>

      <div className="module-grid">
        <article className="module-card emphasis">
          <h3>Live alert stream</h3>
          <p>
            {status.message || 'Listen for wallet, transfer, and low-balance updates as they arrive.'}
          </p>

          <div className="notification-summary">
            <span>{unreadCount} unread</span>
            <span>{notifications.length} total</span>
          </div>

          <div className="segmented-control" aria-label="Notification filters">
            {['all', 'unread', 'read'].map((option) => (
              <button
                key={option}
                type="button"
                className={`segment-button ${filter === option ? 'active' : ''}`}
                onClick={() => setFilter(option)}
              >
                {option === 'all' ? 'All' : option === 'unread' ? 'Unread' : 'Read'}
              </button>
            ))}
          </div>
        </article>

        <article className="module-card">
          <h3>Current window</h3>
          <p>{getFilterSummary(filter)}</p>
          <ul className="feature-list">
            <li>Transfer status updates</li>
            <li>Low-balance thresholds</li>
            <li>Deposit completion events</li>
          </ul>
        </article>
      </div>

      <div className="notification-list" aria-live="polite">
        {filteredNotifications.length === 0 ? (
          <div className="empty-state">
            <p>No notifications in this view yet.</p>
            <span>When the server sends an event, it will appear here automatically.</span>
          </div>
        ) : (
          filteredNotifications.map((notification) => (
            <article
              key={notification.id}
              className={`notification-item ${notification.read ? 'read' : 'unread'}`}
            >
              <div className="notification-header-row">
                <div className="notification-badge">{notification.title}</div>
                <time>{formatTimestamp(notification.createdAt)}</time>
              </div>

              <h4>{notification.message}</h4>

              <div className="notification-meta">
                {notification.walletId && <span>Wallet {notification.walletId}</span>}
                {notification.currency && (
                  <span>
                    {notification.balance != null ? formatCurrency(notification.balance, notification.currency) : notification.currency}
                  </span>
                )}
                {notification.threshold != null && notification.currency && (
                  <span>Threshold {formatCurrency(notification.threshold, notification.currency)}</span>
                )}
              </div>

              {!notification.read && (
                <div className="notification-actions">
                  <button type="button" onClick={() => markNotificationAsRead(notification.id)}>
                    Mark read
                  </button>
                </div>
              )}
            </article>
          ))
        )}
      </div>
    </section>
  )
}
