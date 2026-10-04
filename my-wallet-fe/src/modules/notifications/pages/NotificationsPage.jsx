export function NotificationsPage() {
  return (
    <section className="page-shell">
      <header className="page-header">
        <div>
          <p className="eyebrow">Module</p>
          <h2>Notifications</h2>
        </div>
        <span className="page-badge">Alerts</span>
      </header>

      <div className="module-grid">
        <article className="module-card emphasis">
          <h3>Channel overview</h3>
          <p>Use a dedicated notifications module for server-sent events and in-app transfer alerts.</p>
        </article>

        <article className="module-card">
          <h3>Planned actions</h3>
          <ul className="feature-list">
            <li>Display real-time transfer alerts</li>
            <li>Show event history</li>
            <li>Filter unread notifications</li>
            <li>Prepare SSE integration</li>
          </ul>
        </article>
      </div>
    </section>
  )
}
