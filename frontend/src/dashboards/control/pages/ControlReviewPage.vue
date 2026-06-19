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
<section class="review-page">
        <section class="event-title-row">
          <h1>검수</h1>
          <p>이벤트 증거와 GPS 판선 결과를 확인하고 검수 처리합니다.</p>
        </section>

        <div class="review-tabs">
          <button class="active" type="button">검수 대기</button>
          <button type="button">현장 이벤트</button>
          <button type="button">완료 내역</button>
        </div>

        <section class="review-main-grid">
          <article class="panel review-queue-panel">
            <div class="panel-head"><h2>검수 대기 목록 <small>(12)</small></h2><button type="button">최신순 <span class="filter-caret">⌄</span></button></div>
            <div class="review-cards">
              <article class="selected danger"><header><b>정차 의심</b><span>높음</span></header><strong>12가 3456</strong><p>2차선 <i></i> OUT <i></i> 17:35:42</p></article>
              <article class="danger"><header><b>GPS 영역 이탈</b><span>높음</span></header><strong>46다 7720</strong><p>2차선 <i></i> OUT <i></i> 17:35:18</p></article>
              <article class="warn"><header><b>차선 경계 통과</b><span>보통</span></header><strong>34다 1122</strong><p>2차선 <i></i> OUT <i></i> 17:34:53</p></article>
              <article class="info"><header><b>기준 구역 외 통과</b><span>보통</span></header><strong>67더 9012</strong><p>1차선 <i></i> IN <i></i> 17:34:27</p></article>
              <article class="warn"><header><b>차선 경계 통과</b><span>보통</span></header><strong>85버 1212</strong><p>1차선 <i></i> OUT <i></i> 17:33:18</p></article>
            </div>
            <button class="review-more" type="button">더보기 <span class="filter-caret">⌄</span></button>
          </article>

          <article class="panel evidence-panel">
            <div class="panel-head"><h2>이벤트 증거</h2></div>
            <p class="evidence-sub">이벤트 발생 시 저장된 이미지</p>
            <section class="evidence-images">
              <div class="evidence-main"><span>이벤트 이미지</span><div class="evidence-road"><div class="evidence-car"><b>12가 3456</b></div></div></div>
              <div class="evidence-crop"><span>번호판 crop</span><strong>12가 3456</strong></div>
            </section>
            <section class="evidence-meta">
              <p><span>⌂ 차선</span><b>2차선</b></p>
              <p><span>→ 방향</span><b>OUT (출구)</b></p>
              <p><span>◷ 통과시각</span><b>2025-05-11 17:35:42.289</b></p>
            </section>
          </article>

          <article class="panel review-decision-panel">
            <div class="event-detail-head"><h2>검수 정보 및 판정</h2><span class="event-state danger">정차 의심</span></div>
            <dl class="review-info-list">
              <dt>차량번호</dt><dd>12가 3456</dd>
              <dt>차선</dt><dd>2차선</dd>
              <dt>방향</dt><dd>OUT (출구)</dd>
              <dt>통과시각</dt><dd>2025-05-11 17:35:42.289</dd>
              <dt>이벤트 유형</dt><dd>정차 의심</dd>
            </dl>
            <section class="review-gps-box">
              <h3>GPS 결제 영역 판정</h3>
              <div class="review-gps-content">
                <div class="review-gps-map"><i></i><b></b></div>
                <div class="review-gps-legend"><span class="zone">유효 통과 구역</span><span class="point">GPS 위치(현재)</span><span class="center">톨링 지점(CENTER)</span></div>
              </div>
            </section>
            <dl class="review-result-grid">
              <dt>위도 (LAT)</dt><dd>37.491032</dd>
              <dt>경도 (LNG)</dt><dd>126.725124</dd>
              <dt>속도 (km/h)</dt><dd>0.0</dd>
              <dt>방향각 (HEAD)</dt><dd>0° (N)</dd>
              <dt>GPS 판정</dt><dd><span class="event-state ok">정상 (영역 안)</span></dd>
              <dt>결제 판정</dt><dd><span class="event-state ok">결제 가능</span></dd>
              <dt>검수 상태</dt><dd><span class="event-state warn">대기</span></dd>
            </dl>
            <footer class="review-actions">
              <button class="approve" type="button">✓ 정상 처리</button>
              <button class="field" type="button">♙ 현장 확인 요청</button>
              <button class="hold" type="button">Ⅱ 보류</button>
            </footer>
          </article>
        </section>

        <article class="panel review-history-panel">
          <div class="panel-head"><h2>최근 검수 처리 내역 <small>(최신 5건)</small></h2><button type="button">전체 내역 보기 ›</button></div>
          <table>
            <thead><tr><th>처리 시각</th><th>이벤트 유형</th><th>차량번호</th><th>차선</th><th>방향</th><th>GPS 판정</th><th>결제 판정</th><th>검수 결과</th><th>처리자</th><th>메모</th></tr></thead>
            <tbody>
              <tr><td>17:29:48</td><td>GPS 영역 이탈</td><td>98머 3344</td><td>2차선</td><td>IN</td><td><span class="event-state danger">영역 이탈</span></td><td><span class="event-state danger">결제 불가</span></td><td><span class="event-state warn">현장 확인 요청</span></td><td>operator01</td><td>현장 확인 필요</td></tr>
              <tr><td>17:27:01</td><td>차선 경계 통과</td><td>90허 5678</td><td>2차선</td><td>OUT</td><td><span class="event-state ok">정상</span></td><td><span class="event-state ok">결제 가능</span></td><td><span class="event-state ok">정상 처리</span></td><td>operator02</td><td>-</td></tr>
              <tr><td>17:24:33</td><td>기준 구역 외 통과</td><td>67더 9012</td><td>1차선</td><td>IN</td><td><span class="event-state danger">영역 이탈</span></td><td><span class="event-state danger">결제 불가</span></td><td><span class="event-state ok">정상 처리</span></td><td>operator01</td><td>구역 내 재진입 확인</td></tr>
              <tr><td>17:22:15</td><td>정차 의심</td><td>12가 3456</td><td>2차선</td><td>OUT</td><td><span class="event-state ok">정상</span></td><td><span class="event-state ok">결제 가능</span></td><td><span class="event-state warn">현장 확인 요청</span></td><td>operator03</td><td>정차 8초, 현장 확인</td></tr>
              <tr><td>17:19:12</td><td>차선 경계 통과</td><td>34다 1122</td><td>2차선</td><td>OUT</td><td><span class="event-state ok">정상</span></td><td><span class="event-state ok">결제 가능</span></td><td><span class="event-state ok">정상 처리</span></td><td>operator02</td><td>-</td></tr>
            </tbody>
          </table>
        </article>
      </section>
</template>
