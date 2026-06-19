package com.hifive.iot.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "board_post")
public class BoardPost {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "post_id")
	private Long postId;

	@Column(nullable = false, length = 200)
	private String title;

	@Column(nullable = false, columnDefinition = "text")
	private String content;

	@Column(name = "writer_name", length = 80)
	private String writerName;

	@Column(name = "view_count", nullable = false)
	private long viewCount = 0L;

	@Column(name = "like_count", nullable = false)
	private long likeCount = 0L;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	protected BoardPost() {}

	public BoardPost(String title, String content, String writerName) {
		this.title = title;
		this.content = content;
		this.writerName = writerName;
	}

	@PrePersist
	void onCreate() {
		if (createdAt == null) createdAt = LocalDateTime.now();
		updatedAt = createdAt;
	}

	@PreUpdate
	void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	public void update(String title, String content) {
		this.title = title;
		this.content = content;
	}

	public void incrementViewCount() {
		this.viewCount = this.viewCount + 1;
	}

	public void incrementLikeCount() {
		this.likeCount = this.likeCount + 1;
	}

	public Long getPostId() { return postId; }
	public String getTitle() { return title; }
	public String getContent() { return content; }
	public String getWriterName() { return writerName; }
	public long getViewCount() { return viewCount; }
	public long getLikeCount() { return likeCount; }
	public LocalDateTime getCreatedAt() { return createdAt; }
	public LocalDateTime getUpdatedAt() { return updatedAt; }
}
