import { cleanup, render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { afterEach, describe, expect, it, vi } from 'vitest'
import App from './App'

const summary = {
  id: '11111111-1111-1111-1111-111111111111',
  displayName: 'Everyday account',
  availableBalance: '12480.68',
  currency: 'CAD',
}

const transactions = [
  {
    id: 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    description: 'Payroll deposit',
    amount: '2500.00',
    type: 'CREDIT',
    bookedOn: '2026-08-15',
  },
  {
    id: 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
    description: 'Grocery store',
    amount: '84.19',
    type: 'DEBIT',
    bookedOn: '2026-08-14',
  },
]

function mockApi() {
  vi.stubGlobal(
    'fetch',
    vi.fn((input: RequestInfo | URL) => {
      const body = String(input).endsWith('/summary') ? summary : transactions
      return Promise.resolve({
        ok: true,
        json: () => Promise.resolve(body),
      } as Response)
    }),
  )
}

describe('LedgerLabs dashboard', () => {
  afterEach(() => {
    cleanup()
    vi.unstubAllGlobals()
  })

  it('loads the account overview from the API', async () => {
    mockApi()

    render(<App />)

    expect(screen.getByRole('heading', { name: /welcome back, anuj/i })).toBeVisible()
    expect(await screen.findByText(/12,480\.68/)).toBeVisible()
    expect(screen.getByText('Everyday account')).toBeVisible()
    expect(screen.getByText('Payroll deposit')).toBeVisible()
  })

  it('filters the visible activity without another request', async () => {
    mockApi()
    const user = userEvent.setup()

    render(<App />)
    await screen.findByText('Grocery store')

    await user.click(screen.getByRole('button', { name: 'Money in' }))

    expect(screen.getByText('Payroll deposit')).toBeVisible()
    expect(screen.queryByText('Grocery store')).not.toBeInTheDocument()
    expect(fetch).toHaveBeenCalledTimes(2)
  })
})
