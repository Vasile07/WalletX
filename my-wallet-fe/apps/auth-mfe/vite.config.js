import react from '@vitejs/plugin-react'
import { federation } from '@module-federation/vite'
import { defineConfig } from 'vite'
import { fileURLToPath } from 'node:url'
import { sharedFederationDependencies } from '../../federation.shared.js'

export default defineConfig({
  envDir: fileURLToPath(new URL('../..', import.meta.url)),
  plugins: [
    federation({
      dts: false,
      name: 'auth-mfe',
      filename: 'remoteEntry.js',
      exposes: { './Page': './src/Page.jsx' },
      shared: sharedFederationDependencies,
      dev: { disableDynamicRemoteTypeHints: true, remoteHmr: true },
    }),
    react(),
  ],
  server: { port: 5174, origin: 'http://localhost:5174' },
  build: { target: 'chrome89' },
})
