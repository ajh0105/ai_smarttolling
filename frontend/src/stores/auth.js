import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/auth'

const STORAGE_KEY = 'hifive.member'

const DEMO_MODE = import.meta.env.VITE_DEMO_MODE === 'true'

const DEMO_ACCOUNTS = [
  {
    email: 'user@hifive.demo',
    password: 'demo1234',
    member: { email: 'user@hifive.demo', memberName: '데모 사용자', plateNumber: '12가3456', role: 'USER', assignedDashboardId: null }
  },
  {
    email: 'admin@hifive.demo',
    password: 'admin1234',
    member: { email: 'admin@hifive.demo', memberName: '관리자 (데모)', plateNumber: '00가0000', role: 'MASTER_ADMIN', assignedDashboardId: 1 }
  }
]

export const useAuthStore = defineStore('auth', () => {
  // member: { email, memberName, plateNumber, role, assignedDashboardId } | null
  const member = ref(null)
  const loading = ref(false)
  const error = ref(null)

  const isLoggedIn = computed(() => member.value !== null)
  const isMasterAdmin = computed(() => member.value?.role === 'MASTER_ADMIN')
  const assignedDashboardId = computed(() => member.value?.assignedDashboardId ?? null)

  // 새로고침 후 멤버 정보 복구
  function hydrate() {
    try {
      const raw = localStorage.getItem(STORAGE_KEY)
      if (raw) member.value = JSON.parse(raw)
    } catch {
      member.value = null
    }
  }

  function persist() {
    if (member.value) {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(member.value))
    } else {
      localStorage.removeItem(STORAGE_KEY)
    }
  }

  async function signUp(form) {
    if (DEMO_MODE) {
      return { ok: false, message: '데모 환경에서는 회원가입을 지원하지 않습니다. 아래 데모 계정으로 로그인해 주세요.' }
    }
    loading.value = true
    error.value = null
    try {
      const { data } = await authApi.signUp(form)
      return { ok: true, message: data.message }
    } catch (err) {
      const msg = err?.response?.data?.message ?? '회원가입에 실패했습니다.'
      error.value = msg
      return { ok: false, message: msg }
    } finally {
      loading.value = false
    }
  }

  async function login(form) {
    if (DEMO_MODE) {
      const account = DEMO_ACCOUNTS.find(
        (a) => a.email === form.email && a.password === form.password
      )
      if (account) {
        member.value = account.member
        persist()
        return { ok: true, message: '데모 로그인 성공' }
      }
      return { ok: false, message: '이메일 또는 비밀번호가 올바르지 않습니다.' }
    }
    loading.value = true
    error.value = null
    try {
      const { data } = await authApi.login(form)
      member.value = data.member
      persist()
      return { ok: true, message: data.message }
    } catch (err) {
      const msg = err?.response?.data?.message ?? '로그인에 실패했습니다.'
      error.value = msg
      return { ok: false, message: msg }
    } finally {
      loading.value = false
    }
  }

  async function logout() {
    if (DEMO_MODE) {
      member.value = null
      persist()
      return
    }
    try {
      await authApi.logout()
    } finally {
      member.value = null
      persist()
    }
  }

  return { member, loading, error, isLoggedIn, isMasterAdmin, assignedDashboardId, hydrate, signUp, login, logout }
})
