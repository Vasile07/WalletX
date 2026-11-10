import { BrowserRouter } from 'react-router-dom'
import './App.css'
import { AppShell } from './components/AppShell'
import { AppStateProvider } from './state/appState'

function App() {
  return (
    <BrowserRouter>
      <AppStateProvider>
        <AppShell />
      </AppStateProvider>
    </BrowserRouter>
  )
}

export default App
