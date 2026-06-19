import apiClient from './client'

export const pdmApi = {
  getDashboardSummary() {
    return apiClient.get('/api/v1/pdm/dashboard/summary')
  },
  getCameras() {
    return apiClient.get('/api/v1/pdm/cameras')
  },
  getCameraDetail(cameraId) {
    return apiClient.get(`/api/v1/pdm/cameras/${cameraId}`)
  },
  getQualityMetrics(cameraId, params = {}) {
    return apiClient.get(`/api/v1/pdm/cameras/${cameraId}/quality-metrics`, { params })
  },
  getAlerts(params = {}) {
    return apiClient.get('/api/v1/pdm/alerts', { params })
  },
  updateAlertStatus(alertId, status) {
    return apiClient.patch(`/api/v1/pdm/alerts/${alertId}/status`, { status })
  },
  getCompareResults(params = {}) {
    return apiClient.get('/api/v1/pdm/compare-results', { params })
  },
}

// PDM FastAPI 분석 서버 — 내부 관리자용
// 라우팅: /pdm-internal/* → FastAPI /internal/pdm/*  (vite.config.js proxy)
export const pdmFastApi = {
  // 스케줄러 설정 및 현재 상태 조회
  getStatus() {
    return apiClient.get('/pdm-internal/status')
  },
  // 수동 분석 1회 트리거 (백그라운드 실행, 짧은 ack 반환)
  runOnce() {
    return apiClient.post('/pdm-internal/run-once')
  },
  // 서버 헬스 체크
  health() {
    return apiClient.get('/pdm-internal/health')
  },
}
