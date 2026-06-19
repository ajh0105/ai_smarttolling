<script setup>
import { computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import DemoChatbot from '@/components/DemoChatbot.vue'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const route = useRoute()

const isDashboardRoute = computed(() => ['dashboard', 'master-admin'].includes(route.name))

onMounted(() => {
  auth.hydrate()
  // 이전 빌드/세션에서 body·html에 추가됐을 수 있는 잔존 클래스 청소
  document.documentElement.classList.remove('dashboard-route-active')
  document.body.classList.remove('dashboard-route-active')
})
</script>

<template>
  <!-- 각 페이지 컴포넌트가 자체 SiteHeader/footer를 가지므로 글로벌 chrome은 두지 않는다.
       (예전 AppHeader 'SMART TOLLING / 회사소개 / 솔루션 / 기술스택 ...'이 라우트 전환 중 깜빡 노출되던 문제 제거) -->
  <div class="min-h-screen flex flex-col" :class="{ 'dashboard-app-shell': isDashboardRoute }">
    <div class="flex-1 app-router-slot" :class="{ 'dashboard-app-main': isDashboardRoute }">
      <router-view />
    </div>
    <!-- 챗봇은 대시보드(/dashboard, /master-admin)에서만 노출 -->
    <DemoChatbot v-if="isDashboardRoute" />
  </div>
</template>

<style>
/* dashboard 라우트 전용 fullscreen wrapper.
   wrapper 자체에만 적용하므로 dashboard에서 다른 페이지로 나가면 자동 복원된다. */
.dashboard-app-shell {
  position: fixed;
  inset: 0;
  width: 100vw;
  height: 100dvh;
  min-height: 100dvh;
  margin: 0;
  padding: 0;
  overflow: hidden;
  background: #eef3f8;
  z-index: 1;
}

.dashboard-app-main {
  display: block;
  width: 100%;
  height: 100%;
  min-height: 0;
  margin: 0;
  padding: 0;
  overflow: auto;
  background: #eef3f8;
}

.dashboard-app-main .master-shell,
.dashboard-app-main .ops-shell {
  width: 100%;
  min-width: 1440px;
  min-height: 100%;
  height: auto;
  margin: 0;
}
</style>
