<script setup>
import SiteHeader from '@/components/SiteHeader.vue'
</script>

<style scoped>
/* 프로젝트 카드 상단 "주요 기능 NN" 라벨 */
.card .card-tag {
  display: block;
  font-family: ui-monospace, SFMono-Regular, Menlo, monospace;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.18em;
  color: #1b3be8;
  margin: 8px 0 4px;
}
.card.dark .card-tag {
  color: #2ec7ff;
}
.card h3 {
  margin-top: 4px;
}
</style>

<template>
  <div class="site-shell">
    <SiteHeader />

    <main>
      <section class="sub-hero">
        <img class="sub-hero-bg" src="/index-assets/img/hero.png" alt="" />
        <div class="sub-hero-shade"></div>
        <div class="inner">
          <nav class="breadcrumb"><RouterLink to="/">홈</RouterLink><span>›</span><span>소개</span></nav>
          <span class="eyebrow">TEAM HI-FIVE</span>
          <h1>함께 만든<br />팀 프로젝트, Hi-Five</h1>
          <p class="lead">위성 GPS 기반 차세대 Hi-Pass 시스템을 5명의 팀원이 함께 기획하고 개발했습니다.</p>
        </div>
      </section>

      <section class="page-section" style="padding: 28px 0 0;">
        <div class="inner">
          <nav class="tab-nav" aria-label="소개 메뉴">
            <a href="#why" class="active">기획 배경</a>
            <a href="#project">프로젝트 소개</a>
            <a href="#members">팀원 소개</a>
            <a href="#timeline">개발 일정</a>
          </nav>
        </div>
      </section>

      <section class="page-section" id="why" style="padding-top: 40px;">
        <div class="inner">
          <div class="intro-row">
            <div class="intro-title">
              <span class="eyebrow">WHY SMART HI-PASS</span>
              <h2 style="margin: 13px 0 0; font-size: 26px; line-height: 1.4; letter-spacing: -0.065em; font-weight: 900; color: #10213a;">
                왜 스마트하이패스를<br />기획하게 되었나요?
              </h2>
            </div>
            <div class="intro-body">
              하이패스의 편리함을 바탕으로, 차량 통행 정보와 정산 데이터를<br />
              더 체계적으로 관리할 수 있는 방법을 고민했습니다.
            </div>
          </div>

          <div class="grid cols-3" style="margin-top: 44px;">
            <article class="card">
              <span class="card-icon"><svg viewBox="0 0 24 24" aria-hidden="true"><circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/></svg></span>
              <h3>문제 인식</h3>
              <p>차량번호, 차종, 통행 시간, 차로, 정산 상태를 정확하게 관리하는 것이 중요하다고 보았습니다.</p>
            </article>
            <article class="card">
              <span class="card-icon"><svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 2v4M12 18v4M4 12H2M22 12h-2M6 6 4 4M20 20l-2-2M6 18l-2 2M20 4l-2 2"/><circle cx="12" cy="12" r="4"/></svg></span>
              <h3>아이디어</h3>
              <p>번호판 인식 기술로 차량 통행 정보를 수집하고, 대시보드에서 정산 현황을 확인할 수 있도록 구성했습니다.</p>
            </article>
            <article class="card">
              <span class="card-icon"><svg viewBox="0 0 24 24" aria-hidden="true"><path d="m5 12 4 4 10-10"/></svg></span>
              <h3>제공 가치</h3>
              <p>사용자는 통행 정보를 쉽게 확인하고, 관리자는 차량 데이터와 정산 상태를 효율적으로 관리할 수 있습니다.</p>
            </article>
          </div>
        </div>
      </section>

      <section class="page-section dark" id="project">
        <div class="inner">
          <div class="section-heading">
            <span class="eyebrow">PROJECT OVERVIEW</span>
            <h2>Hi-Five는 어떤 프로젝트인가요?</h2>
            <p>프로젝트의 주요 기능과 특징을 소개합니다.</p>
          </div>
          <div class="grid cols-4">
            <article class="card dark">
              <span class="card-icon"><svg viewBox="0 0 24 24" aria-hidden="true"><rect x="3" y="7" width="18" height="10" rx="2"/><path d="M7 11h2M11 11h2M15 11h2M7 14h10"/></svg></span>
              <small class="card-tag">CRNN-OCR 번호판 인식</small>
              <p>차량 번호판의 숫자와 한글을 인식하여 통행 차량 정보를 자동으로 추출합니다.</p>
            </article>
            <article class="card dark">
              <span class="card-icon"><svg viewBox="0 0 24 24" aria-hidden="true"><rect x="3" y="4" width="6" height="6" rx="1"/><rect x="3" y="14" width="6" height="6" rx="1"/><rect x="15" y="9" width="6" height="6" rx="1"/><path d="M9 7h6M9 17h6"/></svg></span>
              <small class="card-tag">다중 프레임 추론 로직</small>
              <p>여러 프레임의 OCR 결과를 누적 분석해 가장 신뢰도 높은 결과를 선택합니다.</p>
            </article>
            <article class="card dark">
              <span class="card-icon"><svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 21s7-5.5 7-12a7 7 0 1 0-14 0c0 6.5 7 12 7 12Z"/><circle cx="12" cy="9" r="2.5"/></svg></span>
              <small class="card-tag">GPS / KPS 위치 추적</small>
              <p>차량의 위치와 차로 흐름을 추적해 통행 경로 판단의 정확도를 높입니다.</p>
            </article>
            <article class="card dark">
              <span class="card-icon"><svg viewBox="0 0 24 24" aria-hidden="true"><path d="M2 12a10 10 0 0 1 20 0M5 12a7 7 0 0 1 14 0M8 12a4 4 0 0 1 8 0"/><circle cx="12" cy="12" r="1.5"/></svg></span>
              <small class="card-tag">WebTransport 실시간 전송</small>
              <p>QUIC 기반 통신으로 인식 데이터를 빠르고 안정적으로 서버에 전달합니다.</p>
            </article>
          </div>
        </div>
      </section>

      <section class="page-section" id="members">
        <div class="inner">
          <div class="section-heading center" style="text-align: center;">
            <span class="eyebrow">OUR TEAM</span>
            <h2>Hi-Five를 만든 5명의 팀원</h2>
            <p>각자의 전문 분야로 프로젝트를 함께 완성한 사람들입니다.</p>
          </div>

          <div class="grid cols-5">
            <article class="team-card featured">
              <div class="team-avatar has-photo"><img src="/member01.png" alt="팀원 01" /></div>
              <span class="role">팀장 · 기획</span>
              <h3>이름 01</h3>
              <p>프로젝트 총괄 / 일정 관리</p>
              <div class="team-skills"><span>PM</span><span>기획</span></div>
              <div class="socials"><a href="#">in</a><a href="#">@</a></div>
            </article>
            <article class="team-card">
              <div class="team-avatar has-photo"><img src="/member02.png" alt="팀원 02" /></div>
              <span class="role">프론트엔드</span>
              <h3>이름 02</h3>
              <p>UI 컴포넌트 / 화면 개발</p>
              <div class="team-skills"><span>React</span><span>JS</span></div>
              <div class="socials"><a href="#">in</a><a href="#">@</a></div>
            </article>
            <article class="team-card">
              <div class="team-avatar has-photo"><img src="/member03.png" alt="팀원 03" /></div>
              <span class="role">백엔드</span>
              <h3>이름 03</h3>
              <p>API 설계 / 서버 구현</p>
              <div class="team-skills"><span>Spring</span><span>JPA</span></div>
              <div class="socials"><a href="#">in</a><a href="#">@</a></div>
            </article>
            <article class="team-card">
              <div class="team-avatar has-photo"><img src="/member04.png" alt="팀원 04" /></div>
              <span class="role">디자인</span>
              <h3>이름 04</h3>
              <p>UI/UX 디자인 / 프로토타입</p>
              <div class="team-skills"><span>Figma</span><span>UX</span></div>
              <div class="socials"><a href="#">in</a><a href="#">@</a></div>
            </article>
            <article class="team-card">
              <div class="team-avatar has-photo"><img src="/member05.png" alt="팀원 05" /></div>
              <span class="role">DB · 인프라</span>
              <h3>이름 05</h3>
              <p>데이터베이스 / 배포</p>
              <div class="team-skills"><span>MySQL</span><span>AWS</span></div>
              <div class="socials"><a href="#">in</a><a href="#">@</a></div>
            </article>
          </div>
        </div>
      </section>

      <section class="page-section tinted" id="timeline">
        <div class="inner">
          <div class="section-heading">
            <span class="eyebrow">TIMELINE</span>
            <h2>프로젝트 진행 과정</h2>
          </div>
          <ul class="timeline">
            <li><span class="year">Week 1 — 2</span><p class="item">기획 · 요구사항 분석 · 컨셉 정의</p></li>
            <li><span class="year">Week 3 — 4</span><p class="item">UI/UX 디자인 · 와이어프레임 · 프로토타입</p></li>
            <li><span class="year">Week 5 — 8</span><p class="item">프론트엔드 · 백엔드 개발</p></li>
            <li><span class="year">Week 9</span><p class="item">통합 테스트 · QA · 버그 수정</p></li>
            <li><span class="year">Week 10</span><p class="item">최종 발표 · 회고</p></li>
          </ul>
        </div>
      </section>

      <section class="cta-band">
        <div class="inner">
          <h2>프로젝트에 대해 더 알고 싶으세요?</h2>
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
          <div><h4>서비스</h4><RouterLink to="/service#frontend">프론트엔드</RouterLink><RouterLink to="/service#backend">백엔드</RouterLink><RouterLink to="/service#infra">DB · 인프라</RouterLink><RouterLink to="/service#tools">협업 도구</RouterLink></div>
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
.team-avatar.has-photo {
  overflow: hidden;
  background: linear-gradient(135deg, #e1ecf9 0%, #c5d8f1 100%);
}
.team-avatar.has-photo::after {
  display: none;
}
.team-avatar.has-photo img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
  display: block;
}
</style>
