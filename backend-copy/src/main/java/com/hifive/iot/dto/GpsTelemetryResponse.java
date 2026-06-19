package com.hifive.iot.dto;

import java.time.LocalDateTime;

public record GpsTelemetryResponse(
	Long id,
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
}
