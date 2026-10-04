export function WalletsPage() {
  return (
    <section className="page-shell">
      <header className="page-header">
        <div>
          <p className="eyebrow">Module</p>
          <h2>Wallets</h2>
        </div>
        <span className="page-badge">Wallets</span>
      </header>

      <div className="module-grid">
        <article className="module-card emphasis">
          <h3>Wallet overview</h3>
          <p>List user wallets, balances, and currency data for the RON account model.</p>
        </article>

        <article className="module-card">
          <h3>Planned actions</h3>
          <ul className="feature-list">
            <li>Create wallet</li>
            <li>View balance</li>
            <li>Track deposit history</li>
            <li>Scope data by authenticated user</li>
          </ul>
        </article>
      </div>
    </section>
  )
}
