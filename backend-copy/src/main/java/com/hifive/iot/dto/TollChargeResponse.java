package com.hifive.iot.dto;

import java.time.LocalDateTime;

public record TollChargeResponse(
	Long id,
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
}
