package com.hifive.iot.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "board_comment", indexes = {
	@Index(name = "idx_board_comment_post_id", columnList = "post_id, created_at")
})
public class BoardComment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comment_id")
	private Long commentId;

	@Column(name = "post_id", nullable = false)
	private Long postId;

	@Column(name = "writer_name", length = 80)
	private String writerName;

	@Column(nullable = false, columnDefinition = "text")
	private String content;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	protected BoardComment() {}

	public BoardComment(Long postId, String writerName, String content) {
		this.postId = postId;
		this.writerName = writerName;
		this.content = content;
	}

	@PrePersist
	void onCreate() {
		if (createdAt == null) createdAt = LocalDateTime.now();
	}

	public Long getCommentId() { return commentId; }
	public Long getPostId() { return postId; }
	public String getWriterName() { return writerName; }
	public String getContent() { return content; }
	public LocalDateTime getCreatedAt() { return createdAt; }
}
