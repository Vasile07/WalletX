import { afterEach, describe, expect, it, vi } from 'vitest'
import { apiRequest } from './index.js'

describe('shared API client', () => {
  afterEach(() => {
    localStorage.removeItem('walletx.auth.token')
    vi.unstubAllGlobals()
  })

  it('attaches the shell-persisted bearer token to protected API calls', async () => {
    localStorage.setItem('walletx.auth.token', JSON.stringify('shell-owned-token'))
    const fetchMock = vi.fn().mockResolvedValue({
      ok: true,
      status: 200,
      text: async () => '[]',
    })
    vi.stubGlobal('fetch', fetchMock)

    await apiRequest('/wallets')

    expect(fetchMock).toHaveBeenCalledWith(
      expect.stringContaining('/wallets'),
      expect.objectContaining({
        headers: expect.objectContaining({
          Authorization: 'Bearer shell-owned-token',
        }),
      }),
    )
  })
})
