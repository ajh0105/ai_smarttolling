<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useBoardStore } from '@/stores/board'
import SiteHeader from '@/components/SiteHeader.vue'

const router = useRouter()
const board = useBoardStore()

const form = reactive({
  category: '',
  title: '',
  tags: '',
  content: '',
  hidden: false,
  allowComments: true,
  pinned: false
})
const message = ref('')
const messageType = ref('error')
const submitting = ref(false)

async function handleSubmit() {
  message.value = ''
  if (!form.title.trim() || !form.content.trim()) {
    message.value = '제목과 본문은 필수입니다.'
    messageType.value = 'error'
    return
  }
  submitting.value = true
  const result = await board.create({
    title: form.title.trim(),
    content: form.content.trim()
  })
  submitting.value = false
  if (result.ok) {
    router.push('/board')
  } else {
    message.value = result.message || '등록에 실패했습니다.'
    messageType.value = 'error'
  }
}

function notReady() {
  alert('해당 기능은 준비 중입니다.')
}
</script>

<template>
  <div class="site-shell">
    <SiteHeader />

    <main>
      <section class="sub-hero" style="height: 220px;">
        <img class="sub-hero-bg" src="/index-assets/img/hero.png" alt="" />
        <div class="sub-hero-shade"></div>
        <div class="inner">
          <nav class="breadcrumb">
            <RouterLink to="/">홈</RouterLink><span>›</span>
            <RouterLink to="/board">개발일지</RouterLink><span>›</span>
            <span>글쓰기</span>
          </nav>
          <span class="eyebrow">WRITE A POST</span>
          <h1 style="font-size: 28px;">새 개발일지 작성</h1>
        </div>
      </section>

      <section class="page-section">
        <div class="inner" style="max-width: 880px; margin: 0 auto;">
          <form class="form" @submit.prevent="handleSubmit">

            <div class="form-row">
              <label for="post-category">분류</label>
              <select v-model="form.category" id="post-category" class="form-control">
                <option value="">— 분류 선택 —</option>
                <option>공지</option>
                <option>개발 노트</option>
                <option>기술 리뷰</option>
                <option>팀 소식</option>
              </select>
              <span class="field-help">분류는 화면 표시용입니다 (저장되지 않음).</span>
            </div>

            <div class="form-row">
              <label for="post-title">제목 <span class="required">*</span></label>
              <input v-model="form.title" type="text" id="post-title" placeholder="제목을 입력하세요" />
            </div>

            <div class="form-row">
              <label for="post-tags">태그</label>
              <input v-model="form.tags" type="text" id="post-tags" placeholder="쉼표로 구분 (예: GPS, Hi-Pass, AI)" />
              <span class="field-help">태그는 화면 표시용입니다 (저장되지 않음).</span>
            </div>

            <div class="form-row">
              <label for="post-body">본문 <span class="required">*</span></label>
              <textarea v-model="form.content" id="post-body" style="min-height: 340px;" placeholder="내용을 입력하세요"></textarea>
            </div>

            <p
              v-if="message"
              :style="{ color: messageType === 'error' ? '#e2493e' : '#0b7a3f', fontSize: '13px', margin: '4px 0', fontWeight: 600 }"
            >{{ message }}</p>

            <div class="form-actions">
              <RouterLink class="btn ghost square" to="/board">취소</RouterLink>
              <button class="btn secondary square" type="button" @click="notReady">임시저장</button>
              <button class="btn primary square" type="submit" :disabled="submitting">
                {{ submitting ? '등록 중…' : '등록' }}
              </button>
            </div>
          </form>
        </div>
      </section>
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
          <div><h4>서비스</h4><RouterLink to="/service#frontend">프론트엔드</RouterLink><RouterLink to="/service#backend">백엔드</RouterLink><RouterLink to="/service#edge">엣지 · 영상</RouterLink><RouterLink to="/service#infra">인프라 · 데이터</RouterLink></div>
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
