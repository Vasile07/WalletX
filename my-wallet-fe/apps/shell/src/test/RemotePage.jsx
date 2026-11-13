import React from 'react'
import { useLocation } from 'react-router-dom'
import { useAppState } from '@walletx/shared'

export default function RemotePage() {
  const { pathname } = useLocation()
  const { user, token } = useAppState()
  return <p>Remote rendered at {pathname} for {user?.name || 'anonymous'}: {token || 'no token'}</p>
}
