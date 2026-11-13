import react from '@vitejs/plugin-react'
import { federation } from '@module-federation/vite'
import { defineConfig, loadEnv } from 'vite'
import { fileURLToPath } from 'node:url'
import { sharedFederationDependencies } from '../../federation.shared.js'

const root = fileURLToPath(new URL('../..', import.meta.url))
const remoteDefinitions = [
  ['auth-mfe', 5174],
  ['wallet-mfe', 5175],
  ['transfer-mfe', 5176],
  ['notification-mfe', 5177],
]

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, root)

  return {
    envDir: root,
    plugins: [
      federation({
        dts: false,
        name: 'walletx-shell',
        remotes: Object.fromEntries(
          remoteDefinitions.map(([name, port]) => [
            name,
            {
              type: 'module',
              name,
              entry: env[`VITE_${name.replaceAll('-', '_').toUpperCase()}_REMOTE_URL`] ||
                `http://localhost:${port}/remoteEntry.js`,
              entryGlobalName: name,
              shareScope: 'default',
            },
          ]),
        ),
        shared: sharedFederationDependencies,
        dev: { disableDynamicRemoteTypeHints: true, remoteHmr: true },
      }),
      react(),
    ],
    server: { port: 5173 },
    build: { target: 'chrome89' },
  }
})
