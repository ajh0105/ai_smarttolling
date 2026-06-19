package com.hifive.iot.dto;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonAlias;

public record GpsTelemetryRequest(
	@JsonAlias("deviceId")
	String gpsDeviceId,
	String plateNumber,
	String edgeNodeId,
	String laneId,
	Integer trackId,
	@JsonAlias("lat")
	Double latitude,
	@JsonAlias("lng")
	Double longitude,
	Double speedKmh,
	Double speed,
	Double heading,
	@JsonAlias("altitude")
	Double altitudeM,
	@JsonAlias({"accuracy", "accuracyMeters"})
	Double accuracyM,
	OffsetDateTime capturedAt,
	@JsonAlias("timestamp")
	String time,
	String provider,
	String fixStatus,
	String statusMessage,
	String rawSentence
) {
}
