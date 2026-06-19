<script setup>
import { computed, onBeforeUnmount, onMounted, provide, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { adminApi } from '@/api/admin'
import { useDashboardApi } from '@/composables/useDashboardApi'
import ChartJsPanel from '@/components/charts/ChartJsPanel.vue'
// Dashboard CSS는 동적으로 head에 추가하고 페이지 떠날 때 제거 — 다른 페이지에 누출되지 않도록 격리
import masterDashboardCss from '@/dashboards/master/styles/master-dashboard.css?inline'
import MasterDashboardHomePage from '@/dashboards/master/pages/MasterDashboardHomePage.vue'
import MasterFallbackPage from '@/dashboards/master/pages/MasterFallbackPage.vue'
import MemberCompanyListPage from '@/dashboards/master/pages/MemberCompanyListPage.vue'
import MemberAccountManagementPage from '@/dashboards/master/pages/MemberAccountManagementPage.vue'
import MemberPermissionManagementPage from '@/dashboards/master/pages/MemberPermissionManagementPage.vue'
import FeeSettlementManagementPage from '@/dashboards/master/pages/FeeSettlementManagementPage.vue'
import BranchCenterManagementPage from '@/dashboards/master/pages/BranchCenterManagementPage.vue'
import EdgeEquipmentManagementPage from '@/dashboards/master/pages/EdgeEquipmentManagementPage.vue'
import TerminalDeviceManagementPage from '@/dashboards/master/pages/TerminalDeviceManagementPage.vue'
import SystemControlPage from '@/dashboards/master/pages/SystemControlPage.vue'
import IngressMonitoringPage from '@/dashboards/master/pages/IngressMonitoringPage.vue'
import BackendDbMonitoringPage from '@/dashboards/master/pages/BackendDbMonitoringPage.vue'
import SystemMonitoringFallbackPage from '@/dashboards/master/pages/SystemMonitoringFallbackPage.vue'
import IncidentAlertPage from '@/dashboards/master/pages/IncidentAlertPage.vue'
import NoticeManagementPage from '@/dashboards/master/pages/NoticeManagementPage.vue'
import AuditLogPage from '@/dashboards/master/pages/AuditLogPage.vue'
import MasterSettingsPage from '@/dashboards/master/pages/MasterSettingsPage.vue'
import MasterPdmPage from '@/dashboards/pdm/pages/MasterPdmPage.vue'

const router = useRouter()
const auth = useAuthStore()
const masterApiState = useDashboardApi({ scope: 'admin', pollMs: 3000 })

const THEME_STORAGE_KEY = 'hifive.dashboard.theme'
const nowText = ref('')
const themeMode = ref('light')
const activeMenu = ref('대시보드')
const activeTab = ref('companies')
const selectedCenter = ref('서울 톨게이트')
const expandedCenter = ref('')
const showCenterModal = ref(false)
const centerMetricPeriod = ref('daily')
const search = ref('')
const members = ref([])
const companies = ref([])
const memberMessage = ref('')
const showCompanyModal = ref(false)
const companyModalMessage = ref('')
const savingCompany = ref(false)
const companyForm = ref({
  name: '',
  owner: '',
  phone: '',
  email: '',
  centers: 0,
  status: '정상'
})

const kpiSection = ref(null)
const mapSection = ref(null)
const mapStage = ref(null)
const centerSection = ref(null)
const noticeSection = ref(null)
const companySection = ref(null)
const isMapEditMode = ref(false)
const mapPositionDirty = ref(false)
const mapChangeCount = ref(0)
const mapEditSnapshot = ref([])
const draggingCenterName = ref('')
const draggingLabelName = ref('')
const isLightMode = computed(() => themeMode.value === 'light')
const topbarSystemState = computed(() => {
  if (activeMenu.value === '예지보전') {
    return { label: '예지보전 확인 필요', className: 'global-warn' }
  }
  return { label: '전체 정상', className: 'global-ok' }
})

function toggleThemeMode() {
  themeMode.value = isLightMode.value ? 'dark' : 'light'
  localStorage.setItem(THEME_STORAGE_KEY, themeMode.value)
}

let timer = null
const MAP_MARKER_STORAGE_KEY = 'hifive.masterAdmin.mapMarkers.v1'

const menuGroups = [
  { label: '대시보드', icon: 'dashboard2.png' },
  { label: '예지보전', icon: 'cctv.png' },
  {
    label: '센터 관리',
    icon: 'member_set.png',
    children: ['센터 목록', '계정 관리', '권한 관리', '요금 정산 관리']
  },
  { label: '시스템 관제', icon: 'system_set.png' },
  { label: 'Edge 장비', icon: 'edge_set.png' },
  { label: 'Ingress', icon: 'ingress.png' },
  { label: '백엔드/DB', icon: 'be_db_set.png' },
  { label: '장애 알림', icon: 'warning_set.png' },
  { label: '지점 관리', icon: 'place_set.png' },
  { label: '감사 로그', icon: 'log_set.png' },
  { label: '설정', icon: 'setting.png' }
]

function getNavIcon(filename) {
  return new URL(`../dashboards/icons/admin/${filename}`, import.meta.url).href
}

const centers = ref([
  { name: '서울 톨게이트', current: 1248, today: 12456, status: '정상', x: 38.6, y: 27.6, labelX: -116, labelY: -18, dashboardId: 'SEOUL-TOLL' },
  { name: '수원 톨게이트', current: 987, today: 9876, status: '정상', x: 37.8, y: 42.2, labelX: -116, labelY: -18, dashboardId: 'SUWON-TOLL' },
  { name: '대전 톨게이트', current: 765, today: 7654, status: '주의', x: 42.1, y: 56.4, labelX: -116, labelY: -18, dashboardId: 'DAEJEON-TOLL' },
  { name: '대구 톨게이트', current: 532, today: 5321, status: '점검중', x: 64.4, y: 61.0, labelX: 20, labelY: -18, dashboardId: 'DAEGU-TOLL' },
  { name: '부산 톨게이트', current: 1102, today: 11023, status: '정상', x: 65.2, y: 75.8, labelX: 20, labelY: -18, dashboardId: 'BUSAN-TOLL' },
  { name: '광주 톨게이트', current: 624, today: 6120, status: '정상', x: 37.8, y: 79.0, labelX: -116, labelY: -18, dashboardId: 'GWANGJU-TOLL' },
  { name: '강릉 톨게이트', current: 410, today: 4320, status: '정상', x: 64.0, y: 18.7, labelX: 20, labelY: -18, dashboardId: 'GANGNEUNG-TOLL' },
  { name: '제주 톨게이트', current: 302, today: 2210, status: '정상', x: 46.2, y: 96.0, labelX: 20, labelY: -18, dashboardId: 'JEJU-TOLL' }
])

const notices = [
  { title: '[공지] 시스템 점검 안내 (05/25 00:00 ~ 02:00)', date: '2025-05-20' },
  { title: '[안내] 요금 정산 시스템 업데이트 완료', date: '2025-05-19' },
  { title: '[안내] GPS 단말기 펌웨어 업데이트 안내', date: '2025-05-18' },
  { title: '[공지] 개인정보 처리방침 변경 안내', date: '2025-05-15' }
]

const topCenters = computed(() => centers.value.slice(0, 5))

const dummyCompanies = [
  { name: '하이패스 서울(주)', owner: '김서울', phone: '02-1234-5678', email: 'seoul@hipass.com', centers: 5, status: '정상' },
  { name: '수원 하이패스(주)', owner: '이수원', phone: '031-234-5678', email: 'suwon@hipass.com', centers: 4, status: '정상' },
  { name: '대전 하이패스(주)', owner: '박대전', phone: '042-345-6789', email: 'daejeon@hipass.com', centers: 3, status: '주의' },
  { name: '대구 하이패스(주)', owner: '최대구', phone: '053-456-7890', email: 'daegu@hipass.com', centers: 4, status: '정상' },
  { name: '부산 하이패스(주)', owner: '정부산', phone: '051-567-8901', email: 'busan@hipass.com', centers: 6, status: '정상' },
  { name: '광주 스마트톨링(주)', owner: '윤광주', phone: '062-111-2200', email: 'gwangju@hipass.com', centers: 2, status: '정상' },
  { name: '강릉 톨링서비스(주)', owner: '한강릉', phone: '033-555-1900', email: 'gangneung@hipass.com', centers: 2, status: '정상' },
  { name: '제주 하이로드(주)', owner: '오제주', phone: '064-700-1200', email: 'jeju@hipass.com', centers: 1, status: '정상' },
  { name: '인천 스마트패스(주)', owner: '장인천', phone: '032-810-3300', email: 'incheon@hipass.com', centers: 3, status: '주의' },
  { name: '울산 하이패스(주)', owner: '문울산', phone: '052-420-7788', email: 'ulsan@hipass.com', centers: 2, status: '정상' },
  { name: '전주 톨링네트워크(주)', owner: '서전주', phone: '063-230-5588', email: 'jeonju@hipass.com', centers: 2, status: '비활성' },
  { name: '청주 교통관리(주)', owner: '남청주', phone: '043-610-4411', email: 'cheongju@hipass.com', centers: 2, status: '정상' }
]

const displayCompanies = computed(() => (companies.value.length >= 10 ? companies.value : dummyCompanies))

const filteredCompanies = computed(() => {
  const keyword = search.value.trim()
  return displayCompanies.value.filter((company) => !keyword || company.name.includes(keyword))
})

const filteredMembers = computed(() => {
  const keyword = search.value.trim().toLowerCase()
  return members.value.filter((member) => {
    return !keyword ||
      member.email?.toLowerCase().includes(keyword) ||
      member.memberName?.toLowerCase().includes(keyword)
  })
})

const chartGridColor = 'rgba(140, 176, 220, 0.14)'
const chartTextColor = '#aebfd4'

function lineData(values, color = '#38d778', fill = false) {
  return {
    labels: values.map((_, index) => index + 1),
    datasets: [{
      data: values,
      borderColor: color,
      backgroundColor: fill ? `${color}22` : 'transparent',
      borderWidth: 2,
      pointRadius: 3,
      pointHoverRadius: 4,
      tension: 0.35,
      fill
    }]
  }
}

function heartbeatData(values, color = '#38d778') {
  return {
    labels: values.map((_, index) => index + 1),
    datasets: [{
      data: values,
      borderColor: color,
      backgroundColor: 'transparent',
      borderWidth: 2.4,
      pointRadius: 0,
      pointHoverRadius: 0,
      tension: 0,
      fill: false
    }]
  }
}

function multiLineData(series) {
  return {
    labels: ['16:35', '16:45', '16:55', '17:05', '17:15', '17:25', '17:35'],
    datasets: series.map((item) => ({
      label: item.label,
      data: item.values,
      borderColor: item.color,
      backgroundColor: 'transparent',
      borderWidth: 2,
      pointRadius: 3,
      pointHoverRadius: 4,
      tension: 0.35
    }))
  }
}

const sparklineOptions = {
  responsive: true,
  maintainAspectRatio: false,
  animation: { duration: 650, easing: 'easeOutQuart' },
  plugins: { legend: { display: false }, tooltip: { enabled: false } },
  scales: { x: { display: false }, y: { display: false } }
}

const compactLineOptions = {
  responsive: true,
  maintainAspectRatio: false,
  animation: { duration: 750, easing: 'easeOutQuart' },
  plugins: { legend: { display: false } },
  scales: {
    x: { ticks: { color: chartTextColor, font: { size: 10 } }, grid: { color: chartGridColor } },
    y: { ticks: { color: chartTextColor, font: { size: 10 } }, grid: { color: chartGridColor } }
  }
}

const doughnutOptions = {
  responsive: true,
  maintainAspectRatio: false,
  animation: { duration: 700, easing: 'easeOutQuart' },
  cutout: '66%',
  plugins: { legend: { display: false }, tooltip: { enabled: true } },
  scales: { x: { display: false }, y: { display: false } }
}

const dashboardSparklines = [
  heartbeatData([12, 12, 7, 20, 8, 12, 12, 12], '#38d778'),
  heartbeatData([11, 11, 6, 18, 8, 11, 11, 11], '#38d778'),
  heartbeatData([10, 10, 5, 17, 7, 10, 10, 10], '#38d778'),
  heartbeatData([12, 12, 6, 21, 9, 12, 12, 12], '#38d778'),
  heartbeatData([11, 11, 6, 19, 8, 11, 11, 11], '#38d778'),
  heartbeatData([8, 8, 15, 5, 18, 9, 16, 8], '#ffb928'),
  heartbeatData([8, 8, 14, 6, 17, 10, 16, 8], '#ffb928'),
  heartbeatData([11, 11, 6, 18, 8, 11, 11, 11], '#38d778'),
  heartbeatData([10, 10, 5, 17, 7, 10, 10, 10], '#38d778'),
  heartbeatData([12, 12, 6, 20, 8, 12, 12, 12], '#38d778'),
  heartbeatData([11, 11, 6, 18, 8, 11, 11, 11], '#38d778'),
  heartbeatData([10, 10, 5, 17, 7, 10, 10, 10], '#38d778'),
  heartbeatData([11, 11, 6, 19, 8, 11, 11, 11], '#38d778'),
  heartbeatData([8, 8, 15, 5, 18, 9, 16, 8], '#ffb928'),
  heartbeatData([12, 12, 7, 20, 8, 12, 12, 12], '#38d778')
]

const edgeFleetDoughnutData = {
  labels: ['Normal', 'Warning', 'Stale'],
  datasets: [{ data: [78, 7, 5], backgroundColor: ['#38d778', '#ffb928', '#ff635c'], borderWidth: 0 }]
}

const edgeMetricCharts = [
  { title: 'FPS', subtitle: '(frame/sec)', status: '정상', color: '#2f8cff', label: '평균 FPS', data: lineData([28, 31, 27, 29, 31, 30, 33, 29], '#2f8cff') },
  { title: 'YOLO 처리 시간', subtitle: '(ms)', status: '정상', color: '#42d779', label: '평균 YOLO ms', data: lineData([25, 27, 24, 29, 30, 32, 31, 28], '#42d779') },
  { title: 'OCR 처리 시간', subtitle: '(ms)', status: '주의', color: '#b28cff', label: '평균 OCR ms', data: lineData([54, 64, 59, 62, 61, 60, 58, 63], '#b28cff'), warn: true },
  { title: 'Spool Count', subtitle: '(건)', status: '정상', color: '#ffb928', label: '현재 Spool', data: lineData([72, 64, 70, 75, 73, 72, 69, 67], '#ffb928') },
  { title: 'Sent Event Count', subtitle: '(건/분)', status: '정상', color: '#26c8e8', label: '전송 이벤트', data: lineData([45, 43, 56, 62, 51, 44, 38, 42], '#26c8e8') }
]

const ingressCharts = [
  { title: '수신 이벤트 (건)', data: lineData([6800, 7600, 8150, 7900, 8500, 9000, 9300], '#2f8cff') },
  { title: 'ACK / RETRY (건)', data: multiLineData([{ label: 'ACK', values: [6700, 7400, 7800, 8250, 8800, 9050, 9200], color: '#42d779' }, { label: 'RETRY', values: [210, 230, 260, 300, 350, 410, 480], color: '#ffb928' }]) },
  { title: 'REJECT / MALFORMED (건)', data: multiLineData([{ label: 'REJECT', values: [82, 96, 101, 112, 118, 130, 138], color: '#ff635c' }, { label: 'MALFORMED', values: [12, 13, 14, 15, 16, 17, 18], color: '#b28cff' }]) },
  { title: '현재 연결 수 (개)', data: lineData([190, 205, 210, 208, 206, 202, 200], '#26c8e8') }
]

const incidentTimelineData = {
  datasets: [
    { label: '치명', data: [{ x: 72, y: 5 }, { x: 66, y: 3 }], backgroundColor: '#ff635c', pointRadius: 6 },
    { label: '경고', data: [{ x: 28, y: 5 }, { x: 70, y: 4 }, { x: 52, y: 2 }, { x: 42, y: 1 }], backgroundColor: '#ffb928', pointRadius: 6 },
    { label: '정보', data: [{ x: 32, y: 4 }, { x: 23, y: 3 }, { x: 69, y: 1 }], backgroundColor: '#3d8cff', pointRadius: 6 },
    { label: '해제', data: [{ x: 92, y: 5 }, { x: 88, y: 4 }, { x: 92, y: 2 }, { x: 92, y: 1 }], backgroundColor: '#50d779', pointRadius: 6 }
  ]
}

const incidentTimelineOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: { legend: { display: false }, tooltip: { enabled: true } },
  scales: {
    x: { min: 0, max: 100, ticks: { display: false }, grid: { color: chartGridColor } },
    y: { min: 0.5, max: 5.5, ticks: { display: false }, grid: { color: chartGridColor } }
  }
}

const companyPermissionChartData = {
  labels: ['MASTER', 'ADMIN', 'OPERATOR', 'LOCKED'],
  datasets: [{ data: [1, 8, 26, 2], backgroundColor: ['#5ea8ff', '#38d778', '#ffb928', '#ff635c'], borderRadius: 6 }]
}

const companyPermissionChartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: { legend: { display: false } },
  scales: {
    x: { ticks: { color: '#334155', font: { size: 11, weight: '700' } }, grid: { display: false } },
    y: { ticks: { display: false }, grid: { color: chartGridColor } }
  }
}

const subpageTitle = computed(() => {
  if (activeMenu.value === '센터 관리') return '센터 목록'
  return activeMenu.value
})

const subpageDescription = computed(() => {
  const descriptions = {
    '센터 목록': '등록된 센터 정보를 조회하고 수정, 권한 설정을 관리합니다.',
    '계정 관리': '회원 계정의 로그인 정보, 상태, 허용 대시보드를 관리합니다.',
    '권한 관리': '회원별 접근 가능 지점과 정산/단말/공지 권한을 설정합니다.',
    '요금 정산 관리': '센터별 정산 상태와 미정산 건을 확인합니다.',
    '지점(관제센터) 관리': '전국 톨게이트 지점의 운영 상태와 대시보드 진입을 관리합니다.',
    '단말기 관리': 'GPS/OCR 수집 단말 상태와 배치 지점을 관리합니다.',
    '시스템 모니터링': '서버, DB, GPS 수신 상태를 점검합니다.',
    '공지사항': '센터와 지점에 노출할 공지사항을 관리합니다.',
    '감사 로그': '관리자 작업 이력과 권한 변경 이력을 확인합니다.',
    '설정': '마스터 관리자 콘솔의 기본 표시와 운영 정책을 설정합니다.'
  }
  return descriptions[subpageTitle.value] ?? '선택한 관리 항목의 상세 설정을 확인합니다.'
})

const deviceRows = [
  { id: 'PICO2W-NEO7M-RC-01', center: '서울 톨게이트', type: 'GPS', status: '정상', lastSeen: '10:32:45' },
  { id: 'EDGE-OCR-CAM-SEOUL-01', center: '서울 톨게이트', type: 'OCR', status: '정상', lastSeen: '10:32:41' },
  { id: 'PICO2W-NEO7M-DAEJEON-01', center: '대전 톨게이트', type: 'GPS', status: '주의', lastSeen: '10:28:10' },
  { id: 'EDGE-OCR-CAM-DAEGU-01', center: '대구 톨게이트', type: 'OCR', status: '점검중', lastSeen: '09:51:22' }
]

const auditRows = [
  { time: '2026-05-08 10:24:11', actor: 'master', action: '서울 톨게이트 마커 위치 수정', result: '성공' },
  { time: '2026-05-08 10:18:02', actor: 'master', action: '회원 대시보드 권한 변경', result: '성공' },
  { time: '2026-05-08 09:42:33', actor: 'admin@hifive.com', action: '센터 목록 조회', result: '성공' }
]

const recentAuditRows = [
  { time: '17:34:58', actor: 'admin', target: '서울도로공사 / 서울 톨게이트 A', reason: '장애 원인 확인', result: '성공' },
  { time: '17:29:33', actor: 'admin', target: '경기도로공사 / 수원 톨게이트 A', reason: '상태 점검', result: '성공' },
  { time: '17:24:11', actor: 'admin', target: '대전교통공사 / 대전 톨게이트 B', reason: '주의 상태 확인', result: '성공' },
  { time: '17:18:07', actor: 'operator02', target: '대구교통공사 / 대구 톨게이트 C', reason: '장애 처리 지원', result: '성공' },
  { time: '17:12:45', actor: 'admin', target: '부산운영공사 / 부산 톨게이트 D', reason: '장기 점검', result: '성공' }
]

const recentAlerts = [
  { level: '치명', tone: 'danger', title: '대구 톨게이트 C · 2번 레일 CCTV 수신 중단', time: '17:33:12' },
  { level: '경고', tone: 'caution', title: '대전 톨게이트 B · OCR Task Drop 증가', time: '17:31:05' },
  { level: '경고', tone: 'caution', title: '수원 톨게이트 A · Spool Count 85% 초과', time: '17:29:48' },
  { level: '정보', tone: 'info', title: '부산 톨게이트 D · LAN → LTE 경로 전환', time: '17:27:21' },
  { level: '정보', tone: 'info', title: 'Ingress · Retry 비율 일시 증가', time: '17:26:34' }
]

const companyByCenter = {
  '서울 톨게이트': '서울도로공사',
  '수원 톨게이트': '경기도로공사',
  '대전 톨게이트': '대전교통공사',
  '대구 톨게이트': '대구교통공사',
  '부산 톨게이트': '부산운영공사',
  '광주 톨게이트': '광주도로공사',
  '강릉 톨게이트': '강원도로공사',
  '제주 톨게이트': '제주교통공사'
}

const recentIssueByCenter = {
  '대전 톨게이트': '1건',
  '대구 톨게이트': '2건'
}

const centerDetailMeta = {
  '서울 톨게이트': {
    address: '서울특별시 서초구 양재대로 12',
    lat: '37.56668',
    lng: '126.97841',
    edgeCount: 3,
    metrics: {
      daily: { label: '일간', traffic: 12456, settlement: 2450800, bars: [42, 58, 74, 92, 86, 68, 54] },
      weekly: { label: '주간', traffic: 86420, settlement: 17155600, bars: [56, 68, 72, 88, 94, 80, 64] },
      monthly: { label: '월간', traffic: 358420, settlement: 71135800, bars: [48, 64, 79, 84, 91, 88, 76] }
    }
  },
  '수원 톨게이트': {
    address: '경기도 수원시 영통구 중부대로 88',
    lat: '37.26357',
    lng: '127.02860',
    edgeCount: 2,
    metrics: {
      daily: { label: '일간', traffic: 9876, settlement: 1896200, bars: [34, 44, 61, 78, 72, 62, 48] },
      weekly: { label: '주간', traffic: 68450, settlement: 13005500, bars: [45, 54, 66, 74, 82, 73, 58] },
      monthly: { label: '월간', traffic: 292380, settlement: 55552200, bars: [42, 58, 70, 76, 84, 79, 67] }
    }
  },
  '대전 톨게이트': {
    address: '대전광역시 유성구 북유성대로 154',
    lat: '36.35041',
    lng: '127.38455',
    edgeCount: 3,
    metrics: {
      daily: { label: '일간', traffic: 7654, settlement: 1454260, bars: [28, 38, 50, 68, 61, 47, 36] },
      weekly: { label: '주간', traffic: 52380, settlement: 9952200, bars: [38, 46, 59, 70, 66, 56, 44] },
      monthly: { label: '월간', traffic: 218700, settlement: 41553000, bars: [36, 49, 61, 73, 70, 63, 54] }
    }
  },
  '대구 톨게이트': {
    address: '대구광역시 동구 팔공로 221',
    lat: '35.87144',
    lng: '128.60144',
    edgeCount: 3,
    metrics: {
      daily: { label: '일간', traffic: 5321, settlement: 1010990, bars: [24, 32, 45, 54, 50, 41, 30] },
      weekly: { label: '주간', traffic: 38210, settlement: 7259900, bars: [31, 40, 52, 62, 58, 48, 37] },
      monthly: { label: '월간', traffic: 162400, settlement: 30856000, bars: [30, 43, 55, 66, 61, 53, 42] }
    }
  },
  '부산 톨게이트': {
    address: '부산광역시 금정구 중앙대로 2101',
    lat: '35.17955',
    lng: '129.07564',
    edgeCount: 2,
    metrics: {
      daily: { label: '일간', traffic: 11023, settlement: 2094370, bars: [38, 52, 67, 80, 88, 74, 59] },
      weekly: { label: '주간', traffic: 76230, settlement: 14483700, bars: [46, 58, 70, 84, 91, 79, 66] },
      monthly: { label: '월간', traffic: 321500, settlement: 61085000, bars: [43, 60, 76, 86, 92, 84, 72] }
    }
  },
  '광주 톨게이트': {
    address: '광주광역시 광산구 무진대로 303',
    lat: '35.15955',
    lng: '126.85260',
    edgeCount: 2,
    metrics: {
      daily: { label: '일간', traffic: 6120, settlement: 1162800, bars: [26, 36, 49, 61, 57, 44, 34] },
      weekly: { label: '주간', traffic: 42100, settlement: 7999000, bars: [34, 45, 56, 68, 63, 53, 41] },
      monthly: { label: '월간', traffic: 178300, settlement: 33877000, bars: [32, 47, 59, 70, 66, 58, 46] }
    }
  },
  '강릉 톨게이트': {
    address: '강원특별자치도 강릉시 경강로 2301',
    lat: '37.75185',
    lng: '128.87606',
    edgeCount: 2,
    metrics: {
      daily: { label: '일간', traffic: 4320, settlement: 820800, bars: [20, 30, 42, 53, 49, 38, 28] },
      weekly: { label: '주간', traffic: 30140, settlement: 5726600, bars: [28, 38, 49, 60, 56, 46, 35] },
      monthly: { label: '월간', traffic: 129900, settlement: 24681000, bars: [27, 41, 52, 64, 60, 50, 39] }
    }
  },
  '제주 톨게이트': {
    address: '제주특별자치도 제주시 공항로 2',
    lat: '33.49962',
    lng: '126.53119',
    edgeCount: 1,
    metrics: {
      daily: { label: '일간', traffic: 2210, settlement: 419900, bars: [16, 22, 31, 44, 40, 30, 23] },
      weekly: { label: '주간', traffic: 15980, settlement: 3036200, bars: [24, 31, 43, 54, 50, 39, 30] },
      monthly: { label: '월간', traffic: 68420, settlement: 12999800, bars: [22, 36, 48, 58, 54, 44, 34] }
    }
  }
}

const selectedCenterDetail = computed(() => {
  const center = centers.value.find((item) => item.name === selectedCenter.value) ?? centers.value[0]
  const meta = centerDetailMeta[center.name] ?? centerDetailMeta['서울 톨게이트']
  const metric = meta.metrics[centerMetricPeriod.value] ?? meta.metrics.daily
  return { ...center, ...meta, metric }
})

function expandCenterMetricValues(pattern, total, count) {
  const source = pattern.length ? pattern : [1]
  const weights = Array.from({ length: count }, (_, index) => source[index % source.length])
  const sum = weights.reduce((acc, value) => acc + value, 0) || 1
  return weights.map((value) => Math.round((total * value) / sum))
}

const centerMetricChartLabels = computed(() => {
  if (centerMetricPeriod.value === 'daily') {
    return Array.from({ length: 24 }, (_, index) => String(index + 1) + '\uc2dc')
  }
  if (centerMetricPeriod.value === 'weekly') {
    return ['\uc6d4', '\ud654', '\uc218', '\ubaa9', '\uae08', '\ud1a0', '\uc77c']
  }
  return Array.from({ length: 12 }, (_, index) => String(index + 1) + '\uc6d4')
})

const centerMetricChartValues = computed(() => {
  return expandCenterMetricValues(
    selectedCenterDetail.value.metric.bars,
    selectedCenterDetail.value.metric.settlement,
    centerMetricChartLabels.value.length
  )
})

const centerMetricChartData = computed(() => ({
  labels: centerMetricChartLabels.value,
  datasets: [{
    label: '\uc815\uc0b0 \uae08\uc561',
    data: centerMetricChartValues.value,
    backgroundColor: 'rgba(47, 140, 255, 0.72)',
    borderColor: '#66b7ff',
    borderWidth: 1,
    borderRadius: 5,
    maxBarThickness: 26
  }]
}))

const centerMetricChartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  animation: { duration: 650, easing: 'easeOutQuart' },
  plugins: {
    legend: { display: false },
    tooltip: {
      callbacks: {
        label: (context) => '\uc815\uc0b0 \uae08\uc561: \u20a9' + Number(context.parsed.y).toLocaleString()
      }
    }
  },
  scales: {
    x: {
      ticks: { color: chartTextColor, font: { size: 10 }, maxRotation: 0, autoSkip: true },
      grid: { display: false }
    },
    y: {
      min: 0,
      ticks: { display: false },
      grid: { color: chartGridColor }
    }
  }
}

function centerOwner(centerName) {
  return companyByCenter[centerName] ?? '담당 센터'
}

function centerIssueCount(centerName) {
  return recentIssueByCenter[centerName] ?? '0건'
}

function updateTime() {
  const now = new Date()
  const pad = (value) => String(value).padStart(2, '0')
  nowText.value = `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())} ${pad(now.getHours())}:${pad(now.getMinutes())}:${pad(now.getSeconds())}`
}

function statusClass(status) {
  if (status === '정상') return 'ok'
  if (status === '주의') return 'caution'
  if (status === '점검중') return 'danger'
  return 'muted'
}

function statusText(status) {
  return status === '점검중' ? '점검중' : status
}

function applyMapMarkerPositions(markerPositions) {
  if (!Array.isArray(markerPositions)) return

  markerPositions.forEach((position) => {
    const center = centers.value.find((item) => item.name === position.name)
    if (!center) return

    center.x = Number(position.x ?? center.x)
    center.y = Number(position.y ?? center.y)
    center.labelX = Number(position.labelX ?? center.labelX)
    center.labelY = Number(position.labelY ?? center.labelY)
  })
}

function createMapMarkerSnapshot() {
  return centers.value.map(({ name, x, y, labelX, labelY }) => ({
    name,
    x,
    y,
    labelX,
    labelY
  }))
}

async function saveMapMarkerPositions() {
  if (!mapPositionDirty.value) return

  const markerPositions = createMapMarkerSnapshot()
  localStorage.setItem(MAP_MARKER_STORAGE_KEY, JSON.stringify(markerPositions))

  try {
    await adminApi.saveMapMarkers(markerPositions)
    mapPositionDirty.value = false
    mapChangeCount.value = 0
    mapEditSnapshot.value = createMapMarkerSnapshot()
  } catch {
    memberMessage.value = '지도 위치를 백엔드에 저장하지 못했습니다. 로그인 상태와 백엔드 실행 상태를 확인해 주세요.'
  }
}

async function loadMapMarkerPositions() {
  try {
    const { data } = await adminApi.mapMarkers()
    if (Array.isArray(data) && data.length > 0) {
      applyMapMarkerPositions(data)
      localStorage.setItem(MAP_MARKER_STORAGE_KEY, JSON.stringify(data))
      return
    }
  } catch {
    memberMessage.value = '저장된 지도 위치를 백엔드에서 불러오지 못해 브라우저 캐시를 사용합니다.'
  }

  const saved = localStorage.getItem(MAP_MARKER_STORAGE_KEY)
  if (!saved) return

  try {
    const markerPositions = JSON.parse(saved)
    applyMapMarkerPositions(markerPositions)
  } catch {
    localStorage.removeItem(MAP_MARKER_STORAGE_KEY)
  }
}

async function fetchMembers() {
  const { data } = await adminApi.members()
  members.value = data
}

async function fetchCompanies() {
  const { data } = await adminApi.companies()
  companies.value = data
}

function openCompanyModal() {
  companyForm.value = {
    name: '',
    owner: '',
    phone: '',
    email: '',
    centers: 0,
    status: '정상'
  }
  companyModalMessage.value = ''
  showCompanyModal.value = true
}

async function createCompany() {
  if (savingCompany.value) return

  if (!companyForm.value.name || !companyForm.value.owner || !companyForm.value.phone || !companyForm.value.email) {
    companyModalMessage.value = '센터명, 대표자, 연락처, 이메일을 모두 입력해 주세요.'
    return
  }

  if (Number(companyForm.value.centers) < 0) {
    companyModalMessage.value = '지점 수는 0 이상으로 입력해 주세요.'
    return
  }

  savingCompany.value = true
  companyModalMessage.value = ''

  try {
    const { data } = await adminApi.createCompany({
      ...companyForm.value,
      centers: Number(companyForm.value.centers)
    })
    companies.value = [...companies.value, data]
    showCompanyModal.value = false
    memberMessage.value = `${data.name} 센터가 추가되었습니다.`
  } catch (error) {
    const status = error?.response?.status
    if (status === 403) {
      companyModalMessage.value = '관리자 세션이 만료되었거나 권한이 없습니다. admin 계정으로 다시 로그인해 주세요.'
    } else if (status === 400 || status === 500) {
      companyModalMessage.value = '센터 추가에 실패했습니다. 이메일 중복 여부와 입력값을 확인해 주세요.'
    } else {
      companyModalMessage.value = '백엔드 연결 상태를 확인해 주세요.'
    }
  } finally {
    savingCompany.value = false
  }
}

async function assignDashboard(member, dashboardId) {
  const { data } = await adminApi.assignDashboard(member.email, dashboardId)
  members.value = members.value.map((item) => item.email === data.email ? data : item)
  memberMessage.value = `${data.memberName} 회원을 ${dashboardId || '미할당'} 대시보드에 연결했습니다.`
}

function activateMenu(menu) {
  activeMenu.value = menu

  if (menu === '대시보드') {
    kpiSection.value?.scrollIntoView({ behavior: 'smooth', block: 'start' })
    return
  }

  if (['센터 관리', '센터 목록', '계정 관리', '권한 관리', '요금 정산 관리'].includes(menu)) {
    activeTab.value = menu === '계정 관리' ? 'accounts' : menu === '권한 관리' ? 'permissions' : 'companies'
    return
  }
}

function enterCenter(center) {
  selectedCenter.value = center.name
  router.push({ path: '/dashboard', query: { center: center.dashboardId } })
}

function enterCompactCenter(center) {
  selectedCenter.value = center.name
  router.push({ path: '/dashboard', query: { center: center.dashboardId } })
}

function showActionMessage(message) {
  memberMessage.value = message
  window.alert(message)
}

function openQuickCompanyAdd() {
  activateMenu('센터 목록')
  openCompanyModal()
}

function openCenterAdd() {
  activateMenu('지점 관리')
  showActionMessage('지점 추가 화면을 준비했습니다.')
}

function openNoticeCreate() {
  activateMenu('공지사항')
  showActionMessage('공지사항 등록 화면을 준비했습니다.')
}

function openSystemNotice() {
  activateMenu('시스템 모니터링')
  showActionMessage('시스템 공지 등록 화면을 준비했습니다.')
}

function showMoreNotices() {
  activateMenu('공지사항')
}

function zoomMap(action) {
  const actionText = action === 'reset' ? '초기화' : action === 'in' ? '확대' : '축소'
  showActionMessage(`지도 ${actionText} 기능을 적용했습니다.`)
}

function resetAccount(member) {
  showActionMessage(`${member.memberName} 계정의 비밀번호 초기화를 요청했습니다.`)
}

function editAccount(member) {
  showActionMessage(`${member.memberName} 계정 정보 수정 화면을 준비했습니다.`)
}

function toggleAccountLock(member) {
  showActionMessage(`${member.memberName} 계정 잠금 상태를 변경했습니다.`)
}

function exportSettlementReport() {
  showActionMessage('정산 리포트를 생성했습니다.')
}

function openDeviceRegister() {
  activateMenu('단말기 관리')
  showActionMessage('단말 등록 화면을 준비했습니다.')
}

function showDeviceDetail(device) {
  showActionMessage(`${device.id} 단말 상세 정보를 확인했습니다.`)
}

function exportAuditLog() {
  showActionMessage('감사 로그 내보내기를 시작했습니다.')
}

function saveMasterSettings() {
  showActionMessage('관리자 대시보드 설정이 저장되었습니다.')
}

function toggleMapEditMode() {
  if (!isMapEditMode.value) {
    mapEditSnapshot.value = createMapMarkerSnapshot()
    mapPositionDirty.value = false
    mapChangeCount.value = 0
    isMapEditMode.value = true
  } else {
    isMapEditMode.value = false
  }
  draggingCenterName.value = ''
  draggingLabelName.value = ''
}

function resetMapEditPositions() {
  applyMapMarkerPositions(mapEditSnapshot.value)
  mapPositionDirty.value = false
  mapChangeCount.value = 0
  draggingCenterName.value = ''
  draggingLabelName.value = ''
}

function cancelMapEditMode() {
  resetMapEditPositions()
  isMapEditMode.value = false
}

function toggleCenterDetail(center) {
  if (isMapEditMode.value) return
  selectedCenter.value = center.name
  centerMetricPeriod.value = 'daily'
  showCenterModal.value = true
}

function markMapDirty() {
  if (!mapPositionDirty.value) {
    mapChangeCount.value += 1
  }
  mapPositionDirty.value = true
}

function startMarkerDrag(event, center) {
  if (!isMapEditMode.value) return
  event.preventDefault()
  event.stopPropagation()
  draggingCenterName.value = center.name
  selectedCenter.value = center.name
  event.currentTarget.setPointerCapture?.(event.pointerId)
  updateMarkerPosition(event)
}

function updateMarkerPosition(event) {
  if (draggingLabelName.value) {
    updateLabelPosition(event)
    return
  }

  if (!draggingCenterName.value || !mapStage.value) return

  const rect = mapStage.value.getBoundingClientRect()
  const x = ((event.clientX - rect.left) / rect.width) * 100
  const y = ((event.clientY - rect.top) / rect.height) * 100
  const center = centers.value.find((item) => item.name === draggingCenterName.value)

  if (!center) return
  const nextX = Number(Math.min(96, Math.max(4, x)).toFixed(1))
  const nextY = Number(Math.min(96, Math.max(4, y)).toFixed(1))
  if (center.x !== nextX || center.y !== nextY) {
    center.x = nextX
    center.y = nextY
    markMapDirty()
  }
}

function startLabelDrag(event, center) {
  if (!isMapEditMode.value) return
  event.preventDefault()
  event.stopPropagation()
  draggingLabelName.value = center.name
  selectedCenter.value = center.name
  event.currentTarget.setPointerCapture?.(event.pointerId)
  updateLabelPosition(event)
}

function updateLabelPosition(event) {
  if (!draggingLabelName.value || !mapStage.value) return

  const rect = mapStage.value.getBoundingClientRect()
  const center = centers.value.find((item) => item.name === draggingLabelName.value)
  if (!center) return

  const markerX = (center.x / 100) * rect.width
  const markerY = (center.y / 100) * rect.height
  const pointerX = event.clientX - rect.left
  const pointerY = event.clientY - rect.top

  const nextLabelX = Number(Math.min(180, Math.max(-180, pointerX - markerX)).toFixed(0))
  const nextLabelY = Number(Math.min(90, Math.max(-90, pointerY - markerY)).toFixed(0))
  if (center.labelX !== nextLabelX || center.labelY !== nextLabelY) {
    center.labelX = nextLabelX
    center.labelY = nextLabelY
    markMapDirty()
  }
}

function stopMarkerDrag() {
  draggingCenterName.value = ''
  draggingLabelName.value = ''
}

function notifyCompanyEdited() {
  window.alert('센터 정보가 수정되었습니다.')
}

function notifyPermissionSaved() {
  window.alert('권한 정보가 저장되었습니다.')
}

function logout() {
  window.alert('로그아웃 되었습니다.')
  auth.logout().finally(() => router.push('/'))
}

// Dashboard CSS를 head에 동적으로 추가/제거 — 페이지를 떠나는 순간 완전히 사라져 다른 페이지에 누출 없음
let dashboardStyleEl = null

onMounted(() => {
  void loadMapMarkerPositions()
  updateTime()
  timer = setInterval(updateTime, 1000)
  fetchMembers().catch(() => {
    memberMessage.value = '회원 목록을 불러오지 못했습니다. 백엔드 실행 상태와 관리자 로그인을 확인해 주세요.'
  })
  fetchCompanies().catch(() => {
    memberMessage.value = '센터 목록을 불러오지 못했습니다. 백엔드 실행 상태를 확인해 주세요.'
  })

  dashboardStyleEl = document.createElement('style')
  dashboardStyleEl.setAttribute('data-dashboard-css', 'master')
  dashboardStyleEl.textContent = masterDashboardCss
  document.head.appendChild(dashboardStyleEl)
})

onBeforeUnmount(() => {
  clearInterval(timer)

  if (dashboardStyleEl) {
    dashboardStyleEl.remove()
    dashboardStyleEl = null
  }
})
const masterDashboardContext = {
  MAP_MARKER_STORAGE_KEY,
  THEME_STORAGE_KEY,
  activateMenu,
  activeMenu,
  activeTab,
  applyMapMarkerPositions,
  assignDashboard,
  auditRows,
  auth,
  cancelMapEditMode,
  centerDetailMeta,
  centerIssueCount,
  centerMetricChartData,
  centerMetricChartLabels,
  centerMetricChartOptions,
  centerMetricChartValues,
  centerMetricPeriod,
  centerOwner,
  centerSection,
  centers,
  chartGridColor,
  chartTextColor,
  compactLineOptions,
  companies,
  companyByCenter,
  companyForm,
  companyModalMessage,
  companyPermissionChartData,
  companyPermissionChartOptions,
  companySection,
  createCompany,
  createMapMarkerSnapshot,
  dashboardSparklines,
  deviceRows,
  displayCompanies,
  doughnutOptions,
  draggingCenterName,
  draggingLabelName,
  dummyCompanies,
  edgeFleetDoughnutData,
  edgeMetricCharts,
  editAccount,
  enterCenter,
  enterCompactCenter,
  expandCenterMetricValues,
  expandedCenter,
  exportAuditLog,
  exportSettlementReport,
  fetchCompanies,
  fetchMembers,
  filteredCompanies,
  filteredMembers,
  heartbeatData,
  incidentTimelineData,
  incidentTimelineOptions,
  ingressCharts,
  isLightMode,
  isMapEditMode,
  kpiSection,
  lineData,
  loadMapMarkerPositions,
  logout,
  masterApiState,
  mapChangeCount,
  mapEditSnapshot,
  mapPositionDirty,
  mapSection,
  mapStage,
  markMapDirty,
  memberMessage,
  members,
  menuGroups,
  multiLineData,
  noticeSection,
  notices,
  notifyCompanyEdited,
  notifyPermissionSaved,
  nowText,
  openCenterAdd,
  openCompanyModal,
  openDeviceRegister,
  openNoticeCreate,
  openQuickCompanyAdd,
  openSystemNotice,
  recentAlerts,
  recentAuditRows,
  recentIssueByCenter,
  resetAccount,
  resetMapEditPositions,
  router,
  saveMapMarkerPositions,
  saveMasterSettings,
  savingCompany,
  search,
  selectedCenter,
  selectedCenterDetail,
  showActionMessage,
  showCenterModal,
  showCompanyModal,
  showDeviceDetail,
  showMoreNotices,
  sparklineOptions,
  startLabelDrag,
  startMarkerDrag,
  statusClass,
  statusText,
  stopMarkerDrag,
  subpageDescription,
  subpageTitle,
  themeMode,
  toggleAccountLock,
  toggleCenterDetail,
  toggleMapEditMode,
  toggleThemeMode,
  topCenters,
  updateLabelPosition,
  updateMarkerPosition,
  updateTime,
  zoomMap
}

provide('masterDashboard', masterDashboardContext)

</script>

<template>
  <div class="master-shell" :class="{ light: isLightMode }">
    <aside class="sidebar">
      <div class="sidebar-brand">
        <img src="/hifive_logo.png" alt="HI-FIVE" class="sidebar-logo" />
        <strong>HI-FIVE</strong>
      </div>

      <nav class="sidebar-nav">
        <div v-for="menu in menuGroups" :key="menu.label" class="nav-group">
          <button
            class="nav-main"
            :class="{ active: activeMenu === menu.label || menu.children?.includes(activeMenu) }"
            type="button"
            @click="activateMenu(menu.label)"
          >
            <span class="nav-icon"><img :src="getNavIcon(menu.icon)" :alt="menu.label" /></span>
            <span>{{ menu.label }}</span>
          </button>

          <div v-if="menu.children" class="nav-children">
            <button
              v-for="child in menu.children"
              :key="child"
              :class="{ active: activeMenu === child }"
              type="button"
              @click="activateMenu(child)"
            >
              {{ child }}
            </button>
          </div>
        </div>
      </nav>

      <p class="copyright">© 2025 HI-FIVE All rights reserved.</p>
    </aside>

    <div class="content-shell">
      <header class="topbar">
        <div class="topbar-title">
          <span class="brand-diamond small"></span>
          <strong>{{ activeMenu === 'Ingress' ? 'Ingress 관제' : activeMenu === '시스템 관제' ? 'Master Admin · 시스템 관제' : activeMenu === 'Edge 장비' ? 'Edge 장비 관제' : activeMenu === '백엔드/DB' ? 'Backend / DB 관제' : activeMenu === '장애 알림' ? '장애 알림' : activeMenu === '지점 관리' ? '지점 관리' : activeMenu === '감사 로그' ? '감사 로그' : activeMenu === '설정' ? '설정' : ['센터 관리', '센터 목록'].includes(activeMenu) ? '센터 관리' : '대시보드' }}</strong>
          <span>{{ activeMenu === 'Ingress' ? 'Python Ingress 서비스 상태 및 이벤트 흐름을 모니터링합니다.' : activeMenu === 'Edge 장비' ? 'Jetson Edge 장비 상태 및 성능 모니터링' : activeMenu === '장애 알림' ? '시스템 및 서비스 이상 상황을 모니터링하고 대응합니다.' : activeMenu === '지점 관리' ? '스마트 톨링 지점 및 제어센터 등록/수정/관리' : activeMenu === '감사 로그' ? '관리자 작업, 권한 변경, 대리조회 이력을 추적합니다.' : activeMenu === '설정' ? '관리자 콘솔 운영 정책과 시스템 기본값을 관리합니다.' : ['센터 관리', '센터 목록'].includes(activeMenu) ? '센터 계정, 권한, 지점 연결 상태를 통합 관리합니다.' : activeMenu === '시스템 관제' || activeMenu === '백엔드/DB' ? '' : 'Master Admin Dashboard' }}</span>
        </div>

        <div class="header-tools">
          <span class="clock">{{ nowText }}</span>
          <span class="system-state" :class="topbarSystemState.className"><i></i>{{ topbarSystemState.label }}</span>
          <span class="admin-chip"><b>{{ auth.member?.memberName ?? 'master' }}</b><small>최종 관리자</small></span>
          <button class="theme-btn" type="button" :title="isLightMode ? '다크 모드' : '라이트 모드'" :aria-label="isLightMode ? '다크 모드' : '라이트 모드'" @click="toggleThemeMode">
            <span>{{ isLightMode ? '☾' : '☀' }}</span>
          </button>
          <button class="home-btn" type="button" @click="router.push('/')">홈</button>
          <button class="logout-btn" type="button" @click="logout">로그아웃</button>
        </div>
      </header>

      <main>
        <MasterDashboardHomePage v-if="activeMenu === '대시보드'" />
        <MasterPdmPage v-else-if="activeMenu === '예지보전'" />
        <MasterFallbackPage v-else-if="!['Ingress', '시스템 관제', 'Edge 장비', '백엔드/DB', '장애 알림', '지점 관리', '감사 로그', '설정', '센터 관리', '센터 목록', '예지보전'].includes(activeMenu)" />
        <MemberCompanyListPage v-else-if="['센터 관리', '센터 목록'].includes(activeMenu)" />
        <MemberAccountManagementPage v-else-if="activeMenu === '계정 관리'" />
        <MemberPermissionManagementPage v-else-if="activeMenu === '권한 관리'" />
        <FeeSettlementManagementPage v-else-if="activeMenu === '요금 정산 관리'" />
        <BranchCenterManagementPage v-else-if="['지점(관제센터) 관리', '지점 관리'].includes(activeMenu)" />
        <EdgeEquipmentManagementPage v-else-if="activeMenu === 'Edge 장비'" />
        <TerminalDeviceManagementPage v-else-if="['단말기 관리'].includes(activeMenu)" />
        <SystemControlPage v-else-if="activeMenu === '시스템 관제'" />
        <IngressMonitoringPage v-else-if="activeMenu === 'Ingress'" />
        <BackendDbMonitoringPage v-else-if="activeMenu === '백엔드/DB'" />
        <SystemMonitoringFallbackPage v-else-if="['시스템 모니터링', '시스템 관제'].includes(activeMenu)" />
        <IncidentAlertPage v-else-if="activeMenu === '장애 알림'" />
        <NoticeManagementPage v-else-if="['공지사항'].includes(activeMenu)" />
        <AuditLogPage v-else-if="activeMenu === '감사 로그'" />
        <MasterSettingsPage v-else />

        <div v-if="showCenterModal" class="modal-backdrop" @click.self="showCenterModal = false">
          <article class="center-detail-modal glass">
            <div class="modal-head">
              <div>
                <p class="subpage-kicker">TOLLING CENTER</p>
                <h3>{{ selectedCenterDetail.name }}</h3>
              </div>
              <span class="center-modal-status" :class="statusClass(selectedCenterDetail.status)">
                {{ statusText(selectedCenterDetail.status) }}
              </span>
              <button class="modal-close" type="button" @click="showCenterModal = false">×</button>
            </div>

            <section class="center-modal-summary">
              <dl>
                <div>
                  <dt>지점명</dt>
                  <dd>{{ selectedCenterDetail.name }}</dd>
                </div>
                <div>
                  <dt>담당 센터</dt>
                  <dd>{{ centerOwner(selectedCenterDetail.name) }}</dd>
                </div>
                <div>
                  <dt>Edge 대수</dt>
                  <dd>{{ selectedCenterDetail.edgeCount }}대</dd>
                </div>
                <div>
                  <dt>최근 장애</dt>
                  <dd>{{ centerIssueCount(selectedCenterDetail.name) }}</dd>
                </div>
                <div class="wide">
                  <dt>위치 주소</dt>
                  <dd>{{ selectedCenterDetail.address }}</dd>
                </div>
                <div>
                  <dt>위도</dt>
                  <dd>{{ selectedCenterDetail.lat }}</dd>
                </div>
                <div>
                  <dt>경도</dt>
                  <dd>{{ selectedCenterDetail.lng }}</dd>
                </div>
              </dl>
              <button class="center-dashboard-btn" type="button" @click="enterCenter(selectedCenterDetail)">
                관제 대시보드 진입
              </button>
            </section>

            <section class="center-modal-metrics">
              <div class="center-period-tabs">
                <button
                  v-for="period in ['daily', 'weekly', 'monthly']"
                  :key="period"
                  type="button"
                  :class="{ active: centerMetricPeriod === period }"
                  @click="centerMetricPeriod = period"
                >
                  {{ selectedCenterDetail.metrics[period].label }}
                </button>
              </div>

              <div class="center-metric-cards">
                <article>
                  <span>통행량</span>
                  <strong>{{ selectedCenterDetail.metric.traffic.toLocaleString() }}</strong>
                  <small>{{ selectedCenterDetail.metric.label }} 기준</small>
                </article>
                <article>
                  <span>정산 금액</span>
                  <strong>₩{{ selectedCenterDetail.metric.settlement.toLocaleString() }}</strong>
                  <small>정산 후보 포함</small>
                </article>
              </div>

              <div class="center-bar-chart">
                <ChartJsPanel
                  type="bar"
                  :data="centerMetricChartData"
                  :options="centerMetricChartOptions"
                  :height="132"
                />
              </div>
            </section>
          </article>
        </div>

        <div v-if="showCompanyModal" class="modal-backdrop" @click.self="showCompanyModal = false">
          <form class="company-modal glass" @submit.prevent="createCompany">
            <div class="modal-head">
              <div>
                <p class="subpage-kicker">CENTER</p>
                <h3>센터 추가</h3>
              </div>
              <button class="modal-close" type="button" @click="showCompanyModal = false">×</button>
            </div>

            <div class="modal-grid">
              <label>
                <span>센터명</span>
                <input v-model.trim="companyForm.name" required type="text" placeholder="예: 인천 하이패스(주)" />
              </label>
              <label>
                <span>대표자</span>
                <input v-model.trim="companyForm.owner" required type="text" placeholder="대표자명" />
              </label>
              <label>
                <span>연락처</span>
                <input v-model.trim="companyForm.phone" required type="text" placeholder="032-123-4567" />
              </label>
              <label>
                <span>이메일</span>
                <input v-model.trim="companyForm.email" required type="email" placeholder="company@hipass.com" />
              </label>
              <label>
                <span>지점 수</span>
                <input v-model.number="companyForm.centers" min="0" required type="number" />
              </label>
              <label>
                <span>상태</span>
                <select v-model="companyForm.status">
                  <option>정상</option>
                  <option>주의</option>
                  <option>비활성</option>
                </select>
              </label>
            </div>

            <p v-if="companyModalMessage" class="modal-message">{{ companyModalMessage }}</p>

            <div class="modal-actions">
              <button class="small-btn" type="button" @click="showCompanyModal = false">취소</button>
              <button class="small-btn add-btn" type="button" :disabled="savingCompany" @click="createCompany">
                {{ savingCompany ? '저장 중...' : '저장' }}
              </button>
            </div>
          </form>
        </div>
      </main>
    </div>
  </div>
</template>
