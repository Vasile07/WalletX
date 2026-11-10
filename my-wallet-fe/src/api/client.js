export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api'

export const apiRoutes = {
  auth: {
    login: '/auth/login',
    register: '/auth/register',
    me: '/auth/me',
  },
  wallets: '/wallets',
  transfers: '/transfers',
  notifications: '/notifications',
}

function getStoredToken() {
  const storedToken = localStorage.getItem('walletx.auth.token')
  if (!storedToken) {
    return null
  }

  try {
    const parsedToken = JSON.parse(storedToken)
    return typeof parsedToken === 'string' ? parsedToken : storedToken
  } catch {
    return storedToken
  }
}

export async function apiRequest(path, options = {}) {
  const token = options.token ?? getStoredToken()

  const requestOptions = {
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...(options.headers ?? {}),
    },
    ...options,
  }

  const response = await fetch(`${API_BASE_URL}${path}`, requestOptions)

  if (!response.ok) {
    const responseText = await response.text()
    const parsed = responseText ? JSON.parse(responseText) : null
    const message =
      typeof parsed === 'string'
        ? parsed
        : parsed?.message || parsed?.error || `Request failed with status ${response.status}`
    throw new Error(message || `Request failed with status ${response.status}`)
  }

  if (response.status === 204) {
    return null
  }

  const responseText = await response.text()
  if (!responseText) {
    return null
  }

  try {
    return JSON.parse(responseText)
  } catch {
    return responseText
  }
}
