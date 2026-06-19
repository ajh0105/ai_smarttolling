package com.hifive.iot.service;

import java.util.List;

import com.hifive.iot.dto.BoardCommentRequest;
import com.hifive.iot.dto.BoardCommentResponse;
import com.hifive.iot.dto.BoardPostRequest;
import com.hifive.iot.dto.BoardPostResponse;
import com.hifive.iot.entity.BoardComment;
import com.hifive.iot.entity.BoardPost;
import com.hifive.iot.repository.BoardCommentRepository;
import com.hifive.iot.repository.BoardPostRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BoardService {

	private final BoardPostRepository postRepository;
	private final BoardCommentRepository commentRepository;

	public BoardService(BoardPostRepository postRepository, BoardCommentRepository commentRepository) {
		this.postRepository = postRepository;
		this.commentRepository = commentRepository;
	}

	@Transactional(readOnly = true)
	public List<BoardPostResponse> findAll() {
		return postRepository.findAllByOrderByPostIdDesc().stream()
			.map(p -> BoardPostResponse.from(p, commentRepository.countByPostId(p.getPostId())))
			.toList();
	}

	@Transactional(readOnly = true)
	public BoardPostResponse findById(Long postId) {
		BoardPost post = postRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다: " + postId));
		long commentCount = commentRepository.countByPostId(postId);
		return BoardPostResponse.from(post, commentCount);
	}

	@Transactional
	public BoardPostResponse create(BoardPostRequest request, String writerName) {
		BoardPost saved = postRepository.save(new BoardPost(
			request.title(),
			request.content(),
			writerName
		));
		return BoardPostResponse.from(saved, 0L);
	}

	@Transactional
	public BoardPostResponse update(Long postId, BoardPostRequest request) {
		BoardPost post = postRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다: " + postId));
		post.update(request.title(), request.content());
		return BoardPostResponse.from(post, commentRepository.countByPostId(postId));
	}

	@Transactional
	public void delete(Long postId) {
		if (!postRepository.existsById(postId)) {
			throw new IllegalArgumentException("게시글을 찾을 수 없습니다: " + postId);
		}
		commentRepository.deleteByPostId(postId);
		postRepository.deleteById(postId);
	}

	@Transactional
	public long incrementView(Long postId) {
		BoardPost post = postRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다: " + postId));
		post.incrementViewCount();
		return post.getViewCount();
	}

	@Transactional
	public long incrementLike(Long postId) {
		BoardPost post = postRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다: " + postId));
		post.incrementLikeCount();
		return post.getLikeCount();
	}

	@Transactional(readOnly = true)
	public List<BoardCommentResponse> findComments(Long postId) {
		return commentRepository.findByPostIdOrderByCreatedAtAsc(postId).stream()
			.map(BoardCommentResponse::from)
			.toList();
	}

	@Transactional
	public BoardCommentResponse addComment(Long postId, BoardCommentRequest request, String writerName) {
		if (!postRepository.existsById(postId)) {
			throw new IllegalArgumentException("게시글을 찾을 수 없습니다: " + postId);
		}
		BoardComment saved = commentRepository.save(new BoardComment(
			postId,
			writerName,
			request.content()
		));
		return BoardCommentResponse.from(saved);
	}

	@Transactional
	public void deleteComment(Long commentId) {
		if (!commentRepository.existsById(commentId)) {
			throw new IllegalArgumentException("댓글을 찾을 수 없습니다: " + commentId);
		}
		commentRepository.deleteById(commentId);
	}
}
