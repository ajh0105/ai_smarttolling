# Hi-Five — 위성 GPS 기반 차세대 스마트 하이패스 시스템

> 팀 Hi-Five가 함께 기획·개발한 풀스택 + AI 팀 프로젝트입니다.

---

## 데모 링크

| 구분 | URL | 비고 |
|---|---|---|
| **프론트엔드 포트폴리오** | [GitHub Pages 링크] | 정적 빌드 · 백엔드 없이 UI 확인 가능 |
| **AI 모델 (번호판 인식)** | [Hugging Face Space 링크] | YOLO + OCR 실시간 추론 데모 |

> AI 모델 기능(번호판 인식, RAG 챗봇 등)은 무거운 GPU 서버가 필요하기 때문에
> Hugging Face Spaces에 별도 배포하였습니다.
> GitHub Pages에서는 Vue 기반 프론트엔드 UI를 중심으로 확인할 수 있습니다.

---

## 프로젝트 개요

| 항목 | 내용 |
|---|---|
| 주제 | KPS(위성 측위) + AI를 활용한 차세대 하이패스 통행 관제 플랫폼 |
| 팀명 | Hi-Five (5인 팀) |
| 기간 | 2025년 ~ 2026년 |
| 주요 기술 | Vue 3 · Spring Boot · FastAPI · YOLO · RAG 챗봇 · PostgreSQL |

---

## 아키텍처

```
┌──────────────────────────────────────────────────────────┐
│                      GitHub Pages                        │
│              Vue 3 프론트엔드 (정적 빌드)                    │
│  홈 · 소개 · 서비스 · 관제 대시보드 UI · 게시판               │
└──────────────────┬───────────────────────────────────────┘
                   │  REST API  (로컬/운영 환경에서만 연결)
┌──────────────────▼───────────────────────────────────────┐
│              Spring Boot 백엔드                           │
│  회원/인증 · 정산 · 통행 이벤트 · 게시판 API               │
└──────────────────┬───────────────────────────────────────┘
                   │
     ┌─────────────┴──────────────────┐
     │                                │
┌────▼──────────────┐    ┌────────────▼───────────────────┐
│  FastAPI Edge 서버  │    │     PostgreSQL DB               │
│  WebTransport ·    │    │  통행/정산/GPS/챗봇 지식 청크     │
│  Ingress 관리      │    └────────────────────────────────┘
└────────────────────┘

     별도 배포 (Hugging Face Spaces)
┌──────────────────────────────────────────┐
│  AI 모델 서버                              │
│  · YOLOv8s 번호판 탐지 (TensorRT/Jetson)  │
│  · OCR 차량번호 인식                       │
│  · RAG 챗봇 (Ollama + pgvector)           │
└──────────────────────────────────────────┘
```

---

## 주요 기능

### 프론트엔드 (GitHub Pages에서 확인 가능)

- **홈 페이지** — 서비스 소개, 통계, 파트너 섹션
- **소개 페이지** — 기획 배경, 팀원 소개, 개발 일정
- **서비스 페이지** — 속도·정확도·안정성·운영 4대 가치 소개
- **통합 관제 대시보드** — 실시간 통행 이벤트, GPS, 정산, 장비 모니터링 UI
- **마스터 어드민** — 회원 관리, 감사 로그, 시스템 제어 UI
- **게시판** — 개발 일지, 공지사항 CRUD

### AI 기능 (Hugging Face에서 체험 가능)

- **번호판 탐지** — YOLOv8s · 50K 파일럿 학습 · AI Hub 데이터셋
- **OCR 인식** — 차량번호 문자 추출 (TensorRT 최적화, Jetson 엣지 추론)
- **RAG 챗봇** — 정산·GPS·통행 이벤트 업무 현황 질의응답 (Ollama + pgvector)

---

## 기술 스택

### 프론트엔드

| 분류 | 기술 |
|---|---|
| UI 프레임워크 | Vue 3 (Composition API) |
| 빌드 도구 | Vite 5 |
| 상태 관리 | Pinia |
| 라우팅 | Vue Router 4 |
| 스타일 | Tailwind CSS 3 |
| 차트 | ECharts 6, Chart.js |
| 동영상 스트리밍 | HLS.js |
| HTTP 클라이언트 | Axios |

### 백엔드

| 분류 | 기술 |
|---|---|
| API 서버 | Spring Boot |
| 인증 | Spring Session (세션 쿠키) |
| Edge 서버 | FastAPI + WebTransport |
| DB | PostgreSQL (pgvector 확장) |
| 메시지 직렬화 | Protocol Buffers |

### AI / 엣지

| 분류 | 기술 |
|---|---|
| 번호판 탐지 | YOLOv8s (Ultralytics) |
| 엣지 추론 | NVIDIA Jetson + TensorRT |
| OCR | 커스텀 파이프라인 |
| 챗봇 LLM | Ollama (Gemma 4, Qwen) |
| 벡터 검색 | pgvector |

---

## 로컬 실행

### 프론트엔드

```bash
cd frontend
npm install
npm run dev
# http://localhost:5173
```

> 백엔드(Spring Boot)가 실행되지 않으면 API 호출은 실패하지만
> 홈, 소개, 서비스 페이지는 정상적으로 확인할 수 있습니다.

### FastAPI Edge 서버

```bash
cd fastapi-edge
pip install -r requirements.txt
uvicorn app.main:app --reload --port 8000
```

자세한 내용은 [fastapi-edge/README.md](fastapi-edge/README.md)를 참고하세요.

### RAG 챗봇

```bash
# Ollama 및 Docker 필요
cd chatbot
python -m venv .venv && .venv\Scripts\activate
pip install -r requirements.txt
docker compose up -d   # PostgreSQL + pgvector
python app.py
```

자세한 내용은 [chatbot_README.md](chatbot_README.md)를 참고하세요.

---

## GitHub Pages 배포 구조

이 프로젝트는 **두 가지 환경**으로 나뉘어 배포됩니다.

| 환경 | 목적 | 이유 |
|---|---|---|
| **GitHub Pages** | Vue 프론트엔드 UI 시연 | 정적 파일만 서빙하면 되므로 무료·간단 |
| **Hugging Face Spaces** | AI 모델 실시간 추론 데모 | GPU 서버 및 Python 런타임이 필요 |

GitHub Pages에는 `vite build`로 생성된 정적 파일만 올라갑니다.
백엔드 API, DB, AI 모델 서버는 포함되지 않으므로 관제 대시보드 등 인증이 필요한 기능은
실제 서버 환경에서만 동작합니다.

배포는 `.github/workflows/deploy.yml`을 통해 `main` 브랜치 push 시 자동으로 실행됩니다.

---

## AI 모델 보고서

- [YOLO 학습 보고서](yolo_REPORT.md) — YOLOv8s 50K 파일럿 학습 결과
- [RAG 챗봇 설명](chatbot_README.md) — Flask + Ollama + pgvector 챗봇 구조

---

## 팀 Hi-Five

| 역할 | 담당 |
|---|---|
| 프론트엔드 | Vue 대시보드 · 랜딩 페이지 · UI/UX |
| 백엔드 | Spring Boot API · 인증 · 정산 · 통행 |
| AI / 엣지 | YOLO · OCR · Jetson 엣지 추론 · RAG 챗봇 |
| DB · 인프라 | PostgreSQL · Docker · CI/CD |
| 기획 · 협업 | 요구사항 분석 · Git 관리 · 문서화 |

---

© 2026 Hi-Five Team. All Rights Reserved.
