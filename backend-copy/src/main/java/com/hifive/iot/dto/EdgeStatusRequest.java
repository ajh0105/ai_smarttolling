package com.hifive.iot.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAlias;

public record EdgeStatusRequest(
	@JsonAlias("device_id") String deviceId,
	@JsonAlias("camera_id") String cameraId,
	@JsonAlias("camera_role") String cameraRole,
	Map<String, Object> source,
	Map<String, Object> runtime,
	Map<String, Object> transport,
	Map<String, Object> spool,
	@JsonAlias("status_send") Map<String, Object> statusSend,
	@JsonAlias("ts_ms") Long tsMs
) {
}
