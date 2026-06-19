<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useBoardStore } from '@/stores/board'
import SiteHeader from '@/components/SiteHeader.vue'

const route = useRoute()
const router = useRouter()
const board = useBoardStore()

const postId = computed(() => Number(route.params.id))
const post = computed(() => board.currentPost)
const comments = computed(() => board.comments)

const editing = ref(false)
const editForm = ref({ title: '', content: '' })
const commentText = ref('')
const submitting = ref(false)

async function loadAll(id) {
  if (!Number.isFinite(id)) return
  await board.fetchById(id)
  if (board.currentPost) {
    await board.viewHit(id)
    await board.fetchComments(id)
  }
}

onMounted(() => loadAll(postId.value))

watch(postId, (newId) => loadAll(newId))

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

function fmtDateTime(raw) {
  if (!raw) return '-'
  try {
    const d = new Date(raw)
    if (Number.isNaN(d.getTime())) return raw
    const y = d.getFullYear()
    const m = String(d.getMonth() + 1).padStart(2, '0')
    const day = String(d.getDate()).padStart(2, '0')
    const hh = String(d.getHours()).padStart(2, '0')
    const mm = String(d.getMinutes()).padStart(2, '0')
    return `${y}.${m}.${day} ${hh}:${mm}`
  } catch {
    return raw
  }
}

async function handleLike() {
  if (!post.value) return
  await board.like(post.value.postId)
}

function startEdit() {
  if (!post.value) return
  editForm.value = { title: post.value.title, content: post.value.content }
  editing.value = true
}

function cancelEdit() {
  editing.value = false
}

async function saveEdit() {
  if (!post.value) return
  if (!editForm.value.title.trim() || !editForm.value.content.trim()) {
    alert('제목과 본문은 필수입니다.')
    return
  }
  submitting.value = true
  const result = await board.update(post.value.postId, {
    title: editForm.value.title.trim(),
    content: editForm.value.content.trim()
  })
  submitting.value = false
  if (result.ok) {
    editing.value = false
  } else {
    alert(result.message || '수정에 실패했습니다.')
  }
}

async function handleDelete() {
  if (!post.value) return
  if (!confirm('정말 삭제하시겠습니까?')) return
  const result = await board.remove(post.value.postId)
  if (result.ok) {
    router.push('/board')
  } else {
    alert(result.message || '삭제에 실패했습니다.')
  }
}

async function handleAddComment() {
  if (!post.value) return
  const content = commentText.value.trim()
  if (!content) return
  submitting.value = true
  const result = await board.addComment(post.value.postId, content)
  submitting.value = false
  if (result.ok) {
    commentText.value = ''
  } else {
    alert(result.message || '댓글 등록에 실패했습니다.')
  }
}

async function handleDeleteComment(commentId) {
  if (!confirm('댓글을 삭제하시겠습니까?')) return
  await board.removeComment(commentId, post.value?.postId)
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
            <span>게시글 보기</span>
          </nav>
          <span class="eyebrow">DEV LOG</span>
          <h1 style="font-size: 28px;">개발일지</h1>
        </div>
      </section>

      <section class="page-section">
        <div class="inner" style="max-width: 880px; margin: 0 auto;">
          <div v-if="board.loading" style="text-align:center; padding:40px; color:#6b7280;">불러오는 중…</div>
          <div v-else-if="!post" style="text-align:center; padding:40px; color:#6b7280;">
            게시글을 찾을 수 없습니다.
            <div style="margin-top:16px;">
              <RouterLink class="btn ghost square" to="/board">← 목록으로</RouterLink>
            </div>
          </div>

          <template v-else>
            <header class="post-header">
              <span class="tag-chip">개발 노트</span>
              <template v-if="!editing">
                <h2>{{ post.title }}</h2>
              </template>
              <template v-else>
                <input v-model="editForm.title" type="text" class="edit-title" placeholder="제목" />
              </template>
              <div class="post-meta">
                <span><svg viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="8" r="4"/><path d="M4 21c0-4 4-7 8-7s8 3 8 7"/></svg><b>{{ post.writerName || '방문자' }}</b></span>
                <span><svg viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" stroke-width="2"><rect x="4" y="5" width="16" height="16" rx="2"/><path d="M16 3v4M8 3v4M4 11h16"/></svg>{{ fmtDate(post.createdAt) }}</span>
                <span><svg viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" stroke-width="2"><path d="M2 12s4-8 10-8 10 8 10 8-4 8-10 8-10-8-10-8Z"/><circle cx="12" cy="12" r="3"/></svg>조회 {{ post.viewCount ?? 0 }}</span>
                <span><svg viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 12a8 8 0 1 1-3-6L20 4v6h-6"/></svg>좋아요 {{ post.likeCount ?? 0 }}</span>
              </div>
            </header>

            <article class="post-body">
              <template v-if="!editing">
                <div style="white-space: pre-wrap; padding: 24px 0; line-height: 1.7; color: #1f2937; min-height: 200px;">{{ post.content }}</div>
              </template>
              <template v-else>
                <textarea v-model="editForm.content" class="edit-content" placeholder="본문" />
              </template>
            </article>

            <div v-if="!editing" class="post-actions">
              <button class="like-button" type="button" @click="handleLike">
                <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2"><path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.6l-1-1a5.5 5.5 0 0 0-7.8 7.8l1 1L12 21l7.8-7.8 1-1a5.5 5.5 0 0 0 0-7.6Z"/></svg>
                좋아요 <b style="color:#0b4383;">{{ post.likeCount ?? 0 }}</b>
              </button>
            </div>

            <div class="post-footer-actions">
              <RouterLink class="btn ghost square" to="/board">← 목록으로</RouterLink>
              <div v-if="!editing" style="display: flex; gap: 8px;">
                <button class="btn secondary square" type="button" @click="startEdit">수정</button>
                <button class="btn ghost square" type="button" @click="handleDelete">삭제</button>
                <RouterLink class="btn primary square" to="/board/write">글쓰기</RouterLink>
              </div>
              <div v-else style="display: flex; gap: 8px;">
                <button class="btn ghost square" type="button" @click="cancelEdit">취소</button>
                <button class="btn primary square" type="button" :disabled="submitting" @click="saveEdit">
                  {{ submitting ? '저장 중…' : '저장' }}
                </button>
              </div>
            </div>

            <section class="comments">
              <h3>댓글 <i>{{ comments.length }}</i></h3>

              <form class="comment-form" @submit.prevent="handleAddComment">
                <textarea v-model="commentText" placeholder="댓글을 입력하세요" :disabled="submitting" />
                <button class="btn primary square" type="submit" :disabled="submitting || !commentText.trim()" style="height: auto; align-self: stretch;">
                  {{ submitting ? '등록 중…' : '등록' }}
                </button>
              </form>

              <ul class="comment-list">
                <li v-if="!comments.length" class="comment" style="color:#6b7280; padding:18px 16px; text-align:center;">
                  아직 댓글이 없습니다.
                </li>
                <li v-for="c in comments" :key="c.commentId" class="comment">
                  <div class="comment-meta">
                    <b>{{ c.writerName || '방문자' }}</b>
                    <time>{{ fmtDateTime(c.createdAt) }}</time>
                    <button type="button" class="comment-delete" @click="handleDeleteComment(c.commentId)" aria-label="댓글 삭제">×</button>
                  </div>
                  <p>{{ c.content }}</p>
                </li>
              </ul>
            </section>
          </template>
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

<style scoped>
.edit-title {
  width: 100%;
  font-size: 24px;
  font-weight: 800;
  padding: 10px 14px;
  border: 1px solid #c7d8ff;
  border-radius: 8px;
  color: #1f2937;
}
.edit-content {
  width: 100%;
  min-height: 240px;
  padding: 14px;
  border: 1px solid #c7d8ff;
  border-radius: 8px;
  font-size: 14px;
  line-height: 1.7;
  color: #1f2937;
  resize: vertical;
}
.comment-delete {
  background: transparent;
  border: none;
  color: #9ca3af;
  font-size: 18px;
  cursor: pointer;
  margin-left: auto;
  padding: 0 4px;
}
.comment-delete:hover {
  color: #e2493e;
}
.comment-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}
</style>
