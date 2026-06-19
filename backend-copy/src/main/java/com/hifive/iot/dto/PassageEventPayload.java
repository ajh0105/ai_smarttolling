package com.hifive.iot.dto;

import java.time.LocalDateTime;

public record PassageEventPayload(
	String eventId,
	String deviceId,
	String cameraId,
	String cameraGroupId,
	String cameraRole,
	LocalDateTime timestamp,
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
	String schemaVersion
) {
}
