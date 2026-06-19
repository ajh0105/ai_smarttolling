import { computed, onBeforeUnmount, onMounted, shallowRef } from 'vue'
import { dashboardApi } from '@/api/dashboard'

const EMPTY_ARRAY = Object.freeze([])
const EMPTY_OBJECT = Object.freeze({})

function readData(result, fallback) {
  return result?.status === 'fulfilled' ? (result.value?.data ?? fallback) : fallback
}

function asArray(value) {
  if (Array.isArray(value)) return value
  if (Array.isArray(value?.items)) return value.items
  if (Array.isArray(value?.content)) return value.content
  if (Array.isArray(value?.data)) return value.data
  return EMPTY_ARRAY
}

function firstItem(value) {
  const items = asArray(value)
  return items[0] ?? value ?? EMPTY_OBJECT
}

function isFreshVideoStatus(status) {
  if (!status || status.stale === true || status.connected !== true) return false
  const state = String(status.streamStatus ?? status.stream_status ?? status.status ?? '').toUpperCase()
  if (!['RUNNING', 'CONNECTED', 'STREAMING'].includes(state)) return false
  const frameAge = Number(status.lastFrameAgeMs ?? status.last_frame_age_ms)
  const transport = String(status.transport ?? '').toUpperCase()
  const staleMs = transport.includes('HLS') ? 10000 : 3000
  return !Number.isFinite(frameAge) || frameAge <= staleMs
}

export function useDashboardApi(options = {}) {
  const pollMs = options.pollMs ?? 3000
  const scope = options.scope ?? 'all'
  const loading = shallowRef(false)
  const error = shallowRef('')

  const operatorSummary = shallowRef(EMPTY_OBJECT)
  const operatorPassages = shallowRef(EMPTY_ARRAY)
  const operatorVideoStatus = shallowRef(EMPTY_OBJECT)
  const operatorDeviceStatuses = shallowRef(EMPTY_ARRAY)
  const operatorGpsJudgements = shallowRef(EMPTY_ARRAY)
  const operatorSettlementSummary = shallowRef(EMPTY_OBJECT)
  const operatorSettlementCandidates = shallowRef(EMPTY_ARRAY)

  const adminDashboardSummary = shallowRef(EMPTY_OBJECT)
  const adminSystemSummary = shallowRef(EMPTY_OBJECT)
  const adminSystemPipeline = shallowRef(EMPTY_OBJECT)
  const adminBackendStatus = shallowRef(EMPTY_OBJECT)
  const adminDbStatus = shallowRef(EMPTY_OBJECT)
  const adminVideoStatus = shallowRef(EMPTY_OBJECT)
  const adminEdges = shallowRef(EMPTY_ARRAY)
  const adminIngressStatus = shallowRef(EMPTY_OBJECT)
  const adminIngressEvents = shallowRef(EMPTY_ARRAY)
  const adminIngressTransitions = shallowRef(EMPTY_ARRAY)
  const operatorVideoStreamToken = shallowRef(Date.now())

  let timer = null
  let videoReconnectTimeout = null

  function bumpOperatorVideoStream() {
    operatorVideoStreamToken.value = Date.now()
  }

  function scheduleOperatorVideoReconnect(delayMs = 500) {
    if (videoReconnectTimeout) return
    videoReconnectTimeout = window.setTimeout(() => {
      videoReconnectTimeout = null
      if (operatorVideoIsLive.value) bumpOperatorVideoStream()
    }, delayMs)
  }

  async function refresh() {
    loading.value = true
    let hasFailure = false

    if (scope !== 'admin') {
      const results = await Promise.allSettled([
        dashboardApi.operatorSummary(),
        dashboardApi.operatorPassages(),
        dashboardApi.operatorVideoStatus(),
        dashboardApi.operatorDeviceStatus(),
        dashboardApi.operatorGpsJudgements(),
        dashboardApi.operatorSettlementSummary(),
        dashboardApi.operatorSettlementCandidates()
      ])
      hasFailure = hasFailure || results.some((result) => result.status === 'rejected')

      operatorSummary.value = readData(results[0], operatorSummary.value)
      operatorPassages.value = asArray(readData(results[1], operatorPassages.value))
      operatorVideoStatus.value = readData(results[2], operatorVideoStatus.value)
      operatorDeviceStatuses.value = asArray(readData(results[3], operatorDeviceStatuses.value))
      operatorGpsJudgements.value = asArray(readData(results[4], operatorGpsJudgements.value))
      operatorSettlementSummary.value = readData(results[5], operatorSettlementSummary.value)
      operatorSettlementCandidates.value = asArray(readData(results[6], operatorSettlementCandidates.value))
    }

    if (scope !== 'operator') {
      const results = await Promise.allSettled([
        dashboardApi.adminDashboardSummary(),
        dashboardApi.adminSystemSummary(),
        dashboardApi.adminSystemPipeline(),
        dashboardApi.adminBackendStatus(),
        dashboardApi.adminDbStatus(),
        dashboardApi.adminVideoStatus(),
        dashboardApi.adminEdges(),
        dashboardApi.adminIngressStatus(),
        dashboardApi.adminIngressEvents(),
        dashboardApi.adminIngressTransitions()
      ])
      hasFailure = hasFailure || results.some((result) => result.status === 'rejected')

      adminDashboardSummary.value = readData(results[0], adminDashboardSummary.value)
      adminSystemSummary.value = readData(results[1], adminSystemSummary.value)
      adminSystemPipeline.value = readData(results[2], adminSystemPipeline.value)
      adminBackendStatus.value = readData(results[3], adminBackendStatus.value)
      adminDbStatus.value = readData(results[4], adminDbStatus.value)
      adminVideoStatus.value = readData(results[5], adminVideoStatus.value)
      adminEdges.value = asArray(readData(results[6], adminEdges.value))
      adminIngressStatus.value = firstItem(readData(results[7], adminIngressStatus.value))
      adminIngressEvents.value = asArray(readData(results[8], adminIngressEvents.value))
      adminIngressTransitions.value = asArray(readData(results[9], adminIngressTransitions.value))
    }

    error.value = hasFailure ? 'Spring API 일부 응답 없음' : ''
    loading.value = false
  }

  onMounted(() => {
    void refresh()
    timer = window.setInterval(refresh, pollMs)
  })

  onBeforeUnmount(() => {
    if (timer) window.clearInterval(timer)
    if (videoReconnectTimeout) window.clearTimeout(videoReconnectTimeout)
  })

  const operatorVideoIsLive = computed(() => isFreshVideoStatus(operatorVideoStatus.value))
  const operatorVideoStreamUrl = computed(() => {
    const url = dashboardApi.operatorVideoStreamUrl()
    const separator = url.includes('?') ? '&' : '?'
    return `${url}${separator}t=${operatorVideoStreamToken.value}`
  })

  return {
    loading,
    error,
    operatorSummary,
    operatorPassages,
    operatorVideoStatus,
    operatorDeviceStatuses,
    operatorGpsJudgements,
    operatorSettlementSummary,
    operatorSettlementCandidates,
    operatorVideoIsLive,
    operatorVideoStreamUrl,
    operatorVideoStreamKey: operatorVideoStreamToken,
    refreshOperatorVideoStream: bumpOperatorVideoStream,
    scheduleOperatorVideoReconnect,
    adminDashboardSummary,
    adminSystemSummary,
    adminSystemPipeline,
    adminBackendStatus,
    adminDbStatus,
    adminVideoStatus,
    adminEdges,
    adminIngressStatus,
    adminIngressEvents,
    adminIngressTransitions,
    refresh
  }
}
