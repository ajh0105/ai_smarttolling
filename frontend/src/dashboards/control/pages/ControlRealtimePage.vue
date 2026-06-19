<script setup>
import { inject } from 'vue'
import HlsLiveVideo from '../components/HlsLiveVideo.vue'

const {
  activeMenu,
  centerLabel,
  selectedLaneText,
  selectedLane,
  dashboardKpis,
  operatorVideoStreamUrl,
  operatorVideoIsLive,
  operatorVideoStatusText,
  operatorVideoFpsText,
  updateRenderedVideoFps,
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
<section class="realtime-page">
        <section class="event-title-row">
          <h1>실시간 관제</h1>
          <p>차선별 카메라, GPS, 통행 이벤트 상태를 실시간으로 모니터링합니다.</p>
        </section>

        <section class="realtime-kpi-grid">
          <article class="realtime-kpi ok"><i><img src="../../icons/control/cctv.png" alt="CCTV 영상" /></i><span>CCTV 영상</span><strong>정상</strong><em>2개 레일 수신 중</em></article>
          <article class="realtime-kpi ok"><i><img src="../../icons/control/gps2.png" alt="GPS 수신" /></i><span>GPS 수신</span><strong>정상</strong><em>최근 반영 2초 전</em></article>
          <article class="realtime-kpi info"><i><img src="../../icons/control/caution.png" alt="이벤트 수신" /></i><span>이벤트 수신</span><strong>1,248</strong><em>오늘 누적 통행</em></article>
          <article class="realtime-kpi warn"><i><img src="../../icons/control/notification.png" alt="현장 알림" /></i><span>현장 알림</span><strong>3</strong><em>확인 필요</em></article>
        </section>

        <section class="realtime-main-grid">
          <article class="panel realtime-camera-panel">
            <div class="panel-head">
              <h2>실시간 YOLO 합성 프레임</h2>
              <span class="live-chip" :class="{ waiting: !operatorVideoIsLive }"><i class="dot ok"></i>{{ operatorVideoStatusText }}</span>
            </div>
            <div class="realtime-frame">
              <HlsLiveVideo
                v-if="operatorVideoStreamUrl"
                class="dashboard-live-frame"
                :src="operatorVideoStreamUrl"
                :live="operatorVideoIsLive"
                alt="YOLO 실시간 화면"
                @fps="updateRenderedVideoFps"
              />
              <div v-else class="dashboard-live-placeholder">WAIT</div>
              <section class="realtime-lane green">
                <header><b>1번 레일</b><em>{{ operatorVideoFpsText }}</em></header>
              </section>
              <section class="realtime-lane blue">
                <header><b>2번 레일</b><em>{{ operatorVideoFpsText }}</em></header>
              </section>
            </div>
            <footer class="realtime-meta-cards">
              <div class="realtime-meta-card"><span>원본 해상도</span><b>1920 x 1080</b></div>
              <div class="realtime-meta-card"><span>합성 해상도</span><b>960 x 960</b></div>
              <div class="realtime-meta-card"><span>YOLO 모델</span><b>v8.1</b></div>
              <div class="realtime-meta-card ok"><span>운영 상태</span><b>정상</b></div>
            </footer>
          </article>

          <section class="realtime-side-grid">
            <article class="panel realtime-status-panel">
              <div class="panel-head"><h2>현재 수신 상태</h2></div>
              <div class="status-grid compact">
                <article class="ok"><i><img src="../../icons/control/cctv.png" alt="CCTV" /></i><span>CCTV</span><strong>정상</strong></article>
                <article class="ok"><i><img src="../../icons/control/gps2.png" alt="GPS" /></i><span>GPS</span><strong>정상</strong></article>
                <article class="info"><i><img src="../../icons/control/signalpng.png" alt="LAN" /></i><span>LAN</span><strong>사용 중</strong></article>
                <article class="ok"><i><img src="../../icons/control/data.png" alt="DB 반영" /></i><span>DB 반영</span><strong>정상</strong></article>
              </div>
            </article>

            <article class="panel realtime-event-panel">
              <div class="panel-head"><h2>실시간 통행 이벤트</h2><button type="button">더 보기</button></div>
              <table>
                <thead><tr><th>차량번호</th><th>차선</th><th>방향</th><th>통과시각</th><th>상태</th></tr></thead>
                <tbody>
                  <tr><td>31가 9829</td><td>1차선</td><td><span class="event-pill in">IN</span></td><td>17:36:47</td><td><span class="event-state ok">정상 통과</span></td></tr>
                  <tr><td>46다 7720</td><td>2차선</td><td><span class="event-pill out">OUT</span></td><td>17:36:12</td><td><span class="event-state warn">검수 권장</span></td></tr>
                  <tr><td>12가 3456</td><td>1차선</td><td><span class="event-pill in">IN</span></td><td>17:35:41</td><td><span class="event-state danger">영역 이탈</span></td></tr>
                </tbody>
              </table>
            </article>

            <article class="panel realtime-alert-panel">
              <div class="panel-head"><h2>현장 알림</h2><button type="button">더 보기</button></div>
              <div class="alert-list">
                <article class="alert-row warn"><i>!</i><b>2차선 CCTV 프레임 지연</b><p><span>발생 시각</span>17:35:18</p><button type="button">확인</button><span class="alert-sub">최근 3초 이상 지연</span></article>
                <article class="alert-row info"><i>i</i><b>LAN 경로 안정화</b><p><span>발생 시각</span>17:32:04</p><button type="button">확인</button><span class="alert-sub">이벤트 수신 정상화</span></article>
              </div>
            </article>
          </section>
        </section>
      </section>
</template>
