import apiClient from './client'
import {
  mockSummary,
  mockCameras,
  mockQualityMetrics,
  mockAlerts,
  mockCompareResults,
  mockFastApiStatus
} from '@/data/pdmMockData'

const DEMO_MODE = import.meta.env.VITE_DEMO_MODE === 'true'

function mockRes(data) {
  return Promise.resolve({ data })
}

export const pdmApi = {
  getDashboardSummary() {
    if (DEMO_MODE) return mockRes(mockSummary)
    return apiClient.get('/api/v1/pdm/dashboard/summary')
  },
  getCameras() {
    if (DEMO_MODE) return mockRes(mockCameras)
    return apiClient.get('/api/v1/pdm/cameras')
  },
  getCameraDetail(cameraId) {
    if (DEMO_MODE) return mockRes(mockCameras.find(c => c.cameraId === cameraId) ?? mockCameras[0])
    return apiClient.get(`/api/v1/pdm/cameras/${cameraId}`)
  },
  getQualityMetrics(cameraId, params = {}) {
    if (DEMO_MODE) return mockRes(mockQualityMetrics[cameraId] ?? [])
    return apiClient.get(`/api/v1/pdm/cameras/${cameraId}/quality-metrics`, { params })
  },
  getAlerts(params = {}) {
    if (DEMO_MODE) return mockRes(mockAlerts)
    return apiClient.get('/api/v1/pdm/alerts', { params })
  },
  updateAlertStatus(alertId, status) {
    if (DEMO_MODE) return mockRes({ alertId, status })
    return apiClient.patch(`/api/v1/pdm/alerts/${alertId}/status`, { status })
  },
  getCompareResults(params = {}) {
    if (DEMO_MODE) return mockRes(mockCompareResults)
    return apiClient.get('/api/v1/pdm/compare-results', { params })
  },
}

// PDM FastAPI 분석 서버 — 내부 관리자용
// 라우팅: /pdm-internal/* → FastAPI /internal/pdm/*  (vite.config.js proxy)
export const pdmFastApi = {
  getStatus() {
    if (DEMO_MODE) return mockRes(mockFastApiStatus)
    return apiClient.get('/pdm-internal/status')
  },
  runOnce() {
    if (DEMO_MODE) return mockRes({ message: '[데모] 분석이 백그라운드에서 시작되었습니다.' })
    return apiClient.post('/pdm-internal/run-once')
  },
  health() {
    if (DEMO_MODE) return mockRes({ status: 'ok' })
    return apiClient.get('/pdm-internal/health')
  },
}
