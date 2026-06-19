package com.hifive.iot.dto;

import java.time.LocalDateTime;

public record IngestResultResponse(
	String eventId,
	String status,
	boolean duplicate,
	Integer payloadSizeBytes,
	LocalDateTime receivedAt
) {
}
