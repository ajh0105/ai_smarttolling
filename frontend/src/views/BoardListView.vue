<script setup>
import { onMounted, ref, computed } from 'vue'
import { useBoardStore } from '@/stores/board'
import SiteHeader from '@/components/SiteHeader.vue'

const board = useBoardStore()
const search = ref('')
const searchField = ref('all')
const activeTab = ref('all')

onMounted(() => {
  board.fetchAll()
})

function fmtDate(raw) {
  if (!raw) return '-'
  try {
    const d = new Date(raw)
    if (Number.isNaN(d.getTime())) return raw
    const y = d.getFullYear()
    const m = String(d.getMonth() + 1).padStart(2, '0')
    const day = String(d.getDate()).padStart(2, '0')
    return `${y}.${m}.${day}`
  } catch {
    return raw
  }
}

const filteredPosts = computed(() => {
  const list = board.posts ?? []
  if (!search.value.trim()) return list
  const q = search.value.trim().toLowerCase()
  return list.filter((p) => {
    if (searchField.value === 'title') return (p.title ?? '').toLowerCase().includes(q)
    if (searchField.value === 'writer') return (p.writerName ?? '').toLowerCase().includes(q)
    return (p.title ?? '').toLowerCase().includes(q) || (p.content ?? '').toLowerCase().includes(q)
  })
})
</script>

<template>
  <div class="site-shell">
    <SiteHeader />

    <main>
      <section class="sub-hero">
        <img class="sub-hero-bg" src="/index-assets/img/hero.png" alt="" />
        <div class="sub-hero-shade"></div>
        <div class="inner">
          <nav class="breadcrumb"><RouterLink to="/">홈</RouterLink><span>›</span><span>개발일지</span></nav>
          <span class="eyebrow">DEV LOG</span>
          <h1>Hi-Five의 개발 이야기</h1>
          <p class="lead">기술 트렌드, 개발 노트, 팀 소식을 한 곳에서 만나보세요.</p>
        </div>
      </section>

      <section class="page-section">
        <div class="inner">
          <nav class="tab-nav">
            <a href="#" :class="{ active: activeTab === 'all' }" @click.prevent="activeTab = 'all'">전체</a>
            <a href="#" :class="{ active: activeTab === 'notice' }" @click.prevent="activeTab = 'notice'">공지</a>
            <a href="#" :class="{ active: activeTab === 'note' }" @click.prevent="activeTab = 'note'">개발 노트</a>
            <a href="#" :class="{ active: activeTab === 'review' }" @click.prevent="activeTab = 'review'">기술 리뷰</a>
            <a href="#" :class="{ active: activeTab === 'news' }" @click.prevent="activeTab = 'news'">팀 소식</a>
          </nav>

          <div class="board-toolbar">
            <div class="board-search">
              <select v-model="searchField" aria-label="검색 조건">
                <option value="all">제목 + 내용</option>
                <option value="title">제목</option>
                <option value="writer">작성자</option>
              </select>
              <input v-model="search" type="text" placeholder="검색어를 입력하세요" />
              <button class="btn primary sm" type="button">검색</button>
            </div>
            <RouterLink class="btn primary" to="/board/write">
              <svg viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round"><path d="M12 5v14M5 12h14"/></svg>
              글쓰기
            </RouterLink>
          </div>

          <table class="board-table">
            <colgroup>
              <col style="width: 70px" />
              <col style="width: 100px" />
              <col />
              <col style="width: 120px" />
              <col style="width: 110px" />
              <col style="width: 70px" />
              <col style="width: 70px" />
            </colgroup>
            <thead>
              <tr>
                <th>번호</th>
                <th>분류</th>
                <th>제목</th>
                <th>작성자</th>
                <th>작성일</th>
                <th>조회</th>
                <th>좋아요</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="board.loading">
                <td colspan="7" style="text-align:center; padding:24px; color:#6b7280;">불러오는 중…</td>
              </tr>
              <tr v-else-if="board.error">
                <td colspan="7" style="text-align:center; padding:24px; color:#e2493e;">{{ board.error }}</td>
              </tr>
              <tr v-else-if="!filteredPosts.length">
                <td colspan="7" style="text-align:center; padding:32px; color:#6b7280;">등록된 게시글이 없습니다. 첫 글을 작성해 보세요.</td>
              </tr>
              <tr v-for="(post, i) in filteredPosts" :key="post.postId">
                <td>{{ filteredPosts.length - i }}</td>
                <td><span class="tag-chip">개발 노트</span></td>
                <td class="title-cell">
                  <RouterLink :to="`/board/${post.postId}`">
                    {{ post.title }}
                    <i v-if="(post.commentCount ?? 0) > 0" style="color:#e2493e;font-style:normal;font-weight:800;margin-left:4px;">[{{ post.commentCount }}]</i>
                  </RouterLink>
                </td>
                <td>{{ post.writerName || '방문자' }}</td>
                <td>{{ fmtDate(post.createdAt) }}</td>
                <td>{{ post.viewCount ?? 0 }}</td>
                <td>{{ post.likeCount ?? 0 }}</td>
              </tr>
            </tbody>
          </table>

          <div class="pagination" role="navigation" aria-label="페이지 이동">
            <a href="#" @click.prevent>«</a>
            <a href="#" @click.prevent>‹</a>
            <a href="#" class="active" @click.prevent>1</a>
            <a href="#" @click.prevent>›</a>
            <a href="#" @click.prevent>»</a>
          </div>
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
