<script setup>
// 4가치 카테고리(속도/정확도/안정성/운영) 구성.
// 각 섹션에 사진 1장 — frontend/public/ 에 동일 파일명으로 추가하면 자동 표시.
import SiteHeader from '@/components/SiteHeader.vue'

// Vite 정적 분석을 우회하기 위해 const 변수로 src 분리
const speedSrc = '/01.png'
const accuracySrc = '/02.png'
const resilienceSrc = '/03.png'
const operationsSrc = '/04.png'

function showPlaceholder(e) {
  const img = e.target
  img.style.display = 'none'
  if (img.nextElementSibling && img.nextElementSibling.classList.contains('image-placeholder')) {
    img.nextElementSibling.style.display = 'flex'
  }
}
</script>

<template>
  <div class="site-shell">
    <SiteHeader />

    <main>
      <section class="sub-hero">
        <img class="sub-hero-bg" src="/index-assets/img/hero.png" alt="" />
        <div class="sub-hero-shade"></div>
        <div class="inner">
          <nav class="breadcrumb"><RouterLink to="/">홈</RouterLink><span>›</span><span>서비스</span></nav>
          <span class="eyebrow">SERVICE VALUE</span>
          <h1>속도 · 정확도 · 안정성 · 운영<br />네 가지 가치로 설계된 인프라</h1>
        </div>
      </section>

      <section class="page-section" style="padding: 28px 0 0;">
        <div class="inner">
          <nav class="tab-nav" aria-label="서비스 카테고리">
            <a href="#speed" class="active">속도</a>
            <a href="#accuracy">정확도</a>
            <a href="#resilience">안정성</a>
            <a href="#operations">운영</a>
          </nav>
        </div>
      </section>

      <!-- ============ SPEED ============ -->
      <section class="page-section" id="speed" style="padding-top: 40px;">
        <div class="inner">
          <div class="value-row">
            <div class="value-text">
              <span class="eyebrow">SPEED</span>
              <h2 class="value-title">속도</h2>
              <p class="value-lead">1번의 추론을 <strong>30ms 안으로</strong>.</p>
              <div class="value-kpi">
                <span class="value-kpi-num">30</span><span class="value-kpi-unit">ms</span>
              </div>
              <ul class="value-chips">
                <li>Jetson</li>
                <li>TensorRT</li>
                <li>CUDA</li>
                <li>GStreamer</li>
                <li>WebTransport (저지연)</li>
              </ul>
            </div>
            <figure class="value-figure">
              <img :src="speedSrc" alt="속도 — Jetson Edge 추론 파이프라인" @error="showPlaceholder" />
              <div class="image-placeholder">01.png</div>
            </figure>
          </div>
        </div>
      </section>

      <!-- ============ ACCURACY ============ -->
      <section class="page-section tinted" id="accuracy">
        <div class="inner">
          <div class="value-row reverse">
            <div class="value-text">
              <span class="eyebrow">ACCURACY</span>
              <h2 class="value-title">정확도</h2>
              <p class="value-lead">차로 단위 <strong>10cm 정밀 인식</strong>.</p>
              <div class="value-kpi">
                <span class="value-kpi-num">10</span><span class="value-kpi-unit">cm</span>
              </div>
              <ul class="value-chips">
                <li>YOLO 입력 영상 Crop</li>
                <li>CRNN-OCR</li>
                <li>Best-fit 누적 분석</li>
                <li>KPS 정밀 측위</li>
              </ul>
            </div>
            <figure class="value-figure">
              <img :src="accuracySrc" alt="정확도 — OCR 정밀 인식" @error="showPlaceholder" />
              <div class="image-placeholder">02.png</div>
            </figure>
          </div>
        </div>
      </section>

      <!-- ============ RESILIENCE ============ -->
      <section class="page-section" id="resilience">
        <div class="inner">
          <div class="value-row">
            <div class="value-text">
              <span class="eyebrow">RESILIENCE</span>
              <h2 class="value-title">안정성</h2>
              <p class="value-lead">Watchdog 감지 후 <strong>3초 안에 자동 복구</strong>.</p>
              <div class="value-kpi">
                <span class="value-kpi-num">3</span><span class="value-kpi-unit">초</span>
              </div>
              <ul class="value-chips">
                <li>TLS 1.3</li>
                <li>QUIC</li>
                <li>LTE Failover</li>
                <li>Watchdog 자동 감지</li>
              </ul>
            </div>
            <figure class="value-figure">
              <img :src="resilienceSrc" alt="안정성 — 망 이중화 및 Failover" @error="showPlaceholder" />
              <div class="image-placeholder">03.png</div>
            </figure>
          </div>
        </div>
      </section>

      <!-- ============ OPERATIONS ============ -->
      <section class="page-section tinted" id="operations">
        <div class="inner">
          <div class="value-row reverse">
            <div class="value-text">
              <span class="eyebrow">OPERATIONS</span>
              <h2 class="value-title">운영</h2>
              <p class="value-lead">통합 운영 스택과 <strong>대화형 어시스턴트</strong>까지.</p>
              <div class="value-kpi">
                <span class="value-kpi-num">5</span><span class="value-kpi-unit">stack</span>
              </div>
              <ul class="value-chips">
                <li>FastAPI</li>
                <li>Spring Boot</li>
                <li>PostgreSQL</li>
                <li>Vue Dashboard</li>
                <li>Chat Bot</li>
              </ul>
            </div>
            <figure class="value-figure">
              <img :src="operationsSrc" alt="운영 — 통합 관제 대시보드" @error="showPlaceholder" />
              <div class="image-placeholder">04.png</div>
            </figure>
          </div>
        </div>
      </section>

      <section class="cta-band">
        <div class="inner">
          <h2>더 자세한 기술 이야기가 궁금하다면</h2>
          <p>개발일지에서 자세한 진행 과정을, 문의하기에서 직접 연락할 수 있어요.</p>
          <div style="display: flex; justify-content: center; gap: 10px;">
            <RouterLink class="btn glass lg" to="/board">개발일지 보기</RouterLink>
            <RouterLink class="btn glass lg" to="/#contact">문의하기 →</RouterLink>
          </div>
        </div>
      </section>
    </main>

    <footer class="footer">
      <div class="inner footer-inner">
        <div class="footer-brand">
          <RouterLink class="brand brand-white" to="/" aria-label="Hi-Five">
            <span class="brand-mark" aria-hidden="true"><i></i><i></i><i></i><i></i><i></i><i></i><i></i></span>
            <span>Hi-Five</span>
          </RouterLink>
          <p>Hi-Five는 위성 GPS 기반 Hi-Pass 시스템을 주제로<br />5명의 팀원이 함께 기획·개발한 팀 프로젝트입니다.</p>
          <small>© 2026 Hi-Five Team. All Rights Reserved.</small>
        </div>
        <nav class="footer-nav" aria-label="하단 메뉴">
          <div><h4>소개</h4><RouterLink to="/about#why">기획 배경</RouterLink><RouterLink to="/about#project">프로젝트 소개</RouterLink><RouterLink to="/about#members">팀원 소개</RouterLink><RouterLink to="/about#timeline">개발 일정</RouterLink></div>
          <div><h4>서비스</h4><RouterLink to="/service#speed">속도</RouterLink><RouterLink to="/service#accuracy">정확도</RouterLink><RouterLink to="/service#resilience">안정성</RouterLink><RouterLink to="/service#operations">운영</RouterLink></div>
          <div><h4>개발일지</h4><RouterLink to="/board">전체 글</RouterLink><RouterLink to="/board">공지</RouterLink><RouterLink to="/board">개발 노트</RouterLink><RouterLink to="/board/write">글쓰기</RouterLink></div>
          <div><h4>문의</h4><RouterLink to="/#contact">문의하기</RouterLink><RouterLink to="/login">로그인</RouterLink><RouterLink to="/signup">회원가입</RouterLink></div>
        </nav>
        <div class="footer-side">
          <div class="policy"><a href="#">개인정보처리방침</a><span></span><a href="#">이용약관</a></div>
          <button class="family" type="button"><span>Family Site</span><svg viewBox="0 0 24 24" aria-hidden="true"><path d="m7 10 5 5 5-5"></path></svg></button>
          <div class="social"><a href="#" aria-label="LinkedIn">in</a><a href="#" aria-label="YouTube"><svg viewBox="0 0 24 24" aria-hidden="true"><path d="M22 12s0-3.5-.4-5a2.8 2.8 0 0 0-2-2C17.8 4.5 12 4.5 12 4.5s-5.8 0-7.6.5a2.8 2.8 0 0 0-2 2C2 8.5 2 12 2 12s0 3.5.4 5a2.8 2.8 0 0 0 2 2c1.8.5 7.6.5 7.6.5s5.8 0 7.6-.5a2.8 2.8 0 0 0 2-2c.4-1.5.4-5 .4-5Z"></path><path d="m10 9 5 3-5 3V9Z"></path></svg></a></div>
        </div>
      </div>
    </footer>
  </div>
</template>

<style scoped>
.value-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(0, 1.1fr);
  gap: 56px;
  align-items: center;
}
.value-row.reverse {
  grid-template-columns: minmax(0, 1.1fr) minmax(0, 1fr);
}
.value-row.reverse .value-text {
  order: 2;
}
.value-row.reverse .value-figure {
  order: 1;
}

.value-text .eyebrow {
  font-family: ui-monospace, SFMono-Regular, Menlo, monospace;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.18em;
  color: #1b3be8;
}
.value-title {
  margin: 8px 0 12px;
  font-size: 42px;
  font-weight: 900;
  color: #0b1840;
  letter-spacing: -0.04em;
  line-height: 1;
}
.value-lead {
  margin: 0 0 20px;
  font-size: 17px;
  font-weight: 600;
  color: #1f2937;
  line-height: 1.6;
}
.value-lead strong {
  color: #1b3be8;
  font-weight: 800;
}

.value-kpi {
  display: inline-flex;
  align-items: baseline;
  gap: 6px;
  margin: 8px 0 22px;
  padding: 16px 24px;
  background: linear-gradient(135deg, #eef5ff 0%, #dbeafe 100%);
  border: 1px solid #9fc3ee;
  border-radius: 14px;
  box-shadow: 0 4px 14px rgba(31, 115, 190, 0.10);
}
.value-kpi-num {
  font-size: 64px;
  font-weight: 900;
  color: #1b3be8;
  letter-spacing: -0.05em;
  line-height: 1;
}
.value-kpi-unit {
  font-size: 22px;
  font-weight: 800;
  color: #1b3be8;
  letter-spacing: -0.02em;
}

.value-chips {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.value-chips li {
  display: inline-flex;
  padding: 8px 14px;
  background: #ffffff;
  border: 1px solid #c7d8ff;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 700;
  color: #0b4383;
  letter-spacing: -0.01em;
}

.value-figure {
  position: relative;
  margin: 0;
}
.value-figure img {
  display: block;
  width: 100%;
  height: auto;
  aspect-ratio: 16 / 10;
  object-fit: cover;
  border-radius: 18px;
  box-shadow: 0 16px 38px rgba(11, 24, 64, 0.12);
}
.value-figure .image-placeholder {
  display: none;
  align-items: center;
  justify-content: center;
  width: 100%;
  aspect-ratio: 16 / 10;
  border: 2px dashed rgba(11, 67, 131, 0.22);
  border-radius: 18px;
  color: rgba(11, 67, 131, 0.5);
  font-family: ui-monospace, SFMono-Regular, Menlo, monospace;
  font-size: 13px;
  letter-spacing: 0.04em;
  background: rgba(11, 67, 131, 0.03);
}

/* Dark / Tinted 섹션에서의 색 보정 */
.page-section.dark .value-title { color: #ffffff; }
.page-section.dark .value-lead { color: rgba(255, 255, 255, 0.85); }
.page-section.dark .value-chips li {
  background: rgba(255, 255, 255, 0.06);
  border-color: rgba(255, 255, 255, 0.18);
  color: #ffffff;
}

@media (max-width: 920px) {
  .value-row,
  .value-row.reverse {
    grid-template-columns: 1fr;
    gap: 28px;
  }
  .value-row.reverse .value-text,
  .value-row.reverse .value-figure {
    order: initial;
  }
}
</style>
