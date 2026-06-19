package com.hifive.iot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "hifive.ingress-video")
public class IngressVideoProperties {
	private String baseUrl = "http://127.0.0.1:8000";
	private String statusPath = "/video/status";
	private String streamPath = "/video/stream.mjpg";
	private int connectTimeoutMs = 1000;
	private int readTimeoutMs = 3000;

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getStatusPath() {
		return statusPath;
	}

	public void setStatusPath(String statusPath) {
		this.statusPath = statusPath;
	}

	public String getStreamPath() {
		return streamPath;
	}

	public void setStreamPath(String streamPath) {
		this.streamPath = streamPath;
	}

	public int getConnectTimeoutMs() {
		return connectTimeoutMs;
	}

	public void setConnectTimeoutMs(int connectTimeoutMs) {
		this.connectTimeoutMs = connectTimeoutMs;
	}

	public int getReadTimeoutMs() {
		return readTimeoutMs;
	}

	public void setReadTimeoutMs(int readTimeoutMs) {
		this.readTimeoutMs = readTimeoutMs;
	}

	public String statusUrl() {
		return join(baseUrl, statusPath);
	}

	public String streamUrl() {
		return join(baseUrl, streamPath);
	}

	private String join(String base, String path) {
		String normalizedBase = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
		String normalizedPath = path.startsWith("/") ? path : "/" + path;
		return normalizedBase + normalizedPath;
	}
}
