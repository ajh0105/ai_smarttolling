package com.hifive.iot.mapper;

import com.hifive.iot.dto.GpsTelemetryResponse;
import com.hifive.iot.entity.GpsTelemetryRecord;

public final class GpsTelemetryMapper {

	private GpsTelemetryMapper() {
	}

	public static GpsTelemetryResponse toResponse(GpsTelemetryRecord telemetry) {
		return new GpsTelemetryResponse(
			telemetry.getGpsTelemetryId(),
			telemetry.getGpsDeviceId(),
			telemetry.getPlateNumber(),
			telemetry.getEdgeNodeId(),
			telemetry.getLaneId(),
			telemetry.getTrackId(),
			telemetry.getLatitude(),
			telemetry.getLongitude(),
			telemetry.getSpeedKmh(),
			telemetry.getHeading(),
			telemetry.getAltitudeM(),
			telemetry.getAccuracyM(),
			telemetry.getProvider(),
			telemetry.getFixStatus(),
			telemetry.getStatusMessage(),
			telemetry.getRawSentence(),
			telemetry.getCapturedAt(),
			telemetry.getReceivedAt()
		);
	}
}
