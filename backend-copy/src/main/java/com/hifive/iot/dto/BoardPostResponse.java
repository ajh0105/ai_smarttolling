package com.hifive.iot.dto;

import java.time.LocalDateTime;

import com.hifive.iot.entity.BoardPost;

public record BoardPostResponse(
	Long postId,
	String title,
	String content,
	String writerName,
	long viewCount,
	long likeCount,
	long commentCount,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {
	public static BoardPostResponse from(BoardPost p, long commentCount) {
		return new BoardPostResponse(
			p.getPostId(),
			p.getTitle(),
			p.getContent(),
			p.getWriterName(),
			p.getViewCount(),
			p.getLikeCount(),
			commentCount,
			p.getCreatedAt(),
			p.getUpdatedAt()
		);
	}
}
