<script setup>
import { ref, computed, nextTick, onMounted } from 'vue'
import EChartsPanel from '@/components/charts/EChartsPanel.vue'
import { pdmApi } from '@/api/pdm'

// ── 모델 탭 상태 (카메라별 선택 탭) ──────────────────────────────
const modelTabs = ref({}) // { [cameraId]: 'ALL' | 'RULE_BASED' | 'ISOLATION_FOREST' | 'LSTM_AE' }

function getModelTab(cameraId) {
  return modelTabs.value[cameraId] ?? 'ALL'
}
function setModelTab(cameraId, tab) {
  modelTabs.value = { ...modelTabs.value, [cameraId]: tab }
}

// ── 반응형 상태 ─────────────────────────────────────────────────
const summary = ref({
  totalCameraCount: 0,
  normalCameraCount: 0,
  warningCameraCount: 0,
  criticalCameraCount: 0,
  averageHealthScore: 0
})
const cameras = ref([])
const alerts = ref([])
const compareResults = ref([])
const qualityMetrics = ref({})
const selectedCam = ref(null)
const loading = ref(true)
const chartReady = ref(false)

// ── 시각 포맷 ──────────────────────────────────────────────────
function formatTime(dt) {
  if (!dt) return '—'
  return new Date(dt).toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit', hour12: false })
}
function formatDateTime(dt) {
  if (!dt) return '—'
  return new Date(dt).toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: false })
}

function toPercent(value) {
  const number = Number(value ?? 0)
  if (!Number.isFinite(number)) return 0
  const percent = Math.abs(number) <= 1 ? number * 100 : number
  return Math.min(100, Math.max(0, percent))
}

function formatPercent(value) {
  return `${toPercent(value).toFixed(1)}%`
}

function scorePercent(score) {
  const number = Number(score ?? 0)
  if (!Number.isFinite(number)) return 0
  return Math.min(100, Math.max(0, number))
}

function animatedPercent(value) {
  return chartReady.value ? toPercent(value) : 0
}

function animatedScore(score) {
  return chartReady.value ? scorePercent(score) : 0
}

function gaugeDashOffset(score) {
  return 100 - animatedScore(score)
}

function normalizeRisk(level) {
  return { HIGH: 'CRITICAL' }[level] ?? level
}

function normalizeAlertStatus(status) {
  return {
    CREATED: 'OPEN',
    FALSE_ALARM: 'RESOLVED',
  }[status] ?? status
}

function messageLabel(text) {
  return {
    'Recognition quality is stable': '인식 품질 안정',
    'No immediate maintenance required': '즉시 점검 불필요',
    'Temporary mismatch spike detected': '일시적 전후방 불일치 증가',
    'Check recent camera frame delay and lens contamination': '최근 프레임 지연 및 렌즈 오염 확인',
    'Event count is too low for reliable sequence analysis': '이벤트 수 부족으로 시계열 분석 신뢰도 낮음',
    'Check camera capture pipeline and event forwarding': '카메라 캡처/이벤트 전달 경로 확인',
    'Long-term degradation trend detected': '장기 품질 저하 추세 감지',
    'Short repeated quality drop detected': '단기 반복 품질 저하 감지',
    'Legacy API save verification': '기존 API 저장 검증 알림',
    'Inspect mounting vibration and focus state': '거치대 진동 및 초점 상태 점검',
    'Schedule camera inspection and lens replacement check': '카메라 점검 일정 등록 및 렌즈 교체 여부 확인',
  }[text] ?? text
}

function alertTitleLabel(title) {
  return {
    'Front Camera Low Count WARNING alert': '전방 카메라 이벤트 부족 주의',
    'Rear Camera Degrade CRITICAL alert': '후방 카메라 장기 품질 저하 위험',
    'Front Camera Pattern WARNING alert': '전방 카메라 반복 품질 저하 주의',
    'Rear Camera Spike WARNING alert': '후방 카메라 일시 불일치 증가 주의',
    'Front Camera Legacy CRITICAL alert': '전방 카메라 기존 검증 알림',
  }[title] ?? title
}

function mismatchTypeLabel(type) {
  return {
    PLATE_MISMATCH: '번호판 불일치',
    FRONT_MISSING: '전방 미검출',
    REAR_MISSING: '후방 미검출',
    LOW_CONFIDENCE: '낮은 신뢰도',
  }[type] ?? type ?? '—'
}

// ── 알림 차로 이름 헬퍼 ────────────────────────────────────────
function alertLaneLabel(alert) {
  if (alert.laneId == null) return ''
  const cam = cameras.value.find(c => c.cameraId === alert.cameraId)
  if (!cam) return `${alert.laneId}차로`
  const idx = cam.laneIds?.indexOf(alert.laneId) ?? -1
  return idx >= 0 && cam.laneNames?.[idx] ? cam.laneNames[idx] : `${alert.laneId}차로`
}

// ── 데이터 로딩 ────────────────────────────────────────────────
async function loadData() {
  loading.value = true
  chartReady.value = false
  try {
    const [summaryRes, camerasRes, alertsRes, compareRes] = await Promise.all([
      pdmApi.getDashboardSummary(),
      pdmApi.getCameras(),
      pdmApi.getAlerts(),
      pdmApi.getCompareResults(),
    ])
    summary.value = summaryRes.data
    alerts.value = alertsRes.data.filter(alert => normalizeAlertStatus(alert.status) !== 'RESOLVED')
    compareResults.value = compareRes.data

    // 카메라 상세 (avgOcrConfidence, successRate 등 포함)
    const camList = camerasRes.data
    const detailRes = await Promise.all(camList.map(c => pdmApi.getCameraDetail(c.cameraId)))
    cameras.value = detailRes.map(r => r.data)

    if (cameras.value.length > 0 && selectedCam.value === null) {
      selectedCam.value = cameras.value[0].cameraId
    }

    // 품질 추세 (카메라별)
    const metricsRes = await Promise.all(
      cameras.value.map(c => pdmApi.getQualityMetrics(c.cameraId).catch(() => ({ data: [] })))
    )
    const map = {}
    cameras.value.forEach((c, i) => { map[c.cameraId] = metricsRes[i].data })
    qualityMetrics.value = map
    nextTick(() => {
      requestAnimationFrame(() => { chartReady.value = true })
    })

  } catch (e) {
    console.error('[PDM] 데이터 로딩 실패', e)
  } finally {
    loading.value = false
  }
}

onMounted(loadData)

// ── ECharts 공통 색상 토큰 ──────────────────────────────────────
const EC = {
  textMuted: '#9fb2cb',
  textBase:  '#dce9f8',
  gridLine:  'rgba(117,151,194,0.14)',
  green:     '#4cdf66',
  blue:      '#2f8cff',
  amber:     '#f5b84b',
  red:       '#ef5a54'
}

// ── 품질 추세 라인 차트 ─────────────────────────────────────────
const trendOption = computed(() => {
  const metrics = qualityMetrics.value[selectedCam.value] ?? []
  const labels     = metrics.map(m => formatTime(m.bucketStart))
  const ocrData    = metrics.map(m => parseFloat(toPercent(m.avgOcrConfidence).toFixed(1)))
  const successData= metrics.map(m => parseFloat(toPercent(m.successRate).toFixed(1)))
  const matchData  = metrics.map(m => parseFloat(toPercent(m.matchRate).toFixed(1)))

  return {
    backgroundColor: 'transparent',
    tooltip: { trigger: 'axis', axisPointer: { type: 'cross' } },
    legend: {
      top: 4,
      textStyle: { color: EC.textMuted, fontSize: 12 },
      itemWidth: 12, itemHeight: 8
    },
    grid: { top: 40, right: 16, bottom: 28, left: 48, containLabel: false },
    xAxis: {
      type: 'category',
      data: labels.length ? labels : ['—'],
      axisLine: { lineStyle: { color: EC.gridLine } },
      axisTick: { show: false },
      axisLabel: { color: EC.textMuted, fontSize: 11 }
    },
    yAxis: {
      type: 'value',
      min: (() => {
        const vals = [...ocrData, ...successData, ...matchData].filter(v => v != null && !isNaN(v) && v > 0)
        return vals.length ? Math.max(0, Math.floor(Math.min(...vals) / 10) * 10 - 5) : 0
      })(),
      max: (() => {
        const vals = [...ocrData, ...successData, ...matchData].filter(v => v != null && !isNaN(v))
        return vals.length ? Math.min(100, Math.ceil(Math.max(...vals) / 10) * 10 + 5) : 100
      })(),
      splitLine: { lineStyle: { color: EC.gridLine } },
      axisLabel: { color: EC.textMuted, fontSize: 11 }
    },
    series: [
      {
        name: 'OCR 신뢰도(%)',
        type: 'line', smooth: true,
        data: ocrData,
        lineStyle: { color: EC.blue, width: 2 },
        itemStyle: { color: EC.blue },
        areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: 'rgba(47,140,255,.22)' }, { offset: 1, color: 'rgba(47,140,255,.02)' }] } },
        symbol: 'circle', symbolSize: 5
      },
      {
        name: '인식 성공률(%)',
        type: 'line', smooth: true,
        data: successData,
        lineStyle: { color: EC.green, width: 2 },
        itemStyle: { color: EC.green },
        symbol: 'circle', symbolSize: 5
      },
      {
        name: '전후방 일치율(%)',
        type: 'line', smooth: true,
        data: matchData,
        lineStyle: { color: EC.amber, width: 2 },
        itemStyle: { color: EC.amber },
        symbol: 'circle', symbolSize: 5
      }
    ]
  }
})

// ── 헬퍼 ──────────────────────────────────────────────────────
function riskClass(level) {
  return { NORMAL: 'pdm-ok', WARNING: 'pdm-warn', CRITICAL: 'pdm-danger' }[normalizeRisk(level)] ?? ''
}
function riskLabel(level) {
  return { NORMAL: '정상', WARNING: '주의', CRITICAL: '위험' }[normalizeRisk(level)] ?? level
}
function alertStatusLabel(s) {
  return { OPEN: '미처리', CHECKING: '확인 중', RESOLVED: '완료' }[normalizeAlertStatus(s)] ?? s
}
function alertStatusClass(s) {
  return { OPEN: 'pdm-badge-warn', CHECKING: 'pdm-badge-info', RESOLVED: 'pdm-badge-ok' }[normalizeAlertStatus(s)] ?? ''
}
</script>

<template>
<section class="pdm-page">

  <!-- 헤더 행 -->
  <section class="title-row">
    <h1>예지보전</h1>
    <p>전·후방 카메라 인식 품질 기반 장비 상태 진단</p>
  </section>

  <!-- KPI 요약 카드 5개 -->
  <section class="pdm-kpi-grid">
    <article class="pdm-kpi-card">
      <span>전체 카메라</span>
      <strong>{{ summary.totalCameraCount }}<small>대</small></strong>
    </article>
    <article class="pdm-kpi-card">
      <span>정상</span>
      <strong class="pdm-ok">{{ summary.normalCameraCount }}<small>대</small></strong>
    </article>
    <article class="pdm-kpi-card">
      <span>주의</span>
      <strong class="pdm-warn">{{ summary.warningCameraCount }}<small>대</small></strong>
    </article>
    <article class="pdm-kpi-card">
      <span>위험</span>
      <strong class="pdm-danger">{{ summary.criticalCameraCount }}<small>대</small></strong>
    </article>
    <article class="pdm-kpi-card pdm-kpi-score">
      <span>평균 인식 품질 점수</span>
      <strong>{{ summary.averageHealthScore }}<small>점</small></strong>
      <div class="pdm-score-track">
        <div class="pdm-score-fill" :style="`width:${animatedScore(summary.averageHealthScore)}%`"></div>
      </div>
    </article>
  </section>

  <!-- 카메라 상태 카드 -->
  <section class="pdm-cam-grid">
    <article
      v-for="cam in cameras"
      :key="cam.cameraId"
      class="panel pdm-cam-card"
    >
      <div class="pdm-cam-head">
        <div class="pdm-cam-title">
          <b>{{ cam.cameraCode }}</b>
          <span>{{ cam.cameraName }} · 담당 차로 {{ cam.laneNames?.join(', ') }}</span>
        </div>
        <span class="pdm-risk-badge" :class="riskClass(cam.riskLevel)">{{ riskLabel(cam.riskLevel) }}</span>
      </div>

      <!-- 모델 탭 버튼 -->
      <div class="pdm-model-tab-bar">
        <button :class="{ active: getModelTab(cam.cameraId) === 'ALL' }" @click="setModelTab(cam.cameraId, 'ALL')">종합</button>
        <button :class="{ active: getModelTab(cam.cameraId) === 'RULE_BASED' }" @click="setModelTab(cam.cameraId, 'RULE_BASED')">Rule-Based</button>
        <button :class="{ active: getModelTab(cam.cameraId) === 'ISOLATION_FOREST' }" @click="setModelTab(cam.cameraId, 'ISOLATION_FOREST')">Isolation Forest</button>
        <button :class="{ active: getModelTab(cam.cameraId) === 'LSTM_AE' }" @click="setModelTab(cam.cameraId, 'LSTM_AE')">LSTM-AE</button>
      </div>

      <!-- 종합 탭 -->
      <template v-if="getModelTab(cam.cameraId) === 'ALL'">
        <div class="pdm-cam-body">
          <div class="pdm-gauge-wrap">
            <div class="pdm-css-gauge" :class="riskClass(cam.riskLevel)">
              <svg viewBox="0 0 120 72" aria-hidden="true">
                <path class="pdm-css-gauge-track" pathLength="100" d="M 12 60 A 48 48 0 0 1 108 60" />
                <path class="pdm-css-gauge-value" pathLength="100" d="M 12 60 A 48 48 0 0 1 108 60" :style="{ strokeDashoffset: gaugeDashOffset(cam.healthScore) }" />
              </svg>
              <strong>{{ cam.healthScore }}</strong>
              <span>인식 품질 점수</span>
            </div>
          </div>
          <div class="pdm-cam-metrics">
            <div class="pdm-metric-row">
              <span>OCR 신뢰도</span>
              <div class="pdm-mini-bar-wrap"><div class="pdm-mini-bar pdm-blue" :style="`width:${animatedPercent(cam.avgOcrConfidence)}%`"></div></div>
              <b>{{ formatPercent(cam.avgOcrConfidence) }}</b>
            </div>
            <div class="pdm-metric-row">
              <span>인식 성공률</span>
              <div class="pdm-mini-bar-wrap"><div class="pdm-mini-bar pdm-blue" :style="`width:${animatedPercent(cam.successRate)}%`"></div></div>
              <b>{{ formatPercent(cam.successRate) }}</b>
            </div>
            <div class="pdm-metric-row">
              <span>전후방 일치율</span>
              <div class="pdm-mini-bar-wrap"><div class="pdm-mini-bar" :class="riskClass(cam.riskLevel)" :style="`width:${animatedPercent(cam.matchRate)}%`"></div></div>
              <b>{{ formatPercent(cam.matchRate) }}</b>
            </div>
            <div class="pdm-metric-row">
              <span>미검출률</span>
              <div class="pdm-mini-bar-wrap"><div class="pdm-mini-bar pdm-warn" :style="`width:${animatedPercent(cam.missingRate)}%`"></div></div>
              <b>{{ formatPercent(cam.missingRate) }}</b>
            </div>
            <!-- 모델별 Health Score 요약 -->
            <div class="pdm-model-mini-scores">
              <div class="pdm-model-mini-row">
                <span>Rule-Based</span>
                <b :class="cam.modelType === 'RULE_BASED' ? riskClass(cam.riskLevel) : ''">{{ cam.modelType === 'RULE_BASED' ? cam.healthScore : '—' }}</b>
              </div>
              <div class="pdm-model-mini-row">
                <span>Isolation Forest</span>
                <b :class="cam.modelType === 'ISOLATION_FOREST' ? riskClass(cam.riskLevel) : ''">{{ cam.modelType === 'ISOLATION_FOREST' ? cam.healthScore : '—' }}</b>
              </div>
              <div class="pdm-model-mini-row">
                <span>LSTM-AE</span>
                <b :class="cam.modelType === 'LSTM_AE' ? riskClass(cam.riskLevel) : ''">{{ cam.modelType === 'LSTM_AE' ? cam.healthScore : '—' }}</b>
              </div>
            </div>
          </div>
        </div>
        <div class="pdm-cam-reason">
          <p><span>예상 원인</span>{{ messageLabel(cam.reasonText) }}</p>
          <p><span>권장 점검</span>{{ messageLabel(cam.recommendedAction) }}</p>
        </div>
      </template>

      <!-- 개별 모델 탭 -->
      <template v-else>
        <template v-if="cam.modelType === getModelTab(cam.cameraId)">
          <div class="pdm-model-result-body">
            <div class="pdm-gauge-wrap">
              <div class="pdm-css-gauge" :class="riskClass(cam.riskLevel)">
                <svg viewBox="0 0 120 72" aria-hidden="true">
                  <path class="pdm-css-gauge-track" pathLength="100" d="M 12 60 A 48 48 0 0 1 108 60" />
                  <path class="pdm-css-gauge-value" pathLength="100" d="M 12 60 A 48 48 0 0 1 108 60" :style="{ strokeDashoffset: gaugeDashOffset(cam.healthScore) }" />
                </svg>
                <strong>{{ cam.healthScore }}</strong>
                <span>인식 품질 점수</span>
              </div>
            </div>
            <div class="pdm-model-result-info">
              <span class="pdm-risk-badge" :class="riskClass(cam.riskLevel)">{{ riskLabel(cam.riskLevel) }}</span>
              <div style="margin-top:10px;display:grid;gap:6px">
                <p style="margin:0;font-size:12px;color:#dce9f8"><span style="display:block;margin-bottom:3px;color:#9fb2cb;font-size:11px">분석 이유</span>{{ messageLabel(cam.reasonText) }}</p>
                <p style="margin:0;font-size:12px;color:#dce9f8"><span style="display:block;margin-bottom:3px;color:#9fb2cb;font-size:11px">권장 조치</span>{{ messageLabel(cam.recommendedAction) }}</p>
              </div>
            </div>
          </div>
        </template>
        <div v-else class="pdm-model-pending">
          <span>분석 결과 대기 중</span>
          <small>FastAPI 분석 완료 후 자동 업데이트됩니다</small>
        </div>
      </template>
    </article>
  </section>

  <!-- 품질 추세 + 최근 알림 -->
  <section class="pdm-mid-grid">

    <article class="panel pdm-trend-panel">
      <div class="panel-head">
        <h2>품질 지표 추세</h2>
        <div class="pdm-cam-tabs">
          <button
            v-for="cam in cameras"
            :key="cam.cameraId"
            type="button"
            :class="{ active: selectedCam === cam.cameraId }"
            @click="selectedCam = cam.cameraId"
          >{{ cam.cameraCode }}</button>
        </div>
      </div>
      <EChartsPanel :option="trendOption" :height="220" />
    </article>

    <article class="panel pdm-alert-panel">
      <div class="panel-head">
        <h2>최근 이상 알림</h2>
        <span class="pdm-count-chip">{{ alerts.length }}</span>
      </div>
      <div class="pdm-alert-list pdm-alert-list--scroll">
        <div
          v-for="alert in alerts"
          :key="alert.alertId"
          class="pdm-alert-row"
        >
          <div class="pdm-alert-dot" :class="riskClass(alert.riskLevel)"></div>
          <div class="pdm-alert-body">
            <b>{{ alertTitleLabel(alert.alertTitle) }}</b>
            <span>{{ alert.cameraCode }}{{ alertLaneLabel(alert) ? ' · ' + alertLaneLabel(alert) : '' }}</span>
            <em>{{ messageLabel(alert.reasonText) }}</em>
          </div>
          <div class="pdm-alert-meta">
            <span class="pdm-badge" :class="alertStatusClass(alert.status)">{{ alertStatusLabel(alert.status) }}</span>
            <time>{{ formatTime(alert.createdAt) }}</time>
          </div>
        </div>
      </div>
    </article>
  </section>

  <!-- 전후방 비교 결과 테이블 -->
  <article class="panel pdm-compare-panel">
    <div class="panel-head">
      <h2>전후방 비교 결과</h2>
      <small>최근 30분 · {{ compareResults.length }}건</small>
    </div>
    <table class="pdm-compare-table">
      <thead>
        <tr>
          <th>이벤트 그룹</th>
          <th>차로</th>
          <th>전방 OCR</th>
          <th>후방 OCR</th>
          <th>일치</th>
          <th>불일치 유형</th>
          <th>신뢰도 차이</th>
          <th>비교 시각</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="row in compareResults" :key="row.compareId" :class="{ 'pdm-mismatch-row': !row.isMatched }">
          <td class="pdm-mono">{{ row.eventGroupKey }}</td>
          <td><span class="pdm-lane-chip">{{ row.laneName }}</span></td>
          <td class="pdm-mono">{{ row.frontPlateText ?? '—' }}</td>
          <td class="pdm-mono" :class="{ 'pdm-text-warn': !row.isMatched }">{{ row.rearPlateText ?? '미검출' }}</td>
          <td><span class="pdm-badge" :class="row.isMatched ? 'pdm-badge-ok' : 'pdm-badge-warn'">{{ row.isMatched ? '일치' : '불일치' }}</span></td>
          <td>{{ mismatchTypeLabel(row.mismatchType) }}</td>
          <td>{{ row.confidenceGap != null ? row.confidenceGap.toFixed(2) : '—' }}</td>
          <td>{{ formatDateTime(row.comparedAt) }}</td>
        </tr>
      </tbody>
    </table>
  </article>

</section>
</template>
