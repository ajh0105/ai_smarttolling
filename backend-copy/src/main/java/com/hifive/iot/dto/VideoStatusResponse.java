package com.hifive.iot.dto;

public record VideoStatusResponse(
	boolean connected,
	String streamStatus,
	Double fps,
	Long bitrateKbps,
	Long lastFrameAgeMs,
	Long lastFrameTsMs,
	Integer activeViewers,
	String transport,
	String source,
	String activePath,
	String lastError
) {
	public static VideoStatusResponse waiting(String lastError) {
		return new VideoStatusResponse(
			false,
			"WAITING",
			null,
			null,
			null,
			null,
			0,
			null,
			null,
			null,
			lastError
		);
	}

	public VideoStatusResponse withStatus(String status) {
		return new VideoStatusResponse(
			connected,
			status,
			fps,
			bitrateKbps,
			lastFrameAgeMs,
			lastFrameTsMs,
			activeViewers,
			transport,
			source,
			activePath,
			lastError
		);
	}
}
