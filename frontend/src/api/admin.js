import apiClient from './client'

export const adminApi = {
  members() {
    return apiClient.get('/api/admin/members')
  },
  assignDashboard(email, dashboardId) {
    return apiClient.patch(`/api/admin/members/${encodeURIComponent(email)}/dashboard`, { dashboardId })
  },
  companies() {
    return apiClient.get('/api/admin/companies')
  },
  createCompany(company) {
    return apiClient.post('/api/admin/companies', company)
  },
  mapMarkers() {
    return apiClient.get('/api/admin/map-markers')
  },
  saveMapMarkers(markers) {
    return apiClient.put('/api/admin/map-markers', markers)
  }
}
