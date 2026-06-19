package com.hifive.iot.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class GpsJudgement {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String eventId;
	private Long gpsTelemetryId;
	private Long zoneId;
	private String status;
	private Double distanceM;
	private String paymentDecision;
	private LocalDateTime judgedAt;

	protected GpsJudgement() {
	}

	public GpsJudgement(String eventId, Long gpsTelemetryId, Long zoneId, String status, Double distanceM, String paymentDecision) {
		this.eventId = eventId;
		this.gpsTelemetryId = gpsTelemetryId;
		this.zoneId = zoneId;
		this.status = status;
		this.distanceM = distanceM;
		this.paymentDecision = paymentDecision;
		this.judgedAt = LocalDateTime.now();
	}

	public Long getId() { return id; }
	public String getEventId() { return eventId; }
	public Long getGpsTelemetryId() { return gpsTelemetryId; }
	public Long getZoneId() { return zoneId; }
	public String getStatus() { return status; }
	public Double getDistanceM() { return distanceM; }
	public String getPaymentDecision() { return paymentDecision; }
	public LocalDateTime getJudgedAt() { return judgedAt; }
}
