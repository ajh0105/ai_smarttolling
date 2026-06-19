import apiClient from './client'

export const tollApi = {
  zones() {
    return apiClient.get('/api/toll/zones')
  },
  latestHistory() {
    return apiClient.get('/api/toll/history/latest')
  },
  createZone(payload) {
    return apiClient.post('/api/toll/zones', payload)
  },
  recognizePlate(payload) {
    return apiClient.post('/api/toll/plate-recognitions', payload)
  }
}
