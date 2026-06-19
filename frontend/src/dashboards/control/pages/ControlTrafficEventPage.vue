<script setup>
import { inject, ref, computed, onMounted, onBeforeUnmount } from 'vue'

const {
  activeMenu,
  centerLabel,
  selectedLaneText,
  selectedLane,
  dashboardKpis,
  operatorPassageEvents,
  dashboardDetections,
  statusCards,
  filteredGpsJudgements,
  fieldAlerts,
  filteredTrafficRows,
  currentDateText,
  equipmentCards,
  equipmentLaneRows,
  equipmentAlerts,
  historyRows
} = inject('controlDashboardContext')

const showReviewModal = ref(false)
const showGpsModal = ref(false)
const showEvidenceModal = ref(false)

const filterLane = ref('전체')
const filterDir = ref('전체')
const filterGps = ref('전체')
const showEventOnly = ref(false)
const openDropdown = ref(null)
const selectedNo = ref(1)
const selectedEventId = ref('')
const searchInput = ref('')
const searchQuery = ref('')

const fallbackEvents = [
  { no:1,  eventId:'mock-1',  plate:'12가 3456', lane:'2차선', dir:'OUT', time:'17:35:42.289', gps:'정상',    gpsS:'ok',     pay:'결제 가능', payS:'ok',     evt:'',         evtS:''       },
  { no:2,  eventId:'mock-2',  plate:'45나 6721', lane:'2차선', dir:'IN',  time:'17:33:18.102', gps:'정상',    gpsS:'ok',     pay:'결제 가능', payS:'ok',     evt:'',         evtS:''       },
  { no:3,  eventId:'mock-3',  plate:'67다 9012', lane:'1차선', dir:'OUT', time:'17:31:41.552', gps:'정상',    gpsS:'ok',     pay:'검수 필요', payS:'warn',   evt:'차선 경계',  evtS:'warn'   },
  { no:4,  eventId:'mock-4',  plate:'98머 3344', lane:'2차선', dir:'IN',  time:'17:28:55.873', gps:'정상',    gpsS:'ok',     pay:'검수 필요', payS:'warn',   evt:'정차 의심',  evtS:'danger' },
  { no:5,  eventId:'mock-5',  plate:'34다 1122', lane:'2차선', dir:'OUT', time:'17:26:17.430', gps:'영역 이탈',gpsS:'danger', pay:'결제 불가', payS:'danger', evt:'',         evtS:''       },
  { no:6,  eventId:'mock-6',  plate:'56라 7788', lane:'1차선', dir:'IN',  time:'17:23:50.219', gps:'정상',    gpsS:'ok',     pay:'결제 가능', payS:'ok',     evt:'',         evtS:''       },
  { no:7,  eventId:'mock-7',  plate:'12바 1234', lane:'2차선', dir:'OUT', time:'17:21:38.901', gps:'정상',    gpsS:'ok',     pay:'검수 필요', payS:'warn',   evt:'기준 구역 외',evtS:'danger' },
  { no:8,  eventId:'mock-8',  plate:'90허 5678', lane:'1차선', dir:'IN',  time:'17:19:12.654', gps:'정상',    gpsS:'ok',     pay:'결제 가능', payS:'ok',     evt:'',         evtS:''       },
  { no:9,  eventId:'mock-9',  plate:'22거 4567', lane:'2차선', dir:'IN',  time:'17:17:05.338', gps:'정상',    gpsS:'ok',     pay:'결제 가능', payS:'ok',     evt:'',         evtS:''       },
  { no:10, eventId:'mock-10', plate:'11서 2233', lane:'1차선', dir:'OUT', time:'17:14:46.072', gps:'영역 이탈',gpsS:'danger', pay:'결제 불가', payS:'danger', evt:'',         evtS:''       },
]

const events = computed(() => {
  if (!operatorPassageEvents?.value?.length) {
    return fallbackEvents.map((event) => ({
      ...event,
      date: currentDateText.value
    }))
  }
  return operatorPassageEvents.value.map((event, index) => ({
    no: index + 1,
    eventId: event.eventId || `event-${index}`,
    plate: event.plate,
    lane: `${event.lane}차선`,
    dir: event.direction,
    date: event.date || currentDateText.value,
    time: event.time,
    gps: event.gps,
    gpsS: event.tone === 'danger' ? 'danger' : 'ok',
    pay: event.status,
    payS: event.tone === 'danger' ? 'danger' : event.tone === 'boundary' ? 'warn' : 'ok',
    evt: event.tone === 'danger' ? '검수 필요' : '',
    evtS: event.tone === 'danger' ? 'danger' : '',
    eventImageUrl: event.eventImageUrl,
    cropImageUrl: event.cropImageUrl
  }))
})

const selectedEvent = computed(() => {
  const rows = events.value
  if (selectedEventId.value) {
    const byId = rows.find(r => r.eventId === selectedEventId.value)
    if (byId) return byId
  }
  return rows.find(r => r.no === selectedNo.value) ?? rows[0] ?? { ...fallbackEvents[0], date: currentDateText.value }
})

const detailBadge = computed(() => {
  const r = selectedEvent.value
  if (r.evtS)          return { text: r.evt, cls: r.evtS }
  if (r.payS !== 'ok') return { text: r.pay, cls: r.payS }
  if (r.gpsS !== 'ok') return { text: r.gps, cls: r.gpsS }
  return { text: '정상 통과', cls: 'ok' }
})

const dirLabel = computed(() => selectedEvent.value.dir === 'IN' ? 'IN (진입)' : 'OUT (출구)')
const gpsAreaLabel = computed(() => selectedEvent.value.gpsS === 'ok' ? '정상 (영역 안)' : selectedEvent.value.gps)

const filteredEvents = computed(() => events.value.filter(r => {
  if (filterLane.value !== '전체' && r.lane !== filterLane.value) return false
  if (filterDir.value  !== '전체' && r.dir  !== filterDir.value)  return false
  if (filterGps.value  !== '전체' && r.gps  !== filterGps.value)  return false
  if (showEventOnly.value && r.gpsS === 'ok' && r.payS === 'ok' && r.evtS === '') return false
  if (searchQuery.value && !r.plate.includes(searchQuery.value)) return false
  return true
}))

function toggleDropdown(key) {
  openDropdown.value = openDropdown.value === key ? null : key
}

function setFilter(key, value) {
  if (key === 'lane') filterLane.value = value
  else if (key === 'dir') filterDir.value = value
  else if (key === 'gps') filterGps.value = value
  openDropdown.value = null
}

function runSearch() {
  searchQuery.value = searchInput.value.trim()
}

function resetFilters() {
  filterLane.value = '전체'
  filterDir.value = '전체'
  filterGps.value = '전체'
  showEventOnly.value = false
  openDropdown.value = null
  searchInput.value = ''
  searchQuery.value = ''
}

function selectEvent(row) {
  selectedNo.value = row.no
  selectedEventId.value = row.eventId
}

function onDocClick(e) {
  if (!e.target.closest('.filter-dropdown-wrap')) openDropdown.value = null
}

onMounted(() => document.addEventListener('click', onDocClick))
onBeforeUnmount(() => document.removeEventListener('click', onDocClick))
</script>

<template>
<section class="traffic-event-page">
        <section class="event-title-row">
          <h1>통행 이벤트</h1>
          <p>통행 기록과 현장 이벤트를 조회하고 검수/처리할 수 있습니다.</p>
        </section>

        <section class="event-filter-row">
          <label class="date-range"><span>기간</span><b>{{ currentDateText }} 00:00 ~ {{ currentDateText }} 23:59</b><i>▣</i></label>

          <div class="filter-dropdown-wrap">
            <button class="filter-toggle" :class="{active: filterLane !== '전체'}" type="button" @click.stop="toggleDropdown('lane')">차선 <b>{{ filterLane }}</b></button>
            <div v-if="openDropdown === 'lane'" class="filter-dropdown">
              <button :class="{chosen: filterLane==='전체'}"  @click="setFilter('lane','전체')">전체</button>
              <button :class="{chosen: filterLane==='1차선'}" @click="setFilter('lane','1차선')">1차선</button>
              <button :class="{chosen: filterLane==='2차선'}" @click="setFilter('lane','2차선')">2차선</button>
            </div>
          </div>

          <div class="filter-dropdown-wrap">
            <button class="filter-toggle" :class="{active: filterDir !== '전체'}" type="button" @click.stop="toggleDropdown('dir')">방향 <b>{{ filterDir }}</b></button>
            <div v-if="openDropdown === 'dir'" class="filter-dropdown">
              <button :class="{chosen: filterDir==='전체'}" @click="setFilter('dir','전체')">전체</button>
              <button :class="{chosen: filterDir==='IN'}"   @click="setFilter('dir','IN')">IN</button>
              <button :class="{chosen: filterDir==='OUT'}"  @click="setFilter('dir','OUT')">OUT</button>
            </div>
          </div>

          <div class="filter-dropdown-wrap">
            <button class="filter-toggle" :class="{active: filterGps !== '전체'}" type="button" @click.stop="toggleDropdown('gps')">GPS 판정 <b>{{ filterGps }}</b></button>
            <div v-if="openDropdown === 'gps'" class="filter-dropdown">
              <button :class="{chosen: filterGps==='전체'}"    @click="setFilter('gps','전체')">전체</button>
              <button :class="{chosen: filterGps==='정상'}"    @click="setFilter('gps','정상')">정상</button>
              <button :class="{chosen: filterGps==='영역 이탈'}" @click="setFilter('gps','영역 이탈')">영역 이탈</button>
            </div>
          </div>

          <button class="filter-toggle event-view-btn" :class="{active: showEventOnly}" type="button" @click="showEventOnly = !showEventOnly">이벤트 보기</button>

          <label class="event-search"><input type="search" placeholder="차량번호 검색" v-model="searchInput" @keyup.enter="runSearch" /><span class="search-icon-btn" @click="runSearch">⌕</span></label>
          <button class="reset-filter" type="button" @click="resetFilters">필터 초기화</button>
        </section>

        <section class="event-main-grid">
          <article class="panel event-list-panel">
            <table class="event-table">
              <thead>
                <tr><th>No.</th><th>차량번호</th><th>차선</th><th>방향</th><th>통과시각</th><th>GPS 판정</th><th>결제 판정</th><th>현장 이벤트</th></tr>
              </thead>
              <tbody>
                <tr v-for="row in filteredEvents" :key="row.eventId" :class="{selected: selectedEvent.eventId === row.eventId}" @click="selectEvent(row)">
                  <td>{{ row.no }}</td>
                  <td>{{ row.plate }}</td>
                  <td>{{ row.lane }}</td>
                  <td><span class="event-pill" :class="row.dir.toLowerCase()">{{ row.dir }}</span></td>
                  <td>{{ row.time }}</td>
                  <td><span class="event-state" :class="row.gpsS">{{ row.gps }}</span></td>
                  <td><span class="event-state" :class="row.payS">{{ row.pay }}</span></td>
                  <td><span v-if="row.evt" class="event-state" :class="row.evtS">{{ row.evt }}</span><span v-else>-</span></td>
                </tr>
              </tbody>
            </table>
            <footer class="event-pagination">
              <span>전체 1,248건</span>
              <div><button>‹</button><button class="active">1</button><button>2</button><button>3</button><button>4</button><button>5</button><button>…</button><button>125</button><button>›</button></div>
              <button class="filter-toggle">10개씩 보기</button>
            </footer>
          </article>

          <article class="panel event-detail-panel">
            <div class="event-detail-head">
              <h2>선택 이벤트 상세</h2>
              <span class="event-state" :class="detailBadge.cls">{{ detailBadge.text }}</span>
            </div>
            <section class="event-detail-top">
              <dl>
                <dt>차량번호</dt><dd>{{ selectedEvent.plate }}</dd>
                <dt>차선</dt><dd>{{ selectedEvent.lane }}</dd>
                <dt>방향</dt><dd>{{ dirLabel }}</dd>
                <dt>통과시각</dt><dd>{{ selectedEvent.date }} {{ selectedEvent.time }}</dd>
              </dl>
              <div class="plate-crop">
                <span>번호판 crop</span>
                <img v-if="selectedEvent.cropImageUrl" class="event-evidence-img crop" :src="selectedEvent.cropImageUrl" :alt="`${selectedEvent.plate} crop`" />
                <div v-else class="event-evidence-empty crop">{{ selectedEvent.plate || '-' }}</div>
              </div>
              <div class="event-image">
                <span>이벤트 이미지</span>
                <img v-if="selectedEvent.eventImageUrl" class="event-evidence-img" :src="selectedEvent.eventImageUrl" :alt="`${selectedEvent.plate} event`" />
                <div v-else class="event-evidence-empty">이미지 없음</div>
              </div>
            </section>

            <section class="gps-judge-card">
              <div class="event-detail-head">
                <h2>GPS 결제 영역 판정</h2>
                <span class="event-state" :class="selectedEvent.gpsS">{{ gpsAreaLabel }}</span>
              </div>
              <div class="gps-judge-body">
                <div class="gps-legend"><span class="zone">유효 통행 구역</span><span class="point">현재 GPS 위치</span><span class="lane">A 통행 차선(예시)</span></div>
                <div class="gps-map-mini"><i></i><b></b><em>A</em></div>
                <dl>
                  <dt>위도 (LAT)</dt><dd>37.491032</dd>
                  <dt>경도 (LNG)</dt><dd>126.725124</dd>
                  <dt>속도 (km/h)</dt><dd>58.6</dd>
                  <dt>방향각 (°)</dt><dd>185° (S)</dd>
                  <dt>GPS 판정</dt><dd><span class="event-state" :class="selectedEvent.gpsS">{{ selectedEvent.gps }}</span></dd>
                  <dt>결제 판정</dt><dd><span class="event-state" :class="selectedEvent.payS">{{ selectedEvent.pay }}</span></dd>
                </dl>
              </div>
            </section>

            <footer class="event-detail-actions">
              <button type="button" @click="showEvidenceModal=true">▧ 증빙 이미지</button>
              <button type="button" @click="showGpsModal=true">⌖ GPS 판정 확인</button>
              <button type="button" class="review" @click="showReviewModal=true">♙ 검수</button>
            </footer>
          </article>
        </section>

        <article class="panel event-history-panel">
          <div class="panel-head"><h2>선택 이벤트 처리 내역</h2></div>
          <table>
            <thead><tr><th>순번</th><th>처리시각</th><th>처리자</th><th>처리구분</th><th>내용</th><th>결과</th></tr></thead>
            <tbody>
              <tr><td>1</td><td>2025-05-11 17:36:02</td><td>operator01</td><td>자동 판정</td><td>GPS 영역 판정: 정상, 결제 판정: 결제 가능</td><td><span class="event-state ok">완료</span></td></tr>
              <tr><td>2</td><td>2025-05-11 17:36:12</td><td>operator01</td><td>검수 요청</td><td>정차 의심 이벤트 - 현장 확인 요청</td><td><span class="event-state warn">요청 중</span></td></tr>
              <tr><td>3</td><td>2025-05-11 17:36:28</td><td>operator02</td><td>현장 확인 결과</td><td>현장 확인 결과: 이상 없음</td><td><span class="event-state ok">완료</span></td></tr>
              <tr><td>4</td><td>2025-05-11 17:36:45</td><td>operator01</td><td>검수 완료</td><td>검수 완료 처리</td><td><span class="event-state ok">완료</span></td></tr>
            </tbody>
          </table>
        </article>
      </section>

  <!-- 증빙 이미지 모달 -->
  <Teleport to="body">
    <div v-if="showEvidenceModal" class="modal-backdrop" @click.self="showEvidenceModal=false">
      <div class="modal-panel evidence-modal-panel">
        <header class="modal-header">
          <div style="display:flex;align-items:center;gap:12px">
            <h2>증빙 이미지</h2>
            <span style="color:#9fb4ce;font-size:13px">{{ selectedEvent.plate }} · {{ selectedEvent.date }} {{ selectedEvent.time }}</span>
          </div>
          <button type="button" class="modal-close" @click="showEvidenceModal=false">✕</button>
        </header>
        <div class="modal-body evidence-modal-body">
          <section class="evidence-img-section">
            <span class="evidence-label">이벤트 이미지</span>
            <img v-if="selectedEvent.eventImageUrl" class="event-evidence-img modal" :src="selectedEvent.eventImageUrl" :alt="`${selectedEvent.plate} event`" />
            <div v-else class="event-evidence-empty modal">이미지 없음</div>
          </section>
          <section class="evidence-img-section">
            <span class="evidence-label">번호판 crop</span>
            <div class="plate-crop" style="width:100%">
              <img v-if="selectedEvent.cropImageUrl" class="event-evidence-img crop modal" :src="selectedEvent.cropImageUrl" :alt="`${selectedEvent.plate} crop`" />
              <div v-else class="event-evidence-empty crop modal">{{ selectedEvent.plate || '-' }}</div>
            </div>
          </section>
        </div>
        <footer class="modal-footer">
          <button type="button" class="modal-export">⇩ 내보내기</button>
          <button type="button" @click="showEvidenceModal=false">닫기</button>
        </footer>
      </div>
    </div>
  </Teleport>

  <!-- GPS 판정 확인 모달 -->
  <Teleport to="body">
    <div v-if="showGpsModal" class="modal-backdrop" @click.self="showGpsModal=false">
      <div class="modal-panel">
        <header class="modal-header">
          <div style="display:flex;align-items:center;gap:12px">
            <h2>GPS 결제 영역 판정 확인</h2>
            <span class="event-state ok">정상 (영역 안)</span>
          </div>
          <button type="button" class="modal-close" @click="showGpsModal=false">✕</button>
        </header>
        <div class="modal-body">
          <div class="modal-gps-meta">
            <span><b>차량번호</b>{{ selectedEvent.plate }}</span>
            <span><b>통과시각</b>{{ selectedEvent.date }} {{ selectedEvent.time }}</span>
            <span><b>차선</b>{{ selectedEvent.lane }}</span>
            <span><b>방향</b>{{ dirLabel }}</span>
          </div>
          <div class="gps-judge-body modal-gps-body">
            <div class="gps-legend"><span class="zone">유효 통행 구역</span><span class="point">현재 GPS 위치</span><span class="lane">A 통행 차선(예시)</span></div>
            <div class="gps-map-mini"><i></i><b></b><em>A</em></div>
            <dl>
              <dt>위도 (LAT)</dt><dd>37.491032</dd>
              <dt>경도 (LNG)</dt><dd>126.725124</dd>
              <dt>속도 (km/h)</dt><dd>58.6</dd>
              <dt>방향각 (°)</dt><dd>185° (S)</dd>
              <dt>GPS 판정</dt><dd><span class="event-state ok">정상</span></dd>
              <dt>결제 판정</dt><dd><span class="event-state ok">결제 가능</span></dd>
            </dl>
          </div>
        </div>
        <footer class="modal-footer">
          <button type="button" @click="showGpsModal=false">닫기</button>
        </footer>
      </div>
    </div>
  </Teleport>

  <!-- 검수 모달 -->
  <Teleport to="body">
    <div v-if="showReviewModal" class="modal-backdrop" @click.self="showReviewModal=false">
      <div class="modal-panel modal-panel-wide">
        <header class="modal-header">
          <div style="display:flex;align-items:center;gap:12px">
            <h2>검수</h2>
            <span class="event-state" :class="detailBadge.cls">{{ detailBadge.text }}</span>
          </div>
          <button type="button" class="modal-close" @click="showReviewModal=false">✕</button>
        </header>
        <div class="modal-body">
          <section class="modal-evidence-row">
            <div class="event-image" style="flex:1"><span>이벤트 이미지</span><img v-if="selectedEvent.eventImageUrl" class="event-evidence-img" :src="selectedEvent.eventImageUrl" :alt="`${selectedEvent.plate} event`" /><div v-else class="event-evidence-empty">이미지 없음</div></div>
            <div class="plate-crop" style="width:140px"><span>번호판 crop</span><img v-if="selectedEvent.cropImageUrl" class="event-evidence-img crop" :src="selectedEvent.cropImageUrl" :alt="`${selectedEvent.plate} crop`" /><div v-else class="event-evidence-empty crop">{{ selectedEvent.plate || '-' }}</div></div>
            <dl class="modal-event-dl">
              <dt>차량번호</dt><dd>{{ selectedEvent.plate }}</dd>
              <dt>차선</dt><dd>{{ selectedEvent.lane }}</dd>
              <dt>방향</dt><dd>{{ dirLabel }}</dd>
              <dt>통과시각</dt><dd>{{ selectedEvent.date }} {{ selectedEvent.time }}</dd>
            </dl>
          </section>
          <section class="modal-gps-section">
            <div class="event-detail-head" style="margin-bottom:10px"><h3 style="margin:0;font-size:15px;color:#f1f7ff">GPS 결제 영역 판정</h3><span class="event-state ok">정상 (영역 안)</span></div>
            <div class="gps-judge-body modal-gps-body">
              <div class="gps-legend"><span class="zone">유효 통행 구역</span><span class="point">현재 GPS 위치</span><span class="lane">A 통행 차선</span></div>
              <div class="gps-map-mini"><i></i><b></b><em>A</em></div>
              <dl>
                <dt>위도 (LAT)</dt><dd>37.491032</dd>
                <dt>경도 (LNG)</dt><dd>126.725124</dd>
                <dt>속도 (km/h)</dt><dd>58.6</dd>
                <dt>방향각 (°)</dt><dd>185° (S)</dd>
                <dt>GPS 판정</dt><dd><span class="event-state ok">정상</span></dd>
                <dt>결제 판정</dt><dd><span class="event-state ok">결제 가능</span></dd>
              </dl>
            </div>
          </section>
          <section class="modal-history-section">
            <h3 style="margin:0 0 8px;font-size:14px;color:#9fb4ce">처리 내역</h3>
            <table class="modal-history-table">
              <thead><tr><th>순번</th><th>처리시각</th><th>처리자</th><th>처리구분</th><th>내용</th><th>결과</th></tr></thead>
              <tbody>
                <tr><td>1</td><td>2025-05-11 17:36:02</td><td>operator01</td><td>자동 판정</td><td>GPS 영역 판정: 정상, 결제 판정: 결제 가능</td><td><span class="event-state ok">완료</span></td></tr>
                <tr><td>2</td><td>2025-05-11 17:36:12</td><td>operator01</td><td>검수 요청</td><td>정차 의심 이벤트 - 현장 확인 요청</td><td><span class="event-state warn">요청 중</span></td></tr>
                <tr><td>3</td><td>2025-05-11 17:36:28</td><td>operator02</td><td>현장 확인 결과</td><td>현장 확인 결과: 이상 없음</td><td><span class="event-state ok">완료</span></td></tr>
                <tr><td>4</td><td>2025-05-11 17:36:45</td><td>operator01</td><td>검수 완료</td><td>검수 완료 처리</td><td><span class="event-state ok">완료</span></td></tr>
              </tbody>
            </table>
          </section>
        </div>
        <footer class="modal-footer modal-review-actions">
          <button type="button" class="modal-approve">검수 승인</button>
          <button type="button" class="modal-field">현장 확인 요청</button>
          <button type="button" class="modal-reject">반려</button>
          <button type="button" @click="showReviewModal=false">닫기</button>
        </footer>
      </div>
    </div>
  </Teleport>
</template>
