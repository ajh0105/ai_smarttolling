import apiClient from './client'

export const gpsApi = {
  latest() {
    return apiClient.get('/api/gps/telemetry/latest')
  }
}
