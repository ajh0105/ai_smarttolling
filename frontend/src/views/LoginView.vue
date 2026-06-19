<script setup>
import { reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import SiteHeader from '@/components/SiteHeader.vue'

const auth = useAuthStore()
const router = useRouter()
const route = useRoute()

const form = reactive({ email: '', password: '', keepLoggedIn: false })
const message = ref('')
const messageType = ref('error')

async function handleSubmit() {
  message.value = ''
  if (!form.email || !form.password) {
    message.value = '이메일과 비밀번호를 입력해 주세요.'
    messageType.value = 'error'
    return
  }
  const result = await auth.login({ email: form.email, password: form.password })
  if (result.ok) {
    // 로그인 후 항상 홈으로 (redirect 쿼리가 있으면 그곳으로).
    // 관리자는 헤더의 '대시보드' 버튼을 통해 직접 진입한다.
    const redirect = route.query.redirect ?? '/'
    router.push(redirect)
  } else {
    message.value = result.message || '로그인에 실패했습니다.'
    messageType.value = 'error'
  }
}

function notReady() {
  alert('소셜 로그인은 준비 중입니다.')
}
</script>

<template>
  <div class="site-shell">
    <SiteHeader />

    <main class="auth-main">
      <div class="auth-wrap">
        <aside class="auth-side">
          <RouterLink to="/" class="brand brand-white" aria-label="HiFive">
            <span class="brand-mark" aria-hidden="true"><i></i><i></i><i></i><i></i><i></i><i></i><i></i></span>
            <span style="font-size: 26px;">Hi-Five</span>
          </RouterLink>

          <h2>위성 GPS 기반<br />차세대 Hi-Pass 시스템에<br />다시 오신 것을 환영합니다.</h2>
          <p class="side-lead">로그인하시고 Hi-Five의 모든 서비스를 이용해 보세요.</p>

          <ul>
            <li>실시간 통합 관제 데이터 열람</li>
            <li>개발일지 작성 및 댓글 참여</li>
            <li>비즈니스 협력 · 제휴 문의</li>
          </ul>
        </aside>

        <section class="auth-card" aria-labelledby="login-title">
          <h1 id="login-title">로그인</h1>
          <p class="subtitle">Hi-Five 계정으로 로그인해주세요.</p>

          <form class="auth-form" @submit.prevent="handleSubmit">
            <div class="form-row">
              <label for="login-email">이메일</label>
              <input v-model.trim="form.email" type="email" id="login-email" placeholder="이메일을 입력하세요" autocomplete="email" />
            </div>
            <div class="form-row">
              <label for="login-pw">비밀번호</label>
              <input v-model="form.password" type="password" id="login-pw" placeholder="비밀번호를 입력하세요" autocomplete="current-password" />
            </div>

            <div class="auth-options">
              <label><input v-model="form.keepLoggedIn" type="checkbox" /> 로그인 상태 유지</label>
            </div>

            <p
              v-if="message"
              class="auth-message"
              :style="{ color: messageType === 'error' ? '#e2493e' : '#0b7a3f', fontSize: '13px', margin: '4px 0 -4px', fontWeight: 600 }"
            >{{ message }}</p>

            <button class="btn primary full lg" type="submit" :disabled="auth.loading">
              {{ auth.loading ? '로그인 중…' : '로그인' }}
            </button>
          </form>

          <div class="auth-divider">소셜 계정으로 로그인</div>

          <div class="social-login">
            <button class="social-btn" type="button" @click="notReady"><span class="ic kakao"></span> 카카오로 시작하기</button>
            <button class="social-btn" type="button" @click="notReady"><span class="ic naver"></span> 네이버로 시작하기</button>
            <button class="social-btn" type="button" @click="notReady"><span class="ic google"></span> Google로 시작하기</button>
          </div>

          <div class="auth-footer">
            아직 회원이 아니신가요?<RouterLink to="/signup">회원가입</RouterLink>
          </div>
        </section>
      </div>
    </main>

    <footer class="footer">
      <div class="inner footer-inner">
        <div class="footer-brand">
          <RouterLink class="brand brand-white" to="/" aria-label="HiFive">
            <span class="brand-mark" aria-hidden="true"><i></i><i></i><i></i><i></i><i></i><i></i><i></i></span>
            <span>HiFive</span>
          </RouterLink>
          <p>HiFive는 위성 GPS 기반 Hi-Pass 시스템을 주제로<br />5명의 팀원이 함께 기획·개발한 팀 프로젝트입니다.</p>
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
