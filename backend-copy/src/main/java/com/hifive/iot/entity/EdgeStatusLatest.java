package com.hifive.iot.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class EdgeStatusLatest {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false, unique = true, length = 80)
	private String deviceId;
	private String cameraId;
	private String cameraRole;
	private String sourceMode;
	private String sourceValue;
	private String sourceState;
	private Long uptimeMs;
	private Double latestFps;
	private Double latestYoloMs;
	private Double latestOcrMs;
	private Long processedFrames;
	private Long processedOcrTasks;
	private Long droppedOcrTasks;
	private Long yoloDetections;
	private Long sentEvents;
	private String lastError;
	private String activePath;
	private Boolean failoverEnabled;
	private Boolean outageActive;
	private Long spoolCount;
	private LocalDateTime statusTs;
	private Long statusAgeMs;
	private Boolean stale;
	private Boolean alive;
	private Boolean sourceRunning;
	private Double ocrDropRatio;
	private Boolean spoolWarning;
	private String deviceHealthStatus;
	private LocalDateTime receivedAt;

	protected EdgeStatusLatest() {
	}

	public EdgeStatusLatest(String deviceId) {
		this.deviceId = deviceId;
	}

	public void apply(String cameraId, String cameraRole, String sourceMode, String sourceValue, String sourceState,
		Long uptimeMs, Double latestFps, Double latestYoloMs, Double latestOcrMs, Long processedFrames,
		Long processedOcrTasks, Long droppedOcrTasks, Long yoloDetections, Long sentEvents, String lastError,
		String activePath, Boolean failoverEnabled, Boolean outageActive, Long spoolCount, LocalDateTime statusTs,
		Long statusAgeMs, Boolean stale, Boolean alive, Boolean sourceRunning, Double ocrDropRatio,
		Boolean spoolWarning, String deviceHealthStatus, LocalDateTime receivedAt) {
		this.cameraId = cameraId;
		this.cameraRole = cameraRole;
		this.sourceMode = sourceMode;
		this.sourceValue = sourceValue;
		this.sourceState = sourceState;
		this.uptimeMs = uptimeMs;
		this.latestFps = latestFps;
		this.latestYoloMs = latestYoloMs;
		this.latestOcrMs = latestOcrMs;
		this.processedFrames = processedFrames;
		this.processedOcrTasks = processedOcrTasks;
		this.droppedOcrTasks = droppedOcrTasks;
		this.yoloDetections = yoloDetections;
		this.sentEvents = sentEvents;
		this.lastError = lastError;
		this.activePath = activePath;
		this.failoverEnabled = failoverEnabled;
		this.outageActive = outageActive;
		this.spoolCount = spoolCount;
		this.statusTs = statusTs;
		this.statusAgeMs = statusAgeMs;
		this.stale = stale;
		this.alive = alive;
		this.sourceRunning = sourceRunning;
		this.ocrDropRatio = ocrDropRatio;
		this.spoolWarning = spoolWarning;
		this.deviceHealthStatus = deviceHealthStatus;
		this.receivedAt = receivedAt;
	}

	public Long getId() { return id; }
	public String getDeviceId() { return deviceId; }
	public String getCameraId() { return cameraId; }
	public String getCameraRole() { return cameraRole; }
	public String getSourceMode() { return sourceMode; }
	public String getSourceValue() { return sourceValue; }
	public String getSourceState() { return sourceState; }
	public Long getUptimeMs() { return uptimeMs; }
	public Double getLatestFps() { return latestFps; }
	public Double getLatestYoloMs() { return latestYoloMs; }
	public Double getLatestOcrMs() { return latestOcrMs; }
	public Long getProcessedFrames() { return processedFrames; }
	public Long getProcessedOcrTasks() { return processedOcrTasks; }
	public Long getDroppedOcrTasks() { return droppedOcrTasks; }
	public Long getYoloDetections() { return yoloDetections; }
	public Long getSentEvents() { return sentEvents; }
	public String getLastError() { return lastError; }
	public String getActivePath() { return activePath; }
	public Boolean getFailoverEnabled() { return failoverEnabled; }
	public Boolean getOutageActive() { return outageActive; }
	public Long getSpoolCount() { return spoolCount; }
	public LocalDateTime getStatusTs() { return statusTs; }
	public Long getStatusAgeMs() { return statusAgeMs; }
	public Boolean getStale() { return stale; }
	public Boolean getAlive() { return alive; }
	public Boolean getSourceRunning() { return sourceRunning; }
	public Double getOcrDropRatio() { return ocrDropRatio; }
	public Boolean getSpoolWarning() { return spoolWarning; }
	public String getDeviceHealthStatus() { return deviceHealthStatus; }
	public LocalDateTime getReceivedAt() { return receivedAt; }
}
