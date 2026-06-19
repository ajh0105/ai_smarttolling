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
<section class="panel empty-page">
        <h1>{{ activeMenu }}</h1>
        <p>{{ centerLabel }} · {{ selectedLaneText }} 기준 상세 화면입니다.</p>
        <button type="button" @click="activeMenu = '대시보드'">대시보드로 돌아가기</button>
      </section>
</template>
