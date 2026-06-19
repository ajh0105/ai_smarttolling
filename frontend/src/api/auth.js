import apiClient from './client'

/**
 * 백엔드 API
 *  POST /api/auth/signup  { email, password, memberName, plateNumber }
 *  POST /api/auth/login   { email, password }
 *  POST /api/auth/logout
 */
export const authApi = {
  signUp(payload) {
    return apiClient.post('/api/auth/signup', payload)
  },
  login(payload) {
    return apiClient.post('/api/auth/login', payload)
  },
  logout() {
    return apiClient.post('/api/auth/logout')
  }
}
