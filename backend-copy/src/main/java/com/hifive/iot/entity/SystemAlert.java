package com.hifive.iot.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class SystemAlert {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String alertId;
	private String severity;
	private String component;
	private String siteId;
	private String deviceId;
	private String title;
	private String message;
	private String impact;
	private String status;
	private String assignedTo;
	private LocalDateTime createdAt;
	private LocalDateTime acknowledgedAt;
	private LocalDateTime resolvedAt;

	protected SystemAlert() {
	}

	public SystemAlert(String severity, String component, String siteId, String deviceId, String title, String message, String impact) {
		this.alertId = "alert-" + UUID.randomUUID();
		this.severity = severity;
		this.component = component;
		this.siteId = siteId;
		this.deviceId = deviceId;
		this.title = title;
		this.message = message;
		this.impact = impact;
		this.status = "OPEN";
		this.createdAt = LocalDateTime.now();
	}

	public void acknowledge() { this.status = "ACKED"; this.acknowledgedAt = LocalDateTime.now(); }
	public void assign(String assignedTo) { this.assignedTo = assignedTo; this.status = "ASSIGNED"; }
	public void resolve() { this.status = "RESOLVED"; this.resolvedAt = LocalDateTime.now(); }
	public String getAlertId() { return alertId; }
	public String getSeverity() { return severity; }
	public String getComponent() { return component; }
	public String getSiteId() { return siteId; }
	public String getDeviceId() { return deviceId; }
	public String getTitle() { return title; }
	public String getMessage() { return message; }
	public String getImpact() { return impact; }
	public String getStatus() { return status; }
	public String getAssignedTo() { return assignedTo; }
	public LocalDateTime getCreatedAt() { return createdAt; }
	public LocalDateTime getAcknowledgedAt() { return acknowledgedAt; }
	public LocalDateTime getResolvedAt() { return resolvedAt; }
}
