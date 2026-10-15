import { useEffect, useMemo, useState } from 'react'
import './App.css'

type AccountSummary = {
  id: string
  displayName: string
  availableBalance: string
  currency: string
}

type Transaction = {
  id: string
  description: string
  amount: string
  type: 'CREDIT' | 'DEBIT'
  bookedOn: string
}

type ActivityFilter = 'ALL' | Transaction['type']

const ACCOUNT_ID = '11111111-1111-1111-1111-111111111111'
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? ''
const USE_DEMO_DATA = import.meta.env.PROD && !import.meta.env.VITE_API_BASE_URL

const DEMO_SUMMARY: AccountSummary = {
  id: ACCOUNT_ID,
  displayName: 'Everyday account',
  availableBalance: '12480.68',
  currency: 'CAD',
}

const DEMO_TRANSACTIONS: Transaction[] = [
  {
    id: 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    description: 'Payroll deposit',
    amount: '2500.00',
    type: 'CREDIT',
    bookedOn: '2026-10-15',
  },
  {
    id: 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
    description: 'Grocery market',
    amount: '84.19',
    type: 'DEBIT',
    bookedOn: '2026-10-14',
  },
  {
    id: 'cccccccc-cccc-cccc-cccc-cccccccccccc',
    description: 'Electricity bill',
    amount: '126.45',
    type: 'DEBIT',
    bookedOn: '2026-10-12',
  },
  {
    id: 'dddddddd-dddd-dddd-dddd-dddddddddddd',
    description: 'Coffee shop',
    amount: '6.80',
    type: 'DEBIT',
    bookedOn: '2026-10-11',
  },
]

const money = (amount: string | number, currency: string) =>
  new Intl.NumberFormat('en-CA', {
    style: 'currency',
    currency,
    minimumFractionDigits: 2,
  }).format(Number(amount))

const shortDate = (date: string) =>
  new Intl.DateTimeFormat('en-CA', {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
    timeZone: 'UTC',
  }).format(new Date(`${date}T00:00:00Z`))

function App() {
  const [summary, setSummary] = useState<AccountSummary | null>(() =>
    USE_DEMO_DATA ? DEMO_SUMMARY : null,
  )
  const [transactions, setTransactions] = useState<Transaction[]>(() =>
    USE_DEMO_DATA ? DEMO_TRANSACTIONS : [],
  )
  const [filter, setFilter] = useState<ActivityFilter>('ALL')
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (USE_DEMO_DATA) return

    const controller = new AbortController()

    async function loadDashboard() {
      try {
        const [summaryResponse, transactionsResponse] = await Promise.all([
          fetch(`${API_BASE_URL}/api/accounts/${ACCOUNT_ID}/summary`, { signal: controller.signal }),
          fetch(`${API_BASE_URL}/api/accounts/${ACCOUNT_ID}/transactions`, { signal: controller.signal }),
        ])

        if (!summaryResponse.ok || !transactionsResponse.ok) {
          throw new Error('The banking service is currently unavailable.')
        }

        const [account, activity] = await Promise.all([
          summaryResponse.json() as Promise<AccountSummary>,
          transactionsResponse.json() as Promise<Transaction[]>,
        ])

        setSummary(account)
        setTransactions(activity)
      } catch (requestError) {
        if (requestError instanceof DOMException && requestError.name === 'AbortError') return
        setError('We could not load your account. Please try again shortly.')
      }
    }

    void loadDashboard()
    return () => controller.abort()
  }, [])

  const visibleTransactions = useMemo(
    () =>
      filter === 'ALL'
        ? transactions
        : transactions.filter((transaction) => transaction.type === filter),
    [filter, transactions],
  )

  const totals = useMemo(
    () =>
      transactions.reduce(
        (result, transaction) => ({
          ...result,
          [transaction.type]: result[transaction.type] + Number(transaction.amount),
        }),
        { CREDIT: 0, DEBIT: 0 },
      ),
    [transactions],
  )

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <a className="brand" href="#overview" aria-label="LedgerLabs home">
          <span className="brand-mark" aria-hidden="true">L</span>
          <span>LedgerLabs</span>
        </a>
        <p className="brand-note">Banking, made beautifully clear.</p>

        <nav aria-label="Primary navigation">
          <a className="nav-link active" href="#overview"><span>01</span> Overview</a>
          <a className="nav-link" href="#activity"><span>02</span> Activity</a>
          <a className="nav-link" href="#platform"><span>03</span> Platform</a>
        </nav>

        <div className="sidebar-bottom">
          <p className="secure-label"><span className="status-dot" /> Systems operational</p>
          <a
            className="github-link"
            href="https://github.com/AnujModi/ledgerlabs"
            target="_blank"
            rel="noreferrer"
          >
            View source <span aria-hidden="true">↗</span>
          </a>
        </div>
      </aside>

      <main>
        <header className="topbar">
          <div className="environment-pill"><span /> Demo environment</div>
          <div className="profile">
            <span className="profile-copy"><strong>Anuj Modi</strong><small>Personal banking</small></span>
            <span className="avatar" aria-hidden="true">AM</span>
          </div>
        </header>

        <section className="hero-section" id="overview">
          <p className="eyebrow">Your financial home</p>
          <h1>Welcome back, Anuj.</h1>
          <p className="hero-copy">
            Your money at a glance—calm, current, and built around the decisions that matter.
          </p>
        </section>

        {error ? (
          <section className="error-panel" role="alert">
            <span aria-hidden="true">!</span>
            <div><strong>Account data unavailable</strong><p>{error}</p></div>
          </section>
        ) : (
          <section className="account-grid" aria-label="Account overview">
            <article className="balance-card">
              <div className="card-topline">
                <span>{summary?.displayName ?? 'Loading account…'}</span>
                <span className="card-chip">•• 4821</span>
              </div>
              <div className="balance-copy">
                <span>Available balance</span>
                <strong>{summary ? money(summary.availableBalance, summary.currency) : '—'}</strong>
                <small>{summary?.currency ?? 'CAD'} · Updated just now</small>
              </div>
              <div className="card-actions" aria-label="Quick actions">
                <button type="button"><span aria-hidden="true">↗</span> Send</button>
                <button type="button"><span aria-hidden="true">＋</span> Add money</button>
                <button type="button" aria-label="More account actions">•••</button>
              </div>
            </article>

            <div className="metric-stack">
              <article className="metric-card">
                <span className="metric-icon credit" aria-hidden="true">↓</span>
                <div><small>Money in</small><strong>{money(totals.CREDIT, summary?.currency ?? 'CAD')}</strong></div>
                <span className="metric-trend positive">This month</span>
              </article>
              <article className="metric-card">
                <span className="metric-icon debit" aria-hidden="true">↑</span>
                <div><small>Money out</small><strong>{money(totals.DEBIT, summary?.currency ?? 'CAD')}</strong></div>
                <span className="metric-trend">This month</span>
              </article>
            </div>
          </section>
        )}

        <section className="activity-section" id="activity">
          <div className="section-heading">
            <div><p className="eyebrow">Recent movement</p><h2>Account activity</h2></div>
            <div className="filter-group" aria-label="Filter activity">
              {([
                ['ALL', 'All'],
                ['CREDIT', 'Money in'],
                ['DEBIT', 'Money out'],
              ] as const).map(([value, label]) => (
                <button
                  className={filter === value ? 'selected' : ''}
                  key={value}
                  onClick={() => setFilter(value)}
                  type="button"
                >
                  {label}
                </button>
              ))}
            </div>
          </div>

          <div className="activity-list" aria-live="polite">
            {visibleTransactions.map((transaction) => (
              <article className="transaction-row" key={transaction.id}>
                <span className={`transaction-icon ${transaction.type.toLowerCase()}`} aria-hidden="true">
                  {transaction.type === 'CREDIT' ? '↓' : '↑'}
                </span>
                <div className="transaction-copy">
                  <strong>{transaction.description}</strong>
                  <span>{shortDate(transaction.bookedOn)} · {transaction.type === 'CREDIT' ? 'Deposit' : 'Card payment'}</span>
                </div>
                <strong className={`transaction-amount ${transaction.type.toLowerCase()}`}>
                  {transaction.type === 'CREDIT' ? '+' : '−'}{money(transaction.amount, summary?.currency ?? 'CAD')}
                </strong>
              </article>
            ))}
            {!error && summary && visibleTransactions.length === 0 && (
              <p className="empty-state">No activity matches this filter.</p>
            )}
            {!error && !summary && <div className="loading-line">Loading your latest activity…</div>}
          </div>
        </section>

        <section className="platform-section" id="platform">
          <div>
            <p className="eyebrow">Built in the open</p>
            <h2>Modern banking architecture,<br />explained—not hidden.</h2>
          </div>
          <div className="platform-copy">
            <p>
              LedgerLabs is a full-stack learning project focused on the engineering decisions
              behind reliable financial software: precise money types, tested APIs, observable
              services, and independently deployable interfaces.
            </p>
            <div className="tech-list" aria-label="Technology stack">
              <span>Java 21</span><span>Spring Boot</span><span>React 19</span>
              <span>TypeScript</span><span>Docker</span><span>PostgreSQL</span>
            </div>
          </div>
        </section>

        <footer>
          <span>LedgerLabs · A production-minded banking sandbox</span>
          <span>Designed and engineered by Anuj Modi</span>
        </footer>
      </main>
    </div>
  )
}

export default App
