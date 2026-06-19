<script setup>
import { inject, ref, computed } from 'vue'
import ChartJsPanel from '@/components/charts/ChartJsPanel.vue'

const {
  activeMenu,
  centerLabel,
  isLightMode,
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

const activeTab = ref('금일')
const chartMode = ref('금액')

const tabDataMap = {
  '금일': {
    comp: '전일 대비', chartLabel: '시간대별',
    labels: ['00', '02', '04', '06', '08', '10', '12', '14', '16', '18', '20', '22', '24'],
    amountIn:  [12, 28, 48, 92, 154, 220, 265, 232, 210, 238, 195, 124, 42],
    amountOut: [7, 18, 32, 56, 84, 110, 126, 92, 86, 98, 76, 48, 18],
    countIn:   [6, 15, 25, 48, 81, 116, 139, 122, 110, 125, 103, 65, 22],
    countOut:  [4, 9, 17, 29, 44, 58, 66, 48, 45, 52, 40, 25, 9],
    kpi: { fee: '₩2,450,800', feeTrend: '▲ 6.1%', payable: '2,108', payTrend: '▲5.7%', gpsOut: '214', gpsTrend: '▼ 5.3%', pending: '₩231,600', pendTrend: '▲ 7.9%' },
    totalAmount: '₩2,450,800', totalCount: '1,248 건',
    donut: [2108, 214, 148], donutTotal: '2,470',
    donutLabels: ['2,108 건 (85.2%)', '214 건 (8.7%)', '148 건 (6.0%)'],
    impact: { okFee: '₩2,108,200', okCount: '2,108 건', okRate: '85.9%', danFee: '₩231,600', danCount: '214 건', danRate: '8.4%', nofix: '148 건 (6.0%)' }
  },
  '주간': {
    comp: '전주 대비', chartLabel: '요일별',
    labels: ['월', '화', '수', '목', '금', '토', '일'],
    amountIn:  [2480, 2640, 2890, 3120, 3380, 3840, 2180],
    amountOut: [1240, 1320, 1440, 1560, 1690, 1920, 1090],
    countIn:   [1305, 1389, 1521, 1642, 1779, 2021, 1147],
    countOut:  [653, 695, 758, 821, 890, 1011, 574],
    kpi: { fee: '₩16,842,400', feeTrend: '▲ 3.2%', payable: '14,562', payTrend: '▲2.8%', gpsOut: '1,486', gpsTrend: '▼ 4.1%', pending: '₩1,628,800', pendTrend: '▲ 5.6%' },
    totalAmount: '₩16,842,400', totalCount: '17,072 건',
    donut: [14562, 1486, 1024], donutTotal: '17,072',
    donutLabels: ['14,562 건 (85.3%)', '1,486 건 (8.7%)', '1,024 건 (6.0%)'],
    impact: { okFee: '₩15,213,600', okCount: '14,562 건', okRate: '85.3%', danFee: '₩1,628,800', danCount: '1,486 건', danRate: '8.7%', nofix: '1,024 건 (6.0%)' }
  },
  '월간': {
    comp: '전월 대비', chartLabel: '주차별',
    labels: ['1주차', '2주차', '3주차', '4주차'],
    amountIn:  [16840, 18240, 17620, 18480],
    amountOut: [8420, 9120, 8810, 9240],
    countIn:   [8863, 9600, 9274, 9732],
    countOut:  [4432, 4800, 4637, 4866],
    kpi: { fee: '₩68,342,400', feeTrend: '▲ 1.8%', payable: '59,840', payTrend: '▲1.4%', gpsOut: '6,244', gpsTrend: '▼ 2.7%', pending: '₩6,804,800', pendTrend: '▲ 3.2%' },
    totalAmount: '₩68,342,400', totalCount: '70,100 건',
    donut: [59840, 6244, 4016], donutTotal: '70,100',
    donutLabels: ['59,840 건 (85.4%)', '6,244 건 (8.9%)', '4,016 건 (5.7%)'],
    impact: { okFee: '₩61,537,600', okCount: '59,840 건', okRate: '85.4%', danFee: '₩6,804,800', danCount: '6,244 건', danRate: '8.9%', nofix: '4,016 건 (5.7%)' }
  }
}

const d = computed(() => tabDataMap[activeTab.value] ?? tabDataMap['금일'])

const tollChartData = computed(() => ({
  labels: d.value.labels,
  datasets: [
    { label: 'IN 입구', data: chartMode.value === '금액' ? d.value.amountIn : d.value.countIn, backgroundColor: 'rgba(47,140,255,0.82)', borderRadius: 4, stack: 'toll' },
    { label: 'OUT 출구', data: chartMode.value === '금액' ? d.value.amountOut : d.value.countOut, backgroundColor: 'rgba(157,98,221,0.86)', borderRadius: 4, stack: 'toll' }
  ]
}))

const chartTotalLabel = computed(() => chartMode.value === '금액' ? d.value.totalAmount : d.value.totalCount)

const tollChartOptions = computed(() => {
  const tickColor = isLightMode.value ? '#334155' : '#9fb4ce'
  const gridColor = isLightMode.value ? '#c7d4e3' : 'rgba(117,151,194,.1)'

  return {
  responsive: true,
  maintainAspectRatio: false,
  layout: { padding: 0 },
  plugins: {
    legend: { display: false },
    tooltip: {
      callbacks: {
        label(context) {
          return chartMode.value === '금액'
            ? `${context.dataset.label}: ${context.parsed.y.toLocaleString()}천원`
            : `${context.dataset.label}: ${context.parsed.y}건`
        }
      }
    }
  },
  scales: {
    x: { stacked: true, ticks: { color: tickColor, font: { weight: '700' } }, grid: { color: gridColor } },
    y: { stacked: true, grace: 0, beginAtZero: true, ticks: { color: tickColor, font: { weight: '700' }, callback: (value) => chartMode.value === '금액' ? `${value}K` : `${value}` }, grid: { color: gridColor } }
  }
  }
})

const settlementDonutData = computed(() => ({
  labels: ['결제 가능', '검수 필요', '보류'],
  datasets: [{ data: d.value.donut, backgroundColor: ['#35d36d', '#ffd13f', '#ff625c'], borderWidth: 0 }]
}))

const settlementDonutOptions = {
  cutout: '68%',
  plugins: { legend: { display: false } },
  scales: { x: { display: false }, y: { display: false } }
}
</script>

<template>
<section class="settlement-page">
        <section class="event-title-row">
          <h1>정산</h1>
          <p>통행 이벤트 기반 정산 현황을 요약합니다.</p>
        </section>

        <div class="settlement-tabs">
          <button :class="{active: activeTab==='금일'}" type="button" @click="activeTab='금일'">금일</button>
          <button :class="{active: activeTab==='주간'}" type="button" @click="activeTab='주간'">주간</button>
          <button :class="{active: activeTab==='월간'}" type="button" @click="activeTab='월간'">월간</button>
          <button :class="{active: activeTab==='사용자 지정'}" type="button" @click="activeTab='사용자 지정'">사용자 지정 <span class="filter-caret">▣</span></button>
        </div>

        <section class="settlement-kpi-grid">
          <article class="settlement-kpi fee"><i><img :src="getKpiIcon('won.png')" alt="통행료" /></i><span>총 통행료</span><strong>{{ d.kpi.fee }}</strong><em>{{ d.comp }} {{ d.kpi.feeTrend }}</em></article>
          <article class="settlement-kpi card"><i><img :src="getKpiIcon('payment.png')" alt="결제" /></i><span>결제 가능 건수</span><strong>{{ d.kpi.payable }} <small>건</small></strong><em>{{ d.comp }} {{ d.kpi.payTrend }}</em></article>
          <article class="settlement-kpi danger"><i><img :src="getKpiIcon('error.png')" alt="이탈" /></i><span>GPS 영역 이탈 건수</span><strong>{{ d.kpi.gpsOut }} <small>건</small></strong><em>{{ d.comp }} {{ d.kpi.gpsTrend }}</em></article>
          <article class="settlement-kpi hold"><i><img :src="getKpiIcon('list.png')" alt="검수" /></i><span>검수 대기 금액</span><strong>{{ d.kpi.pending }}</strong><em>{{ d.comp }} {{ d.kpi.pendTrend }}</em></article>
        </section>

        <section class="settlement-mid-grid">
          <article class="panel toll-chart-panel">
            <div class="panel-head">
              <h2>{{ d.chartLabel }} 통행료 ({{ chartMode }})</h2>
              <div class="chart-toggle">
                <button :class="{active: chartMode==='금액'}" type="button" @click="chartMode='금액'">금액</button>
                <button :class="{active: chartMode==='건수'}" type="button" @click="chartMode='건수'">건수</button>
              </div>
            </div>
            <div class="chart-legend"><span class="in">IN 입구</span><span class="out">OUT 출구</span><strong>{{ chartTotalLabel }}<br /><small>합계</small></strong></div>
            <div class="toll-chart-wrap"><ChartJsPanel type="bar" :data="tollChartData" :options="tollChartOptions" /></div>
          </article>

          <article class="panel settlement-donut-panel">
            <div class="panel-head"><h2>정산 상태 요약</h2></div>
            <div class="settlement-donut-wrap">
              <div class="settlement-donut chart-donut">
                <ChartJsPanel type="doughnut" :data="settlementDonutData" :options="settlementDonutOptions" :height="170" />
                <div class="settlement-donut-center"><strong>{{ d.donutTotal }} <small>건</small></strong><span>총 통행 이벤트</span></div>
              </div>
              <dl>
                <dt class="ok">결제 가능</dt><dd>{{ d.donutLabels[0] }}</dd>
                <dt class="warn">검수 필요</dt><dd>{{ d.donutLabels[1] }}</dd>
                <dt class="danger">보류</dt><dd>{{ d.donutLabels[2] }}</dd>
              </dl>
            </div>
          </article>

          <article class="panel gps-impact-panel">
            <div class="panel-head"><h2>GPS 판정 영향 <small>ⓘ</small></h2></div>
            <section class="impact-card ok">
              <header><b>정상 판정 (영역 내 통과)</b><span>정상</span></header>
              <div><p>금액<strong>{{ d.impact.okFee }}</strong></p><p>건수<strong>{{ d.impact.okCount }}</strong></p><p>비율<strong>{{ d.impact.okRate }}</strong></p></div>
            </section>
            <section class="impact-card danger">
              <header><b>영역 이탈 (검수 필요)</b><span>검수 필요</span></header>
              <div><p>금액<strong>{{ d.impact.danFee }}</strong></p><p>건수<strong>{{ d.impact.danCount }}</strong></p><p>비율<strong>{{ d.impact.danRate }}</strong></p></div>
            </section>
            <p class="nofix-line"><i></i>미송신 / No Fix <span>건수 {{ d.impact.nofix }}</span></p>
          </article>
        </section>

        <article class="panel settlement-table-panel">
          <div class="panel-head">
            <h2>정산 후보 요약 <small>(결제 대상)</small></h2>
            <div><button type="button">⇩ 내보내기 <span class="filter-caret">⌄</span></button><button type="button">⟳ 새로고침</button></div>
          </div>
          <table>
            <thead><tr><th>No.</th><th>차량번호</th><th>방향</th><th>차선</th><th>통과시각</th><th>GPS 판정</th><th>결제 판정</th><th>금액</th><th>상태</th></tr></thead>
            <tbody>
              <tr><td>1</td><td>12가 3456</td><td><span class="event-pill in">IN</span></td><td>2차선</td><td>17:35:42</td><td><span class="event-state ok">정상 (영역 내)</span></td><td><span class="event-state ok">결제 가능</span></td><td>₩1,900</td><td><span class="event-state ok">결제 완료</span></td></tr>
              <tr><td>2</td><td>45나 6721</td><td><span class="event-pill out">OUT</span></td><td>2차선</td><td>17:33:18</td><td><span class="event-state ok">정상 (영역 내)</span></td><td><span class="event-state ok">결제 가능</span></td><td>₩1,900</td><td><span class="event-state warn">대기</span></td></tr>
              <tr><td>3</td><td>67다 9012</td><td><span class="event-pill in">IN</span></td><td>1차선</td><td>17:31:41</td><td><span class="event-state danger">영역 이탈</span></td><td><span class="event-state warn">검수 필요</span></td><td>₩1,900</td><td><span class="event-state warn">검수 대기</span></td></tr>
              <tr><td>4</td><td>34머 1122</td><td><span class="event-pill out">OUT</span></td><td>2차선</td><td>17:26:17</td><td><span class="event-state danger">영역 이탈</span></td><td><span class="event-state warn">검수 필요</span></td><td>₩1,900</td><td><span class="event-state warn">검수 대기</span></td></tr>
              <tr><td>5</td><td>90허 5678</td><td><span class="event-pill in">IN</span></td><td>1차선</td><td>17:19:12</td><td><span class="event-state ok">정상 (영역 내)</span></td><td><span class="event-state ok">결제 가능</span></td><td>₩1,900</td><td><span class="event-state warn">대기</span></td></tr>
              <tr><td>6</td><td>78버 3347</td><td><span class="event-pill out">OUT</span></td><td>2차선</td><td>17:12:22</td><td><span class="event-state muted">No Fix / 미송신</span></td><td><span class="event-state danger">보류</span></td><td>₩1,900</td><td><span class="event-state danger">보류</span></td></tr>
            </tbody>
          </table>
        </article>
      </section>
</template>
