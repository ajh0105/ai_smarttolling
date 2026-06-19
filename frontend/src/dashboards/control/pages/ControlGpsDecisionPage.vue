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
<section class="gps-decision-page">
        <section class="event-title-row">
          <h1>GPS 판정</h1>
          <p>GPS 수신 데이터로 결제 영역 내 통과 여부를 판정합니다.</p>
        </section>

        <section class="gps-filter-row">
          <label class="date-range"><span>기간</span><b>2026-05-11 00:00 ~ 2026-05-11 23:59</b><i>▣</i></label>
          <button class="filter-toggle" type="button">판정 상태 <b>전체</b></button>
          <label class="event-search"><input type="search" placeholder="차량번호 검색" /><span>⌕</span></label>
          <button class="reset-filter" type="button">필터 초기화</button>
        </section>

        <section class="gps-decision-grid">
          <article class="panel gps-log-panel">
            <div class="panel-head">
              <h2>GPS Telemetry 실시간 로그</h2>
              <div class="gps-log-actions"><span class="event-state ok">저장 중</span><button type="button">열 설정</button></div>
            </div>
            <div class="gps-log-scroll">
            <table>
              <thead><tr><th>TIME</th><th>차량번호</th><th>LAT/LNG</th><th>SPD (km/h)</th><th>HEAD (°)</th><th>GPS 판정</th></tr></thead>
              <tbody>
                <tr><td>17:36:47.120</td><td>12가 3456</td><td>37.491032, 126.725124</td><td>58.6</td><td>87</td><td><span class="event-state ok">정상</span></td></tr>
                <tr><td>17:36:46.120</td><td>45나 6721</td><td>37.491040, 126.725083</td><td>57.9</td><td>88</td><td><span class="event-state ok">정상</span></td></tr>
                <tr><td>17:36:45.120</td><td>67다 9012</td><td>37.490976, 126.725041</td><td>57.4</td><td>87</td><td><span class="event-state ok">정상</span></td></tr>
                <tr><td>17:36:44.120</td><td>98머 3344</td><td>37.490948, 126.724999</td><td>56.8</td><td>87</td><td><span class="event-state ok">정상</span></td></tr>
                <tr><td>17:36:43.120</td><td>34다 1122</td><td>37.490920, 126.724957</td><td>56.1</td><td>88</td><td><span class="event-state ok">정상</span></td></tr>
                <tr><td>17:36:42.120</td><td>56라 7788</td><td>37.490893, 126.724915</td><td>55.6</td><td>87</td><td><span class="event-state ok">정상</span></td></tr>
                <tr><td>17:36:41.120</td><td>12바 1234</td><td>37.490865, 126.724874</td><td>54.9</td><td>87</td><td><span class="event-state danger">수신 불가</span></td></tr>
                <tr><td>17:36:40.120</td><td>90허 5678</td><td>37.490837, 126.724833</td><td>54.2</td><td>86</td><td><span class="event-state ok">정상</span></td></tr>
                <tr><td>17:36:39.120</td><td>22거 4567</td><td>37.490810, 126.724792</td><td>53.9</td><td>86</td><td><span class="event-state ok">정상</span></td></tr>
                <tr><td>17:36:38.120</td><td>11서 2233</td><td>37.490783, 126.724750</td><td>52.7</td><td>85</td><td><span class="event-state ok">정상</span></td></tr>
              </tbody>
            </table>
            </div>
            <footer><span>조회 건수: 1,248건</span><button type="button"><i class="pause-icon"></i>일시정지</button></footer>
          </article>

          <article class="panel gps-area-panel">
            <div class="panel-head"><h2>결제 영역 판정 <small>ⓘ</small></h2></div>
            <div class="gps-area-legend"><span class="ok">유효 결제 영역</span><span class="point">GPS 위치(선택)</span><span class="danger">영역 밖</span></div>
            <section class="gps-case ok-case">
              <b>정상 예시 (영역 안)</b>
              <div class="gps-case-map"><i></i><em></em></div>
              <dl><dt>상태</dt><dd><span class="event-state ok">정상</span></dd><dt>설명</dt><dd>GPS 위치가 유효 결제 영역 내에 있습니다.</dd></dl>
            </section>
            <section class="gps-case danger-case">
              <b>영역 이탈 예시 (영역 밖)</b>
              <div class="gps-case-map"><i></i><em></em><strong>50 m</strong></div>
              <dl><dt>상태</dt><dd><span class="event-state danger">영역 이탈</span></dd><dt>설명</dt><dd>GPS 위치가 유효 결제 영역 밖에 있습니다.</dd></dl>
            </section>
          </article>

          <article class="panel selected-gps-panel">
            <div class="event-detail-head"><h2>선택 이벤트 판정</h2><span class="event-state ok">정상 (결제 가능)</span></div>
            <dl>
              <dt>차량번호</dt><dd>12가 3456</dd>
              <dt>차선</dt><dd>2차선</dd>
              <dt>방향</dt><dd><span class="event-pill in">IN (진입)</span></dd>
              <dt>통과시각</dt><dd>2026-05-11 17:35:42.289</dd>
              <dt>위도 (LAT)</dt><dd>37.491032</dd>
              <dt>경도 (LNG)</dt><dd>126.725124</dd>
              <dt>속도 (SPD)</dt><dd>58.6 km/h</dd>
              <dt>방향각 (HEAD)</dt><dd>87° (E)</dd>
              <dt>GPS 판정</dt><dd><span class="event-state ok">정상 (영역 안)</span></dd>
              <dt>결제 판정</dt><dd><span class="event-state ok">결제 가능</span></dd>
            </dl>
            <button class="decision-run" type="button">⌖ 결제 판정 실행</button>
          </article>
        </section>

      </section>
</template>
