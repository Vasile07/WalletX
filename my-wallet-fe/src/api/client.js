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

export async function apiRequest(path, options = {}) {
  const requestOptions = {
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers ?? {}),
    },
    ...options,
  }

  const response = await fetch(`${API_BASE_URL}${path}`, requestOptions)

  if (!response.ok) {
    const errorText = await response.text()
    throw new Error(errorText || `Request failed with status ${response.status}`)
  }

  if (response.status === 204) {
    return null
  }

  return response.json()
}
