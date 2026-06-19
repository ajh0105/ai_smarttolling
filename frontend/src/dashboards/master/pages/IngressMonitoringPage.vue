<script setup>
import { computed, inject } from 'vue'
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
  filteredCompanies,
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
  masterApiState,
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

const liveIngress = computed(() => masterApiState?.adminIngressStatus?.value ?? {})
const liveEvents = computed(() => masterApiState?.adminIngressEvents?.value ?? [])
const ingressCompactLineOptions = computed(() => {
  const tickColor = isLightMode.value ? '#334155' : chartTextColor
  const gridColor = isLightMode.value ? 'rgba(100, 116, 139, 0.20)' : chartGridColor

  return {
    ...compactLineOptions,
    scales: {
      x: {
        ...compactLineOptions.scales.x,
        ticks: { ...compactLineOptions.scales.x.ticks, color: tickColor, font: { size: 10, weight: '700' } },
        grid: { ...compactLineOptions.scales.x.grid, color: gridColor }
      },
      y: {
        ...compactLineOptions.scales.y,
        ticks: { ...compactLineOptions.scales.y.ticks, color: tickColor, font: { size: 10, weight: '700' } },
        grid: { ...compactLineOptions.scales.y.grid, color: gridColor }
      }
    }
  }
})
</script>

<template>
          <article class="ingress-page">
            <section class="api-sync-strip">
              <article><b>Ingress API</b><span>{{ liveIngress.ingressId ?? 'default' }}</span><span>{{ liveIngress.springForwardStatus ?? '대기' }}</span></article>
              <article><b>수신</b><span>{{ liveIngress.receivedEvents ?? 0 }}건</span><span>ACK {{ liveIngress.ackedEvents ?? 0 }}</span></article>
              <article><b>오류</b><span>RETRY {{ liveIngress.retryEvents ?? 0 }}</span><span>REJECT {{ liveIngress.rejectedEvents ?? 0 }}</span><span>Malformed {{ liveIngress.malformedFrames ?? 0 }}</span></article>
              <article><b>최근 이벤트</b><span>{{ liveEvents.length }}건</span><span>{{ liveIngress.lastEventId ?? '-' }}</span></article>
            </section>
            <section class="ingress-kpi-grid">
              <article class="ingress-kpi alive"><i><img src="../../icons/admin/alive.png" alt="Ingress Alive" /></i><div><span>Ingress Alive</span><strong>정상</strong><small>Uptime 12일 04:32:11</small></div></article>
              <article class="ingress-kpi"><i><img src="../../icons/admin/message.png" alt="수신 이벤트" /></i><div><span>수신 이벤트</span><strong>8,742,318 <em>건</em></strong><small>오늘 128,456건 ▲ +10.4%</small></div></article>
              <article class="ingress-kpi"><i><img src="../../icons/admin/check_button.png" alt="ACK" /></i><div><span>ACK</span><strong>8,698,214 <em>건</em></strong><small>성공률 99.50%</small></div></article>
              <article class="ingress-kpi caution"><i><img src="../../icons/admin/retry.png" alt="RETRY" /></i><div><span>RETRY</span><strong>31,245 <em>건</em></strong><small>비율 0.36%</small></div></article>
              <article class="ingress-kpi danger"><i><img src="../../icons/admin/reject.png" alt="REJECT" /></i><div><span>REJECT</span><strong>8,214 <em>건</em></strong><small>비율 0.09%</small></div></article>
              <article class="ingress-kpi malformed"><i><img src="../../icons/admin/parentheses.png" alt="Malformed" /></i><div><span>Malformed</span><strong>4,645 <em>건</em></strong><small>비율 0.05%</small></div></article>
            </section>

            <section class="ingress-main-grid">
              <article class="ingress-panel ingress-info-panel">
                <h3>연결 상태 및 주요 정보</h3>
                <ul class="ingress-info-list">
                  <li><span>현재 연결 수</span><strong>156 개</strong></li>
                  <li><span>총 연결 수 (누적)</span><strong>12,568 개</strong></li>
                  <li><span>마지막 event_id</span><b>evt_20250511_173504_f31a9c2b</b></li>
                  <li><span>마지막 payload 크기</span><strong>1,284 bytes</strong></li>
                  <li><span>Spring forward 결과</span><em class="ok">성공</em></li>
                  <li><span>Jetson 상태 수신 여부</span><em class="ok">수신 중</em></li>
                  <li><span>Jetson 상태 Stale 여부</span><em class="ok">정상</em></li>
                </ul>
                <footer><p><span>버전</span><b>ingress 1.3.7</b></p><p><span>시작 시간</span><b>2025-05-01 13:02:56</b></p><p><span>메모리 사용률</span><b>342 MB (28%)</b></p></footer>
              </article>

              <article class="ingress-panel ingress-chart-panel">
                <div class="ingress-panel-title"><h3>Ingress 지표 추이 <small>(최근 1시간)</small></h3><button type="button">최근 1시간</button></div>
                <div class="ingress-chart-grid">
                  <section v-for="chart in ingressCharts" :key="chart.title"><h4>{{ chart.title }}</h4><ChartJsPanel type="line" :data="chart.data" :options="ingressCompactLineOptions" :height="128" /></section>
                </div>
              </article>

              <section class="ingress-side-stack">
                <article class="ingress-panel">
                  <div class="ingress-panel-title"><h3>LAN / LTE 전환 로그 <small>(최근 10건)</small></h3><button type="button">전체 보기 ›</button></div>
                  <table class="ingress-table"><thead><tr><th>시간</th><th>이전 경로</th><th>변경 경로</th><th>사유</th></tr></thead><tbody>
                    <tr><td>17:32:41</td><td class="route-lan">LAN</td><td class="route-lan">LAN</td><td>수동 복구</td></tr>
                    <tr><td>17:21:18</td><td class="route-lan">LAN</td><td class="route-lte">LTE</td><td>신호 회복</td></tr>
                    <tr><td>17:12:05</td><td class="route-lte">LTE</td><td class="route-lan">LAN</td><td>LAN 끊김</td></tr>
                    <tr><td>16:58:33</td><td class="route-lan">LAN</td><td class="route-lte">LTE</td><td>신호 회복</td></tr>
                    <tr><td>16:44:10</td><td class="route-lte">LTE</td><td class="route-lan">LAN</td><td>신호 약화</td></tr>
                  </tbody></table>
                </article>
                <article class="ingress-panel">
                  <div class="ingress-panel-title"><h3>Spring Forward 상태 <small>(최근 5건)</small></h3><button type="button">전체 보기 ›</button></div>
                  <table class="ingress-table"><thead><tr><th>시간</th><th>event_id</th><th>전송 결과</th><th>응답 시간</th><th>비고</th></tr></thead><tbody>
                    <tr><td>17:35:04</td><td>evt_20250511_173504_f31a9c2b</td><td class="route-lan">성공</td><td>145 ms</td><td>200 OK</td></tr>
                    <tr><td>17:34:57</td><td>evt_20250511_173457_d9b21a8c</td><td class="route-lan">성공</td><td>153 ms</td><td>200 OK</td></tr>
                    <tr><td>17:34:49</td><td>evt_20250511_173449_bc2f7e11</td><td class="route-lan">성공</td><td>149 ms</td><td>200 OK</td></tr>
                    <tr><td>17:34:39</td><td>evt_20250511_173439_9a6d3b7e</td><td class="route-lan">성공</td><td>161 ms</td><td>200 OK</td></tr>
                  </tbody></table>
                </article>
              </section>
            </section>

            <article class="ingress-panel ingress-error-panel">
              <div class="ingress-panel-title"><h3>최근 Ingress 오류 <small>(최근 10건)</small></h3><button type="button">전체 보기 ›</button></div>
              <table class="ingress-table error-table"><thead><tr><th>발생시각</th><th>event_id</th><th>source</th><th>오류 유형</th><th>payload 크기</th><th>처리 결과</th><th>상태</th></tr></thead><tbody>
                <tr><td>2025-05-11 17:31:45</td><td>evt_20250511_173145_7b2a12fc</td><td>Edge-DAE-02</td><td>Malformed JSON</td><td>512 bytes</td><td><span class="ingress-badge reject">REJECT</span></td><td><span class="ingress-badge hold">확인 필요</span></td></tr>
                <tr><td>2025-05-11 17:30:12</td><td>evt_20250511_173012_4c9d8f11</td><td>Edge-DGU-01</td><td>Schema Validation Error</td><td>768 bytes</td><td><span class="ingress-badge reject">REJECT</span></td><td><span class="ingress-badge hold">확인 필요</span></td></tr>
                <tr><td>2025-05-11 17:28:55</td><td>evt_20250511_172855_f12e3d9a</td><td>Edge-DBS-03</td><td>Spring Forward 실패 (5xx)</td><td>1,102 bytes</td><td><span class="ingress-badge retry">RETRY</span></td><td><span class="ingress-badge retry">재시도 중</span></td></tr>
                <tr><td>2025-05-11 17:27:21</td><td>evt_20250511_172721_9b4f88cd</td><td>Edge-DGW-02</td><td>Healthz Check Down</td><td>-</td><td>-</td><td><span class="ingress-badge info">조치 중</span></td></tr>
                <tr><td>2025-05-11 17:25:47</td><td>evt_20250511_172547_0c3d5f89</td><td>Edge-DJE-01</td><td>Payload 필드 누락</td><td>634 bytes</td><td><span class="ingress-badge reject">REJECT</span></td><td><span class="ingress-badge hold">확인 필요</span></td></tr>
              </tbody></table>
              <p class="ingress-footnote">※ 시간은 서버 시간 기준(KST) 입니다.</p>
            </article>
          </article>
</template>
