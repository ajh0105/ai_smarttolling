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

const formatNumber = (value, fallback = '-') => {
  const numeric = Number(value)
  return Number.isFinite(numeric) ? numeric.toLocaleString() : fallback
}

const liveEdge = computed(() => masterApiState?.adminEdges?.value?.[0] ?? {})
const liveIngress = computed(() => masterApiState?.adminIngressStatus?.value ?? {})
const liveBackend = computed(() => masterApiState?.adminBackendStatus?.value ?? {})
const liveDb = computed(() => masterApiState?.adminDbStatus?.value ?? {})
</script>

<template>
          <article class="system-control-page">
            <section class="api-sync-strip">
              <article>
                <b>Edge</b>
                <span>FPS {{ formatNumber(liveEdge.latestFps) }}</span>
                <span>Spool {{ formatNumber(liveEdge.spoolCount) }}</span>
                <span>{{ liveEdge.activePath ?? 'LAN' }}</span>
              </article>
              <article>
                <b>Ingress</b>
                <span>수신 {{ formatNumber(liveIngress.receivedEvents) }}</span>
                <span>ACK {{ formatNumber(liveIngress.ackedEvents) }}</span>
                <span>RETRY {{ formatNumber(liveIngress.retryEvents) }}</span>
              </article>
              <article>
                <b>Backend</b>
                <span>Validation {{ liveBackend.validationFailureRate ?? '-' }}</span>
                <span>Duplicate {{ formatNumber(liveBackend.duplicateBlockedCount) }}</span>
                <span>p95 {{ liveBackend.apiLatencyP95Ms ?? '-' }} ms</span>
              </article>
              <article>
                <b>DB / API</b>
                <span>Write {{ formatNumber(liveDb.writeTps) }}</span>
                <span>Read {{ formatNumber(liveDb.readTps) }}</span>
                <span>{{ liveDb.backupStatus ?? '대기' }}</span>
              </article>
            </section>
            <section class="system-kpi-grid">
              <article class="system-kpi"><i><img src="../../icons/admin/cpu2.png" alt="전체 Edge" /></i><div><span>전체 Edge</span><strong>86 <em>대</em></strong><small>전체 등록</small></div></article>
              <article class="system-kpi ok"><i><img src="../../icons/admin/check_button.png" alt="정상 Edge" /></i><div><span>정상 Edge</span><strong>78 <em>대</em></strong><small>정상 비율 90.7%</small></div></article>
              <article class="system-kpi ok"><i><img src="../../icons/admin/monitor.png" alt="Ingress 상태" /></i><div><span>Ingress 상태</span><strong>정상</strong><small>Uptime 12일 04:32</small></div></article>
              <article class="system-kpi ok"><i><img src="../../icons/admin/backend2.png" alt="Backend 상태" /></i><div><span>Backend 상태</span><strong>정상</strong><small>Uptime 8일 11:18</small></div></article>
              <article class="system-kpi ok"><i><img src="../../icons/admin/db2.png" alt="DB 상태" /></i><div><span>DB 상태</span><strong>정상</strong><small>복제 지연 0.2초</small></div></article>
              <article class="system-kpi danger"><i><img src="../../icons/admin/caution.png" alt="최근 장애" /></i><div><span>최근 장애</span><strong>2 <em>건</em></strong><small>최근 24시간</small></div></article>
            </section>

            <article class="system-panel pipeline-panel">
              <h3>시스템 파이프라인 상태 <small>ⓘ</small></h3>
              <div class="pipeline-flow">
                <section class="pipeline-node">
                  <i><img src="../../icons/admin/cpu2.png" alt="Jetson Edge" /></i><h4>Jetson Edge</h4><b>정상 운영</b>
                  <dl><dt>FPS</dt><dd>29.8</dd><dt>Spool</dt><dd>12</dd><dt>Active Path</dt><dd>LAN</dd></dl>
                  <p><span></span>정상 장비 78 / Stale 5</p>
                </section>
                <section class="pipeline-node">
                  <i><img src="../../icons/admin/python.png" alt="Python Ingress" /></i><h4>Python Ingress</h4><b>정상 운영</b>
                  <dl><dt>수신 이벤트</dt><dd>128,456건</dd><dt>ACK</dt><dd>126,842건</dd><dt>RETRY</dt><dd>1,208건</dd></dl>
                  <p><span></span>연결 상태 정상</p>
                </section>
                <section class="pipeline-node">
                  <i><img src="../../icons/admin/backend2.png" alt="Spring Boot Backend" /></i><h4>Spring Boot Backend</h4><b>정상 운영</b>
                  <dl><dt>Validation 실패</dt><dd>0.18%</dd><dt>Duplicate 차단</dt><dd>1,284건</dd><dt>API Latency (p95)</dt><dd>128 ms</dd></dl>
                  <p><span></span>API 상태 정상</p>
                </section>
                <section class="pipeline-node">
                  <i><img src="../../icons/admin/db3.png" alt="PostgreSQL / DB" /></i><h4>PostgreSQL / DB</h4><b>정상 운영</b>
                  <dl><dt>Write TPS</dt><dd>412</dd><dt>Read TPS</dt><dd>256</dd><dt>Backup 상태</dt><dd>정상</dd></dl>
                  <p><span></span>복제 지연 0.2초</p>
                </section>
                <section class="pipeline-node">
                  <i><img src="../../icons/admin/dashboard.png" alt="Dashboard API" /></i><h4>Dashboard API</h4><b>정상 운영</b>
                  <dl><dt>응답 시간 (p95)</dt><dd>145 ms</dd><dt>오류율</dt><dd>0.02%</dd><dt>요청 처리량</dt><dd>1,284 rpm</dd></dl>
                  <p><span></span>API 상태 정상</p>
                </section>
              </div>
            </article>

            <section class="system-summary-grid">
              <article class="system-panel edge-summary">
                <div class="system-panel-title"><h3>Edge Fleet 요약</h3><button type="button">전체 Edge 보기 ›</button></div>
                <div class="edge-metrics">
                  <div><i><img src="../../icons/admin/cpu2.png" alt="Stale 장비" /></i><span>Stale 장비</span><strong>5 <em>대</em></strong><small>5.8%</small></div>
                  <div><i><img src="../../icons/admin/cctv.png" alt="Source Running" /></i><span>Source Running</span><strong>73 <em>대</em></strong><small>84.9%</small></div>
                  <div><i><img src="../../icons/admin/pause.png" alt="Source Idle" /></i><span>Source Idle</span><strong>10 <em>대</em></strong><small>11.6%</small></div>
                  <div><i><img src="../../icons/admin/ocr.png" alt="OCR Drop 비" /></i><span>OCR Drop 비</span><strong>0.12%</strong><small>정상 범위</small></div>
                  <div><i><img src="../../icons/admin/lan.png" alt="Active Path - LAN" /></i><span>Active Path - LAN</span><strong>72 <em>대</em></strong><small>83.7%</small></div>
                  <div><i><img src="../../icons/admin/antenna.png" alt="Active Path - LTE" /></i><span>Active Path - LTE</span><strong>14 <em>대</em></strong><small>16.3%</small></div>
                </div>
                <div class="donut-wrap">
                  <h4>Edge 상태 분포</h4>
                  <div class="system-donut system-donut-chart"><ChartJsPanel type="doughnut" :data="edgeFleetDoughnutData" :options="doughnutOptions" :height="138" /><div class="system-donut-center"><strong>86</strong><span>대</span></div></div>
                  <ul>
                    <li><b class="ok"></b>정상 78 (90.7%)</li>
                    <li><b class="warn"></b>주의 7 (8.1%)</li>
                    <li><b class="danger"></b>Stale 5 (5.8%)</li>
                  </ul>
                </div>
              </article>

              <article class="system-panel ingress-summary">
                <div class="system-panel-title"><h3>Ingress 요약</h3><button type="button">상세 보기 ›</button></div>
                <div class="ingress-mini-grid">
                  <div><i><img src="../../icons/admin/event.png" alt="수신 이벤트" /></i><span>수신 이벤트</span><strong>128,456건</strong><small>최근 1시간</small></div>
                  <div><i><img src="../../icons/admin/ingress_check.png" alt="ACK" /></i><span>ACK</span><strong>128,842건</strong><small>성공률 98.7%</small></div>
                  <div><i><img src="../../icons/admin/retry.png" alt="RETRY" /></i><span>RETRY</span><strong>1,208건</strong><small>0.94%</small></div>
                  <div><i><img src="../../icons/admin/reject.png" alt="REJECT" /></i><span>REJECT</span><strong>184건</strong><small>0.14%</small></div>
                  <div><i><img src="../../icons/admin/parentheses.png" alt="Malformed" /></i><span>Malformed</span><strong>222건</strong><small>0.17%</small></div>
                  <div><i><img src="../../icons/admin/people2.png" alt="현재 연결 수" /></i><span>현재 연결 수</span><strong>156개</strong><small>총 1,024개</small></div>
                </div>
                <p class="forward-result">Spring Forward 결과 <b>정상</b><span>최근 10분</span></p>
              </article>

              <article class="system-panel backend-summary">
                <div class="system-panel-title"><h3>Backend / DB 요약</h3><button type="button">상세 보기 ›</button></div>
                <div class="backend-mini-grid">
                  <div><i><img src="../../icons/admin/fail.png" alt="Validation 실패율" /></i><span>Validation 실패율</span><strong>0.18%</strong><small>정상</small></div>
                  <div><i><img src="../../icons/admin/block.png" alt="Duplicate 차단" /></i><span>Duplicate 차단</span><strong>1,284건</strong><small>최근 1시간</small></div>
                  <div><i><img src="../../icons/admin/save.png" alt="저장 성공률" /></i><span>저장 성공률</span><strong>99.98%</strong><small>정상</small></div>
                  <div><i><img src="../../icons/admin/db_link.png" alt="DB 연결 상태" /></i><span>DB 연결 상태</span><strong>정상</strong><small>활성</small></div>
                  <div><i><img src="../../icons/admin/latency.png" alt="Query Latency (p95)" /></i><span>Query Latency (p95)</span><strong>18 ms</strong><small>정상</small></div>
                  <div><i><img src="../../icons/admin/backup.png" alt="백업 상태" /></i><span>백업 상태</span><strong>정상</strong><small>최근 1시간 전</small></div>
                </div>
              </article>
            </section>

            <article class="system-panel system-alert-table">
              <div class="system-panel-title"><h3>최근 시스템 알림</h3><button type="button">전체 알림 보기 ›</button></div>
              <table>
                <thead><tr><th>발생시각</th><th>구성요소</th><th>심각도</th><th>내용</th><th>상태</th></tr></thead>
                <tbody>
                  <tr><td>2025-05-11 17:32:41</td><td>Jetson (JETSON-27)</td><td><span class="sys-badge danger">치명</span></td><td>Jetson 상태 Stale (서울 톨링 A) - 10분 이상 상태 미수신</td><td><span class="sys-badge hold">조치중</span></td></tr>
                  <tr><td>2025-05-11 17:22:17</td><td>Ingress</td><td><span class="sys-badge warn">경고</span></td><td>Malformed Payload 증가 - 최근 10분간 45건</td><td><span class="sys-badge hold">조치중</span></td></tr>
                  <tr><td>2025-05-11 16:58:11</td><td>Network</td><td><span class="sys-badge warn">경고</span></td><td>LAN → LTE 경로 전환 - 부산 톨링 D</td><td><span class="sys-badge ok">완료</span></td></tr>
                  <tr><td>2025-05-11 16:44:10</td><td>Backend (Spring)</td><td><span class="sys-badge info">정보</span></td><td>API Latency (p95) 상승 - 210ms (임계치 300ms)</td><td><span class="sys-badge ok">완료</span></td></tr>
                  <tr><td>2025-05-11 15:31:05</td><td>PostgreSQL</td><td><span class="sys-badge info">정보</span></td><td>자동 백업 완료 - 스냅샷 생성 성공</td><td><span class="sys-badge ok">완료</span></td></tr>
                </tbody>
              </table>
            </article>
          </article>
</template>
