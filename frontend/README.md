# HiFive — Frontend (Vue 3 + Pinia)

기존 정적 HTML 페이지들을 **Vue 3 + Pinia + Vue Router + Tailwind CSS** 기반의 SPA 로 새롭게 구성했습니다.
Spring Boot 백엔드의 회원/게시판 API 와 연동되며, 관제 대시보드는 실시간 통과 로그/KPI 를 보여줍니다.

## 폴더 구조

```
frontend/
├── index.html
├── package.json
├── vite.config.js
├── tailwind.config.js
├── postcss.config.js
└── src/
    ├── main.js                # Vue + Pinia + Router 부트스트랩
    ├── App.vue                # 공통 레이아웃
    ├── router/index.js        # 9개 라우트 + 인증 가드
    ├── api/                   # axios 인스턴스 + 백엔드 API 래퍼
    │   ├── client.js
    │   ├── auth.js
    │   └── board.js
    ├── stores/                # Pinia 스토어
    │   ├── auth.js
    │   └── board.js
    ├── components/
    │   ├── AppHeader.vue
    │   └── AppFooter.vue
    ├── styles/main.css        # Tailwind + HiFive 디자인 토큰
    └── views/                 # 9 페이지
        ├── HomeView.vue       # (구) index.html
        ├── LoginView.vue
        ├── SignupView.vue
        ├── DashboardView.vue  # 실시간 KPI + 통과 로그 + 게시판
        ├── CompanyView.vue
        ├── SolutionView.vue
        ├── TechnologyView.vue
        ├── GuideView.vue
        └── ContactView.vue
```

## 빠른 시작

```bash
cd frontend
npm install
npm run dev          # http://localhost:5173
npm run build        # dist/ 빌드
npm run preview      # 빌드 결과 프리뷰
```

`vite.config.js` 의 dev proxy 가 `/api/*` 요청을 `http://localhost:8080` (Spring Boot) 로 전달합니다.

## 백엔드 API 매핑

| 화면 / 기능 | Pinia Action | HTTP |
|---|---|---|
| 회원가입 | `useAuthStore().signUp()` | `POST /api/auth/signup` |
| 로그인 | `useAuthStore().login()` | `POST /api/auth/login` |
| 로그아웃 | `useAuthStore().logout()` | `POST /api/auth/logout` |
| 게시판 목록 | `useBoardStore().fetchAll()` | `GET /api/board` |
| 게시판 등록 | `useBoardStore().create()` | `POST /api/board` |

세션 기반 인증을 사용하므로 axios 는 `withCredentials: true` 로 동작합니다.

## 라우팅

| 경로 | 컴포넌트 | 비고 |
|---|---|---|
| `/` | HomeView | 랜딩 |
| `/login` | LoginView | 로그인 시 차단 |
| `/signup` | SignupView | 로그인 시 차단 |
| `/dashboard` | DashboardView | **요인증** |
| `/company` | CompanyView | 회사소개 |
| `/solution` | SolutionView | 솔루션 |
| `/technology` | TechnologyView | 기술스택 |
| `/guide` | GuideView | 도입 안내 |
| `/contact` | ContactView | 1:1 문의 |

## 디자인 토큰

| 변수 | 색상 | 용도 |
|---|---|---|
| `--deep` | `#080C18` | 배경 강조 |
| `--navy` | `#0B1840` | 본문 |
| `--blue` | `#1B3BE8` | 브랜드 |
| `--sky` | `#38BEF5` | 강조 |
| `--cloud` | `#E8EFFE` | 페이지 배경 |

폰트: `Big Shoulders Display` (헤드라인) · `Fira Mono` (코드 라벨) · `Noto Sans KR` (본문).

## 다음 단계 권장

1. `npm install` 후 `npm run dev` 로 백엔드와 연결 확인
2. Spring Security CORS 설정에 `http://localhost:5173` 허용 (`allowCredentials=true`)
3. 대시보드 mock 데이터를 실제 gRPC 결과로 교체 (백엔드에 SSE/WebSocket 채널 추가 권장)
