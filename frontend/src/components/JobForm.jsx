import { useState } from 'react'
import { STATUSES, STATUS_LABEL } from '../api.js'

const EMPTY = {
  company: '',
  role: '',
  location: '',
  status: 'APPLIED',
  jobUrl: '',
  notes: '',
  appliedDate: new Date().toISOString().slice(0, 10),
}

export default function JobForm({ job, onSave, onClose }) {
  const [form, setForm] = useState(
    job
      ? { ...EMPTY, ...Object.fromEntries(Object.entries(job).map(([k, v]) => [k, v ?? ''])) }
      : EMPTY
  )
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  const set = (field) => (e) => setForm({ ...form, [field]: e.target.value })

  async function submit(e) {
    e.preventDefault()
    setError('')
    setBusy(true)
    try {
      await onSave({
        company: form.company,
        role: form.role,
        location: form.location || null,
        status: form.status,
        jobUrl: form.jobUrl || null,
        notes: form.notes || null,
        appliedDate: form.appliedDate || null,
      })
    } catch (err) {
      setError(err.message)
      setBusy(false)
    }
  }

  return (
    <div className="overlay" onMouseDown={(e) => e.target === e.currentTarget && onClose()}>
      <form className="modal" onSubmit={submit}>
        <h2>{job ? 'Edit application' : 'Add application'}</h2>
        <div className="grid">
          <label>
            Company
            <input value={form.company} onChange={set('company')} required autoFocus />
          </label>
          <label>
            Role
            <input value={form.role} onChange={set('role')} required />
          </label>
          <label>
            Location
            <input value={form.location} onChange={set('location')} />
          </label>
          <label>
            Status
            <select value={form.status} onChange={set('status')}>
              {STATUSES.map((s) => (
                <option key={s} value={s}>{STATUS_LABEL[s]}</option>
              ))}
            </select>
          </label>
          <label>
            Date applied
            <input type="date" value={form.appliedDate} onChange={set('appliedDate')} />
          </label>
          <label>
            Job link
            <input type="url" value={form.jobUrl} onChange={set('jobUrl')} placeholder="https://" />
          </label>
        </div>
        <label>
          Notes
          <textarea rows={3} maxLength={2000} value={form.notes} onChange={set('notes')} />
        </label>
        {error && <p className="error" role="alert">{error}</p>}
        <div className="actions">
          <button type="button" onClick={onClose}>Cancel</button>
          <button className="primary" disabled={busy}>{busy ? 'Saving…' : 'Save application'}</button>
        </div>
      </form>
    </div>
  )
}
