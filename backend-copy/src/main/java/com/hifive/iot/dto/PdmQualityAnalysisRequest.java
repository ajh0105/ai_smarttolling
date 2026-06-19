package com.hifive.iot.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PdmQualityAnalysisRequest(
	Long cameraId,
	List<Integer> laneIds,
	List<Metric> metrics
) {
	public record Metric(
		LocalDateTime bucketStart,
		Double avgOcrConfidence,
		Double successRate,
		Double missingRate,
		Double matchRate,
		Double mismatchRate
	) {
	}
}
