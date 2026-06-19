<script setup>
import { computed, inject, ref, watch } from 'vue'
import ChartJsPanel from '@/components/charts/ChartJsPanel.vue'

const masterDashboard = inject('masterDashboard')
if (!masterDashboard) {
  throw new Error('masterDashboard context is not provided')
}

const {
  MAP_MARKER_STORAGE_KEY,
  THEME_STORAGE_KEY,
  actionText,
  activateMenu,
  activeMenu,
  activeTab,
  applyMapMarkerPositions,
  assignDashboard,
  auditRows,
  auth,
  cancelMapEditMode,
  center,
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
  descriptions,
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
  filteredMembers,
  heartbeatData,
  incidentTimelineData,
  incidentTimelineOptions,
  ingressCharts,
  isLightMode,
  isMapEditMode,
  keyword,
  kpiSection,
  lineData,
  loadMapMarkerPositions,
  logout,
  mapChangeCount,
  mapEditSnapshot,
  mapPositionDirty,
  mapSection,
  mapStage,
  markMapDirty,
  markerPositions,
  markerX,
  markerY,
  memberMessage,
  members,
  menuGroups,
  meta,
  metric,
  multiLineData,
  nextLabelX,
  nextLabelY,
  nextX,
  nextY,
  noticeSection,
  notices,
  notifyCompanyEdited,
  notifyPermissionSaved,
  now,
  nowText,
  openCenterAdd,
  openCompanyModal,
  openDeviceRegister,
  openNoticeCreate,
  openQuickCompanyAdd,
  openSystemNotice,
  pad,
  pointerX,
  pointerY,
  recentAlerts,
  recentAuditRows,
  recentIssueByCenter,
  rect,
  resetAccount,
  resetMapEditPositions,
  router,
  saveMapMarkerPositions,
  saveMasterSettings,
  saved,
  savingCompany,
  search,
  selectedCenter,
  selectedCenterDetail,
  showActionMessage,
  showCenterModal,
  showCompanyModal,
  showDeviceDetail,
  showMoreNotices,
  source,
  sparklineOptions,
  startLabelDrag,
  startMarkerDrag,
  status,
  statusClass,
  statusText,
  stopMarkerDrag,
  subpageDescription,
  subpageTitle,
  sum,
  themeMode,
  toggleAccountLock,
  toggleCenterDetail,
  toggleMapEditMode,
  toggleThemeMode,
  topCenters,
  updateLabelPosition,
  updateMarkerPosition,
  updateTime,
  weights,
  x,
  y,
  zoomMap
} = masterDashboard

const selectedCompanyEmail = ref('')
const openCompanyFilter = ref('')
const companyScopeFilter = ref('전체 센터')
const statusFilter = ref('전체')
const permissionFilter = ref('전체')
const connectionFilter = ref('전체')

const companyScopeOptions = computed(() => [
  '전체 센터',
  ...new Set((displayCompanies.value ?? []).map((company) => `${company.status} 센터`).filter(Boolean))
])
const statusFilterOptions = computed(() => [
  '전체',
  ...new Set((displayCompanies.value ?? []).map((company) => company.status).filter(Boolean))
])
const permissionFilterOptions = ['전체', '허용', '검토 필요']
const connectionFilterOptions = ['전체', '연결 있음', '5개 이상', '2개 이하']

const companyRows = computed(() => {
  const keyword = search.value.trim().toLowerCase()

  return (displayCompanies.value ?? []).filter((company) => {
    const searchableText = [
      company.name,
      company.owner,
      company.phone,
      company.email,
      `${company.centers ?? 0}개`
    ].join(' ').toLowerCase()

    const permission = companyPermission(company)
    const centerCount = Number(company.centers ?? 0)
    const scopeStatus = companyScopeFilter.value.replace(' 센터', '')

    const matchesKeyword = !keyword || searchableText.includes(keyword)
    const matchesScope = companyScopeFilter.value === '전체 센터' || company.status === scopeStatus
    const matchesStatus = statusFilter.value === '전체' || company.status === statusFilter.value
    const matchesPermission = permissionFilter.value === '전체' || permission === permissionFilter.value
    const matchesConnection =
      connectionFilter.value === '전체' ||
      (connectionFilter.value === '연결 있음' && centerCount > 0) ||
      (connectionFilter.value === '5개 이상' && centerCount >= 5) ||
      (connectionFilter.value === '2개 이하' && centerCount <= 2)

    return matchesKeyword && matchesScope && matchesStatus && matchesPermission && matchesConnection
  })
})

const selectedCompany = computed(() => {
  const rows = companyRows.value ?? []
  return rows.find((company) => company.email === selectedCompanyEmail.value) ?? rows[0] ?? null
})

const selectedCompanyIndex = computed(() => {
  const rows = companyRows.value ?? []
  const index = rows.findIndex((company) => company.email === selectedCompany.value?.email)
  return index >= 0 ? index : 0
})

const selectedCompanyCenters = computed(() => {
  const count = Number(selectedCompany.value?.centers ?? 0)
  return (centers.value ?? []).slice(0, Math.max(count, 1))
})

const selectedCompanyLastChanged = computed(() => (
  selectedCompanyIndex.value === 0 ? '2026-05-12 10:18' : '2026-05-11 17:32'
))

function selectCompany(company) {
  selectedCompanyEmail.value = company?.email ?? ''
}

function companyPermission(company) {
  return ['주의', '점검중', '비활성'].includes(company?.status) ? '검토 필요' : '허용'
}

function toggleCompanyFilter(filterName) {
  openCompanyFilter.value = openCompanyFilter.value === filterName ? '' : filterName
}

function selectCompanyScopeFilter(option) {
  companyScopeFilter.value = option
  openCompanyFilter.value = ''
}

function selectStatusFilter(option) {
  statusFilter.value = option
  openCompanyFilter.value = ''
}

function selectPermissionFilter(option) {
  permissionFilter.value = option
  openCompanyFilter.value = ''
}

function selectConnectionFilter(option) {
  connectionFilter.value = option
  openCompanyFilter.value = ''
}

function resetCompanyFilters() {
  companyScopeFilter.value = '전체 센터'
  statusFilter.value = '전체'
  permissionFilter.value = '전체'
  connectionFilter.value = '전체'
  search.value = ''
  openCompanyFilter.value = ''
}

watch(
  companyRows,
  (rows) => {
    if (!rows.length) {
      selectedCompanyEmail.value = ''
      return
    }

    if (!rows.some((company) => company.email === selectedCompanyEmail.value)) {
      selectedCompanyEmail.value = rows[0].email
    }
  },
  { immediate: true }
)
</script>

<template>
          <article class="company-admin-page">
            <section class="company-admin-kpi-grid">
              <article class="company-admin-kpi"><i><img src="../../icons/admin/people2.png" alt="전체 센터" /></i><div><span>전체 센터</span><strong>{{ displayCompanies.length }}</strong><small>전일 대비 ▲ 2</small></div></article>
              <article class="company-admin-kpi ok"><i><img src="../../icons/admin/check_button.png" alt="정상 센터" /></i><div><span>정상 센터</span><strong>18</strong><small>정상 비율 75.0%</small></div></article>
              <article class="company-admin-kpi warn"><i><img src="../../icons/admin/caution.png" alt="주의 센터" /></i><div><span>주의 센터</span><strong>4</strong><small>권한/정산 확인 필요</small></div></article>
              <article class="company-admin-kpi muted"><i><img src="../../icons/admin/x.png" alt="비활성" /></i><div><span>비활성</span><strong>2</strong><small>최근 30일 미접속</small></div></article>
              <article class="company-admin-kpi"><i><img src="../../icons/admin/connect.png" alt="연결 지점" /></i><div><span>연결 지점</span><strong>38</strong><small>관제센터 포함</small></div></article>
              <article class="company-admin-kpi ok"><i><img src="../../icons/admin/won.png" alt="정산 정상률" /></i><div><span>정산 정상률</span><strong>99.2%</strong><small>미정산 1,248건</small></div></article>
            </section>

            <section class="company-admin-filter-row">
              <div class="company-filter-dropdown">
                <button
                  class="filter-toggle"
                  :class="{ active: companyScopeFilter !== '전체 센터', open: openCompanyFilter === 'scope' }"
                  type="button"
                  @click="toggleCompanyFilter('scope')"
                >
                  {{ companyScopeFilter }}
                </button>
                <div v-if="openCompanyFilter === 'scope'" class="company-filter-menu">
                  <button
                    v-for="option in companyScopeOptions"
                    :key="option"
                    :class="{ selected: companyScopeFilter === option }"
                    type="button"
                    @click="selectCompanyScopeFilter(option)"
                  >
                    {{ option }}
                  </button>
                </div>
              </div>
              <div class="company-filter-dropdown">
                <button
                  class="filter-toggle"
                  :class="{ active: statusFilter !== '전체', open: openCompanyFilter === 'status' }"
                  type="button"
                  @click="toggleCompanyFilter('status')"
                >
                  {{ statusFilter === '전체' ? '상태 전체' : statusFilter }}
                </button>
                <div v-if="openCompanyFilter === 'status'" class="company-filter-menu">
                  <button
                    v-for="option in statusFilterOptions"
                    :key="option"
                    :class="{ selected: statusFilter === option }"
                    type="button"
                    @click="selectStatusFilter(option)"
                  >
                    {{ option === '전체' ? '상태 전체' : option }}
                  </button>
                </div>
              </div>
              <div class="company-filter-dropdown">
                <button
                  class="filter-toggle"
                  :class="{ active: permissionFilter !== '전체', open: openCompanyFilter === 'permission' }"
                  type="button"
                  @click="toggleCompanyFilter('permission')"
                >
                  {{ permissionFilter === '전체' ? '권한 전체' : permissionFilter }}
                </button>
                <div v-if="openCompanyFilter === 'permission'" class="company-filter-menu">
                  <button
                    v-for="option in permissionFilterOptions"
                    :key="option"
                    :class="{ selected: permissionFilter === option }"
                    type="button"
                    @click="selectPermissionFilter(option)"
                  >
                    {{ option === '전체' ? '권한 전체' : option }}
                  </button>
                </div>
              </div>
              <div class="company-filter-dropdown">
                <button
                  class="filter-toggle"
                  :class="{ active: connectionFilter !== '전체', open: openCompanyFilter === 'connection' }"
                  type="button"
                  @click="toggleCompanyFilter('connection')"
                >
                  {{ connectionFilter === '전체' ? '지점 연결 전체' : connectionFilter }}
                </button>
                <div v-if="openCompanyFilter === 'connection'" class="company-filter-menu">
                  <button
                    v-for="option in connectionFilterOptions"
                    :key="option"
                    :class="{ selected: connectionFilter === option }"
                    type="button"
                    @click="selectConnectionFilter(option)"
                  >
                    {{ option === '전체' ? '지점 연결 전체' : option }}
                  </button>
                </div>
              </div>
              <label><input v-model.trim="search" type="search" placeholder="센터명, 대표자, 이메일 검색" /><span>⌕</span></label>
              <button class="primary" type="button" @click="openCompanyModal">센터 추가</button>
            </section>

            <section class="company-admin-main-grid">
              <article class="company-admin-panel company-list-panel">
                <div class="company-panel-head">
                  <h3>센터 목록 <small>운영/권한/지점 연결 현황</small></h3>
                  <div><button type="button">CSV 내보내기</button><button type="button" @click="resetCompanyFilters">필터 초기화</button></div>
                </div>
                <table class="company-admin-table">
                  <thead><tr><th>센터명</th><th>대표자</th><th>연락처</th><th>이메일</th><th>지점</th><th>상태</th><th>최근 로그인</th><th>관리</th></tr></thead>
                  <tbody>
                    <tr
                      v-for="(company, index) in companyRows"
                      :key="company.email"
                      :class="{ selected: selectedCompany?.email === company.email }"
                      @click="selectCompany(company)"
                    >
                      <td><b>{{ company.name }}</b></td>
                      <td>{{ company.owner }}</td>
                      <td>{{ company.phone }}</td>
                      <td>{{ company.email }}</td>
                      <td>{{ company.centers }}개</td>
                      <td><span class="company-state" :class="statusClass(company.status)">{{ company.status }}</span></td>
                      <td>{{ index === 0 ? '2026-05-12 10:24' : '2026-05-11 17:32' }}</td>
                      <td>
                        <span class="company-row-actions">
                          <button type="button" @click.stop="notifyCompanyEdited">수정</button>
                          <button type="button" @click.stop="notifyPermissionSaved">권한</button>
                        </span>
                      </td>
                    </tr>
                  </tbody>
                </table>
                <footer class="company-admin-pagination"><span>전체 {{ companyRows.length || 0 }}건</span><div><button>‹</button><button class="active">1</button><button>2</button><button>3</button><button>›</button></div><button>10개씩 보기</button></footer>
              </article>

              <aside class="company-admin-side">
                <article class="company-admin-panel company-detail-card">
                  <div class="company-panel-head"><h3>선택 센터 상세</h3><span class="company-state" :class="statusClass(selectedCompany?.status)">{{ selectedCompany?.status ?? '-' }}</span></div>
                  <dl>
                    <dt>센터명</dt><dd>{{ selectedCompany?.name ?? '-' }}</dd>
                    <dt>대표자</dt><dd>{{ selectedCompany?.owner ?? '-' }}</dd>
                    <dt>대표 이메일</dt><dd>{{ selectedCompany?.email ?? '-' }}</dd>
                    <dt>담당 지점</dt><dd>{{ selectedCompany?.centers ?? 0 }}개 지점 연결</dd>
                    <dt>연락처</dt><dd>{{ selectedCompany?.phone ?? '-' }}</dd>
                    <dt>정산 권한</dt><dd>{{ companyPermission(selectedCompany) }}</dd>
                    <dt>최근 변경자</dt><dd>admin <small>{{ selectedCompanyLastChanged }}</small></dd>
                  </dl>
                  <div class="company-detail-actions"><button type="button" @click="notifyCompanyEdited">센터 수정</button><button type="button" @click="notifyPermissionSaved">권한 설정</button></div>
                </article>

                <article class="company-admin-panel company-link-card">
                  <h3>관제 대시보드 연결</h3>
                  <ul>
                    <li v-for="(center, index) in selectedCompanyCenters" :key="center.dashboardId">
                      <span>{{ center.name }}</span>
                      <b>{{ center.dashboardId }}</b>
                      <em :class="{ warn: selectedCompany?.status === '주의' && index === 0 }">
                        {{ selectedCompany?.status === '주의' && index === 0 ? '검토 필요' : '진입 가능' }}
                      </em>
                    </li>
                  </ul>
                </article>
              </aside>
            </section>

            <section class="company-admin-bottom-grid">
              <article class="company-admin-panel company-permission-summary">
                <h3>권한/계정 요약</h3>
                <div class="permission-stat-cards">
                  <span><b>MASTER</b><strong>1</strong></span>
                  <span><b>ADMIN</b><strong>8</strong></span>
                  <span><b>OPERATOR</b><strong>26</strong></span>
                  <span><b>LOCKED</b><strong>2</strong></span>
                </div>
                <div class="permission-mini-chart">
                  <ChartJsPanel type="bar" :data="companyPermissionChartData" :options="companyPermissionChartOptions" :height="76" />
                </div>
              </article>
              <article class="company-admin-panel company-audit-card">
                <div class="company-panel-head"><h3>최근 센터 관리 이력</h3><button type="button">전체 보기</button></div>
                <table class="company-admin-table compact">
                  <thead><tr><th>시간</th><th>관리자</th><th>대상</th><th>작업</th><th>결과</th></tr></thead>
                  <tbody>
                    <tr><td>10:24:11</td><td>admin</td><td>하이패스 서울(주)</td><td>권한 수정</td><td><span class="company-state ok">성공</span></td></tr>
                    <tr><td>10:18:02</td><td>admin</td><td>수원 하이패스(주)</td><td>지점 연결</td><td><span class="company-state ok">성공</span></td></tr>
                    <tr><td>09:42:33</td><td>admin</td><td>대전 하이패스(주)</td><td>정보 수정</td><td><span class="company-state warn">검토</span></td></tr>
                  </tbody>
                </table>
              </article>
            </section>
          </article>
</template>
