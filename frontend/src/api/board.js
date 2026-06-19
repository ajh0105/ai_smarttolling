import apiClient from './client'

/**
 * 백엔드 API
 *  GET    /api/board                      목록
 *  GET    /api/board/{id}                 상세
 *  POST   /api/board                      작성  { title, content }
 *  PUT    /api/board/{id}                 수정  { title, content }
 *  DELETE /api/board/{id}                 삭제
 *  POST   /api/board/{id}/view-hit        조회수 +1
 *  POST   /api/board/{id}/like            좋아요 +1
 *  GET    /api/board/{id}/comments        댓글 목록
 *  POST   /api/board/{id}/comments        댓글 작성  { content }
 *  DELETE /api/board/comments/{commentId} 댓글 삭제
 */
export const boardApi = {
  list() {
    return apiClient.get('/api/board')
  },
  get(id) {
    return apiClient.get(`/api/board/${id}`)
  },
  create(payload) {
    return apiClient.post('/api/board', payload)
  },
  update(id, payload) {
    return apiClient.put(`/api/board/${id}`, payload)
  },
  remove(id) {
    return apiClient.delete(`/api/board/${id}`)
  },
  viewHit(id) {
    return apiClient.post(`/api/board/${id}/view-hit`)
  },
  like(id) {
    return apiClient.post(`/api/board/${id}/like`)
  },
  listComments(id) {
    return apiClient.get(`/api/board/${id}/comments`)
  },
  addComment(id, content) {
    return apiClient.post(`/api/board/${id}/comments`, { content })
  },
  removeComment(commentId) {
    return apiClient.delete(`/api/board/comments/${commentId}`)
  }
}
