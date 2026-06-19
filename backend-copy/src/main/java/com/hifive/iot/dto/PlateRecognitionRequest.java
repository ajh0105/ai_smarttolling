package com.hifive.iot.dto;

import java.time.OffsetDateTime;

public record PlateRecognitionRequest(
	String plateNumber,
	String gpsDeviceId,
	String laneId,
	String edgeNodeId,
	Double plateConfidence,
	OffsetDateTime capturedAt
) {
}
