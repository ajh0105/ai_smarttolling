<script setup>
import { reactive, ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import SiteHeader from '@/components/SiteHeader.vue'

const auth = useAuthStore()
const router = useRouter()

const form = reactive({
  agreeAll: false,
  agreeTerms: false,
  agreePrivacy: false,
  agreeAge: false,
  agreeMarketing: false,
  email: '',
  password: '',
  passwordConfirm: '',
  memberName: '',
  nickname: '',
  phone: '',
  authCode: ''
})
const message = ref('')
const messageType = ref('error')

const requiredAgreed = computed(() => form.agreeTerms && form.agreePrivacy && form.agreeAge)

function toggleAll(e) {
  const checked = e.target.checked
  form.agreeAll = checked
  form.agreeTerms = checked
  form.agreePrivacy = checked
  form.agreeAge = checked
  form.agreeMarketing = checked
}

function notReady() {
  alert('해당 기능은 준비 중입니다.')
}

async function handleSubmit() {
  message.value = ''
  if (!requiredAgreed.value) {
    message.value = '필수 약관에 모두 동의해 주세요.'
    messageType.value = 'error'
    return
  }
  if (!form.email || !form.password || !form.memberName) {
    message.value = '이메일, 비밀번호, 이름을 모두 입력해 주세요.'
    messageType.value = 'error'
    return
  }
  if (form.password.length < 6) {
    message.value = '비밀번호는 6자 이상이어야 합니다.'
    messageType.value = 'error'
    return
  }
  if (form.password !== form.passwordConfirm) {
    message.value = '비밀번호 확인이 일치하지 않습니다.'
    messageType.value = 'error'
    return
  }

  const payload = {
    email: form.email,
    password: form.password,
    memberName: form.memberName,
    plateNumber: ''
  }
  const result = await auth.signUp(payload)
  if (result.ok) {
    message.value = '회원가입이 완료되었습니다. 로그인 페이지로 이동합니다.'
    messageType.value = 'success'
    setTimeout(() => router.push('/login'), 900)
  } else {
    message.value = result.message || '회원가입에 실패했습니다.'
    messageType.value = 'error'
  }
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

          <h2>Hi-Five와 함께<br />차세대 교통 인프라의<br />새 챕터를 시작하세요.</h2>
          <p class="side-lead">간단한 가입 절차로 모든 서비스를 이용할 수 있습니다.</p>

          <ul>
            <li>위성 기반 실시간 통행 데이터</li>
            <li>개발일지 · 기술 자료 무제한 열람</li>
            <li>비즈니스 협력 우선 안내</li>
            <li>이벤트 · 채용 정보 알림</li>
          </ul>
        </aside>

        <section class="auth-card" aria-labelledby="signup-title" style="padding: 40px 36px 32px;">
          <h1 id="signup-title">회원가입</h1>
          <p class="subtitle">아래 정보를 입력해 가입을 완료해주세요.</p>

          <ol class="signup-steps" aria-label="가입 진행 단계">
            <li class="active"><i>1</i> 약관 동의</li>
            <li><i>2</i> 정보 입력</li>
            <li><i>3</i> 완료</li>
          </ol>

          <form class="auth-form" @submit.prevent="handleSubmit">
            <div class="form-row">
              <div class="terms-box">
                <label class="all">
                  <input type="checkbox" :checked="form.agreeAll" @change="toggleAll" />
                  <span class="all">전체 약관에 동의합니다</span>
                </label>
                <label>
                  <input v-model="form.agreeTerms" type="checkbox" />
                  <span class="req">[필수]</span> 이용약관 동의
                  <a class="view-link" @click.prevent="notReady">보기</a>
                </label>
                <label>
                  <input v-model="form.agreePrivacy" type="checkbox" />
                  <span class="req">[필수]</span> 개인정보 수집 및 이용 동의
                  <a class="view-link" @click.prevent="notReady">보기</a>
                </label>
                <label>
                  <input v-model="form.agreeAge" type="checkbox" />
                  <span class="req">[필수]</span> 만 14세 이상입니다
                </label>
                <label>
                  <input v-model="form.agreeMarketing" type="checkbox" />
                  <span class="opt">[선택]</span> 마케팅 정보 수신 동의
                  <a class="view-link" @click.prevent="notReady">보기</a>
                </label>
              </div>
            </div>

            <div class="form-row">
              <label for="su-email">이메일 <span class="required">*</span></label>
              <div class="input-group">
                <input v-model.trim="form.email" type="email" id="su-email" placeholder="이메일을 입력하세요" />
                <button class="btn secondary square" type="button" @click="notReady">중복 확인</button>
              </div>
            </div>

            <div class="form-row">
              <label for="su-pw">비밀번호 <span class="required">*</span></label>
              <input v-model="form.password" type="password" id="su-pw" placeholder="영문 + 숫자 + 특수문자 포함 8자 이상" />
              <span class="field-help">영문, 숫자, 특수문자를 조합하여 8자 이상 입력하세요.</span>
            </div>

            <div class="form-row">
              <label for="su-pw2">비밀번호 확인 <span class="required">*</span></label>
              <input v-model="form.passwordConfirm" type="password" id="su-pw2" placeholder="비밀번호를 다시 입력하세요" />
            </div>

            <div class="form-row">
              <label for="su-name">이름 <span class="required">*</span></label>
              <input v-model.trim="form.memberName" type="text" id="su-name" placeholder="이름을 입력하세요" />
            </div>

            <div class="form-row">
              <label for="su-nick">닉네임 <span class="required">*</span></label>
              <div class="input-group">
                <input v-model.trim="form.nickname" type="text" id="su-nick" placeholder="사이트에서 사용할 닉네임" />
                <button class="btn secondary square" type="button" @click="notReady">중복 확인</button>
              </div>
            </div>

            <div class="form-row">
              <label for="su-phone">휴대전화 <span class="required">*</span></label>
              <div class="input-group">
                <input v-model.trim="form.phone" type="tel" id="su-phone" placeholder="'-' 없이 숫자만 입력" />
                <button class="btn secondary square" type="button" @click="notReady">인증번호 발송</button>
              </div>
            </div>

            <div class="form-row">
              <label for="su-code">인증번호</label>
              <input v-model.trim="form.authCode" type="text" id="su-code" placeholder="인증번호 6자리" />
            </div>

            <p
              v-if="message"
              class="auth-message"
              :style="{ color: messageType === 'error' ? '#e2493e' : '#0b7a3f', fontSize: '13px', margin: '4px 0', fontWeight: 600 }"
            >{{ message }}</p>

            <div class="form-actions">
              <RouterLink class="btn ghost square" to="/login">취소</RouterLink>
              <button class="btn primary lg" type="submit" :disabled="auth.loading">
                {{ auth.loading ? '처리 중…' : '가입하기' }}
              </button>
            </div>
          </form>

          <div class="auth-footer">
            이미 회원이신가요?<RouterLink to="/login">로그인</RouterLink>
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
