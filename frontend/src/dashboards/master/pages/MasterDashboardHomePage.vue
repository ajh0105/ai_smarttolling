<script setup>
import { inject } from 'vue'
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
</script>

<template>
<section ref="kpiSection" class="kpi-grid master-kpi-grid">
          <article class="kpi glass">
            <span class="kpi-icon"><img src="../../icons/admin/people2.png" alt="전체 센터" style="width:28px;height:28px;object-fit:contain;" /></span>
            <div>
              <p>전체 센터</p>
              <strong>24</strong>
              <em>전일 대비 ▲ 2</em>
            </div>
          </article>
          <article class="kpi glass">
            <span class="kpi-icon"><img src="../../icons/admin/map2.png" alt="전체 지점" style="width:28px;height:28px;object-fit:contain;" /></span>
            <div>
              <p>전체 지점</p>
              <strong>38</strong>
              <em>전일 대비 ▲ 1</em>
            </div>
          </article>
          <article class="kpi glass">
            <span class="kpi-icon"><img src="../../icons/admin/check_button.png" alt="정상 지점" style="width:28px;height:28px;object-fit:contain;" /></span>
            <div>
              <p>정상 지점</p>
              <strong>32</strong>
              <em>정상 비율 84.2%</em>
            </div>
          </article>
          <article class="kpi glass">
            <span class="kpi-icon"><img src="../../icons/admin/monitor.png" alt="시스템 상태" style="width:28px;height:28px;object-fit:contain;" /></span>
            <div>
              <p>시스템 상태</p>
              <strong>정상</strong>
              <em>모든 시스템 정상 운영 중</em>
            </div>
          </article>
          <article class="kpi glass">
            <span class="kpi-icon"><img src="../../icons/admin/caution.png" alt="주의/장애" style="width:28px;height:28px;object-fit:contain;" /></span>
            <div>
              <p>주의/장애</p>
              <strong>6</strong>
              <em class="down">주의 4 / 장애 2</em>
            </div>
          </article>
        </section>

        <section class="dashboard-grid master-overview-grid">
          <article ref="mapSection" class="map-panel glass">
            <div class="panel-title map-title">
              <h2>전국 지점 위치 현황</h2>
              <div class="map-actions">
                <button
                  class="map-edit-btn"
                  :class="{ active: isMapEditMode }"
                  type="button"
                  @click="toggleMapEditMode"
                >
                  {{ isMapEditMode ? '편집 완료' : '위치 편집' }}
                </button>
                <button
                  v-if="isMapEditMode"
                  class="map-edit-btn ghost save"
                  type="button"
                  :disabled="!mapPositionDirty"
                  @click="saveMapMarkerPositions"
                >
                  변경사항 저장
                </button>
                <button
                  v-if="isMapEditMode"
                  class="map-edit-btn ghost"
                  type="button"
                  @click="resetMapEditPositions"
                >
                  위치 초기화
                </button>
                <button
                  v-if="isMapEditMode"
                  class="map-edit-btn ghost danger"
                  type="button"
                  @click="cancelMapEditMode"
                >
                  편집 취소
                </button>
                <span v-if="isMapEditMode" class="active">편집 모드</span>
              </div>
            </div>

            <div
              ref="mapStage"
              class="map-stage"
              :class="{ editing: isMapEditMode }"
              @pointermove="updateMarkerPosition"
              @pointerup="stopMarkerDrag"
              @pointerleave="stopMarkerDrag"
            >
              <div class="state-summary">
                <div><i class="dot ok"></i><span>정상</span><strong>28</strong></div>
                <div><i class="dot caution"></i><span>주의</span><strong>7</strong></div>
                <div><i class="dot danger"></i><span>점검중</span><strong>3</strong></div>
              </div>

              <div class="map-zoom">
                <button type="button" @click="zoomMap('reset')">◎</button>
                <button type="button" @click="zoomMap('in')">＋</button>
                <button type="button" @click="zoomMap('out')">－</button>
              </div>

              <div
                v-for="center in centers"
                :key="center.name"
                class="map-marker"
                :class="[
                  statusClass(center.status),
                  {
                    selected: selectedCenter === center.name,
                    dragging: draggingCenterName === center.name,
                    labelDragging: draggingLabelName === center.name
                  }
                ]"
                :style="{ left: `${center.x}%`, top: `${center.y}%` }"
              >
                <button
                  class="pin"
                  type="button"
                  :title="isMapEditMode ? '포인터 위치 이동' : center.name"
                  @click="toggleCenterDetail(center)"
                  @pointerdown="startMarkerDrag($event, center)"
                ></button>
                <button
                  class="marker-label"
                  type="button"
                  :style="{ transform: `translate(${center.labelX}px, ${center.labelY}px)` }"
                  :title="isMapEditMode ? '명칭 박스 위치 이동' : center.name"
                  @click="toggleCenterDetail(center)"
                  @pointerdown="startLabelDrag($event, center)"
                >
                  <b>{{ center.name }}</b>
                  <small>{{ statusText(center.status) }}</small>
                </button>
              </div>

            </div>
          </article>

          <aside class="right-column">
            <article class="panel glass dashboard-pipeline-panel">
              <div class="panel-title">
                <h2>시스템 파이프라인 상태</h2>
              </div>

              <table class="dashboard-pipeline-table">
                <tbody>
                  <tr>
                    <td rowspan="3" class="pipeline-table-icon"><img src="../../icons/admin/cpu.png" alt="Edge" /></td>
                    <td rowspan="3" class="pipeline-table-name">Edge</td>
                    <td>FPS</td>
                    <td>29.8</td>
                    <td class="pipeline-spark"><ChartJsPanel type="line" :data="dashboardSparklines[0]" :options="sparklineOptions" :height="22" /></td>
                    <td rowspan="3" class="pipeline-table-state"><b>정상 장비 : 78</b><span>State 5</span></td>
                  </tr>
                  <tr>
                    <td>Spool</td>
                    <td>12</td>
                    <td class="pipeline-spark"><ChartJsPanel type="line" :data="dashboardSparklines[1]" :options="sparklineOptions" :height="22" /></td>
                  </tr>
                  <tr>
                    <td>Active Path</td>
                    <td>LAN</td>
                    <td class="pipeline-spark"><ChartJsPanel type="line" :data="dashboardSparklines[2]" :options="sparklineOptions" :height="22" /></td>
                  </tr>
                  <tr>
                    <td rowspan="3" class="pipeline-table-icon"><img src="../../icons/admin/ingress.png" alt="Ingress" /></td>
                    <td rowspan="3" class="pipeline-table-name">Ingress</td>
                    <td>수신 이벤트</td>
                    <td>128,456건</td>
                    <td class="pipeline-spark"><ChartJsPanel type="line" :data="dashboardSparklines[3]" :options="sparklineOptions" :height="22" /></td>
                    <td rowspan="3" class="pipeline-table-state"><b>연결 상태 정상</b><span>RETRY 1,208</span></td>
                  </tr>
                  <tr>
                    <td>ACK</td>
                    <td>126,842건</td>
                    <td class="pipeline-spark"><ChartJsPanel type="line" :data="dashboardSparklines[4]" :options="sparklineOptions" :height="22" /></td>
                  </tr>
                  <tr>
                    <td>RETRY</td>
                    <td>1,208건</td>
                    <td class="pipeline-spark warn"><ChartJsPanel type="line" :data="dashboardSparklines[5]" :options="sparklineOptions" :height="22" /></td>
                  </tr>
                  <tr>
                    <td rowspan="3" class="pipeline-table-icon"><img src="../../icons/admin/be_db_set.png" alt="Backend" /></td>
                    <td rowspan="3" class="pipeline-table-name">Backend</td>
                    <td>Validation 실패</td>
                    <td>0.18%</td>
                    <td class="pipeline-spark warn"><ChartJsPanel type="line" :data="dashboardSparklines[6]" :options="sparklineOptions" :height="22" /></td>
                    <td rowspan="3" class="pipeline-table-state"><b>API 상태 정상</b><span>p95 128ms</span></td>
                  </tr>
                  <tr>
                    <td>Duplicate 차단</td>
                    <td>1,284건</td>
                    <td class="pipeline-spark"><ChartJsPanel type="line" :data="dashboardSparklines[7]" :options="sparklineOptions" :height="22" /></td>
                  </tr>
                  <tr>
                    <td>API Latency</td>
                    <td>128 ms</td>
                    <td class="pipeline-spark"><ChartJsPanel type="line" :data="dashboardSparklines[8]" :options="sparklineOptions" :height="22" /></td>
                  </tr>
                  <tr>
                    <td rowspan="3" class="pipeline-table-icon"><img src="../../icons/admin/db.png" alt="DB" /></td>
                    <td rowspan="3" class="pipeline-table-name">DB</td>
                    <td>Write TPS</td>
                    <td>412</td>
                    <td class="pipeline-spark"><ChartJsPanel type="line" :data="dashboardSparklines[9]" :options="sparklineOptions" :height="22" /></td>
                    <td rowspan="3" class="pipeline-table-state"><b>복제 지연 0.2초</b><span>Backup 정상</span></td>
                  </tr>
                  <tr>
                    <td>Read TPS</td>
                    <td>256</td>
                    <td class="pipeline-spark"><ChartJsPanel type="line" :data="dashboardSparklines[10]" :options="sparklineOptions" :height="22" /></td>
                  </tr>
                  <tr>
                    <td>Backup 상태</td>
                    <td>정상</td>
                    <td class="pipeline-spark"><ChartJsPanel type="line" :data="dashboardSparklines[11]" :options="sparklineOptions" :height="22" /></td>
                  </tr>
                  <tr>
                    <td rowspan="3" class="pipeline-table-icon"><img src="../../icons/admin/api.png" alt="API" /></td>
                    <td rowspan="3" class="pipeline-table-name">API</td>
                    <td>응답 시간</td>
                    <td>145 ms</td>
                    <td class="pipeline-spark"><ChartJsPanel type="line" :data="dashboardSparklines[12]" :options="sparklineOptions" :height="22" /></td>
                    <td rowspan="3" class="pipeline-table-state"><b>API 상태 정상</b><span>1,284 rpm</span></td>
                  </tr>
                  <tr>
                    <td>오류율</td>
                    <td>0.02%</td>
                    <td class="pipeline-spark warn"><ChartJsPanel type="line" :data="dashboardSparklines[13]" :options="sparklineOptions" :height="22" /></td>
                  </tr>
                  <tr>
                    <td>요청 처리량</td>
                    <td>1,284 rpm</td>
                    <td class="pipeline-spark"><ChartJsPanel type="line" :data="dashboardSparklines[14]" :options="sparklineOptions" :height="22" /></td>
                  </tr>
                </tbody>
              </table>
            </article>
          </aside>

          <section ref="companySection" class="dashboard-lower-panels">
            <article ref="noticeSection" class="panel glass notice-panel">
              <div class="panel-title with-button">
                <h2>시스템 공지사항</h2>
                <button class="text-btn" type="button" @click="showMoreNotices">전체 보기 ›</button>
              </div>
              <ul class="notice-list">
                <li v-for="notice in notices" :key="notice.title">
                  <span>{{ notice.title }}</span>
                  <time>{{ notice.date }}</time>
                </li>
              </ul>
            </article>

            <article class="panel glass alert-panel">
              <div class="panel-title with-button">
                <h2>최근 장애 알림</h2>
                <button class="text-btn" type="button" @click="activateMenu('장애 알림')">전체 보기 ›</button>
              </div>
              <ul class="alert-list">
                <li v-for="alert in recentAlerts" :key="`${alert.time}-${alert.title}`">
                  <b :class="alert.tone">{{ alert.level }}</b>
                  <span>{{ alert.title }}</span>
                  <time>{{ alert.time }}</time>
                </li>
              </ul>
            </article>
          </section>
        </section>
</template>
