<script setup>
import { computed, onBeforeUnmount, onMounted, provide, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useDashboardApi } from '@/composables/useDashboardApi'
// Dashboard CSS는 동적으로 head에 추가하고 페이지 떠날 때 제거 — 다른 페이지에 누출되지 않도록 격리
import controlDashboardCss from '@/dashboards/control/styles/control-dashboard.css?inline'
import ControlDashboardPage from '@/dashboards/control/pages/ControlDashboardPage.vue'
import ControlTrafficEventPage from '@/dashboards/control/pages/ControlTrafficEventPage.vue'
import ControlGpsDecisionPage from '@/dashboards/control/pages/ControlGpsDecisionPage.vue'
import ControlSettlementPage from '@/dashboards/control/pages/ControlSettlementPage.vue'
import ControlEquipmentPage from '@/dashboards/control/pages/ControlEquipmentPage.vue'
import ControlRealtimePage from '@/dashboards/control/pages/ControlRealtimePage.vue'
import ControlSettingsPage from '@/dashboards/control/pages/ControlSettingsPage.vue'
import ControlFallbackPage from '@/dashboards/control/pages/ControlFallbackPage.vue'
import PdmDashboardPage from '@/dashboards/pdm/pages/PdmDashboardPage.vue'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const dashboardApiState = useDashboardApi({ scope: 'operator', pollMs: 1000 })
const renderedVideoFps = ref(null)
const renderedVideoFpsAt = ref(0)
const nowMs = ref(Date.now())
const VIDEO_RENDERED_FPS_GRACE_MS = 15000
const BASE_PASSAGE_COUNT = 1000
const PASSAGE_TOLL_WON = 2000
const seenPassageEventIds = ref(new Set())
const cumulativeLanePassages = ref({ 1: BASE_PASSAGE_COUNT, 2: BASE_PASSAGE_COUNT })
const recentGpsRows = ref([])
const passageReceivedAtById = ref(new Map())

const THEME_STORAGE_KEY = 'hifive.dashboard.theme'
const NETWORK_OVERRIDE_STORAGE_KEY = 'hifive.networkPathOverride'
const nowText = ref('')
const activeMenu = ref('대시보드')
const selectedLane = ref(2)
const isLightMode = ref(true)
const showNotifications = ref(false)
const showOperatorMenu = ref(false)
const networkPathOverride = ref(localStorage.getItem(NETWORK_OVERRIDE_STORAGE_KEY) || '')

let timer = null

const centerLabel = computed(() => {
  const center = route.query.center ?? 'SEOUL-TOLL'
  const names = {
    'SEOUL-TOLL': '서울 톨링 A',
    'SUWON-TOLL': '수원 톨링 A',
    'DAEJEON-TOLL': '대전 톨링 B',
    'DAEGU-TOLL': '대구 톨링 C',
    'BUSAN-TOLL': '부산 톨링 D'
  }
  return names[center] ?? center
})

const navItems = [
  { label: '대시보드', icon: 'dashboard2.png' },
  { label: '예지보전', icon: 'cctv.png' },
  { label: '실시간 관제', icon: 'real-time.png' },
  { label: '통행 이벤트', icon: 'list.png' },
  { label: 'GPS 판정', icon: 'gps.png' },
  { label: '정산', icon: 'won.png' },
  { label: '장비 상태', icon: 'system_set.png' },
  { label: '설정', icon: 'setting.png' }
]

const fallbackDashboardKpis = [
  { title: '오늘 통행', value: '1,248', unit: '대', sub: '전일 대비 ▲ 6.8%', icon: 'car.png', tone: 'purple', trend: 'up' },
  { title: 'GPS 정상 판정', value: '1,212', unit: '건', sub: '정상률 97.1%', icon: 'gps.png', tone: 'blue', trend: 'up' },
  { title: '검수 대기', value: '36', unit: '건', sub: '전일 대비 ▼ 5건', icon: 'caution.png', tone: 'yellow', trend: 'down' },
  { title: '오늘 통행료', value: '₩2,450,800', unit: '', sub: '전일 대비 ▲ 6.1%', icon: 'won.png', tone: 'navy', trend: 'up' }
]

const laneDashboardKpis = {
  1: [
    { title: '상행 통행', value: '684', unit: '대', sub: '전일 대비 ▲ 4.2%', icon: 'car.png', tone: 'purple', trend: 'up' },
    { title: '상행 GPS 정상', value: '667', unit: '건', sub: '정상률 97.5%', icon: 'gps.png', tone: 'blue', trend: 'up' },
    { title: '상행 검수 대기', value: '17', unit: '건', sub: '전일 대비 ▼ 3건', icon: 'caution.png', tone: 'yellow', trend: 'down' },
    { title: '상행 통행료', value: '₩1,342,600', unit: '', sub: '전일 대비 ▲ 5.4%', icon: 'won.png', tone: 'navy', trend: 'up' }
  ],
  2: [
    { title: '하행 통행', value: '564', unit: '대', sub: '전일 대비 ▲ 8.9%', icon: 'car.png', tone: 'purple', trend: 'up' },
    { title: '하행 GPS 정상', value: '545', unit: '건', sub: '정상률 96.6%', icon: 'gps.png', tone: 'blue', trend: 'up' },
    { title: '하행 검수 대기', value: '19', unit: '건', sub: '전일 대비 ▲ 2건', icon: 'caution.png', tone: 'yellow', trend: 'up' },
    { title: '하행 통행료', value: '₩1,108,200', unit: '', sub: '전일 대비 ▲ 7.2%', icon: 'won.png', tone: 'navy', trend: 'up' }
  ]
}

const fallbackDashboardDetections = [
  {
    lane: 1,
    title: '1번 레일',
    size: '960x480',
    plate: '31가 9829',
    distance: '20px',
    fps: 'FPS 29.8',
    color: 'green',
    vehicle: 'black'
  },
  {
    lane: 2,
    title: '2번 레일',
    size: '960x480',
    plate: '46다 7720',
    distance: '18px',
    fps: 'FPS 29.8',
    color: 'blue',
    vehicle: 'silver'
  }
]

const fallbackStatusCards = [
  { label: 'CCTV 영상', value: '정상', icon: 'cctv.png', tone: 'ok' },
  { label: 'GPS 수신', value: '정상', icon: 'gps2.png', tone: 'ok' },
  { label: '이벤트 수신', value: '정상', icon: 'notification.png', tone: 'ok' },
  { label: '통신망', value: 'LAN 사용 중', icon: 'signalpng.png', tone: 'info' },
  { label: '데이터 반영', value: '정상', icon: 'data.png', tone: 'ok' }
]

const fallbackGpsJudgements = [
  { lane: 1, plate: '31가 9829', direction: 'IN', laneText: 'L1', time: '17:36:47', gps: '정상', payment: '결제 가능', tone: 'ok' },
  { lane: 2, plate: '45나 6721', direction: 'OUT', laneText: 'L2', time: '17:36:12', gps: '경계 걸침', payment: '검수 권장', tone: 'boundary' },
  { lane: 1, plate: '67더 9012', direction: 'OUT', laneText: 'L1', time: '17:35:41', gps: '영역 이탈', payment: '검수 필요', tone: 'danger' }
]

const fieldAlerts = [
  { level: 'danger', title: '정차 의심', target: '2차선 · 98머 3344', time: '17:28:55', badge: '주의', icon: 'danger.png' },
  { level: 'warn', title: '2차선 CCTV 수신 지연', target: '2차선 카메라', time: '17:26:28', badge: '주의', icon: 'caution3.png' },
  { level: 'info', title: 'LTE 백업망 전환', target: 'LAN 연결 끊김 감지', time: '17:24:10', badge: '정보', icon: 'information2.png' }
]

const laneFieldAlerts = {
  1: [
    { level: 'warn', title: '상행 GPS 경계 접근', target: '상행 · 31가 9829', time: '17:36:47', badge: '주의', icon: 'caution3.png' },
    { level: 'info', title: '상행 이벤트 정상 수신', target: '상행 레일', time: '17:35:22', badge: '정보', icon: 'information2.png' },
    { level: 'info', title: '상행 LAN 상태 정상', target: '상행 Edge', time: '17:31:08', badge: '정보', icon: 'information2.png' }
  ],
  2: [
    { level: 'danger', title: '하행 정차 의심', target: '하행 · 98머 3344', time: '17:28:55', badge: '주의', icon: 'danger.png' },
    { level: 'warn', title: '하행 CCTV 수신 지연', target: '하행 카메라', time: '17:26:28', badge: '주의', icon: 'caution3.png' },
    { level: 'info', title: '하행 LTE 백업망 전환', target: 'LAN 연결 끊김 감지', time: '17:24:10', badge: '정보', icon: 'information2.png' }
  ]
}

const notifications = [
  { level: 'danger', title: '정차 의심 차량 확인 필요', desc: '2차선 · 98머 3344', time: '17:28:55' },
  { level: 'warn', title: '2차선 CCTV 수신 지연', desc: '카메라 프레임 지연 3초', time: '17:26:28' },
  { level: 'info', title: 'LTE 백업망 전환 기록', desc: 'LAN 연결 끊김 감지 후 복구', time: '17:24:10' }
]

const fallbackTrafficRows = [
  { lane: 1, plate: '31가 9829', direction: 'IN', time: '17:36:47', gps: '정상', status: '정상 통과', tone: 'ok' },
  { lane: 2, plate: '46다 7720', direction: 'OUT', time: '17:36:12', gps: '경계 걸침', status: '검수 권장', tone: 'boundary' },
  { lane: 1, plate: '12가 3456', direction: 'IN', time: '17:35:41', gps: '영역 이탈', status: '검수 필요', tone: 'danger' },
  { lane: 2, plate: '85나 1212', direction: 'OUT', time: '17:35:18', gps: '정상', status: '정상 통과', tone: 'ok' }
]

const equipmentCards = [
  { title: '카메라 입력', status: '정상', desc: 'YOLO 합성 프레임 정상 수신', impact: '운영 정상', icon: 'cctv.png', tone: 'purple' },
  { title: 'GPS 수신', status: '정상', desc: 'GPS Fix 정상', impact: '운영 정상', icon: 'gps.png', tone: 'green' },
  { title: '통행 이벤트 수신', status: '정상', desc: '실시간 이벤트 정상 수신', impact: '운영 정상', icon: 'dashboard2.png', tone: 'blue' },
  { title: '통신망', status: '정상', desc: '현재 통신: LAN 사용 중', impact: '운영 정상', icon: 'signalpng.png', tone: 'teal' },
  { title: '데이터 반영', status: '정상', desc: '서버 반영 정상', impact: '운영 정상', icon: 'data.png', tone: 'mint' }
]

const equipmentLaneRows = [
  { lane: 1, name: '1번 레일 영역', eventAt: '17:36:46 (1초 전)', gps: '정상', analysis: '정상', impact: '운영 정상' },
  { lane: 2, name: '2번 레일 영역', eventAt: '17:36:46 (1초 전)', gps: '정상', analysis: '정상', impact: '운영 정상' }
]

const equipmentAlerts = [
  { tone: 'warn', title: '카메라 수신 지연', desc: '영상 지연 3초 발생', scope: '전 레일', time: '17:33:12' },
  { tone: 'info', title: 'LTE 전환 기록', desc: 'LAN 장애 후 복구', scope: '전 장비', time: '17:24:33' },
  { tone: 'warn', title: 'GPS 수신 지연', desc: 'Fix 지연 2초 발생', scope: '전 레일', time: '17:18:05' }
]

const historyRows = [
  { time: '17:36:28', item: '통행 이벤트 수신', impact: '없음', detail: '양 레일 이벤트 수신 정상', status: '완료', actor: '-' },
  { time: '17:33:12', item: '카메라 영상 수신 지연', impact: '전체 레일', detail: '카메라 영상 수신 지연 3초 발생 후 복구', status: '완료', actor: 'operator01' },
  { time: '17:24:33', item: 'LTE 백업망 전환', impact: '전체 장비', detail: 'LAN 장애 감지로 LTE 백업망 전환, 이후 LAN 복구', status: '완료', actor: 'system' },
  { time: '17:18:05', item: 'GPS 수신 지연', impact: '전체 레일', detail: 'GPS Fix 지연 2초 발생 후 복구', status: '완료', actor: 'operator01' },
  { time: '17:05:44', item: '데이터 반영 지연', impact: '서버 반영', detail: '데이터 반영 지연 5초 발생 후 복구', status: '완료', actor: 'system' },
  { time: '16:58:21', item: 'Edge 상태 점검', impact: '전체 장비', detail: 'Edge 헬스체크 정상 응답 확인', status: '완료', actor: 'system' },
  { time: '16:47:09', item: 'OCR Task 재시작', impact: '2번 레일', detail: 'OCR Task Queue 지연 감지 후 자동 재시작', status: '완료', actor: 'operator02' },
  { time: '16:35:52', item: 'Spool 적재 확인', impact: '없음', detail: '미전송 Spool 잔여 건수 정상 범위 확인', status: '완료', actor: 'system' },
  { time: '16:22:18', item: 'GPS 영역 재검증', impact: '전체 레일', detail: '결제 영역 좌표 기준값 검증 완료', status: '완료', actor: 'operator01' },
  { time: '16:10:03', item: '백업망 상태 확인', impact: '전체 장비', detail: 'LTE 백업망 대기 상태 및 전환 조건 정상', status: '완료', actor: 'system' }
]

const numberText = (value, fallback = '0') => {
  if (value === null || value === undefined || value === '') return fallback
  const numeric = Number(value)
  return Number.isFinite(numeric) ? numeric.toLocaleString() : String(value)
}

const moneyText = (value) => {
  const numeric = Number(value)
  if (!Number.isFinite(numeric)) return '₩0'
  return `₩${numeric.toLocaleString()}`
}

const pad2 = (value) => String(value).padStart(2, '0')

const formatDateText = (date) => (
  `${date.getFullYear()}-${pad2(date.getMonth() + 1)}-${pad2(date.getDate())}`
)

const formatClockText = (date) => (
  `${pad2(date.getHours())}:${pad2(date.getMinutes())}:${pad2(date.getSeconds())}`
)

const firstText = (...values) => {
  const value = values.find((item) => item !== null && item !== undefined && String(item).trim() !== '')
  return value === undefined ? '' : String(value)
}

const passageEventKey = (event, index) => {
  const rawTime = firstText(event.eventTime, event.passedAt, event.createdAt, event.event_time, event.passed_at, event.created_at)
  const plate = firstText(event.plateText, event.plateNumber, event.plate, event.plate_text, event.plate_number)
  const lane = firstText(event.laneNo, event.lane)
  return firstText(event.eventId, event.event_id, event.id) || `${plate}-${lane}-${rawTime}-${index}`
}

const toneFromStatus = (value) => {
  const status = String(value ?? '').toUpperCase()
  if (status.includes('OUT') || status.includes('FAIL') || status.includes('REJECT')) return 'danger'
  if (status.includes('REVIEW') || status.includes('BOUNDARY') || status.includes('WAIT')) return 'boundary'
  return 'ok'
}

const laneDisplayText = (lane) => (Number(lane) === 1 ? '상행' : '하행')

const recognitionDirectionText = (direction) => {
  const value = String(direction ?? '').toUpperCase()
  if (value === 'IN') return 'Front'
  if (value === 'OUT') return 'Rear'
  if (value === 'FRONT') return 'Front'
  if (value === 'REAR') return 'Rear'
  return direction || '-'
}

const isFrontDirection = (direction) => recognitionDirectionText(direction) === 'Front'

const firstNumber = (...values) => {
  for (const value of values) {
    const numeric = Number(value)
    if (Number.isFinite(numeric) && numeric > 0) return numeric
  }
  return null
}

const dashboardKpis = computed(() => {
  const laneText = selectedLaneText.value
  const passageCount = cumulativeLanePassages.value[selectedLane.value] || 0
  const normalRate = passageCount > 0 ? 100 : 0
  const tollAmount = passageCount * PASSAGE_TOLL_WON
  return [
    { ...fallbackDashboardKpis[0], title: `${laneText} 통행`, value: numberText(passageCount), sub: 'Spring API 연결' },
    { ...fallbackDashboardKpis[1], title: `${laneText} GPS 정상`, value: numberText(passageCount), sub: `정상률 ${numberText(normalRate)}%` },
    { ...fallbackDashboardKpis[2], title: `${laneText} 검수 대기`, value: '0', sub: '검수 대기' },
    { ...fallbackDashboardKpis[3], title: `${laneText} 통행료`, value: moneyText(tollAmount), sub: '정산 후보 합계' }
  ]
})

const dashboardDetections = computed(() => {
  const fpsLabel = operatorVideoFpsText.value
  return fallbackDashboardDetections.map((lane) => ({
    ...lane,
    fps: fpsLabel
  }))
})

const renderedVideoIsRecent = computed(() => {
  if (!renderedVideoFps.value || !renderedVideoFpsAt.value) return false
  return nowMs.value - renderedVideoFpsAt.value <= VIDEO_RENDERED_FPS_GRACE_MS
})

const operatorVideoIsLive = computed(() => dashboardApiState.operatorVideoIsLive.value || renderedVideoIsRecent.value)

const operatorVideoStatusText = computed(() => (operatorVideoIsLive.value ? 'LIVE' : 'WAIT'))

const operatorVideoFpsText = computed(() => {
  if (!operatorVideoIsLive.value) return 'FPS --'
  const video = dashboardApiState.operatorVideoStatus.value
  const fps = firstNumber(
    renderedVideoFps.value,
    video.fps,
    video.streamFps,
    video.stream_fps,
    video.latestFps,
    video.latest_fps
  )
  return fps ? `FPS ${fps.toFixed(1)}` : 'FPS --'
})

const operatorVideoFpsValue = computed(() => operatorVideoFpsText.value.replace('FPS ', ''))

const activeNetworkPathLabel = computed(() => {
  const edge = dashboardApiState.operatorDeviceStatuses.value[0] ?? {}
  const rawPath = networkPathOverride.value || edge.activePath || edge.active_path || 'lan'
  return String(rawPath).trim().toLowerCase() === 'lte' ? 'LTE' : 'LAN'
})

const activeNetworkUsageText = computed(() => `${activeNetworkPathLabel.value} 사용 중`)

const statusCards = computed(() => {
  const video = dashboardApiState.operatorVideoStatus.value
  return fallbackStatusCards.map((card, index) => {
    if (index === 0) return { ...card, value: video.connected === false ? '대기' : '정상', tone: video.connected === false ? 'info' : 'ok' }
    if (index === 2) return { ...card, value: dashboardApiState.operatorPassages.value.length ? '수신 중' : card.value }
    if (index === 3) return { ...card, value: activeNetworkUsageText.value, tone: activeNetworkPathLabel.value === 'LTE' ? 'info' : card.tone }
    return card
  })
})

watch(() => dashboardApiState.operatorPassages.value, (passages) => {
  if (!passages.length) return
  const next = new Map(passageReceivedAtById.value)
  let changed = false
  passages.forEach((event, index) => {
    const key = passageEventKey(event, index)
    if (next.has(key)) return
    next.set(key, Date.now())
    changed = true
  })
  if (changed) passageReceivedAtById.value = next
}, { immediate: true })

const trafficRows = computed(() => {
  const passages = dashboardApiState.operatorPassages.value
  if (!passages.length) return fallbackTrafficRows
  return passages
    .map((event, index) => {
      const plate = firstText(event.plateText, event.plateNumber, event.plate, event.plate_text, event.plate_number)
      const lane = Number(event.laneNo ?? event.lane)
      const eventKey = passageEventKey(event, index)
      const receivedAt = passageReceivedAtById.value.get(eventKey) || Date.now()
      const receivedDate = new Date(receivedAt)
      return {
        lane: Number.isFinite(lane) && lane > 0 ? lane : null,
        plate: plate || '-',
        direction: firstText(event.direction, event.passageDirection, event.passage_direction) || '-',
        date: formatDateText(receivedDate),
        time: formatClockText(receivedDate),
        sortTime: receivedAt,
        gps: firstText(event.gpsJudgementStatus, event.gpsStatus, event.gpsJudgement, event.gps_judgement_status, event.gps_status, event.gps_judgement) || '-',
        status: firstText(event.paymentDecision, event.inspectionStatus, event.paymentStatus, event.payment_decision, event.inspection_status, event.payment_status) || '-',
        tone: toneFromStatus(firstText(event.paymentDecision, event.inspectionStatus, event.paymentStatus, event.gpsJudgementStatus, event.payment_decision, event.inspection_status, event.payment_status, event.gps_judgement_status)),
        eventId: eventKey,
        eventImageUrl: firstText(event.eventImageUrl, event.event_image_url, event.eventImagePath, event.event_image_path, event.imageUrl, event.image_url, event.evidenceImageUrl, event.evidence_image_url, event.evidence?.eventImageUrl, event.evidence?.event_image_url),
        cropImageUrl: firstText(event.cropImageUrl, event.crop_image_url, event.plateCropImageUrl, event.plate_crop_image_url, event.plateCropUrl, event.plate_crop_url, event.cropUrl, event.crop_url, event.evidence?.cropImageUrl, event.evidence?.crop_image_url, event.evidence?.plateCropUrl, event.evidence?.plate_crop_url)
      }
    })
    .filter((event) => event.lane && event.plate !== '-' && event.time !== '--:--:--')
    .sort((a, b) => (Number.isFinite(b.sortTime) ? b.sortTime : 0) - (Number.isFinite(a.sortTime) ? a.sortTime : 0))
})

watch(trafficRows, (rows) => {
  if (!dashboardApiState.operatorPassages.value.length) return

  let changed = false
  const nextSeen = new Set(seenPassageEventIds.value)
  const nextCounts = { ...cumulativeLanePassages.value }
  const incomingGpsRows = []

  rows.forEach((row) => {
    if (!row.lane || !isFrontDirection(row.direction)) return
    const key = `${row.eventId}-${row.lane}-${row.plate}`
    if (nextSeen.has(key)) return

    nextSeen.add(key)
    nextCounts[row.lane] = (nextCounts[row.lane] || 0) + 1
    incomingGpsRows.push({
      lane: row.lane,
      plate: row.plate,
      direction: recognitionDirectionText(row.direction),
      laneText: `L${row.lane}`,
      time: row.time,
      gps: '정상',
      payment: '정상 통과',
      tone: 'ok'
    })
    changed = true
  })

  if (!changed) return
  seenPassageEventIds.value = nextSeen
  cumulativeLanePassages.value = nextCounts
  recentGpsRows.value = [...incomingGpsRows, ...recentGpsRows.value].slice(0, 3)
}, { immediate: true })

const gpsJudgements = computed(() => {
  const rows = trafficRows.value
  if (!rows.length) {
    return fallbackGpsJudgements.map((event) => ({
      ...event,
      direction: recognitionDirectionText(event.direction),
      laneText: `L${event.lane}`
    }))
  }
  return rows.slice(0, 6).map((event) => ({
    lane: event.lane,
    plate: event.plate,
    direction: recognitionDirectionText(event.direction),
    laneText: `L${event.lane}`,
    time: event.time,
    gps: event.gps,
    payment: event.status,
    tone: event.tone
  }))
})

const selectedLaneText = computed(() => laneDisplayText(selectedLane.value))
const currentDateText = computed(() => formatDateText(new Date(nowMs.value)))
const topNetworkLabel = computed(() => (activeMenu.value === '대시보드' ? '이벤트 수신' : activeNetworkUsageText.value))
const recentGpsJudgements = computed(() => recentGpsRows.value)
const filteredGpsJudgements = computed(() => gpsJudgements.value.filter((row) => row.lane === selectedLane.value))
const filteredTrafficRows = computed(() => trafficRows.value.filter((row) => row.lane === selectedLane.value))
const selectedFieldAlerts = computed(() => laneFieldAlerts[selectedLane.value] ?? fieldAlerts)

function updateTime() {
  const now = new Date()
  nowMs.value = now.getTime()
  const days = ['일', '월', '화', '수', '목', '금', '토']
  nowText.value = `${formatDateText(now)} (${days[now.getDay()]}) ${formatClockText(now)}`
}

function logout() {
  window.alert('로그아웃 되었습니다.')
  auth.logout().finally(() => router.push('/'))
}

function goHome() {
  showOperatorMenu.value = false
  router.push('/')
}

function goMasterAdmin() {
  showOperatorMenu.value = false
  router.push('/master-admin')
}

function toggleThemeMode() {
  isLightMode.value = !isLightMode.value
  localStorage.setItem(THEME_STORAGE_KEY, isLightMode.value ? 'light' : 'dark')
}

function setNetworkPathOverride(path) {
  const normalized = String(path ?? '').trim().toLowerCase()
  if (normalized === 'lan' || normalized === 'lte') {
    networkPathOverride.value = normalized
    localStorage.setItem(NETWORK_OVERRIDE_STORAGE_KEY, normalized)
    return normalized
  }
  networkPathOverride.value = ''
  localStorage.removeItem(NETWORK_OVERRIDE_STORAGE_KEY)
  return ''
}

function updateRenderedVideoFps(fps) {
  const numeric = Number(fps)
  if (Number.isFinite(numeric) && numeric > 0) {
    renderedVideoFps.value = numeric
    renderedVideoFpsAt.value = Date.now()
  }
}


const controlPageMap = {
  '대시보드': ControlDashboardPage,
  '예지보전': PdmDashboardPage,
  '통행 이벤트': ControlTrafficEventPage,
  'GPS 판정': ControlGpsDecisionPage,
  '정산': ControlSettlementPage,
  '장비 상태': ControlEquipmentPage,
  '실시간 관제': ControlRealtimePage,
  '설정': ControlSettingsPage
}

const controlPageComponent = computed(() => controlPageMap[activeMenu.value] ?? ControlFallbackPage)

function getKpiIcon(filename) {
  return new URL(`../dashboards/icons/control/${filename}`, import.meta.url).href
}

function getAdminIcon(filename) {
  return new URL(`../dashboards/icons/admin/${filename}`, import.meta.url).href
}

provide('controlDashboardContext', {
  activeMenu,
  centerLabel,
  isLightMode,
  selectedLaneText,
  selectedLane,
  dashboardKpis,
  getKpiIcon,
  getAdminIcon,
  dashboardApiState,
  operatorVideoIsLive,
  operatorVideoStatusText,
  operatorVideoFpsText,
  operatorVideoFpsValue,
  operatorVideoStreamUrl: dashboardApiState.operatorVideoStreamUrl,
  operatorVideoStreamKey: dashboardApiState.operatorVideoStreamKey,
  scheduleOperatorVideoReconnect: dashboardApiState.scheduleOperatorVideoReconnect,
  updateRenderedVideoFps,
  operatorVideoStatus: dashboardApiState.operatorVideoStatus,
  operatorPassageEvents: trafficRows,
  currentDateText,
  dashboardDetections,
  statusCards,
  recentGpsJudgements,
  filteredGpsJudgements,
  fieldAlerts: selectedFieldAlerts,
  filteredTrafficRows,
  equipmentCards,
  equipmentLaneRows,
  equipmentAlerts,
  historyRows
})

// Dashboard CSS를 head에 동적으로 추가/제거 — 페이지를 떠나는 순간 완전히 사라져 다른 페이지에 누출 없음
let dashboardStyleEl = null

onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 1000)
  window.hifiveNetwork = setNetworkPathOverride

  dashboardStyleEl = document.createElement('style')
  dashboardStyleEl.setAttribute('data-dashboard-css', 'control')
  dashboardStyleEl.textContent = controlDashboardCss
  document.head.appendChild(dashboardStyleEl)
})

onBeforeUnmount(() => {
  clearInterval(timer)
  if (window.hifiveNetwork === setNetworkPathOverride) delete window.hifiveNetwork

  if (dashboardStyleEl) {
    dashboardStyleEl.remove()
    dashboardStyleEl = null
  }
})
</script>

<template>
  <div class="ops-shell" :class="{ 'light-mode': isLightMode }">
    <aside class="sidebar">
      <div class="brand">
        <img src="/hifive_logo.png" alt="HI-FIVE" class="sidebar-logo" />
        <strong>HI-FIVE</strong>
      </div>

      <nav class="nav">
        <button
          v-for="item in navItems"
          :key="item.label"
          :class="{ active: activeMenu === item.label }"
          type="button"
          @click="activeMenu = item.label"
        >
          <i><img :src="getKpiIcon(item.icon)" :alt="item.label" /></i>
          <span>{{ item.label }}</span>
        </button>
      </nav>

      <div v-if="activeMenu === '예지보전'" class="event-side-stack pdm-side-stack">
        <section class="zone-card event-stats-card">
          <h3>카메라 상태 <small>ⓘ</small></h3>
          <p><span class="zone-icon gps">✓</span><b>정상</b><strong>1대</strong></p>
          <p><span class="zone-icon warn">◔</span><b>주의</b><strong>1대</strong></p>
          <p><span class="zone-icon danger">!</span><b>위험</b><strong>0대</strong></p>
        </section>
        <section class="zone-card gps-guide-card" style="margin-top:10px">
          <p><span class="zone-icon cctv">▧</span><b>평균 Score</b><strong>82점</strong></p>
        </section>
      </div>

      <div v-else-if="activeMenu === '정산'" class="event-side-stack settlement-side-stack">
        <section class="zone-card event-stats-card">
          <h3>정산 상태 <small>ⓘ</small></h3>
          <p><span class="zone-icon gps">✓</span><b>완료</b><strong>2,356건</strong></p>
          <p><span class="zone-icon warn">◔</span><b>대기</b><strong>142건</strong></p>
          <p><span class="zone-icon danger">!</span><b>보류</b><strong>36건</strong></p>
        </section>
      </div>

      <div v-else-if="activeMenu === '검수'" class="event-side-stack review-side-stack">
        <section class="zone-card event-stats-card">
          <h3>검수 요약 <small>ⓘ</small></h3>
          <p><span class="zone-icon warn">⌛</span><b>대기</b><strong>12</strong></p>
          <p><span class="zone-icon cctv">↻</span><b>처리중</b><strong>3</strong></p>
          <p><span class="zone-icon gps">✓</span><b>완료</b><strong>48</strong></p>
        </section>
        <section class="zone-card gps-guide-card">
          <p><span class="zone-icon cctv">▧</span><b>검수 가이드</b><strong></strong></p>
        </section>
      </div>

      <div v-else-if="activeMenu === 'GPS 판정'" class="event-side-stack gps-side-stack">
        <section class="zone-card event-stats-card">
          <h3>GPS 요약 <small>ⓘ</small></h3>
          <p><span class="zone-icon gps">✓</span><b>정상</b><strong>4,512건</strong></p>
          <p><span class="zone-icon danger">!</span><b>영역 이탈</b><strong>214건</strong></p>
          <p><span class="zone-icon lan">◉</span><b>최근 반영</b><strong>2초 전</strong></p>
        </section>
        <section class="zone-card gps-guide-card">
          <p><span class="zone-icon cctv">▧</span><b>GPS 판정 가이드</b><strong></strong></p>
        </section>
      </div>

      <div v-else-if="activeMenu === '통행 이벤트'" class="event-side-stack">
        <section class="zone-card event-filter-card">
          <h3>빠른 필터 <small>⌘</small></h3>
          <p><span class="zone-icon cctv">▣</span><b>오늘</b><strong></strong></p>
          <p><span class="zone-icon gps">✓</span><b>GPS 정상</b><strong></strong></p>
          <p><span class="zone-icon danger">△</span><b>영역 이탈</b><strong></strong></p>
          <p><span class="zone-icon warn">◎</span><b>검수 필요</b><strong></strong></p>
        </section>
        <section class="zone-card event-stats-card">
          <h3>이벤트 통계 <small>(금일)</small></h3>
          <p><b>전체 이벤트</b><strong>1,248건</strong></p>
          <p><b>GPS 정상</b><strong>1,074건</strong></p>
          <p><b>영역 이탈</b><strong>36건</strong></p>
          <p><b>검수 필요</b><strong>138건</strong></p>
        </section>
      </div>

      <section v-else class="zone-card">
        <h3>구역 상태</h3>
        <p><span class="zone-icon cctv">▰</span><b>CCTV</b><strong>정상</strong></p>
        <p><span class="zone-icon gps">⌖</span><b>GPS 수신</b><strong>정상</strong></p>
        <p><span class="zone-icon lan">⌁</span><b>{{ activeNetworkPathLabel }}</b><strong>사용 중</strong></p>
      </section>
    </aside>

    <main class="main">
      <header class="topbar">
        <div class="center-toggle">
          <span>⌖ {{ centerLabel }}</span>
          <div class="lane-toggle" role="group" aria-label="방향 선택">
            <button type="button" :class="{ active: selectedLane === 1 }" @click="selectedLane = 1">상행</button>
            <button type="button" :class="{ active: selectedLane === 2 }" @click="selectedLane = 2">하행</button>
          </div>
        </div>
        <time>{{ nowText }}</time>
        <div class="top-status">
          <span><i class="dot ok"></i>CCTV 정상</span>
          <span><i class="dot ok"></i>GPS 정상</span>
          <span><i class="dot info"></i>{{ topNetworkLabel }}</span>
        </div>
        <button
          class="theme-toggle"
          type="button"
          :aria-label="isLightMode ? '다크 모드로 변경' : '라이트 모드로 변경'"
          @click="toggleThemeMode"
        >
          {{ isLightMode ? '☾' : '☀' }}
        </button>
        <div class="notify-wrap">
          <button class="bell" type="button" @click="showNotifications = !showNotifications">
            <span>{{ notifications.length }}</span>♢
          </button>
          <section v-if="showNotifications" class="notification-popover">
            <header>
              <b>알림 내역</b>
              <button type="button" @click="showNotifications = false">×</button>
            </header>
            <article v-for="item in notifications" :key="`${item.title}-${item.time}`" :class="item.level">
              <i>{{ item.level === 'info' ? 'i' : '!' }}</i>
              <div>
                <strong>{{ item.title }}</strong>
                <span>{{ item.desc }}</span>
              </div>
              <time>{{ item.time }}</time>
            </article>
          </section>
        </div>
        <div class="operator-wrap">
          <button class="operator" type="button" @click="showOperatorMenu = !showOperatorMenu">
            <b>{{ auth.member?.memberName ?? 'operator01' }}</b>
            <small>운영자</small>
          </button>
          <section v-if="showOperatorMenu" class="operator-menu">
            <button type="button" @click="goHome">홈</button>
            <button type="button" @click="goMasterAdmin">관리자</button>
            <button type="button" @click="logout">로그아웃</button>
          </section>
        </div>
      </header>

            <component :is="controlPageComponent" />

      </main>
  </div>
</template>

