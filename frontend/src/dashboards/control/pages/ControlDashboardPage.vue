<script setup>
import { inject } from 'vue'
import HlsLiveVideo from '../components/HlsLiveVideo.vue'

const {
  activeMenu,
  centerLabel,
  selectedLaneText,
  selectedLane,
  dashboardKpis,
  getKpiIcon,
  getAdminIcon,
  operatorVideoStreamUrl,
  operatorVideoIsLive,
  operatorVideoStatusText,
  updateRenderedVideoFps,
  operatorVideoFpsValue,
  dashboardDetections,
  statusCards,
  recentGpsJudgements,
  fieldAlerts,
  equipmentCards,
  equipmentLaneRows,
  equipmentAlerts,
  historyRows
} = inject('controlDashboardContext')
</script>

<template>
<section class="dashboard-page">
        <section class="kpi-grid">
          <article v-for="card in dashboardKpis" :key="card.title" class="kpi-card" :class="card.tone">
            <i><img :src="getKpiIcon(card.icon)" :alt="card.title" /></i>
            <div>
              <span>{{ card.title }}</span>
              <strong>{{ card.value }} <small>{{ card.unit }}</small></strong>
              <em :class="card.trend">{{ card.sub }}</em>
            </div>
          </article>
        </section>

        <section class="dashboard-grid">
          <article class="panel yolo-panel">
            <div class="panel-head">
              <h2>YOLO 합성 960x960</h2>
              <span class="live-chip" :class="{ waiting: !operatorVideoIsLive }"><i class="dot ok"></i>{{ operatorVideoStatusText }}</span>
            </div>
            <div class="yolo-frame">
              <HlsLiveVideo
                v-if="operatorVideoStreamUrl"
                class="dashboard-live-frame"
                :src="operatorVideoStreamUrl"
                :live="operatorVideoIsLive"
                alt="YOLO 실시간 화면"
                @fps="updateRenderedVideoFps"
              />
              <div v-else class="dashboard-live-placeholder">WAIT</div>
              <section
                v-for="lane in dashboardDetections"
                :key="lane.lane"
                class="dash-lane"
                :class="[lane.color, { selected: selectedLane === lane.lane }]"
              >
                <div class="dash-lane-title">
                  <b>{{ lane.title }}</b>
                </div>
                <span class="fps-chip">{{ lane.fps }}</span>
              </section>
            </div>
            <footer class="frame-meta">
              <p><span>원본 해상도</span><b>1920 x 1080</b></p>
              <p><span>합성 해상도</span><b>960 x 960</b></p>
              <p><span>YOLO 모델</span><b>v8.1 (tuned)</b></p>
              <p><span>FPS</span><b>{{ operatorVideoFpsValue }}</b></p>
              <p><span>구역</span><b>{{ centerLabel }}</b></p>
              <p><span>운영 상태</span><b>정상</b></p>
            </footer>
          </article>

          <section class="right-column">
            <article class="panel current-panel">
              <div class="panel-head"><h2>현재 상태</h2></div>
              <div class="status-grid">
                <article v-for="item in statusCards" :key="item.label" :class="item.tone">
                  <i><img :src="getKpiIcon(item.icon)" :alt="item.label" /></i>
                  <span>{{ item.label }}</span>
                  <strong>{{ item.value }}</strong>
                </article>
              </div>
            </article>

            <article class="panel gps-panel">
              <div class="panel-head">
                <h2>최근 GPS 판정</h2>
                <button type="button">전체 보기 ›</button>
              </div>
              <table>
                <thead>
                  <tr><th>차량번호</th><th>인식 방향</th><th>운행 방향</th><th>통과 시각</th><th>GPS 판정</th><th>결제 판정</th></tr>
                </thead>
                <tbody>
                  <tr v-for="row in recentGpsJudgements" :key="`${row.plate}-${row.time}`">
                    <td><i class="row-dot" :class="row.tone"></i>{{ row.plate }}</td>
                    <td><span class="pill">{{ row.direction }}</span></td>
                    <td><span class="pill">{{ row.laneText }}</span></td>
                    <td>{{ row.time }}</td>
                    <td><span class="state" :class="row.tone">{{ row.gps }}</span></td>
                    <td><span class="pay" :class="row.tone">{{ row.payment }}</span></td>
                  </tr>
                </tbody>
              </table>
            </article>

            <article class="panel field-panel">
              <div class="panel-head">
                <h2>현장 알림 <small>(최근 3건)</small></h2>
                <button type="button">전체 보기 ›</button>
              </div>
              <div class="field-list">
                <article v-for="alert in fieldAlerts" :key="alert.title" :class="alert.level">
                  <i><img :src="getAdminIcon(alert.icon)" :alt="alert.title" /></i>
                  <b>{{ alert.title }}</b>
                  <span>{{ alert.target }}</span>
                  <time>{{ alert.time }}</time>
                  <em>{{ alert.badge }}</em>
                </article>
              </div>
            </article>
          </section>
        </section>

      </section>
</template>
