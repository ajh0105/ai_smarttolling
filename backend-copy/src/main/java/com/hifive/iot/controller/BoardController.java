package com.hifive.iot.controller;

import java.util.List;
import java.util.Map;

import com.hifive.iot.dto.BoardCommentRequest;
import com.hifive.iot.dto.BoardCommentResponse;
import com.hifive.iot.dto.BoardPostRequest;
import com.hifive.iot.dto.BoardPostResponse;
import com.hifive.iot.entity.Member;
import com.hifive.iot.service.BoardService;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/board")
public class BoardController {

	private static final String LOGIN_MEMBER = "loginMember";

	private final BoardService boardService;

	public BoardController(BoardService boardService) {
		this.boardService = boardService;
	}

	@GetMapping
	public List<BoardPostResponse> findAll() {
		return boardService.findAll();
	}

	@GetMapping("/{id}")
	public BoardPostResponse findById(@PathVariable("id") Long id) {
		return boardService.findById(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public BoardPostResponse create(@RequestBody BoardPostRequest request, HttpSession session) {
		return boardService.create(request, resolveWriterName(session));
	}

	@PutMapping("/{id}")
	public BoardPostResponse update(@PathVariable("id") Long id, @RequestBody BoardPostRequest request) {
		return boardService.update(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("id") Long id) {
		boardService.delete(id);
	}

	@PostMapping("/{id}/view-hit")
	public Map<String, Long> incrementView(@PathVariable("id") Long id) {
		long viewCount = boardService.incrementView(id);
		return Map.of("viewCount", viewCount);
	}

	@PostMapping("/{id}/like")
	public Map<String, Long> incrementLike(@PathVariable("id") Long id) {
		long likeCount = boardService.incrementLike(id);
		return Map.of("likeCount", likeCount);
	}

	@GetMapping("/{id}/comments")
	public List<BoardCommentResponse> findComments(@PathVariable("id") Long id) {
		return boardService.findComments(id);
	}

	@PostMapping("/{id}/comments")
	@ResponseStatus(HttpStatus.CREATED)
	public BoardCommentResponse addComment(@PathVariable("id") Long id,
	                                       @RequestBody BoardCommentRequest request,
	                                       HttpSession session) {
		return boardService.addComment(id, request, resolveWriterName(session));
	}

	@DeleteMapping("/comments/{commentId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteComment(@PathVariable("commentId") Long commentId) {
		boardService.deleteComment(commentId);
	}

	private String resolveWriterName(HttpSession session) {
		Member loginMember = (Member) session.getAttribute(LOGIN_MEMBER);
		return loginMember == null ? "방문자" : loginMember.memberName();
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Map<String, String>> handleNotFound(IllegalArgumentException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
	}
}
