export function TransfersPage() {
  return (
    <section className="page-shell">
      <header className="page-header">
        <div>
          <p className="eyebrow">Module</p>
          <h2>Transfers</h2>
        </div>
        <span className="page-badge">Transfers</span>
      </header>

      <div className="module-grid">
        <article className="module-card emphasis">
          <h3>Transfer workspace</h3>
          <p>Prepare the form, validation rules, and confirmation flow for money transfers.</p>
        </article>

        <article className="module-card">
          <h3>Planned actions</h3>
          <ul className="feature-list">
            <li>Select sender and receiver wallets</li>
            <li>Validate sufficient funds</li>
            <li>Display confirmation and result states</li>
            <li>Track transfer history</li>
          </ul>
        </article>
      </div>
    </section>
  )
}
