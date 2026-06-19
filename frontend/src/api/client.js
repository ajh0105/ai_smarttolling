import axios from 'axios'

// HiFive 백엔드(Spring Boot) 호출용 axios 인스턴스
// dev 환경에서는 vite.config.js의 proxy 설정으로 /api가 Spring Boot로 포워딩됨
const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  withCredentials: true, // 세션 쿠키 유지 (Spring Session 기반 로그인)
  headers: { 'Content-Type': 'application/json' }
})

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (import.meta.env.DEV) {
      console.error('[API Error]', error?.response?.status, error?.response?.data)
    }
    return Promise.reject(error)
  }
)

export default apiClient
