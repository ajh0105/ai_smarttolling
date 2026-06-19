package com.hifive.iot.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class SettlementCandidate {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String eventId;
	private String plateText;
	private String siteId;
	private Integer laneNo;
	private String direction;
	private LocalDateTime eventTime;
	private String gpsJudgementStatus;
	private String paymentDecision;
	private Integer amount;
	private String status;
	private LocalDateTime createdAt;
	private LocalDateTime confirmedAt;

	protected SettlementCandidate() {
	}

	public SettlementCandidate(String eventId, String plateText, String siteId, Integer laneNo, String direction,
		LocalDateTime eventTime, String gpsJudgementStatus, String paymentDecision, Integer amount, String status) {
		this.eventId = eventId;
		this.plateText = plateText;
		this.siteId = siteId;
		this.laneNo = laneNo;
		this.direction = direction;
		this.eventTime = eventTime;
		this.gpsJudgementStatus = gpsJudgementStatus;
		this.paymentDecision = paymentDecision;
		this.amount = amount;
		this.status = status;
		this.createdAt = LocalDateTime.now();
	}

	public void confirm() {
		this.status = "CONFIRMED";
		this.confirmedAt = LocalDateTime.now();
	}

	public String getEventId() { return eventId; }
	public String getPlateText() { return plateText; }
	public String getSiteId() { return siteId; }
	public Integer getLaneNo() { return laneNo; }
	public String getDirection() { return direction; }
	public LocalDateTime getEventTime() { return eventTime; }
	public String getGpsJudgementStatus() { return gpsJudgementStatus; }
	public String getPaymentDecision() { return paymentDecision; }
	public Integer getAmount() { return amount; }
	public String getStatus() { return status; }
	public LocalDateTime getCreatedAt() { return createdAt; }
	public LocalDateTime getConfirmedAt() { return confirmedAt; }
}
