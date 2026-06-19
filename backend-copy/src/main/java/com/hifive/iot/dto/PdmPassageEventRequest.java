package com.hifive.iot.dto;

import java.time.LocalDateTime;

public record PdmPassageEventRequest(
	String eventUuid,
	String deviceId,
	String cameraCode,
	Integer laneId,
	String plateText,
	Double plateConfidence,
	Integer candidateCount,
	Double agreementRatio,
	Boolean needsReview,
	String eventStatus,
	LocalDateTime eventTime
) {
}
