package com.hifive.iot.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class AuditLog {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String actor;
	private String action;
	private String targetType;
	private String targetId;
	private String reason;
	private LocalDateTime createdAt;

	protected AuditLog() {
	}

	public AuditLog(String actor, String action, String targetType, String targetId, String reason) {
		this.actor = actor;
		this.action = action;
		this.targetType = targetType;
		this.targetId = targetId;
		this.reason = reason;
		this.createdAt = LocalDateTime.now();
	}

	public String getActor() { return actor; }
	public String getAction() { return action; }
	public String getTargetType() { return targetType; }
	public String getTargetId() { return targetId; }
	public String getReason() { return reason; }
	public LocalDateTime getCreatedAt() { return createdAt; }
}
