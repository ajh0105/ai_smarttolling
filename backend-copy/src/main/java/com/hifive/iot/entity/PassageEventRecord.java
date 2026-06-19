package com.hifive.iot.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
	name = "passage_event",
	uniqueConstraints = {
		@UniqueConstraint(name = "uk_passage_event_event_id", columnNames = "event_id")
	},
	indexes = {
		@Index(name = "idx_passage_event_received_at", columnList = "received_at")
	}
)
public class PassageEventRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "passage_event_id")
	private Long passageEventId;

	@Column(name = "event_id", nullable = false, length = 120)
	private String eventId;

	@Column(name = "payload_format", nullable = false, length = 20)
	private String payloadFormat;

	@Column(name = "payload_bytes", nullable = false, columnDefinition = "bytea")
	private byte[] payloadBytes;

	@Column(name = "payload_size_bytes", nullable = false)
	private Integer payloadSizeBytes;

	@Column(name = "device_id", length = 80)
	private String deviceId;

	@Column(name = "camera_id", length = 80)
	private String cameraId;

	@Column(name = "camera_group_id", length = 80)
	private String cameraGroupId;

	@Column(name = "camera_role", length = 30)
	private String cameraRole;

	@Column(name = "event_time")
	private LocalDateTime eventTime;

	@Column(length = 20)
	private String direction;

	@Column(name = "lane_no")
	private Integer laneNo;

	@Column(name = "global_lane_no")
	private Integer globalLaneNo;

	@Column(name = "local_track_id", length = 80)
	private String localTrackId;

	@Column(name = "vehicle_pass_id", length = 120)
	private String vehiclePassId;

	@Column(name = "vehicle_confidence")
	private Double vehicleConfidence;

	@Column(name = "plate_text", length = 30)
	private String plateText;

	@Column(name = "plate_confidence")
	private Double plateConfidence;

	@Column(name = "candidate_count")
	private Integer candidateCount;

	@Column(name = "agreement_ratio")
	private Double agreementRatio;

	@Column(name = "bbox_x")
	private Double bboxX;

	@Column(name = "bbox_y")
	private Double bboxY;

	@Column(name = "bbox_w")
	private Double bboxW;

	@Column(name = "bbox_h")
	private Double bboxH;

	@Column(name = "bbox_coord", length = 40)
	private String bboxCoord;

	@Column(name = "needs_review")
	private Boolean needsReview;

	@Column(name = "review_reason", length = 300)
	private String reviewReason;

	@Column(name = "schema_version", length = 20)
	private String schemaVersion;

	@Column(name = "gps_judgement_status", length = 40)
	private String gpsJudgementStatus;

	@Column(name = "payment_decision", length = 40)
	private String paymentDecision;

	@Column(name = "inspection_status", length = 40)
	private String inspectionStatus;

	@Column(name = "event_status", length = 20)
	private String eventStatus;

	@Column(name = "received_at", nullable = false)
	private LocalDateTime receivedAt;

	protected PassageEventRecord() {
	}

	public PassageEventRecord(String eventId, byte[] payloadBytes, LocalDateTime receivedAt) {
		this(eventId, "protobuf", payloadBytes, receivedAt);
	}

	private PassageEventRecord(
		String eventId,
		String payloadFormat,
		byte[] payloadBytes,
		LocalDateTime receivedAt
	) {
		this.eventId = eventId;
		this.payloadFormat = payloadFormat;
		this.payloadBytes = payloadBytes;
		this.payloadSizeBytes = payloadBytes.length;
		this.receivedAt = receivedAt;
	}

	public static PassageEventRecord demo(
		String eventId,
		byte[] payloadBytes,
		String deviceId,
		String cameraId,
		String cameraRole,
		Integer laneNo,
		String plateText,
		Double plateConfidence,
		Integer candidateCount,
		Double agreementRatio,
		Boolean needsReview,
		String eventStatus,
		LocalDateTime eventTime,
		LocalDateTime receivedAt
	) {
		PassageEventRecord event = new PassageEventRecord(eventId, "json", payloadBytes, receivedAt);
		event.deviceId = deviceId;
		event.cameraId = cameraId;
		event.cameraRole = cameraRole;
		event.direction = cameraRole;
		event.laneNo = laneNo;
		event.plateText = plateText;
		event.plateConfidence = plateConfidence;
		event.candidateCount = candidateCount;
		event.agreementRatio = agreementRatio;
		event.needsReview = needsReview;
		event.eventStatus = eventStatus;
		event.eventTime = eventTime;
		event.schemaVersion = "hifive.pdm.demo.v1";
		return event;
	}

	public PassageEventRecord(
		String eventId,
		byte[] payloadBytes,
		String deviceId,
		String cameraId,
		String cameraGroupId,
		String cameraRole,
		LocalDateTime eventTime,
		String direction,
		Integer laneNo,
		Integer globalLaneNo,
		String localTrackId,
		String vehiclePassId,
		Double vehicleConfidence,
		String plateText,
		Double plateConfidence,
		Integer candidateCount,
		Double agreementRatio,
		Double bboxX,
		Double bboxY,
		Double bboxW,
		Double bboxH,
		String bboxCoord,
		Boolean needsReview,
		String reviewReason,
		String schemaVersion,
		String gpsJudgementStatus,
		String paymentDecision,
		String inspectionStatus,
		LocalDateTime receivedAt
	) {
		this(eventId, payloadBytes, receivedAt);
		this.deviceId = deviceId;
		this.cameraId = cameraId;
		this.cameraGroupId = cameraGroupId;
		this.cameraRole = cameraRole;
		this.eventTime = eventTime;
		this.direction = direction;
		this.laneNo = laneNo;
		this.globalLaneNo = globalLaneNo;
		this.localTrackId = localTrackId;
		this.vehiclePassId = vehiclePassId;
		this.vehicleConfidence = vehicleConfidence;
		this.plateText = plateText;
		this.plateConfidence = plateConfidence;
		this.candidateCount = candidateCount;
		this.agreementRatio = agreementRatio;
		this.bboxX = bboxX;
		this.bboxY = bboxY;
		this.bboxW = bboxW;
		this.bboxH = bboxH;
		this.bboxCoord = bboxCoord;
		this.needsReview = needsReview;
		this.reviewReason = reviewReason;
		this.schemaVersion = schemaVersion;
		this.gpsJudgementStatus = gpsJudgementStatus;
		this.paymentDecision = paymentDecision;
		this.inspectionStatus = inspectionStatus;
	}

	public Long getPassageEventId() {
		return passageEventId;
	}

	public String getEventId() {
		return eventId;
	}

	public Integer getPayloadSizeBytes() {
		return payloadSizeBytes;
	}

	public String getPayloadFormat() {
		return payloadFormat;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public String getCameraId() {
		return cameraId;
	}

	public String getCameraGroupId() {
		return cameraGroupId;
	}

	public String getCameraRole() {
		return cameraRole;
	}

	public LocalDateTime getEventTime() {
		return eventTime;
	}

	public String getDirection() {
		return direction;
	}

	public Integer getLaneNo() {
		return laneNo;
	}

	public Integer getGlobalLaneNo() {
		return globalLaneNo;
	}

	public String getLocalTrackId() {
		return localTrackId;
	}

	public String getVehiclePassId() {
		return vehiclePassId;
	}

	public Double getVehicleConfidence() {
		return vehicleConfidence;
	}

	public String getPlateText() {
		return plateText;
	}

	public Double getPlateConfidence() {
		return plateConfidence;
	}

	public Integer getCandidateCount() {
		return candidateCount;
	}

	public Double getAgreementRatio() {
		return agreementRatio;
	}

	public Double getBboxX() {
		return bboxX;
	}

	public Double getBboxY() {
		return bboxY;
	}

	public Double getBboxW() {
		return bboxW;
	}

	public Double getBboxH() {
		return bboxH;
	}

	public String getBboxCoord() {
		return bboxCoord;
	}

	public Boolean getNeedsReview() {
		return needsReview;
	}

	public String getReviewReason() {
		return reviewReason;
	}

	public String getSchemaVersion() {
		return schemaVersion;
	}

	public String getGpsJudgementStatus() {
		return gpsJudgementStatus;
	}

	public String getPaymentDecision() {
		return paymentDecision;
	}

	public String getInspectionStatus() {
		return inspectionStatus;
	}

	public String getEventStatus() {
		return eventStatus;
	}

	public LocalDateTime getReceivedAt() {
		return receivedAt;
	}
}
