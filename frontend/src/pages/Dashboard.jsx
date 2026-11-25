import { useCallback, useEffect, useState } from 'react'
import { useAuth } from '../AuthContext.jsx'
import { api, STATUSES, STATUS_LABEL } from '../api.js'
import JobForm from '../components/JobForm.jsx'

const PAGE_SIZE = 8

export default function Dashboard() {
  const { user, logout } = useAuth()
  const [jobs, setJobs] = useState({ content: [], page: 0, totalPages: 0, totalElements: 0 })
  const [stats, setStats] = useState({})
  const [status, setStatus] = useState('')
  const [search, setSearch] = useState('')
  const [query, setQuery] = useState('')
  const [page, setPage] = useState(0)
  const [editing, setEditing] = useState(null) // null = closed, {} = new, job = edit
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)

  // Wait 300ms after typing stops before searching.
  useEffect(() => {
    const t = setTimeout(() => {
      setQuery(search)
      setPage(0)
    }, 300)
    return () => clearTimeout(t)
  }, [search])

  const load = useCallback(async () => {
    try {
      const params = new URLSearchParams({ page, size: PAGE_SIZE })
      if (status) params.set('status', status)
      if (query) params.set('q', query)
      const [list, counts] = await Promise.all([
        api(`/api/jobs?${params}`),
        api('/api/jobs/stats'),
      ])
      setJobs(list)
      setStats(counts)
      setError('')
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }, [page, status, query])

  useEffect(() => {
    load()
  }, [load])

  async function save(payload) {
    if (editing.id) await api(`/api/jobs/${editing.id}`, { method: 'PUT', body: payload })
    else await api('/api/jobs', { method: 'POST', body: payload })
    setEditing(null)
    load()
  }

  async function changeStatus(job, next) {
    try {
      await api(`/api/jobs/${job.id}/status`, { method: 'PATCH', body: { status: next } })
      load()
    } catch (err) {
      setError(err.message)
    }
  }

  async function remove(job) {
    if (!window.confirm(`Delete your ${job.company} application?`)) return
    try {
      await api(`/api/jobs/${job.id}`, { method: 'DELETE' })
      load()
    } catch (err) {
      setError(err.message)
    }
  }

  const total = STATUSES.reduce((sum, s) => sum + (stats[s] || 0), 0)

  return (
    <div className="shell">
      <header className="topbar">
        <strong className="brand">Job Tracker</strong>
        <div className="who">
          <span>{user.fullName}</span>
          <button onClick={logout}>Log out</button>
        </div>
      </header>

      <section className="pipeline" aria-label="Applications by stage">
        <div className="bar" role="img" aria-label={`${total} applications in total`}>
          {STATUSES.map(
            (s) => stats[s] > 0 && <span key={s} className={`seg ${s}`} style={{ flexGrow: stats[s] }} />
          )}
          {total === 0 && <span className="seg empty" style={{ flexGrow: 1 }} />}
        </div>
        <div className="legend">
          <button className={!status ? 'chip on' : 'chip'} onClick={() => { setStatus(''); setPage(0) }}>
            All <b>{total}</b>
          </button>
          {STATUSES.map((s) => (
            <button
              key={s}
              className={status === s ? 'chip on' : 'chip'}
              onClick={() => { setStatus(s); setPage(0) }}
            >
              <i className={`dot ${s}`} />
              {STATUS_LABEL[s]} <b>{stats[s] || 0}</b>
            </button>
          ))}
        </div>
      </section>

      <div className="toolbar">
        <input
          type="search"
          placeholder="Search by company or role"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          aria-label="Search applications"
        />
        <button className="primary" onClick={() => setEditing({})}>Add application</button>
      </div>

      {error && <p className="error" role="alert">{error}</p>}

      {loading ? (
        <p className="muted">Loading your applications…</p>
      ) : jobs.content.length === 0 ? (
        <div className="empty">
          <p>{total === 0 ? 'No applications yet.' : 'Nothing matches that search or stage.'}</p>
          {total === 0 && <button className="primary" onClick={() => setEditing({})}>Add your first application</button>}
        </div>
      ) : (
        <ul className="list">
          {jobs.content.map((job) => (
            <li key={job.id} className="row">
              <div className="main">
                <h3>
                  {job.jobUrl ? <a href={job.jobUrl} target="_blank" rel="noreferrer">{job.company}</a> : job.company}
                </h3>
                <p>{job.role}{job.location ? `, ${job.location}` : ''}</p>
                {job.notes && <p className="notes">{job.notes}</p>}
              </div>
              <div className="meta">
                <select
                  className={`status ${job.status}`}
                  value={job.status}
                  onChange={(e) => changeStatus(job, e.target.value)}
                  aria-label={`Status for ${job.company}`}
                >
                  {STATUSES.map((s) => (
                    <option key={s} value={s}>{STATUS_LABEL[s]}</option>
                  ))}
                </select>
                <span className="date">{job.appliedDate || 'No date'}</span>
                <div className="row-actions">
                  <button onClick={() => setEditing(job)}>Edit</button>
                  <button className="danger" onClick={() => remove(job)}>Delete</button>
                </div>
              </div>
            </li>
          ))}
        </ul>
      )}

      {jobs.totalPages > 1 && (
        <nav className="pager" aria-label="Pagination">
          <button disabled={page === 0} onClick={() => setPage(page - 1)}>Previous</button>
          <span>Page {jobs.page + 1} of {jobs.totalPages}</span>
          <button disabled={page + 1 >= jobs.totalPages} onClick={() => setPage(page + 1)}>Next</button>
        </nav>
      )}

      {editing && <JobForm job={editing.id ? editing : null} onSave={save} onClose={() => setEditing(null)} />}
    </div>
  )
}
