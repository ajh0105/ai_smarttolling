package com.hifive.iot.service;

import java.io.InputStream;
import java.util.Map;

import com.hifive.iot.dto.VideoStatusResponse;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Service
public class VideoStreamService {
	private final IngressVideoClient ingressVideoClient;

	public VideoStreamService(IngressVideoClient ingressVideoClient) {
		this.ingressVideoClient = ingressVideoClient;
	}

	public VideoStatusResponse getVideoStatus() {
		VideoStatusResponse response = ingressVideoClient.fetchStatus();
		return response.withStatus(resolveStatus(response));
	}

	public StreamingResponseBody streamVideo() {
		return outputStream -> {
			try (InputStream inputStream = ingressVideoClient.openStream()) {
				inputStream.transferTo(outputStream);
			}
		};
	}

	public Map<String, Object> getPipelineVideoNode() {
		VideoStatusResponse status = getVideoStatus();
		return Map.of(
			"name", "Video Receiver",
			"status", status.streamStatus(),
			"connected", status.connected(),
			"fps", nullable(status.fps()),
			"bitrateKbps", nullable(status.bitrateKbps()),
			"lastFrameAgeMs", nullable(status.lastFrameAgeMs()),
			"activeViewers", nullable(status.activeViewers()),
			"transport", nullable(status.transport()),
			"activePath", nullable(status.activePath()),
			"lastError", nullable(status.lastError())
		);
	}

	private String resolveStatus(VideoStatusResponse response) {
		if (response.lastError() != null && !response.lastError().isBlank()) {
			return "ERROR";
		}
		if (!response.connected()) {
			return "WAITING";
		}
		if (response.lastFrameAgeMs() != null && response.lastFrameAgeMs() > 3000) {
			return "STALE";
		}
		return "RUNNING";
	}

	private Object nullable(Object value) {
		return value == null ? "" : value;
	}
}
