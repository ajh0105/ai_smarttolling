import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

// HiFive 프론트엔드 빌드 설정
// Vue는 Spring Boot REST API만 호출한다. Python Ingress는 브라우저에서 직접 호출하지 않는다.
// GitHub Pages 배포 시 VITE_BASE_PATH 환경변수로 리포지토리 경로를 지정한다 (예: /hifive/).
export default defineConfig({
  plugins: [vue()],
  base: process.env.VITE_BASE_PATH || '/',
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    host: '0.0.0.0',
    port: 5173,
    // 시연 / 개발 중 F5에 즉시 새 CSS·HTML이 반영되도록 dev 응답을 캐시하지 않음
    headers: {
      'Cache-Control': 'no-store'
    },
    proxy: {
      '/api': {
        target: process.env.VITE_API_TARGET || 'http://localhost:8585',
        changeOrigin: true
      },
      '/video': {
        target: process.env.VITE_VIDEO_TARGET || 'http://localhost:8000',
        changeOrigin: true
      },
      // PDM FastAPI 분석 서버 (내부 관리자용)
      // 실행: cd pdm-fastapi && uvicorn app.main:app --port 8001
      // Docker 환경: VITE_PDM_FASTAPI_TARGET=http://pdm-fastapi:8001
      '/pdm-internal': {
        target: process.env.VITE_PDM_FASTAPI_TARGET || 'http://localhost:8001',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/pdm-internal/, '/internal/pdm')
      }
    }
  }
})
