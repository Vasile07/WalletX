import { fileURLToPath } from 'node:url'
import react from '@vitejs/plugin-react'
import { defineConfig } from 'vitest/config'

const remotePage = fileURLToPath(new URL('./src/test/RemotePage.jsx', import.meta.url))

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      'auth-mfe/Page': remotePage,
      'wallet-mfe/Page': remotePage,
      'transfer-mfe/Page': remotePage,
      'notification-mfe/Page': remotePage,
    },
  },
  test: {
    environment: 'jsdom',
    globals: true,
  },
})
