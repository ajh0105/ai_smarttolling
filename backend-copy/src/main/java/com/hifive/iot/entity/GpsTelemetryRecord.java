package com.hifive.iot.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(
	name = "gps_telemetry",
	indexes = {
		@Index(name = "idx_gps_telemetry_device_captured", columnList = "gps_device_id,captured_at"),
		@Index(name = "idx_gps_telemetry_plate_captured", columnList = "plate_number,captured_at")
	}
)
public class GpsTelemetryRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "gps_telemetry_id")
	private Long gpsTelemetryId;

	@Column(name = "gps_device_id", nullable = false, length = 80)
	private String gpsDeviceId;

	@Column(name = "plate_number", length = 20)
	private String plateNumber;

	@Column(name = "edge_node_id", length = 40)
	private String edgeNodeId;

	@Column(name = "lane_id", length = 40)
	private String laneId;

	@Column(name = "track_id")
	private Integer trackId;

	@Column
	private Double latitude;

	@Column
	private Double longitude;

	@Column(name = "speed_kmh")
	private Double speedKmh;

	private Double heading;

	@Column(name = "altitude_m")
	private Double altitudeM;

	@Column(name = "accuracy_m")
	private Double accuracyM;

	@Column(length = 40)
	private String provider;

	@Column(name = "fix_status", nullable = false, length = 20)
	private String fixStatus;

	@Column(name = "status_message", length = 120)
	private String statusMessage;

	@Column(name = "raw_sentence", length = 180)
	private String rawSentence;

	@Column(name = "captured_at", nullable = false)
	private LocalDateTime capturedAt;

	@Column(name = "received_at", nullable = false)
	private LocalDateTime receivedAt;

	protected GpsTelemetryRecord() {
	}

	public GpsTelemetryRecord(
		String gpsDeviceId,
		String plateNumber,
		String edgeNodeId,
		String laneId,
		Integer trackId,
		Double latitude,
		Double longitude,
		Double speedKmh,
		Double heading,
		Double altitudeM,
		Double accuracyM,
		String provider,
		String fixStatus,
		String statusMessage,
		String rawSentence,
		LocalDateTime capturedAt,
		LocalDateTime receivedAt
	) {
		this.gpsDeviceId = gpsDeviceId;
		this.plateNumber = plateNumber;
		this.edgeNodeId = edgeNodeId;
		this.laneId = laneId;
		this.trackId = trackId;
		this.latitude = latitude;
		this.longitude = longitude;
		this.speedKmh = speedKmh;
		this.heading = heading;
		this.altitudeM = altitudeM;
		this.accuracyM = accuracyM;
		this.provider = provider;
		this.fixStatus = fixStatus;
		this.statusMessage = statusMessage;
		this.rawSentence = rawSentence;
		this.capturedAt = capturedAt;
		this.receivedAt = receivedAt;
	}

	public Long getGpsTelemetryId() {
		return gpsTelemetryId;
	}

	public String getGpsDeviceId() {
		return gpsDeviceId;
	}

	public String getPlateNumber() {
		return plateNumber;
	}

	public String getEdgeNodeId() {
		return edgeNodeId;
	}

	public String getLaneId() {
		return laneId;
	}

	public Integer getTrackId() {
		return trackId;
	}

	public Double getLatitude() {
		return latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public Double getSpeedKmh() {
		return speedKmh;
	}

	public Double getHeading() {
		return heading;
	}

	public Double getAltitudeM() {
		return altitudeM;
	}

	public Double getAccuracyM() {
		return accuracyM;
	}

	public String getProvider() {
		return provider;
	}

	public String getFixStatus() {
		return fixStatus;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public String getRawSentence() {
		return rawSentence;
	}

	public LocalDateTime getCapturedAt() {
		return capturedAt;
	}

	public LocalDateTime getReceivedAt() {
		return receivedAt;
	}
}
