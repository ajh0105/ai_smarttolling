<script setup>
import { computed } from 'vue'
import { useRoute, useRouter, RouterLink } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const isAboutActive = computed(() => route.name === 'about')
const isServiceActive = computed(() => route.name === 'service')
const isBoardActive = computed(() => ['board', 'board-write', 'board-detail'].includes(route.name))

async function handleLogout() {
  await auth.logout()
  if (route.name && ['dashboard', 'master-admin'].includes(route.name)) {
    router.push('/')
  }
}
</script>

<template>
  <header class="site-header">
    <div class="inner header-inner">
      <RouterLink class="brand" to="/" aria-label="HiFive">
        <span class="brand-mark" aria-hidden="true"><i></i><i></i><i></i><i></i><i></i><i></i><i></i></span>
        <span>Hi-Five</span>
      </RouterLink>
      <nav class="main-nav" aria-label="주요 메뉴">
        <RouterLink to="/about" :class="{ active: isAboutActive }">소개</RouterLink>
        <RouterLink to="/service" :class="{ active: isServiceActive }">서비스</RouterLink>
        <RouterLink to="/board" :class="{ active: isBoardActive }">개발일지</RouterLink>
        <RouterLink to="/#contact">문의하기</RouterLink>
      </nav>
      <div class="header-actions">
        <template v-if="!auth.isLoggedIn">
          <RouterLink class="login-button" to="/login" aria-label="로그인">
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4"></path>
              <path d="M10 17l5-5-5-5M15 12H3"></path>
            </svg>
            <span>로그인</span>
          </RouterLink>
        </template>
        <template v-else>
          <RouterLink
            v-if="auth.isMasterAdmin"
            class="login-button"
            to="/master-admin"
            aria-label="대시보드"
          >
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <rect x="3" y="3" width="7" height="9" rx="1"></rect>
              <rect x="14" y="3" width="7" height="5" rx="1"></rect>
              <rect x="14" y="12" width="7" height="9" rx="1"></rect>
              <rect x="3" y="16" width="7" height="5" rx="1"></rect>
            </svg>
            <span>대시보드</span>
          </RouterLink>
          <a class="login-button" href="#" aria-label="로그아웃" @click.prevent="handleLogout">
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
              <path d="m16 17 5-5-5-5M21 12H9"></path>
            </svg>
            <span>로그아웃</span>
          </a>
        </template>
      </div>
    </div>
  </header>
</template>

<style scoped>
.header-actions :deep(.login-button + .login-button) {
  margin-left: 8px;
}
</style>
