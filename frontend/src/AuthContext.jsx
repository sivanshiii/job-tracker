import { createContext, useContext, useState } from 'react'
import { api } from './api.js'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem('user')
    return saved ? JSON.parse(saved) : null
  })

  function save(data) {
    localStorage.setItem('token', data.token)
    const u = { email: data.email, fullName: data.fullName }
    localStorage.setItem('user', JSON.stringify(u))
    setUser(u)
  }

  async function login(email, password) {
    save(await api('/api/auth/login', { method: 'POST', body: { email, password } }))
  }

  async function register(fullName, email, password) {
    save(await api('/api/auth/register', { method: 'POST', body: { fullName, email, password } }))
  }

  function logout() {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    setUser(null)
  }

  return <AuthContext.Provider value={{ user, login, register, logout }}>{children}</AuthContext.Provider>
}

export const useAuth = () => useContext(AuthContext)
