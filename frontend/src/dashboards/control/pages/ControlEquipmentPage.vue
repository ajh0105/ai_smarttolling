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
  historyRows,
  getKpiIcon
} = inject('controlDashboardContext')
</script>

<template>
<section class="equipment-page">
        <section class="title-row">
          <h1>장비 상태</h1>
          <p>구역 운영에 영향을 주는 상태만 표시합니다.</p>
        </section>

        <section class="equipment-grid">
          <article v-for="card in equipmentCards" :key="card.title" class="equipment-card" :class="card.tone">
            <i><img :src="getKpiIcon(card.icon)" :alt="card.title" /></i>
            <div>
              <span>{{ card.title }}</span>
              <strong>{{ card.status }}</strong>
              <em>{{ card.desc }}</em>
            </div>
            <p>영향 : {{ card.impact }}</p>
          </article>
        </section>

        <section class="equipment-layout">
          <section class="equipment-left-column">
            <article class="panel comm-panel">
              <div class="panel-head"><h2>장비 통신 상태</h2></div>
              <div class="comm-grid">
                <div class="comm-card">
                  <i><img :src="getKpiIcon('lan2.png')" alt="현재 통신" /></i>
                  <span>현재 통신</span>
                  <strong>LAN 사용 중</strong>
                  <small>유선 네트워크 정상</small>
                </div>
                <div class="comm-card">
                  <i><img :src="getKpiIcon('standby.png')" alt="LTE 백업망" /></i>
                  <span>LTE 백업망</span>
                  <strong class="pending">대기</strong>
                  <small>자동 전환 대기 상태</small>
                </div>
              </div>
              <div class="comm-summary">
                <p><span>마지막 전환 시간</span><b>2025-05-08 14:22:31</b></p>
                <p><span>통신 장애 상태</span><b>없음</b></p>
                <p><span>예상 영향</span><b>없음 (운영 정상)</b></p>
              </div>
            </article>

            <article class="panel rail-panel">
              <div class="panel-head"><h2>레일 영역 상태</h2></div>
              <table>
                <thead>
                  <tr><th>레일 영역</th><th>마지막 이벤트 수신</th><th>GPS 판정 가능</th><th>분석 상태</th><th>영향</th></tr>
                </thead>
                <tbody>
                  <tr v-for="row in equipmentLaneRows" :key="row.lane">
                    <td><b class="lane-badge">{{ row.lane }}</b>{{ row.name }}</td>
                    <td class="ok">{{ row.eventAt }}</td>
                    <td class="ok">{{ row.gps }}</td>
                    <td class="ok">{{ row.analysis }}</td>
                    <td class="ok">{{ row.impact }}</td>
                  </tr>
                </tbody>
              </table>
            </article>

            <article class="panel alert-panel">
              <div class="panel-head">
                <h2>알림 및 조치</h2>
                <button type="button">전체 알림 보기 ›</button>
              </div>
              <div class="alert-list">
                <article v-for="alert in equipmentAlerts" :key="alert.title" class="alert-row" :class="alert.tone">
                  <i>{{ alert.tone === 'info' ? 'i' : '!' }}</i>
                  <div>
                    <b>{{ alert.title }}</b>
                    <span>{{ alert.desc }}</span>
                  </div>
                  <p><span>영향 범위</span>{{ alert.scope }}</p>
                  <p><span>발생 시각</span>{{ alert.time }}</p>
                  <button type="button">확인</button>
                  <button type="button">정비 요청</button>
                  <button type="button" class="done">완료</button>
                </article>
              </div>
            </article>
          </section>

          <article class="panel history-panel equipment-history">
            <div class="panel-head">
              <h2>상태 이력</h2>
              <button type="button">전체 이력 보기 ›</button>
            </div>
            <table>
              <thead>
                <tr><th>발생시각</th><th>항목</th><th>영향</th><th>상세 내용</th><th>처리 상태</th><th>처리자</th></tr>
              </thead>
              <tbody>
                <tr v-for="row in historyRows" :key="`${row.time}-${row.item}`">
                  <td>{{ row.time }}</td>
                  <td>{{ row.item }}</td>
                  <td>{{ row.impact }}</td>
                  <td>{{ row.detail }}</td>
                  <td><span class="status-ok">{{ row.status }}</span></td>
                  <td>{{ row.actor }}</td>
                </tr>
              </tbody>
            </table>
          </article>
        </section>
      </section>
</template>
