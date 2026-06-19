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

const incidentTimelineDisplayOptions = computed(() => {
  const gridColor = isLightMode.value ? '#c1d1e3' : chartGridColor

  return {
    ...incidentTimelineOptions,
    scales: {
      x: {
        ...incidentTimelineOptions.scales.x,
        grid: { ...incidentTimelineOptions.scales.x.grid, color: gridColor, lineWidth: 1 }
      },
      y: {
        ...incidentTimelineOptions.scales.y,
        grid: { ...incidentTimelineOptions.scales.y.grid, color: gridColor, lineWidth: 1 }
      }
    }
  }
})
</script>

<template>
          <article class="incident-page">
            <section class="incident-kpi-grid">
              <article class="incident-kpi critical"><i><img src="../../icons/admin/danger.png" alt="치명" /></i><div><span>치명</span><strong>7 <em>건</em></strong><small>전일 대비 ▲ 2</small></div></article>
              <article class="incident-kpi warn"><i><img src="../../icons/admin/caution3.png" alt="경고" /></i><div><span>경고</span><strong>18 <em>건</em></strong><small>전일 대비 ▼ 4</small></div></article>
              <article class="incident-kpi info"><i><img src="../../icons/admin/information.png" alt="정보" /></i><div><span>정보</span><strong>34 <em>건</em></strong><small>전일 대비 ▲ 6</small></div></article>
              <article class="incident-kpi action"><i><img src="../../icons/admin/repair.png" alt="조치중" /></i><div><span>조치중</span><strong>9 <em>건</em></strong><small>전일 대비 ▲ 1</small></div></article>
              <article class="incident-kpi done"><i><img src="../../icons/admin/check_button.png" alt="완료" /></i><div><span>완료 (금일)</span><strong>52 <em>건</em></strong><small>전일 대비 ▲ 8</small></div></article>
              <article class="incident-kpi time"><i><img src="../../icons/admin/latency.png" alt="평균 처리시간" /></i><div><span>평균 처리시간</span><strong>1시간 32분</strong><small>전일 1시간 26분 ▲ 6분</small></div></article>
            </section>

            <section class="incident-main-grid">
              <article class="incident-panel incident-list-panel">
                <div class="incident-filter-row">
                  <label><span>심각도</span><button class="filter-toggle" type="button">전체</button></label>
                  <label><span>구성요소</span><button class="filter-toggle" type="button">전체</button></label>
                  <label><span>지점</span><button class="filter-toggle" type="button">전체</button></label>
                  <label><span>상태</span><button class="filter-toggle" type="button">전체</button></label>
                  <div class="incident-search"><input type="search" placeholder="알림명 검색" /><span>⌕</span></div>
                  <button class="incident-icon-btn" type="button">▤</button>
                </div>
                <table class="incident-table">
                  <thead><tr><th></th><th>발생시각</th><th>구성요소</th><th>지점/서비스</th><th>심각도</th><th>알림명</th><th>영향</th><th>상태</th></tr></thead>
                  <tbody>
                    <tr class="selected"><td><span class="radio on"></span></td><td>17:35:21</td><td>Backend</td><td>서울 통합센터</td><td><b class="incident-badge critical">치명</b></td><td>Backend Validation 실패율 증가</td><td>결제 지연 발생</td><td><b class="incident-badge action">조치중</b></td></tr>
                    <tr><td><span class="radio"></span></td><td>17:34:48</td><td>Edge (Jetson)</td><td>대전 톨링 B</td><td><b class="incident-badge critical">치명</b></td><td>Jetson 상태 Stale</td><td>통행 이벤트 지연</td><td><b class="incident-badge wait">대기</b></td></tr>
                    <tr><td><span class="radio"></span></td><td>17:32:15</td><td>Ingress</td><td>수원 통합 A</td><td><b class="incident-badge warn">경고</b></td><td>Malformed Payload 증가</td><td>일부 이벤트 폐기</td><td><b class="incident-badge wait">대기</b></td></tr>
                    <tr><td><span class="radio"></span></td><td>17:30:02</td><td>Ingress</td><td>광주 통합 E</td><td><b class="incident-badge warn">경고</b></td><td>Retry/Reject 비율 상승</td><td>전달 실패 증가</td><td><b class="incident-badge wait">대기</b></td></tr>
                    <tr><td><span class="radio"></span></td><td>17:28:37</td><td>Edge (Jetson)</td><td>부산 톨링 D</td><td><b class="incident-badge warn">경고</b></td><td>Spool Count High (85%)</td><td>전송 지연 가능성</td><td><b class="incident-badge wait">대기</b></td></tr>
                    <tr><td><span class="radio"></span></td><td>17:26:41</td><td>Network</td><td>경강선 구간</td><td><b class="incident-badge warn">경고</b></td><td>LAN → LTE 경로 전환</td><td>대역폭 감소</td><td><b class="incident-badge info">정보</b></td></tr>
                    <tr><td><span class="radio"></span></td><td>17:24:10</td><td>DB (PostgreSQL)</td><td>통합 DB</td><td><b class="incident-badge warn">경고</b></td><td>Slow Query 감지</td><td>쿼리 처리 지연</td><td><b class="incident-badge wait">대기</b></td></tr>
                    <tr><td><span class="radio"></span></td><td>17:21:05</td><td>Backend</td><td>서울 통합센터</td><td><b class="incident-badge info">정보</b></td><td>API Latency (p95) 1.2s</td><td>응답 지연 증가</td><td><b class="incident-badge info">정보</b></td></tr>
                  </tbody>
                </table>
                <footer class="incident-pagination"><span>전체 342건</span><div><button>‹</button><button class="active">1</button><button>2</button><button>3</button><button>4</button><button>5</button><button>…</button><button>35</button><button>›</button></div><button class="filter-toggle">10개씩 보기</button></footer>
              </article>

              <article class="incident-panel incident-detail-panel">
                <div class="incident-detail-head"><h3>선택 알림 상세</h3><b class="incident-badge critical">치명</b></div>
                <h2><i><img src="../../icons/admin/caution3.png" alt="경고" /></i> Backend Validation 실패율 증가</h2>
                <dl class="incident-detail-grid">
                  <dt>구성요소</dt><dd>Backend (Spring Boot)</dd><dt>발생시각</dt><dd>2025-05-11 17:35:21</dd><dt>지점/서비스</dt><dd>서울 통합센터</dd><dt>상태 지속시간</dt><dd>12분 26초</dd>
                </dl>
                <h4>관련 지표 <small>(최근 10분)</small></h4>
                <div class="incident-related-grid">
                  <div><span>Validation 실패율</span><strong>5.6%</strong><small>임계값: 3%</small></div>
                  <div><span>Validation 오류 수</span><strong>1,248건</strong><small>임계값: 500</small></div>
                  <div><span>Duplicate 이벤트</span><strong>328건</strong><small>임계값: 200</small></div>
                  <div><span>API p95 Latency</span><strong>1.23 s</strong><small>임계값: 1.0 s</small></div>
                </div>
                <section class="incident-impact"><h4>운영 영향</h4><p><i>!</i> 검증 실패로 인한 결제 판정 지연 및 검수 대기 증가가 발생하고 있습니다.</p></section>
                <section class="incident-actions-text"><h4>권장 조치</h4><ol><li>Validation 규칙 및 판단 로직 확인</li><li>최근 배포 내역 및 설정 변경 사항 점검</li><li>관련 로그 분석 및 이상 데이터 샘플 확인</li></ol></section>
                <footer class="incident-actions"><button>확인</button><button>담당자 배정</button><button>유지보수 요청</button><button>완료 처리</button></footer>
              </article>
            </section>

            <section class="incident-bottom-grid">
              <article class="incident-panel incident-timeline">
                <h3>구성요소별 알림 타임라인</h3>
                <div class="timeline-legend"><span class="critical">치명</span><span class="warn">경고</span><span class="info">정보</span><span class="done">해제</span></div>
                <div class="timeline-chart">
                  <div class="timeline-labels"><span>Jetson Edge</span><span>Python Ingress</span><span>Backend (Spring)</span><span>PostgreSQL DB</span><span>Network</span></div>
                  <div class="timeline-board chart-timeline"><ChartJsPanel type="scatter" :data="incidentTimelineData" :options="incidentTimelineDisplayOptions" :height="200" /></div>
                </div>
              </article>

              <article class="incident-panel incident-history">
                <div class="incident-detail-head"><h3>최근 조치 내역</h3><button class="filter-toggle" type="button">전체 내역 보기</button></div>
                <table class="incident-history-table">
                  <thead><tr><th>시간</th><th>처리자</th><th>조치</th><th>메모</th></tr></thead>
                  <tbody>
                    <tr><td>17:35:32</td><td>admin</td><td>알림 확인</td><td><b class="incident-badge info">확인</b></td></tr>
                    <tr><td>17:35:48</td><td>admin</td><td>담당자 배정</td><td><b class="incident-badge action">조치중</b></td></tr>
                    <tr><td>17:32:01</td><td>system</td><td>알림 발생</td><td><b class="incident-badge critical">발생</b></td></tr>
                    <tr><td>17:30:12</td><td>admin</td><td>알림 확인</td><td><b class="incident-badge info">확인</b></td></tr>
                    <tr><td>17:29:45</td><td>admin</td><td>유지보수 요청</td><td><b class="incident-badge wait">요청중</b></td></tr>
                  </tbody>
                </table>
              </article>
            </section>
          </article>
</template>
