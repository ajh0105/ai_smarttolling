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

const liveBackend = computed(() => masterApiState?.adminBackendStatus?.value ?? {})
const liveDb = computed(() => masterApiState?.adminDbStatus?.value ?? {})
</script>

<template>
          <article class="backend-db-page">
            <section class="api-sync-strip">
              <article><b>Spring</b><span>{{ liveBackend.status ?? '대기' }}</span><span>p95 {{ liveBackend.apiLatencyP95Ms ?? '-' }} ms</span></article>
              <article><b>검증</b><span>Validation {{ liveBackend.validationFailureRate ?? '-' }}</span><span>Duplicate {{ liveBackend.duplicateBlockedCount ?? '-' }}</span></article>
              <article><b>PostgreSQL</b><span>{{ liveDb.connectionStatus ?? '대기' }}</span><span>Write {{ liveDb.writeTps ?? '-' }}</span><span>Read {{ liveDb.readTps ?? '-' }}</span></article>
              <article><b>보관</b><span>Backup {{ liveDb.backupStatus ?? '대기' }}</span><span>Storage {{ liveDb.storageUsagePercent ?? '-' }}%</span></article>
            </section>
            <section class="backend-db-kpi-grid">
              <article class="backend-db-kpi ok"><i><img src="../../icons/admin/backend2.png" alt="Backend 상태" /></i><div><span>Backend 상태</span><strong>정상</strong><small>Uptime 12일 04:32:11</small></div></article>
              <article class="backend-db-kpi"><i><img src="../../icons/admin/latency.png" alt="API 응답 p95" /></i><div><span>API 응답 p95</span><strong>128 <em>ms</em></strong><small>p50 42 ms / p99 312 ms</small></div></article>
              <article class="backend-db-kpi purple"><i><img src="../../icons/admin/fail.png" alt="Validation 실패율" /></i><div><span>Validation 실패율</span><strong>0.18 <em>%</em></strong><small>실패 482 / 전체 267,842</small></div></article>
              <article class="backend-db-kpi warn"><i><img src="../../icons/admin/block.png" alt="Duplicate 차단" /></i><div><span>Duplicate 차단</span><strong>3,248 <em>건</em></strong><small>차단 비율 2.18%</small></div></article>
              <article class="backend-db-kpi ok"><i><img src="../../icons/admin/db3.png" alt="DB 연결 상태" /></i><div><span>DB 연결 상태</span><strong>정상</strong><small>Active 28 / Max 100</small></div></article>
              <article class="backend-db-kpi"><i><img src="../../icons/admin/check_button.png" alt="저장 성공률" /></i><div><span>저장 성공률</span><strong>99.92 <em>%</em></strong><small>성공 267,432 / 실패 210</small></div></article>
            </section>

            <section class="backend-db-main-grid">
              <article class="backend-db-panel spring-service">
                <h3>Spring Boot 서비스 상태</h3>
                <table class="backend-db-table">
                  <thead><tr><th>서비스</th><th>상태</th><th>처리 건수(1분)</th><th>에러율</th><th>평균 응답시간</th></tr></thead>
                  <tbody>
                    <tr><td><span class="ok-dot">●</span> Protobuf Decode</td><td><b>정상</b></td><td>8,742</td><td>0.02%</td><td>14 ms</td></tr>
                    <tr><td><span class="ok-dot">●</span> DTO Validation</td><td><b>정상</b></td><td>8,742</td><td>0.18%</td><td>21 ms</td></tr>
                    <tr><td><span class="ok-dot">●</span> Duplicate Check</td><td><b>정상</b></td><td>8,742</td><td>0.02%</td><td>11 ms</td></tr>
                    <tr><td><span class="ok-dot">●</span> GPS 판정 처리</td><td><b>정상</b></td><td>8,742</td><td>0.05%</td><td>19 ms</td></tr>
                    <tr><td><span class="ok-dot">●</span> 결제 판정 처리</td><td><b>정상</b></td><td>8,742</td><td>0.04%</td><td>23 ms</td></tr>
                    <tr><td><span class="ok-dot">●</span> REST API</td><td><b>정상</b></td><td>2,156</td><td>0.08%</td><td>128 ms</td></tr>
                  </tbody>
                </table>
                <footer class="backend-db-panel-footer"><span>ⓘ 기준 구간 : 최근 1분</span><button type="button">상세 모니터링 ↗</button></footer>
              </article>

              <article class="backend-db-panel request-flow">
                <h3>요청/처리 흐름 <small>(실시간)</small></h3>
                <div class="flow-chain">
                  <div><i><img src="../../icons/admin/edge_set.png" alt="Ingress Forward" /></i><strong>Ingress<br />Forward</strong><b>●</b><span>수신 정상<br />8,742 /min</span></div>
                  <div><i><img src="../../icons/admin/parentheses.png" alt="Protobuf Decode" /></i><strong>Protobuf<br />Decode</strong><b>●</b><span>정상<br />8,742 /min</span></div>
                  <div><i><img src="../../icons/admin/latency.png" alt="Validation" /></i><strong>Validation</strong><b>●</b><span>정상<br />실패율 0.18%</span></div>
                  <div><i><img src="../../icons/admin/search.png" alt="Duplicate Check" /></i><strong>Duplicate<br />Check</strong><b>●</b><span>정상<br />차단율 2.18%</span></div>
                  <div><i><img src="../../icons/admin/gps.png" alt="GPS Payment Decision" /></i><strong>GPS<br />Payment Decision</strong><b>●</b><span>정상<br />정상율 97.82%</span></div>
                  <div><i><img src="../../icons/admin/db2.png" alt="PostgreSQL Save" /></i><strong>PostgreSQL<br />Save</strong><b>●</b><span>정상<br />성공률 99.92%</span></div>
                </div>
                <footer class="flow-legend"><span class="ok">정상</span><span class="warn">지연/주의</span><span class="error">오류</span><span class="idle">대기</span><em>자동 갱신 : 5초 ↻</em></footer>
              </article>

              <article class="backend-db-panel postgres-status">
                <h3>PostgreSQL (DB) 상태</h3>
                <ul>
                  <li><span>Active Connections</span><strong>28 / 100</strong><b>정상</b></li>
                  <li><span>Write TPS</span><strong>256 tps</strong><b>정상</b></li>
                  <li><span>Read TPS</span><strong>712 tps</strong><b>정상</b></li>
                  <li><span>Slow Query (1s 이상)</span><strong>3건</strong><b>정상</b></li>
                  <li><span>Storage Usage</span><strong>42.6%</strong><b>정상</b></li>
                  <li><span>Backup Status</span><strong></strong><b>정상</b></li>
                  <li class="wide-value"><span>Last Backup</span><strong>2025-05-11 02:00:00</strong></li>
                  <li><span>Replication Status</span><strong>정상 (Primary)</strong><b>정상</b></li>
                </ul>
                <button type="button">DB 상세 모니터링 ↗</button>
              </article>
            </section>

            <section class="backend-db-bottom-grid">
              <article class="backend-db-panel backend-errors">
                <h3>백엔드 오류 <small>(최근 5건)</small></h3>
                <table class="backend-db-table">
                  <thead><tr><th>발생시각</th><th>단계</th><th>event_id</th><th>오류 유형</th><th>처리 결과</th></tr></thead>
                  <tbody>
                    <tr><td>2025-05-11 17:35:12</td><td>DTO Validation</td><td>evt_20250511_173512_a1b2c3d4</td><td>잘못된 plate 형식</td><td><b class="db-badge reject">REJECT</b></td></tr>
                    <tr><td>2025-05-11 17:34:58</td><td>GPS 판정 처리</td><td>evt_20250511_173458_b2c3d4e5</td><td>GPS 좌표 누락</td><td><b class="db-badge reject">REJECT</b></td></tr>
                    <tr><td>2025-05-11 17:33:21</td><td>Duplicate Check</td><td>evt_20250511_173321_c3d4e5f6</td><td>중복 이벤트 (event_id)</td><td><b class="db-badge duplicate">DUPLICATE</b></td></tr>
                    <tr><td>2025-05-11 17:31:07</td><td>Protobuf Decode</td><td>evt_20250511_173107_d4e5f6a7</td><td>프로토콜 스키마 오류</td><td><b class="db-badge reject">REJECT</b></td></tr>
                    <tr><td>2025-05-11 17:29:44</td><td>REST API</td><td>-</td><td>/api/v1/events 타임아웃</td><td><b class="db-badge timeout">TIMEOUT</b></td></tr>
                  </tbody>
                </table>
                <footer class="backend-db-panel-footer right"><button type="button">전체 오류 로그 보기 ↗</button></footer>
              </article>

              <article class="backend-db-panel db-table-summary">
                <h3>DB 테이블 요약 <small>(주요 테이블)</small></h3>
                <table class="backend-db-table">
                  <thead><tr><th>테이블명</th><th>행 수</th><th>최근 쓰기 시간</th><th>상태</th></tr></thead>
                  <tbody>
                    <tr><td>passage_events</td><td>5,482,193</td><td>2025-05-11 17:36:45</td><td><b class="db-badge ok">정상</b></td></tr>
                    <tr><td>gps_telemetry</td><td>18,742,331</td><td>2025-05-11 17:36:44</td><td><b class="db-badge ok">정상</b></td></tr>
                    <tr><td>settlement_results</td><td>2,156,782</td><td>2025-05-11 17:36:42</td><td><b class="db-badge ok">정상</b></td></tr>
                    <tr><td>review_tasks</td><td>86,451</td><td>2025-05-11 17:35:11</td><td><b class="db-badge ok">정상</b></td></tr>
                    <tr><td>system_audit_logs</td><td>1,283,905</td><td>2025-05-11 17:36:40</td><td><b class="db-badge ok">정상</b></td></tr>
                  </tbody>
                </table>
                <footer class="backend-db-panel-footer right"><button type="button">DB 테이블 상세 보기 ↗</button></footer>
              </article>
            </section>
          </article>
</template>
