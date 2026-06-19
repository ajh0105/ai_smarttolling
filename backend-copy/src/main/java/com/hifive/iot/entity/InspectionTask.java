package com.hifive.iot.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class InspectionTask {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String taskId;
	private String eventId;
	private String plateText;
	private String siteId;
	private Integer laneNo;
	private String reason;
	private String severity;
	private String status;
	private String assignedTo;
	private String resolvedBy;
	private LocalDateTime resolvedAt;
	private String memo;
	private LocalDateTime createdAt;

	protected InspectionTask() {
	}

	public InspectionTask(String eventId, String plateText, String siteId, Integer laneNo, String reason, String severity) {
		this.taskId = "inspect-" + UUID.randomUUID();
		this.eventId = eventId;
		this.plateText = plateText;
		this.siteId = siteId;
		this.laneNo = laneNo;
		this.reason = reason;
		this.severity = severity;
		this.status = "PENDING";
		this.createdAt = LocalDateTime.now();
	}

	public void resolve(String status, String resolvedBy, String memo) {
		this.status = status;
		this.resolvedBy = resolvedBy;
		this.memo = memo;
		this.resolvedAt = LocalDateTime.now();
	}

	public String getTaskId() { return taskId; }
	public String getEventId() { return eventId; }
	public String getPlateText() { return plateText; }
	public String getSiteId() { return siteId; }
	public Integer getLaneNo() { return laneNo; }
	public String getReason() { return reason; }
	public String getSeverity() { return severity; }
	public String getStatus() { return status; }
	public String getAssignedTo() { return assignedTo; }
	public String getResolvedBy() { return resolvedBy; }
	public LocalDateTime getResolvedAt() { return resolvedAt; }
	public String getMemo() { return memo; }
	public LocalDateTime getCreatedAt() { return createdAt; }
}
