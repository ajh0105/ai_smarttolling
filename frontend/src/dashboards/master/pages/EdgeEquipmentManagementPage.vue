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

const liveEdges = computed(() => masterApiState?.adminEdges?.value ?? [])
const liveEdge = computed(() => liveEdges.value[0] ?? {})
const edgeCompactLineOptions = computed(() => {
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
          <article class="edge-page">
            <section class="api-sync-strip">
              <article><b>Edge API</b><span>등록 {{ liveEdges.length || 0 }}대</span><span>Alive {{ liveEdges.filter((edge) => edge.alive).length || 0 }}대</span></article>
              <article><b>선택 장비</b><span>{{ liveEdge.deviceId ?? '대기' }}</span><span>{{ liveEdge.sourceState ?? '-' }}</span></article>
              <article><b>처리</b><span>FPS {{ liveEdge.latestFps ?? '-' }}</span><span>YOLO {{ liveEdge.latestYoloMs ?? '-' }} ms</span><span>OCR {{ liveEdge.latestOcrMs ?? '-' }} ms</span></article>
              <article><b>전송</b><span>Spool {{ liveEdge.spoolCount ?? '-' }}</span><span>{{ liveEdge.activePath ?? 'LAN' }}</span><span>{{ liveEdge.stale ? 'Stale' : '정상' }}</span></article>
            </section>
            <section class="edge-main-grid">
              <article class="edge-panel edge-list-panel">
                <h3>Edge 장비 목록 <small>(42대)</small></h3>
                <div class="edge-filter-row">
                  <button class="filter-toggle" type="button">지점 전체</button><button class="filter-toggle" type="button">Alive 전체</button><button class="filter-toggle" type="button">Source 전체</button><button class="filter-toggle" type="button">Path 전체</button><button class="filter-toggle" type="button">Stale 전체</button>
                  <label><input type="search" placeholder="장비 ID 검색..." /><span>⌕</span></label>
                  <button type="button">필터 초기화</button>
                </div>
                <table class="edge-table">
                  <thead><tr><th></th><th>장비 ID</th><th>지점</th><th>Alive</th><th>Source</th><th>입력 Source</th><th>FPS</th><th>YOLO ms</th><th>OCR ms</th><th>Spool</th><th>Path</th><th>Stale</th><th>최근 오류</th></tr></thead>
                  <tbody>
                    <tr class="selected"><td><span class="radio on"></span></td><td>JETSON-27</td><td>서울 톨링 A</td><td><b class="edge-badge ok">Alive</b></td><td><b class="edge-badge ok">Running</b></td><td>CAM-01 (L1)</td><td>29.8</td><td>18.6</td><td>42.3</td><td>12</td><td><b class="edge-badge path">LAN</b></td><td><b class="edge-badge ok">정상</b></td><td>-</td></tr>
                    <tr><td><span class="radio"></span></td><td>JETSON-12</td><td>수원 톨링 A</td><td><b class="edge-badge ok">Alive</b></td><td><b class="edge-badge ok">Running</b></td><td>CAM-02 (L2)</td><td>29.6</td><td>18.9</td><td>41.0</td><td>8</td><td><b class="edge-badge path">LAN</b></td><td><b class="edge-badge ok">정상</b></td><td>-</td></tr>
                    <tr><td><span class="radio"></span></td><td>JETSON-08</td><td>대전 톨링 B</td><td><b class="edge-badge ok">Alive</b></td><td><b class="edge-badge ok">Running</b></td><td>CAM-01 (L1)</td><td>29.7</td><td>19.2</td><td>43.1</td><td>5</td><td><b class="edge-badge warn">LTE</b></td><td><b class="edge-badge ok">정상</b></td><td>-</td></tr>
                    <tr><td><span class="radio"></span></td><td>JETSON-34</td><td>부산 톨링 A</td><td><b class="edge-badge ok">Alive</b></td><td><b class="edge-badge idle">Idle</b></td><td>-</td><td>0.0</td><td>0.0</td><td>0.0</td><td>0</td><td><b class="edge-badge path">LAN</b></td><td><b class="edge-badge ok">정상</b></td><td>-</td></tr>
                    <tr><td><span class="radio"></span></td><td>JETSON-15</td><td>광주 톨링 A</td><td><b class="edge-badge ok">Alive</b></td><td><b class="edge-badge ok">Running</b></td><td>CAM-01 (L1)</td><td>29.4</td><td>20.1</td><td>44.5</td><td>14</td><td><b class="edge-badge path">LAN</b></td><td><b class="edge-badge ok">정상</b></td><td>-</td></tr>
                    <tr><td><span class="radio"></span></td><td>JETSON-05</td><td>대구 톨링 A</td><td><b class="edge-badge ok">Alive</b></td><td><b class="edge-badge ok">Running</b></td><td>CAM-02 (L2)</td><td>29.5</td><td>18.5</td><td>40.7</td><td>6</td><td><b class="edge-badge warn">LTE</b></td><td><b class="edge-badge ok">정상</b></td><td>-</td></tr>
                    <tr><td><span class="radio"></span></td><td>JETSON-19</td><td>인천 톨링 A</td><td><b class="edge-badge ok">Alive</b></td><td><b class="edge-badge ok">Running</b></td><td>CAM-01 (L1)</td><td>29.3</td><td>19.0</td><td>41.8</td><td>9</td><td><b class="edge-badge path">LAN</b></td><td><b class="edge-badge warn">10분</b></td><td>-</td></tr>
                    <tr><td><span class="radio"></span></td><td>JETSON-21</td><td>울산 톨링 A</td><td><b class="edge-badge ok">Alive</b></td><td><b class="edge-badge idle">Idle</b></td><td>-</td><td>0.0</td><td>0.0</td><td>0.0</td><td>0</td><td><b class="edge-badge path">LAN</b></td><td><b class="edge-badge ok">정상</b></td><td>-</td></tr>
                  </tbody>
                </table>
                <footer class="edge-pagination"><span>1-8 / 42대</span><div><button>‹</button><button class="active">1</button><button>2</button><button>3</button><button>4</button><button>5</button><button>…</button><button>6</button><button>›</button></div><button class="filter-toggle">8개씩 보기</button></footer>
              </article>

              <article class="edge-panel edge-detail-panel">
                <div class="edge-detail-head"><h3>JETSON-27 상세</h3><span>● Alive</span></div>
                <dl class="edge-detail-list">
                  <dt>지점</dt><dd>서울 톨링 A</dd><dt>차선</dt><dd>1번 레일 (L1)</dd><dt>입력</dt><dd>CAM-01</dd><dt>갱신</dt><dd>17:36:47 (2초 전)</dd><dt>상태</dt><dd class="ok">정상</dd>
                </dl>
                <h4>실시간 상태 요약</h4>
                <div class="edge-live-grid">
                  <div><i>▧</i><span>FPS 처리</span><strong>12.8M</strong></div><div><i>◉</i><span>Spool</span><strong>12건</strong></div><div><i>⌘</i><span>OCR Task</span><strong>582K</strong></div><div><i>⌁</i><span>Path</span><strong>LAN</strong></div><div><i>×</i><span>OCR Drop</span><strong class="danger">1.2K</strong></div><div><i>◷</i><span>Uptime</span><strong>12일 04:32</strong></div><div><i>＋</i><span>Sent</span><strong>-</strong></div><div><i>!</i><span>오류</span><strong>-</strong></div>
                </div>
                <section class="edge-recent-error"><h4>최근 오류 <small>(최근 1건)</small></h4><p>- 오류 없음 -</p></section>
                <footer class="edge-actions"><button>원격 재시작</button><button>로그 보기</button><button>상태 이력</button></footer>
              </article>
            </section>

            <section class="edge-chart-row">
              <article v-for="chart in edgeMetricCharts" :key="chart.title" class="edge-panel metric-chart">
                <h3>{{ chart.title }} <small>{{ chart.subtitle }}</small></h3>
                <b :class="{ warn: chart.warn }">{{ chart.status }}</b>
                <strong v-if="chart.total">{{ chart.total }}</strong>
                <ChartJsPanel type="line" :data="chart.data" :options="edgeCompactLineOptions" :height="128" />
                <span>{{ chart.label }}</span>
              </article>
            </section>
          </article>
</template>
