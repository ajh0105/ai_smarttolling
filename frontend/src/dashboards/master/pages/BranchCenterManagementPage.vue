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
          <article class="branch-page">
            <section class="branch-filter-row">
              <label><span>센터</span><button class="filter-toggle" type="button">전체 센터</button></label>
              <label><span>지역</span><button class="filter-toggle" type="button">전체 지역</button></label>
              <label><span>상태</span><button class="filter-toggle" type="button">전체 상태</button></label>
              <label class="branch-search"><input type="search" placeholder="지점명 또는 주소 검색" /><span>⌕</span></label>
              <button class="branch-reset" type="button">필터 초기화</button>
            </section>

            <section class="branch-main-grid">
              <article class="branch-panel branch-map-panel">
                <div class="branch-panel-head">
                  <h3>전국 지점 위치</h3>
                  <div class="branch-map-actions">
                    <button class="primary" type="button">위치 편집</button>
                    <button class="success" type="button">GPS 영역 설정</button>
                    <button type="button">초기화</button>
                  </div>
                </div>
                <div class="branch-map-stage">
                  <div class="branch-map-legend top"><span class="ok">정상 28</span><span class="warn">점검중 3</span><span class="info">장애 2</span></div>
                  <div class="branch-zoom"><button>◎</button><button>＋</button><button>－</button></div>
                  <span class="branch-map-marker ok" style="left:38%;top:32%"><i></i><b>서울 톨게이트</b></span>
                  <span class="branch-map-marker ok" style="left:37%;top:43%"><i></i><b>수원 톨게이트</b></span>
                  <span class="branch-map-marker warn" style="left:47%;top:63%"><i></i><b>대전 톨게이트</b></span>
                  <span class="branch-map-marker danger" style="left:66%;top:59%"><i></i><b>대구 톨게이트</b></span>
                  <span class="branch-map-marker ok" style="left:73%;top:73%"><i></i><b>부산 톨게이트</b></span>
                  <span class="branch-map-marker ok" style="left:34%;top:75%"><i></i><b>광주 톨게이트</b></span>
                  <span class="branch-map-marker ok" style="left:59%;top:24%"><i></i><b>강릉 톨게이트</b></span>
                  <span class="branch-map-marker ok" style="left:47%;top:91%"><i></i><b>제주 톨게이트</b></span>
                  <div class="branch-map-legend bottom"><span class="ok">정상</span><span class="warn">주의</span><span class="caution">점검중</span><span class="danger">장애</span></div>
                </div>
              </article>

              <aside class="branch-side-stack">
                <article class="branch-panel branch-detail">
                  <div class="branch-panel-head"><h3>선택 지점 상세 정보</h3><span class="branch-badge ok">정상</span></div>
                  <dl>
                    <dt>지점명</dt><dd>서울 톨링 A · 제어센터</dd>
                    <dt>담당 센터</dt><dd>서울고속도로(주)</dd>
                    <dt>주소</dt><dd>경기도 성남시 분당구 판교로 255번길 9</dd>
                    <dt>지점 유형</dt><dd>제어센터 / 톨링 게이트</dd>
                    <dt>Edge 장비</dt><dd>3대 <code>wss://seoul-a-ingress.hi-five.kr</code></dd>
                    <dt>운영 상태</dt><dd><span class="branch-badge ok">정상 운영</span></dd>
                    <dt>마지막 변경자</dt><dd><b>admin</b> <small>(2025-05-11 17:33:12)</small></dd>
                  </dl>
                </article>

                <article class="branch-panel gps-zone-card">
                  <div class="branch-panel-head"><h3>GPS 결제 영역 설정 <small>(직사각형 영역)</small></h3><button class="primary">편집</button></div>
                  <div class="gps-zone-body">
                    <div class="gps-zone-map"><span>N</span><span>W</span><span>E</span><span>S</span><i></i><b></b></div>
                    <dl>
                      <dt>중심 좌표 (LAT, LNG)</dt><dd>37.4910401, 126.7251320</dd>
                      <dt>영역 폭</dt><dd>120.0 m</dd>
                      <dt>영역 높이</dt><dd>80.0 m</dd>
                      <dt>방향 기준</dt><dd>도로 진행 방향 기준</dd>
                      <dt>IN/OUT 기준</dt><dd>IN: 진입, OUT: 출구</dd>
                    </dl>
                  </div>
                </article>

                <article class="branch-panel branch-devices">
                  <h3>연결 장비 목록 (3)</h3>
                  <table class="branch-table">
                    <thead><tr><th>Jetson ID</th><th>카메라 레일</th><th>Active Path</th><th>상태</th><th>Stale</th></tr></thead>
                    <tbody>
                      <tr><td>JETSON-27</td><td>1번 레일 (IN)</td><td>LAN</td><td><span class="branch-badge ok">정상</span></td><td>5초 전</td></tr>
                      <tr><td>JETSON-28</td><td>2번 레일 (OUT)</td><td>LAN</td><td><span class="branch-badge ok">정상</span></td><td>8초 전</td></tr>
                      <tr><td>JETSON-29</td><td>후면 레일 (OUT)</td><td>LTE (백업)</td><td><span class="branch-badge warn">주의</span></td><td>28초 전</td></tr>
                    </tbody>
                  </table>
                </article>
              </aside>
            </section>

            <article class="branch-panel branch-history">
              <h3>지점/설정 변경 이력 <small>(최근 6건)</small></h3>
              <table class="branch-table">
                <thead><tr><th>변경시각</th><th>관리자</th><th>변경 항목</th><th>이전 값</th><th>변경 값</th><th>사유</th></tr></thead>
                <tbody>
                  <tr><td>2025-05-11 17:33:12</td><td>admin</td><td>지점 위치 (LAT, LNG)</td><td>37.4910322, 126.7251241</td><td>37.4910401, 126.7251320</td><td>정밀 위치 보정</td></tr>
                  <tr><td>2025-05-11 16:15:47</td><td>admin</td><td>GPS 영역 폭</td><td>110.0 m</td><td>120.0 m</td><td>진입 차로 확인 반영</td></tr>
                  <tr><td>2025-05-10 14:22:05</td><td>operator02</td><td>Edge 장비 추가</td><td>2대</td><td>3대 (JETSON-29 추가)</td><td>후면 레일 장비 설치</td></tr>
                  <tr><td>2025-05-09 11:08:33</td><td>admin</td><td>Ingress Endpoint</td><td>wss://old-ingress.hi-five.kr</td><td>wss://seoul-a-ingress.hi-five.kr</td><td>Ingress 주소 변경</td></tr>
                  <tr><td>2025-05-08 15:44:12</td><td>admin</td><td>GPS 영역 높이</td><td>70.0 m</td><td>80.0 m</td><td>출구 차로 포지션 조정</td></tr>
                  <tr><td>2025-05-07 09:17:28</td><td>operator01</td><td>지점 상태 변경</td><td>주의</td><td>정상</td><td>장애 조치 완료</td></tr>
                </tbody>
              </table>
              <button type="button">전체 변경 이력 보기 →</button>
            </article>
          </article>
</template>
