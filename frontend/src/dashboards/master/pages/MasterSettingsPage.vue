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
          <article class="settings-page">
            <section class="settings-kpi-grid">
              <article class="settings-kpi ok"><i><img src="../../icons/admin/dashboard.png" alt="콘솔 상태" /></i><div><span>콘솔 상태</span><strong>정상</strong><small>최근 저장 10:24</small></div></article>
              <article class="settings-kpi"><i><img src="../../icons/admin/retry.png" alt="자동 새로고침" /></i><div><span>자동 새로고침</span><strong>30초</strong><small>전체 대시보드 적용</small></div></article>
              <article class="settings-kpi warn"><i><img src="../../icons/admin/protection.png" alt="보안 정책" /></i><div><span>보안 정책</span><strong>강화</strong><small>2FA 권장 3건</small></div></article>
              <article class="settings-kpi"><i><img src="../../icons/admin/log_set.png" alt="로그 보존" /></i><div><span>로그 보존</span><strong>365일</strong><small>감사 로그 기준</small></div></article>
              <article class="settings-kpi ok"><i><img src="../../icons/admin/backup.png" alt="백업 정책" /></i><div><span>백업 정책</span><strong>활성</strong><small>매일 02:00</small></div></article>
              <article class="settings-kpi"><i><img src="../../icons/admin/night-mode.png" alt="기본 테마" /></i><div><span>기본 테마</span><strong>{{ isLightMode ? 'Light' : 'Dark' }}</strong><small>사용자별 저장</small></div></article>
            </section>

            <section class="settings-main-grid">
              <article class="settings-panel settings-form-panel">
                <div class="settings-panel-head">
                  <h3>관리자 콘솔 기본 설정</h3>
                  <button type="button" @click="saveMasterSettings">변경사항 저장</button>
                </div>
                <div class="settings-form-grid">
                  <label><span>기본 지도 편집</span><select><option>허용</option><option>승인 후 허용</option><option>차단</option></select><small>지점 마커 위치와 라벨 편집 권한</small></label>
                  <label><span>대시보드 자동 새로고침</span><select><option>30초</option><option>1분</option><option>5분</option><option>수동</option></select><small>관리자/관제 대시보드 데이터 갱신 주기</small></label>
                  <label><span>공지 노출 범위</span><select><option>전체 센터</option><option>선택 센터</option><option>내부 관리자만</option></select><small>시스템 공지와 점검 공지 기본 대상</small></label>
                  <label><span>기본 진입 대시보드</span><select><option>관리자 대시보드</option><option>시스템 관제</option><option>장애 알림</option></select><small>로그인 후 최초 진입 화면</small></label>
                  <label><span>위험 알림 기준</span><select><option>치명 즉시 알림</option><option>경고 이상 알림</option><option>전체 알림</option></select><small>상단 알림 버튼과 장애 알림 집계 기준</small></label>
                  <label><span>라이트/다크 모드</span><select :value="themeMode" @change="themeMode = $event.target.value"><option value="dark">Dark</option><option value="light">Light</option></select><small>현재 브라우저에 저장되는 UI 테마</small></label>
                </div>
              </article>

              <aside class="settings-side-stack">
                <article class="settings-panel settings-security">
                  <h3>보안 및 접근 정책</h3>
                  <ul>
                    <li><span>관리자 세션 만료</span><b>60분</b><em>활성</em></li>
                    <li><span>비밀번호 정책</span><b>12자 이상</b><em>강화</em></li>
                    <li><span>IP 접근 제한</span><b>사내망 우선</b><em class="warn">검토</em></li>
                    <li><span>권한 변경 승인</span><b>MASTER 승인</b><em>활성</em></li>
                  </ul>
                </article>

                <article class="settings-panel settings-backup">
                  <h3>백업 / 보존 정책</h3>
                  <dl>
                    <dt>DB 백업</dt><dd>매일 02:00</dd>
                    <dt>감사 로그 보존</dt><dd>365일</dd>
                    <dt>장애 알림 보존</dt><dd>180일</dd>
                    <dt>최근 백업</dt><dd>2026-05-12 02:00</dd>
                  </dl>
                </article>
              </aside>
            </section>

            <section class="settings-bottom-grid">
              <article class="settings-panel settings-notification">
                <h3>알림 채널 설정</h3>
                <div>
                  <span><b>시스템 상태</b><em>콘솔 배너 / 이메일</em><strong>ON</strong></span>
                  <span><b>장애 알림</b><em>브라우저 / Slack / 이메일</em><strong>ON</strong></span>
                  <span><b>권한 변경</b><em>감사 로그 / 이메일</em><strong>ON</strong></span>
                  <span><b>백업 실패</b><em>콘솔 배너 / SMS</em><strong>ON</strong></span>
                </div>
              </article>

              <article class="settings-panel settings-history">
                <div class="settings-panel-head"><h3>최근 설정 변경 이력</h3><button type="button">전체 보기</button></div>
                <table class="settings-table">
                  <thead><tr><th>시간</th><th>관리자</th><th>항목</th><th>이전 값</th><th>변경 값</th><th>결과</th></tr></thead>
                  <tbody>
                    <tr><td>10:24:11</td><td>admin</td><td>자동 새로고침</td><td>1분</td><td>30초</td><td><span class="settings-badge ok">성공</span></td></tr>
                    <tr><td>09:52:44</td><td>admin</td><td>공지 노출 범위</td><td>선택 센터</td><td>전체 센터</td><td><span class="settings-badge ok">성공</span></td></tr>
                    <tr><td>09:20:31</td><td>operator02</td><td>지도 편집</td><td>허용</td><td>승인 후 허용</td><td><span class="settings-badge warn">검토</span></td></tr>
                    <tr><td>08:48:10</td><td>system</td><td>백업 정책</td><td>대기</td><td>활성</td><td><span class="settings-badge ok">성공</span></td></tr>
                  </tbody>
                </table>
              </article>
            </section>
          </article>
</template>
