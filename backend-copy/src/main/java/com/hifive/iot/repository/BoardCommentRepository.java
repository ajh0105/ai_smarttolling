package com.hifive.iot.repository;

import java.util.List;

import com.hifive.iot.entity.BoardComment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardCommentRepository extends JpaRepository<BoardComment, Long> {
	List<BoardComment> findByPostIdOrderByCreatedAtAsc(Long postId);
	long countByPostId(Long postId);
	void deleteByPostId(Long postId);
}
