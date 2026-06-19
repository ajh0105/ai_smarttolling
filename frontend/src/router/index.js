import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

// 대시보드(/dashboard, /master-admin)는 MASTER_ADMIN 만 진입 가능.
// 일반 회원이 직접 URL로 접근하면 홈으로 리다이렉트.
const routes = [
  { path: '/', name: 'home', component: () => import('@/views/HomeView.vue') },
  { path: '/login', name: 'login', component: () => import('@/views/LoginView.vue'), meta: { hideAuth: true } },
  { path: '/signup', name: 'signup', component: () => import('@/views/SignupView.vue'), meta: { hideAuth: true } },
  { path: '/board', name: 'board', component: () => import('@/views/BoardListView.vue') },
  { path: '/board/write', name: 'board-write', component: () => import('@/views/BoardWriteView.vue') },
  { path: '/board/:id', name: 'board-detail', component: () => import('@/views/BoardDetailView.vue') },
  { path: '/about', name: 'about', component: () => import('@/views/AboutView.vue') },
  { path: '/service', name: 'service', component: () => import('@/views/ServiceView.vue') },
  { path: '/dashboard', name: 'dashboard', component: () => import('@/views/ControlStatusDashboardView.vue'), meta: { requiresAuth: true, requiresMasterAdmin: true } },
  { path: '/master-admin', name: 'master-admin', component: () => import('@/views/MasterAdminDashboardView.vue'), meta: { requiresAuth: true, requiresMasterAdmin: true } },
  { path: '/:pathMatch(.*)*', redirect: { name: 'home' } }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior(to, from, savedPosition) {
    // hash가 있으면 해당 요소로 부드럽게 스크롤 (예: /#contact)
    if (to.hash) {
      return { el: to.hash, behavior: 'smooth', top: 60 }
    }
    // 뒤로가기 등으로 돌아갈 때는 이전 위치 복원
    if (savedPosition) return savedPosition
    return { top: 0 }
  }
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  auth.hydrate()
  // 미로그인 → 로그인 페이지로
  if (to.meta.requiresAuth && !auth.isLoggedIn) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
  // 대시보드는 MASTER_ADMIN 전용. 일반 회원은 홈으로
  if (to.meta.requiresMasterAdmin && !auth.isMasterAdmin) {
    return { name: 'home' }
  }
  // 이미 로그인된 상태에서 /login·/signup 접근 시 → 홈으로
  // (대시보드는 헤더의 '대시보드' 버튼을 통해 직접 진입)
  if (to.meta.hideAuth && auth.isLoggedIn) {
    return { name: 'home' }
  }
})

export default router
