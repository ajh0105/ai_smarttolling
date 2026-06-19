import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { boardApi } from '@/api/board'

export const useBoardStore = defineStore('board', () => {
  const posts = ref([])
  const currentPost = ref(null)
  const comments = ref([])
  const loading = ref(false)
  const error = ref(null)

  const total = computed(() => posts.value.length)

  async function fetchAll() {
    loading.value = true
    error.value = null
    try {
      const { data } = await boardApi.list()
      posts.value = (data ?? []).map(normalizePost)
    } catch (err) {
      error.value = err?.response?.data?.message ?? '게시글을 불러오지 못했습니다.'
    } finally {
      loading.value = false
    }
  }

  async function fetchById(id) {
    loading.value = true
    error.value = null
    try {
      const { data } = await boardApi.get(id)
      currentPost.value = normalizePost(data)
      return { ok: true, post: currentPost.value }
    } catch (err) {
      const msg = err?.response?.data?.message ?? '게시글을 찾을 수 없습니다.'
      error.value = msg
      currentPost.value = null
      return { ok: false, message: msg }
    } finally {
      loading.value = false
    }
  }

  async function create(form) {
    error.value = null
    try {
      const { data } = await boardApi.create(form)
      const newPost = normalizePost(data)
      posts.value.unshift(newPost)
      return { ok: true, post: newPost }
    } catch (err) {
      const msg = err?.response?.data?.message ?? '게시글 등록에 실패했습니다.'
      error.value = msg
      return { ok: false, message: msg }
    }
  }

  async function update(id, form) {
    error.value = null
    try {
      const { data } = await boardApi.update(id, form)
      const updated = normalizePost(data)
      const idx = posts.value.findIndex((p) => p.postId === updated.postId)
      if (idx >= 0) posts.value[idx] = updated
      if (currentPost.value && currentPost.value.postId === updated.postId) currentPost.value = updated
      return { ok: true, post: updated }
    } catch (err) {
      const msg = err?.response?.data?.message ?? '게시글 수정에 실패했습니다.'
      error.value = msg
      return { ok: false, message: msg }
    }
  }

  async function remove(id) {
    error.value = null
    try {
      await boardApi.remove(id)
      posts.value = posts.value.filter((p) => p.postId !== id)
      if (currentPost.value && currentPost.value.postId === id) currentPost.value = null
      return { ok: true }
    } catch (err) {
      const msg = err?.response?.data?.message ?? '게시글 삭제에 실패했습니다.'
      error.value = msg
      return { ok: false, message: msg }
    }
  }

  async function viewHit(id) {
    try {
      const { data } = await boardApi.viewHit(id)
      if (currentPost.value && currentPost.value.postId === id) {
        currentPost.value = { ...currentPost.value, viewCount: data.viewCount }
      }
      return { ok: true, viewCount: data.viewCount }
    } catch {
      return { ok: false }
    }
  }

  async function like(id) {
    try {
      const { data } = await boardApi.like(id)
      if (currentPost.value && currentPost.value.postId === id) {
        currentPost.value = { ...currentPost.value, likeCount: data.likeCount }
      }
      return { ok: true, likeCount: data.likeCount }
    } catch {
      return { ok: false }
    }
  }

  async function fetchComments(id) {
    try {
      const { data } = await boardApi.listComments(id)
      comments.value = (data ?? []).map(normalizeComment)
      return { ok: true, comments: comments.value }
    } catch (err) {
      const msg = err?.response?.data?.message ?? '댓글을 불러오지 못했습니다.'
      return { ok: false, message: msg }
    }
  }

  async function addComment(id, content) {
    try {
      const { data } = await boardApi.addComment(id, content)
      const newComment = normalizeComment(data)
      comments.value.push(newComment)
      if (currentPost.value && currentPost.value.postId === id) {
        currentPost.value = {
          ...currentPost.value,
          commentCount: (currentPost.value.commentCount ?? 0) + 1
        }
      }
      return { ok: true, comment: newComment }
    } catch (err) {
      const msg = err?.response?.data?.message ?? '댓글 등록에 실패했습니다.'
      return { ok: false, message: msg }
    }
  }

  async function removeComment(commentId, postId) {
    try {
      await boardApi.removeComment(commentId)
      comments.value = comments.value.filter((c) => c.commentId !== commentId)
      if (postId && currentPost.value && currentPost.value.postId === postId) {
        currentPost.value = {
          ...currentPost.value,
          commentCount: Math.max(0, (currentPost.value.commentCount ?? 1) - 1)
        }
      }
      return { ok: true }
    } catch (err) {
      const msg = err?.response?.data?.message ?? '댓글 삭제에 실패했습니다.'
      return { ok: false, message: msg }
    }
  }

  function normalizePost(p) {
    if (!p) return null
    return {
      ...p,
      createdAt: normalizeDate(p.createdAt),
      updatedAt: normalizeDate(p.updatedAt)
    }
  }

  function normalizeComment(c) {
    if (!c) return null
    return { ...c, createdAt: normalizeDate(c.createdAt) }
  }

  function normalizeDate(raw) {
    if (!raw) return null
    if (typeof raw === 'string') return raw
    if (Array.isArray(raw)) {
      const [y, m, d, h = 0, mi = 0, s = 0] = raw
      return new Date(y, m - 1, d, h, mi, s).toISOString()
    }
    return raw
  }

  return {
    posts,
    currentPost,
    comments,
    loading,
    error,
    total,
    fetchAll,
    fetchById,
    create,
    update,
    remove,
    viewHit,
    like,
    fetchComments,
    addComment,
    removeComment
  }
})
