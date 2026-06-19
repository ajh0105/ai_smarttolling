import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/auth'

const STORAGE_KEY = 'hifive.member'

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
    try {
      await authApi.logout()
    } finally {
      member.value = null
      persist()
    }
  }

  return { member, loading, error, isLoggedIn, isMasterAdmin, assignedDashboardId, hydrate, signUp, login, logout }
})
