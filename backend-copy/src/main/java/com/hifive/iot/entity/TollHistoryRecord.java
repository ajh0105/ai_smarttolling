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
	name = "toll_history",
	indexes = {
		@Index(name = "idx_toll_history_plate_charged", columnList = "plate_number,charged_at"),
		@Index(name = "idx_toll_history_device_charged", columnList = "gps_device_id,charged_at")
	}
)
public class TollHistoryRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "toll_history_id")
	private Long tollHistoryId;

	@Column(name = "plate_number", nullable = false, length = 20)
	private String plateNumber;

	@Column(name = "gps_device_id", nullable = false, length = 80)
	private String gpsDeviceId;

	@Column(name = "lane_id", length = 40)
	private String laneId;

	@Column(name = "toll_zone_id", nullable = false)
	private Long tollZoneId;

	@Column(name = "gps_telemetry_id", nullable = false)
	private Long gpsTelemetryId;

	@Column(name = "amount", nullable = false)
	private Integer amount;

	@Column(name = "payment_status", nullable = false, length = 20)
	private String paymentStatus;

	@Column(name = "source_type", nullable = false, length = 40)
	private String sourceType;

	@Column(name = "plate_confidence")
	private Double plateConfidence;

	@Column(name = "charged_at", nullable = false)
	private LocalDateTime chargedAt;

	protected TollHistoryRecord() {
	}

	public TollHistoryRecord(
		String plateNumber,
		String gpsDeviceId,
		String laneId,
		Long tollZoneId,
		Long gpsTelemetryId,
		Integer amount,
		String paymentStatus,
		String sourceType,
		Double plateConfidence,
		LocalDateTime chargedAt
	) {
		this.plateNumber = plateNumber;
		this.gpsDeviceId = gpsDeviceId;
		this.laneId = laneId;
		this.tollZoneId = tollZoneId;
		this.gpsTelemetryId = gpsTelemetryId;
		this.amount = amount;
		this.paymentStatus = paymentStatus;
		this.sourceType = sourceType;
		this.plateConfidence = plateConfidence;
		this.chargedAt = chargedAt;
	}

	public Long getTollHistoryId() {
		return tollHistoryId;
	}

	public String getPlateNumber() {
		return plateNumber;
	}

	public String getGpsDeviceId() {
		return gpsDeviceId;
	}

	public String getLaneId() {
		return laneId;
	}

	public Long getTollZoneId() {
		return tollZoneId;
	}

	public Long getGpsTelemetryId() {
		return gpsTelemetryId;
	}

	public Integer getAmount() {
		return amount;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public String getSourceType() {
		return sourceType;
	}

	public Double getPlateConfidence() {
		return plateConfidence;
	}

	public LocalDateTime getChargedAt() {
		return chargedAt;
	}
}
