package com.hifive.iot.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hifive.iot.config.IngressVideoProperties;
import com.hifive.iot.dto.VideoStatusResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class IngressVideoClient {
	private final IngressVideoProperties properties;
	private final ObjectMapper objectMapper;

	public IngressVideoClient(IngressVideoProperties properties, ObjectMapper objectMapper) {
		this.properties = properties;
		this.objectMapper = objectMapper;
	}

	public VideoStatusResponse fetchStatus() {
		HttpURLConnection connection = null;
		try {
			connection = openConnection(properties.statusUrl());
			connection.setRequestMethod("GET");
			int statusCode = connection.getResponseCode();
			if (statusCode < 200 || statusCode >= 300) {
				throw unavailable("Ingress video status returned " + statusCode);
			}
			try (InputStream inputStream = connection.getInputStream()) {
				JsonNode node = objectMapper.readTree(inputStream);
				return new VideoStatusResponse(
					bool(node, "connected", true),
					text(node, "streamStatus", text(node, "stream_status", "WAITING")),
					decimal(node, "fps"),
					longValue(node, "bitrateKbps", "bitrate_kbps"),
					longValue(node, "lastFrameAgeMs", "last_frame_age_ms"),
					longValue(node, "lastFrameTsMs", "last_frame_ts_ms"),
					intValue(node, "activeViewers", "active_viewers"),
					text(node, "transport", null),
					text(node, "source", null),
					text(node, "activePath", text(node, "active_path", null)),
					text(node, "lastError", text(node, "last_error", ""))
				);
			}
		} catch (IOException exception) {
			throw unavailable("Ingress video status unavailable");
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	public InputStream openStream() {
		try {
			HttpURLConnection connection = openConnection(properties.streamUrl());
			connection.setRequestMethod("GET");
			int statusCode = connection.getResponseCode();
			if (statusCode < 200 || statusCode >= 300) {
				connection.disconnect();
				throw unavailable("Ingress video stream returned " + statusCode);
			}
			return new DisconnectingInputStream(connection.getInputStream(), connection);
		} catch (IOException exception) {
			throw unavailable("Ingress video stream unavailable");
		}
	}

	private HttpURLConnection openConnection(String targetUrl) throws IOException {
		URL url = URI.create(targetUrl).toURL();
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setConnectTimeout(properties.getConnectTimeoutMs());
		connection.setReadTimeout(properties.getReadTimeoutMs());
		return connection;
	}

	private ResponseStatusException unavailable(String message) {
		return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, message);
	}

	private boolean bool(JsonNode node, String field, boolean fallback) {
		JsonNode value = node.get(field);
		return value == null || value.isNull() ? fallback : value.asBoolean();
	}

	private String text(JsonNode node, String field, String fallback) {
		JsonNode value = node.get(field);
		return value == null || value.isNull() ? fallback : value.asText();
	}

	private Double decimal(JsonNode node, String field) {
		JsonNode value = node.get(field);
		return value == null || value.isNull() ? null : value.asDouble();
	}

	private Long longValue(JsonNode node, String camelField, String snakeField) {
		JsonNode value = node.get(camelField);
		if (value == null || value.isNull()) {
			value = node.get(snakeField);
		}
		return value == null || value.isNull() ? null : value.asLong();
	}

	private Integer intValue(JsonNode node, String camelField, String snakeField) {
		JsonNode value = node.get(camelField);
		if (value == null || value.isNull()) {
			value = node.get(snakeField);
		}
		return value == null || value.isNull() ? null : value.asInt();
	}

	private static class DisconnectingInputStream extends InputStream {
		private final InputStream delegate;
		private final HttpURLConnection connection;

		DisconnectingInputStream(InputStream delegate, HttpURLConnection connection) {
			this.delegate = delegate;
			this.connection = connection;
		}

		@Override
		public int read() throws IOException {
			return delegate.read();
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			return delegate.read(b, off, len);
		}

		@Override
		public void close() throws IOException {
			try {
				delegate.close();
			} finally {
				connection.disconnect();
			}
		}
	}
}
