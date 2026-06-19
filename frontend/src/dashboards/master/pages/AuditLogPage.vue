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
          <article class="audit-log-page">
            <section class="audit-kpi-grid">
              <article class="audit-kpi"><i><img src="../../icons/admin/log_set.png" alt="금일 로그" /></i><div><span>금일 로그</span><strong>1,284</strong><small>전일 대비 ▲ 8.4%</small></div></article>
              <article class="audit-kpi ok"><i><img src="../../icons/admin/check_button.png" alt="성공 작업" /></i><div><span>성공 작업</span><strong>1,271</strong><small>성공률 98.9%</small></div></article>
              <article class="audit-kpi warn"><i><img src="../../icons/admin/role.png" alt="권한 변경" /></i><div><span>권한 변경</span><strong>34</strong><small>최근 24시간</small></div></article>
              <article class="audit-kpi info"><i><img src="../../icons/admin/search.png" alt="대리조회" /></i><div><span>대리조회</span><strong>18</strong><small>감사 대상 3건</small></div></article>
              <article class="audit-kpi danger"><i><img src="../../icons/admin/reject.png" alt="실패/거부" /></i><div><span>실패/거부</span><strong>13</strong><small>접근 거부 5건</small></div></article>
              <article class="audit-kpi"><i><img src="../../icons/admin/export.png" alt="내보내기" /></i><div><span>내보내기</span><strong>7</strong><small>CSV/PDF 생성</small></div></article>
            </section>

            <section class="audit-filter-row">
              <button class="filter-toggle" type="button">기간: 오늘</button>
              <button class="filter-toggle" type="button">작업 유형 전체</button>
              <button class="filter-toggle" type="button">작업자 전체</button>
              <button class="filter-toggle" type="button">결과 전체</button>
              <label><input type="search" placeholder="작업 내용, 대상, IP 검색" /><span>⌕</span></label>
              <button class="primary" type="button" @click="exportAuditLog">내보내기</button>
            </section>

            <section class="audit-main-grid">
              <article class="audit-panel audit-list-panel">
                <div class="audit-panel-head"><h3>감사 로그 목록 <small>최근 작업 이력</small></h3><button type="button">보존 정책 보기</button></div>
                <table class="audit-log-table">
                  <thead><tr><th></th><th>발생시각</th><th>작업자</th><th>대상</th><th>작업 유형</th><th>작업 내용</th><th>IP</th><th>결과</th></tr></thead>
                  <tbody>
                    <tr class="selected"><td><span class="radio on"></span></td><td>2026-05-12 10:24:11</td><td>admin</td><td>하이패스 서울(주)</td><td><b class="audit-badge info">권한</b></td><td>센터 관제 대시보드 접근 권한 수정</td><td>192.168.0.89</td><td><b class="audit-badge ok">성공</b></td></tr>
                    <tr><td><span class="radio"></span></td><td>2026-05-12 10:18:02</td><td>admin</td><td>수원 톨게이트</td><td><b class="audit-badge info">지점</b></td><td>대시보드 연결 지점 변경</td><td>192.168.0.89</td><td><b class="audit-badge ok">성공</b></td></tr>
                    <tr><td><span class="radio"></span></td><td>2026-05-12 09:42:33</td><td>operator02</td><td>대전 톨게이트 B</td><td><b class="audit-badge warn">대리조회</b></td><td>장애 원인 확인 목적 대리조회</td><td>192.168.0.121</td><td><b class="audit-badge ok">성공</b></td></tr>
                    <tr><td><span class="radio"></span></td><td>2026-05-12 09:21:08</td><td>admin</td><td>GPS 영역</td><td><b class="audit-badge info">설정</b></td><td>서울 톨링 A GPS 결제 영역 수정</td><td>192.168.0.89</td><td><b class="audit-badge ok">성공</b></td></tr>
                    <tr><td><span class="radio"></span></td><td>2026-05-12 08:55:41</td><td>operator01</td><td>Backend / DB</td><td><b class="audit-badge danger">접근</b></td><td>권한 없는 DB 상세 모니터링 접근</td><td>192.168.0.144</td><td><b class="audit-badge danger">거부</b></td></tr>
                    <tr><td><span class="radio"></span></td><td>2026-05-12 08:31:19</td><td>system</td><td>시스템 공지</td><td><b class="audit-badge info">공지</b></td><td>점검 공지 자동 게시</td><td>internal</td><td><b class="audit-badge ok">성공</b></td></tr>
                    <tr><td><span class="radio"></span></td><td>2026-05-12 08:12:04</td><td>admin</td><td>JETSON-27</td><td><b class="audit-badge warn">장비</b></td><td>원격 재시작 요청</td><td>192.168.0.89</td><td><b class="audit-badge ok">성공</b></td></tr>
                  </tbody>
                </table>
                <footer class="audit-pagination"><span>전체 12,568건</span><div><button>‹</button><button class="active">1</button><button>2</button><button>3</button><button>…</button><button>126</button><button>›</button></div><button>10개씩 보기</button></footer>
              </article>

              <aside class="audit-side-stack">
                <article class="audit-panel audit-detail-card">
                  <div class="audit-panel-head"><h3>선택 로그 상세</h3><span class="audit-badge ok">성공</span></div>
                  <dl>
                    <dt>로그 ID</dt><dd>AUD-20260512-102411-001</dd>
                    <dt>작업자</dt><dd>admin <small>최종 관리자</small></dd>
                    <dt>작업 대상</dt><dd>하이패스 서울(주)</dd>
                    <dt>작업 유형</dt><dd>권한 변경</dd>
                    <dt>요청 IP</dt><dd>192.168.0.89</dd>
                    <dt>User Agent</dt><dd>Chrome / Windows</dd>
                    <dt>처리 결과</dt><dd><span class="audit-badge ok">성공</span></dd>
                  </dl>
                </article>

                <article class="audit-panel audit-risk-card">
                  <h3>감사 위험도 분석</h3>
                  <div class="audit-risk-score"><strong>LOW</strong><span>정상 범위 작업</span></div>
                  <ul>
                    <li><span>비정상 시간대 접근</span><b>없음</b></li>
                    <li><span>권한 상승 시도</span><b>없음</b></li>
                    <li><span>반복 실패</span><b>없음</b></li>
                    <li><span>대리조회 사유</span><b class="warn">검토 3건</b></li>
                  </ul>
                </article>
              </aside>
            </section>

            <section class="audit-bottom-grid">
              <article class="audit-panel audit-type-card">
                <h3>작업 유형 분포</h3>
                <div class="audit-bars">
                  <span><b style="width:82%"></b><em>권한/계정 524</em></span>
                  <span><b style="width:64%"></b><em>지점/장비 408</em></span>
                  <span><b style="width:38%"></b><em>대리조회 218</em></span>
                  <span><b style="width:24%"></b><em>접근 거부 13</em></span>
                </div>
              </article>
              <article class="audit-panel audit-recent-card">
                <div class="audit-panel-head"><h3>최근 대리조회 감사</h3><button type="button">전체 감사 로그 보기</button></div>
                <table class="audit-log-table compact">
                  <thead><tr><th>시간</th><th>관리자</th><th>센터 / 지점</th><th>사유</th><th>결과</th></tr></thead>
                  <tbody>
                    <tr v-for="audit in recentAuditRows" :key="`${audit.time}-${audit.target}`"><td>{{ audit.time }}</td><td>{{ audit.actor }}</td><td>{{ audit.target }}</td><td>{{ audit.reason }}</td><td><span class="audit-badge ok">{{ audit.result }}</span></td></tr>
                  </tbody>
                </table>
              </article>
            </section>
          </article>
</template>
