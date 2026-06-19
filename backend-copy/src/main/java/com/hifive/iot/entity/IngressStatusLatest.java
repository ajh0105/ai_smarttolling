package com.hifive.iot.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class IngressStatusLatest {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false, unique = true)
	private String ingressId;
	private Long uptimeSec;
	private Long receivedEvents;
	private Long ackedEvents;
	private Long retryEvents;
	private Long rejectedEvents;
	private Long malformedFrames;
	private Long networkTransitionEvents;
	private Long edgeStatusEvents;
	private Long activeConnections;
	private Long totalConnections;
	private String lastEventId;
	private Long lastPayloadBytes;
	private String springForwardStatus;
	private Integer springForwardStatusCode;
	private String edgeStatusForwardStatus;
	private String latestEdgeDeviceId;
	private Boolean latestEdgeStale;
	private LocalDateTime receivedAt;

	protected IngressStatusLatest() {
	}

	public IngressStatusLatest(String ingressId) {
		this.ingressId = ingressId;
	}

	public void apply(Long uptimeSec, Long receivedEvents, Long ackedEvents, Long retryEvents, Long rejectedEvents,
		Long malformedFrames, Long networkTransitionEvents, Long edgeStatusEvents, Long activeConnections,
		Long totalConnections, String lastEventId, Long lastPayloadBytes, String springForwardStatus,
		Integer springForwardStatusCode, String edgeStatusForwardStatus, String latestEdgeDeviceId,
		Boolean latestEdgeStale, LocalDateTime receivedAt) {
		this.uptimeSec = uptimeSec;
		this.receivedEvents = receivedEvents;
		this.ackedEvents = ackedEvents;
		this.retryEvents = retryEvents;
		this.rejectedEvents = rejectedEvents;
		this.malformedFrames = malformedFrames;
		this.networkTransitionEvents = networkTransitionEvents;
		this.edgeStatusEvents = edgeStatusEvents;
		this.activeConnections = activeConnections;
		this.totalConnections = totalConnections;
		this.lastEventId = lastEventId;
		this.lastPayloadBytes = lastPayloadBytes;
		this.springForwardStatus = springForwardStatus;
		this.springForwardStatusCode = springForwardStatusCode;
		this.edgeStatusForwardStatus = edgeStatusForwardStatus;
		this.latestEdgeDeviceId = latestEdgeDeviceId;
		this.latestEdgeStale = latestEdgeStale;
		this.receivedAt = receivedAt;
	}

	public Long getId() { return id; }
	public String getIngressId() { return ingressId; }
	public Long getUptimeSec() { return uptimeSec; }
	public Long getReceivedEvents() { return receivedEvents; }
	public Long getAckedEvents() { return ackedEvents; }
	public Long getRetryEvents() { return retryEvents; }
	public Long getRejectedEvents() { return rejectedEvents; }
	public Long getMalformedFrames() { return malformedFrames; }
	public Long getNetworkTransitionEvents() { return networkTransitionEvents; }
	public Long getEdgeStatusEvents() { return edgeStatusEvents; }
	public Long getActiveConnections() { return activeConnections; }
	public Long getTotalConnections() { return totalConnections; }
	public String getLastEventId() { return lastEventId; }
	public Long getLastPayloadBytes() { return lastPayloadBytes; }
	public String getSpringForwardStatus() { return springForwardStatus; }
	public Integer getSpringForwardStatusCode() { return springForwardStatusCode; }
	public String getEdgeStatusForwardStatus() { return edgeStatusForwardStatus; }
	public String getLatestEdgeDeviceId() { return latestEdgeDeviceId; }
	public Boolean getLatestEdgeStale() { return latestEdgeStale; }
	public LocalDateTime getReceivedAt() { return receivedAt; }
}
