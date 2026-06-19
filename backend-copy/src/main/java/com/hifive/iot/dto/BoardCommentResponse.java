package com.hifive.iot.dto;

import java.time.LocalDateTime;

import com.hifive.iot.entity.BoardComment;

public record BoardCommentResponse(
	Long commentId,
	Long postId,
	String writerName,
	String content,
	LocalDateTime createdAt
) {
	public static BoardCommentResponse from(BoardComment c) {
		return new BoardCommentResponse(
			c.getCommentId(),
			c.getPostId(),
			c.getWriterName(),
			c.getContent(),
			c.getCreatedAt()
		);
	}
}
