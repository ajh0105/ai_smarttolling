<script setup>
import SiteHeader from '@/components/SiteHeader.vue'
</script>

<template>
  <div class="site-shell is-home">
    <SiteHeader />

    <main id="top">
      <section class="hero" id="company">
        <img class="hero-bg" src="/index-assets/img/hero.png" alt="Hi-Five 위성 기반 Hi-Pass 시스템" />
        <div class="hero-shade"></div>

        <div class="inner hero-inner">
          <div class="hero-copy">
            <h1>KPS와 AI로 완성하는<br />차세대 스마트 하이패스</h1>
            <p>차량과 도로, 결제 데이터를 하나로 연결해<br />미래형 고속도로 인프라를 현실로 만듭니다.</p>
            <a class="pill-button hero-button" href="#service">Hi-Five 소개 <span aria-hidden="true">&rarr;</span></a>
          </div>

          <div class="hero-features" aria-label="핵심 기능">
            <article>
              <span class="feature-icon">
                <svg viewBox="0 0 32 32" aria-hidden="true">
                  <path d="m5 22 5 5 5-5-5-5-5 5ZM17 10l5 5 5-5-5-5-5 5ZM12 19l7-7M10 17 7 8M15 22l9 3"></path>
                </svg>
              </span>
              <strong>위성 GPS 기반</strong>
              <small>정밀 통행 인식</small>
            </article>
            <article>
              <span class="feature-icon">
                <svg viewBox="0 0 32 32" aria-hidden="true">
                  <rect x="7" y="6" width="18" height="20" rx="3"></rect>
                  <path d="M11 11h10M11 15h6M10 22h12M12 19l2-2 3 3 3-4"></path>
                </svg>
              </span>
              <strong>Hi-Pass</strong>
              <small>스마트 결제</small>
            </article>
            <article>
              <span class="feature-icon">
                <svg viewBox="0 0 32 32" aria-hidden="true">
                  <path d="M10 23h14a5 5 0 0 0 .5-10 7.5 7.5 0 0 0-14.2-2.8A6.5 6.5 0 0 0 10 23Z"></path>
                  <path d="M16 16v8M12 20h8"></path>
                </svg>
              </span>
              <strong>통합 관제 플랫폼</strong>
              <small>실시간 모니터링</small>
            </article>
            <article>
              <span class="feature-icon">
                <svg viewBox="0 0 32 32" aria-hidden="true">
                  <path d="M16 4 7 8v6c0 6 3.8 10.8 9 13 5.2-2.2 9-7 9-13V8l-9-4Z"></path>
                  <path d="m11.5 16 3 3 6-7"></path>
                </svg>
              </span>
              <strong>안정성·신뢰성</strong>
              <small>국가 인프라 운영</small>
            </article>
          </div>

          <div class="hero-stats">
            <div>
              <span>일 통행 처리</span>
              <strong>1,250만 건+</strong>
            </div>
            <div>
              <span>전국 커버리지</span>
              <strong>100%</strong>
            </div>
            <div>
              <span>시스템 가동률</span>
              <strong>99.99%</strong>
            </div>
          </div>
        </div>
      </section>

      <section class="mission">
        <div class="inner mission-inner">
          <div class="mission-title">
            <span>우리의 미션</span>
            <h2>국가 교통 인프라의 혁신으로<br />국민의 이동을 더 가치 있게</h2>
          </div>
          <p class="mission-text">
            Hi-Five는 위성, 통신, 빅데이터 기술을 결합하여<br />
            고속도로의 효율적인 운영과 안전한 이동 경험을 제공하고,<br />
            미래 모빌리티 생태계를 선도합니다.
          </p>
          <img class="mission-art" src="/index-assets/img/mission-lineart.png" alt="" />
        </div>
      </section>

      <section class="services" id="service">
        <div class="inner service-grid">
          <article class="service-card control-card">
            <h3>통합 관제 플랫폼</h3>
            <p>전국 고속도로의 통행 관제·장애 데이터를<br />하나의 플랫폼에서 실시간 통합 관제</p>
            <div class="dashboard-shot">
              <img src="/index-assets/img/control-dashboard.png" alt="통합 관제 대시보드" />
            </div>
            <a class="small-button dark-button" href="#">자세히 보기 <span aria-hidden="true">&rarr;</span></a>
          </article>

          <article class="service-card coverage-card">
            <h3>전국 커버리지</h3>
            <p>위성 기반의 정밀한 전국 고속도로 커버리지로<br />끊김 없는 서비스 제공</p>
            <div class="korea-map">
              <img src="/index-assets/img/korea-network-map.png" alt="전국 네트워크 지도" />
            </div>
            <ul class="map-legend">
              <li><i class="blue"></i>운영 중</li>
              <li><i class="orange"></i>구축 예정</li>
              <li><i class="soft-blue"></i>확장 예정</li>
            </ul>
            <a class="small-button" href="#">자세히 보기 <span aria-hidden="true">&rarr;</span></a>
          </article>

          <article class="service-card data-card" id="effect">
            <h3>데이터로 비즈니스 가치를 창출</h3>
            <p>정밀한 통행 데이터와 AI 분석으로 교통 흐름 예측,<br />수요 분석 및 수익 최적화 지원</p>
            <div class="data-summary">
              <div class="traffic-number">
                <span>월 통행량</span>
                <strong>12,580,000 <small>건+</small></strong>
                <em>▲ 7.2% (전월 대비)</em>
              </div>
              <div class="vehicle-share">
                <div class="donut" aria-hidden="true"></div>
                <ul>
                  <li><i class="blue"></i>승용차 <b>72%</b></li>
                  <li><i class="soft-blue"></i>화물차 <b>24%</b></li>
                  <li><i class="gray"></i>기타 <b>4%</b></li>
                </ul>
              </div>
            </div>
            <div class="bar-chart">
              <b>시간대별 통행량</b>
              <div class="bars" aria-hidden="true">
                <i style="height:18%"></i><i style="height:30%"></i><i style="height:42%"></i><i style="height:56%"></i>
                <i style="height:70%"></i><i style="height:82%"></i><i style="height:97%"></i><i style="height:90%"></i>
                <i style="height:76%"></i><i style="height:64%"></i><i style="height:50%"></i><i style="height:34%"></i>
              </div>
              <span>0시&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;4시&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;8시&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;12시&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;16시&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;20시</span>
            </div>
            <a class="small-button" href="#">자세히 보기 <span aria-hidden="true">&rarr;</span></a>
          </article>

          <article class="service-card stable-card">
            <h3>운영 안정성</h3>
            <p>이중화된 시스템과 24/7 모니터링으로<br />최고 수준의 안정성 보장</p>
            <ul class="stable-list">
              <li>
                <span>
                  <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 20a8 8 0 1 0-8-8M12 7v5l3 2"></path></svg>
                </span>
                <p><b>시스템 가동률</b><em>99.999%</em></p>
              </li>
              <li>
                <span>
                  <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M4 12a8 8 0 0 1 16 0M4 12v4h4v-4H4Zm12 0v4h4v-4h-4Z"></path></svg>
                </span>
                <p><b>24/7 관제 센터</b><em>365일 무중단 운영</em></p>
              </li>
              <li>
                <span>
                  <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M6 20h12V8l-6-4-6 4v12ZM9 20v-7h6v7"></path></svg>
                </span>
                <p><b>재해 복구 시스템</b><em>이중화 및 백업 체계</em></p>
              </li>
              <li>
                <span>
                  <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 3 5 6v5c0 5 3 8.5 7 10 4-1.5 7-5 7-10V6l-7-3Z"></path><path d="m8.5 12 2.5 2.5 5-5"></path></svg>
                </span>
                <p><b>보안 인증</b><em>ISMS-P, ISO/IEC 27001</em></p>
              </li>
            </ul>
            <a class="small-button" href="#">자세히 보기 <span aria-hidden="true">&rarr;</span></a>
          </article>
        </div>
      </section>

      <section class="partners" id="team">
        <div class="inner partners-inner">
          <div class="partners-copy">
            <span>함께하는 파트너</span>
            <p>국가 인프라의 동반자로서 함께 성장합니다.</p>
          </div>
          <div class="partner-logos" aria-label="파트너 로고">
            <span class="ministry"><i></i>국토교통부</span>
            <span class="ex"><b>ex</b> 한국도로공사</span>
            <span class="transport"><i></i>한국교통연구원</span>
            <span class="kt">kt</span>
            <span class="lgu">LG U<sup>+</sup></span>
            <span class="sk">SK telecom</span>
          </div>
        </div>
      </section>

      <section class="contact" id="contact">
        <div class="inner contact-inner">
          <div class="contact-copy">
            <h2>문의 및 상담</h2>
            <p>Hi-Five가 만드는 스마트한 고속도로,<br />지금 바로 문의하세요.</p>
            <a class="pill-button contact-button" href="#">문의하기 <span aria-hidden="true">&rarr;</span></a>
          </div>

          <address class="contact-info">
            <p>
              <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M22 16.5v3a2 2 0 0 1-2.2 2 19.8 19.8 0 0 1-8.6-3.1 19.5 19.5 0 0 1-6-6A19.8 19.8 0 0 1 2 3.8 2 2 0 0 1 4 1.6h3a2 2 0 0 1 2 1.7l.5 3a2 2 0 0 1-.6 1.8L7.7 9.3a16 16 0 0 0 7 7l1.2-1.2a2 2 0 0 1 1.8-.6l3 .5a2 2 0 0 1 1.3 1.5Z"></path></svg>
              <span>02-1234-5678</span>
            </p>
            <p>
              <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M4 5h16v14H4z"></path><path d="m4 7 8 6 8-6"></path></svg>
              <span>contact@hifive.co.kr</span>
            </p>
            <p>
              <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 21s7-5.5 7-12a7 7 0 1 0-14 0c0 6.5 7 12 7 12Z"></path><circle cx="12" cy="9" r="2.5"></circle></svg>
              <span>서울 강남구 테헤란로 253<br />에스빌딩 6층 78</span>
            </p>
          </address>

          <article class="proposal-card">
            <img src="/index-assets/img/img02.png" alt="" />
            <div>
              <h3>제품 및 사업 문의</h3>
              <p>기술 제휴, 서비스 연계, 비즈니스 협력 등<br />다양한 제안을 기다립니다.</p>
              <a class="pill-button proposal-button" href="#">제휴 문의하기 <span aria-hidden="true">&rarr;</span></a>
            </div>
          </article>
        </div>
      </section>
    </main>

    <footer class="footer" id="log">
      <div class="inner footer-inner">
        <div class="footer-brand">
          <a class="brand brand-white" href="#top" aria-label="Hi-Five">
            <span class="brand-mark" aria-hidden="true">
              <i></i><i></i><i></i><i></i><i></i><i></i><i></i>
            </span>
            <span>Hi-Five</span>
          </a>
          <p>Hi-Five는 위성 GPS 기반 Hi-Pass 시스템을 주제로<br />5명의 팀원이 함께 기획·개발한 팀 프로젝트입니다.</p>
          <small>© 2026 Hi-Five Team. All Rights Reserved.</small>
        </div>

        <nav class="footer-nav" aria-label="하단 메뉴">
          <div>
            <h4>소개</h4>
            <RouterLink to="/about#why">기획 배경</RouterLink>
            <RouterLink to="/about#project">프로젝트 소개</RouterLink>
            <RouterLink to="/about#members">팀원 소개</RouterLink>
            <RouterLink to="/about#timeline">개발 일정</RouterLink>
          </div>
          <div>
            <h4>서비스</h4>
            <RouterLink to="/service#frontend">프론트엔드</RouterLink>
            <RouterLink to="/service#backend">백엔드</RouterLink>
            <RouterLink to="/service#infra">DB · 인프라</RouterLink>
            <RouterLink to="/service#tools">협업 도구</RouterLink>
          </div>
          <div>
            <h4>개발일지</h4>
            <RouterLink to="/board">전체 글</RouterLink>
            <RouterLink to="/board">공지</RouterLink>
            <RouterLink to="/board">개발 노트</RouterLink>
            <RouterLink to="/board/write">글쓰기</RouterLink>
          </div>
          <div>
            <h4>문의</h4>
            <a href="#contact">문의하기</a>
            <RouterLink to="/login">로그인</RouterLink>
            <RouterLink to="/signup">회원가입</RouterLink>
          </div>
        </nav>

        <div class="footer-side">
          <div class="policy">
            <a href="#">개인정보처리방침</a>
            <span></span>
            <a href="#">이용약관</a>
          </div>
          <button class="family" type="button">
            <span>Family Site</span>
            <svg viewBox="0 0 24 24" aria-hidden="true"><path d="m7 10 5 5 5-5"></path></svg>
          </button>
          <div class="social">
            <a href="#" aria-label="LinkedIn">in</a>
            <a href="#" aria-label="YouTube">
              <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M22 12s0-3.5-.4-5a2.8 2.8 0 0 0-2-2C17.8 4.5 12 4.5 12 4.5s-5.8 0-7.6.5a2.8 2.8 0 0 0-2 2C2 8.5 2 12 2 12s0 3.5.4 5a2.8 2.8 0 0 0 2 2c1.8.5 7.6.5 7.6.5s5.8 0 7.6-.5a2.8 2.8 0 0 0 2-2c.4-1.5.4-5 .4-5Z"></path><path d="m10 9 5 3-5 3V9Z"></path></svg>
            </a>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>
