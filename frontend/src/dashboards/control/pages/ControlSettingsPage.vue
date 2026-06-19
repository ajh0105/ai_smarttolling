<script setup>
import { inject } from 'vue'

const {
  activeMenu,
  centerLabel,
  selectedLaneText,
  selectedLane,
  dashboardKpis,
  dashboardDetections,
  statusCards,
  filteredGpsJudgements,
  fieldAlerts,
  filteredTrafficRows,
  equipmentCards,
  equipmentLaneRows,
  equipmentAlerts,
  historyRows
} = inject('controlDashboardContext')
</script>

<template>
<section class="settings-page">
        <section class="event-title-row">
          <h1>설정</h1>
          <p>관제센터 운영 기준, GPS 영역, 알림, 계정 및 화면 환경을 관리합니다.</p>
        </section>

        <section class="settings-grid">
          <article class="panel settings-card">
            <div class="panel-head"><h2>관제센터 기본 설정</h2><button type="button">편집</button></div>
            <dl>
              <dt>관제센터</dt><dd>서울 톨링 A</dd>
              <dt>운영 차선</dt><dd>1차선, 2차선</dd>
              <dt>Ingress Endpoint</dt><dd>wss://seoul-a-ingress.hi-five.kr</dd>
              <dt>데이터 반영 주기</dt><dd>5초</dd>
            </dl>
          </article>

          <article class="panel settings-card">
            <div class="panel-head"><h2>GPS 결제 영역</h2><button type="button">좌표 편집</button></div>
            <div class="settings-map">
              <i></i><b></b>
            </div>
            <dl class="compact-list">
              <dt>중심 좌표</dt><dd>37.491032, 126.725124</dd>
              <dt>영역 폭</dt><dd>120.0 m</dd>
              <dt>영역 높이</dt><dd>80.0 m</dd>
            </dl>
          </article>

          <article class="panel settings-card">
            <div class="panel-head"><h2>알림 기준</h2><button type="button">저장</button></div>
            <div class="setting-options">
              <label><span>CCTV 수신 지연</span><input value="3초 이상" /></label>
              <label><span>GPS No Fix</span><input value="10초 이상" /></label>
              <label><span>Spool 경고</span><input value="85%" /></label>
              <label><span>정차 의심 기준</span><input value="8초 이상" /></label>
            </div>
          </article>

          <article class="panel settings-card">
            <div class="panel-head"><h2>화면 및 권한</h2><button type="button">적용</button></div>
            <div class="setting-toggles">
              <label><span>다크 테마 기본 사용</span><input type="checkbox" checked /></label>
              <label><span>이벤트 팝업 자동 열기</span><input type="checkbox" checked /></label>
              <label><span>검수 완료 전 결제 보류</span><input type="checkbox" checked /></label>
              <label><span>운영자 조치 로그 필수</span><input type="checkbox" /></label>
            </div>
          </article>
        </section>

        <article class="panel settings-history-panel">
          <div class="panel-head"><h2>설정 변경 이력</h2><button type="button">전체 보기 ›</button></div>
          <table>
            <thead><tr><th>변경시각</th><th>관리자</th><th>항목</th><th>이전 값</th><th>변경 값</th><th>상태</th></tr></thead>
            <tbody>
              <tr><td>2026-05-11 17:32:44</td><td>operator01</td><td>GPS 영역 폭</td><td>110.0 m</td><td>120.0 m</td><td><span class="event-state ok">적용</span></td></tr>
              <tr><td>2026-05-11 16:58:11</td><td>operator02</td><td>CCTV 지연 기준</td><td>5초</td><td>3초</td><td><span class="event-state ok">적용</span></td></tr>
              <tr><td>2026-05-11 15:40:20</td><td>operator01</td><td>이벤트 팝업</td><td>OFF</td><td>ON</td><td><span class="event-state ok">적용</span></td></tr>
            </tbody>
          </table>
        </article>
      </section>
</template>
