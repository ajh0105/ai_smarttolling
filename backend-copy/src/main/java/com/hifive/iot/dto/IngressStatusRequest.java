package com.hifive.iot.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAlias;

public record IngressStatusRequest(
	@JsonAlias("ingress_id") String ingressId,
	@JsonAlias("uptime_sec") Long uptimeSec,
	@JsonAlias("received_events") Long receivedEvents,
	@JsonAlias("acked_events") Long ackedEvents,
	@JsonAlias("retry_events") Long retryEvents,
	@JsonAlias("rejected_events") Long rejectedEvents,
	@JsonAlias("malformed_frames") Long malformedFrames,
	@JsonAlias("network_transition_events") Long networkTransitionEvents,
	@JsonAlias("edge_status_events") Long edgeStatusEvents,
	@JsonAlias("active_connections") Long activeConnections,
	@JsonAlias("total_connections") Long totalConnections,
	@JsonAlias("last_event_id") String lastEventId,
	@JsonAlias("last_payload_bytes") Long lastPayloadBytes,
	@JsonAlias("spring_forward") Map<String, Object> springForward,
	@JsonAlias("edge_status_forward") Map<String, Object> edgeStatusForward,
	@JsonAlias("latest_edge_status") Map<String, Object> latestEdgeStatus
) {
}
