import apiClient from './client'

export const dashboardApi = {
  operatorSummary() {
    return apiClient.get('/api/operator/dashboard/summary')
  },
  operatorPassages() {
    return apiClient.get('/api/operator/passages')
  },
  operatorVideoStatus() {
    return apiClient.get('/api/operator/video/status')
  },
  operatorDeviceStatus() {
    return apiClient.get('/api/operator/device-status')
  },
  operatorGpsJudgements() {
    return apiClient.get('/api/operator/gps-judgements')
  },
  operatorSettlementSummary() {
    return apiClient.get('/api/operator/settlements/summary')
  },
  operatorSettlementCandidates() {
    return apiClient.get('/api/operator/settlements/candidates')
  },

  adminDashboardSummary() {
    return apiClient.get('/api/admin/dashboard/summary')
  },
  adminSystemSummary() {
    return apiClient.get('/api/admin/system/summary')
  },
  adminSystemPipeline() {
    return apiClient.get('/api/admin/system/pipeline')
  },
  adminBackendStatus() {
    return apiClient.get('/api/admin/backend/status')
  },
  adminDbStatus() {
    return apiClient.get('/api/admin/db/status')
  },
  adminVideoStatus() {
    return apiClient.get('/api/admin/video/status')
  },
  adminEdges() {
    return apiClient.get('/api/admin/edges')
  },
  adminIngressStatus() {
    return apiClient.get('/api/admin/ingress/status')
  },
  adminIngressEvents() {
    return apiClient.get('/api/admin/ingress/events/recent')
  },
  adminIngressTransitions() {
    return apiClient.get('/api/admin/ingress/transitions')
  },

  operatorVideoStreamUrl() {
    return '/video/hls/master.m3u8'
  }
}
