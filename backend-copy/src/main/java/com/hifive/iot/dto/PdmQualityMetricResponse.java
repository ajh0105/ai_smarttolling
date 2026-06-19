package com.hifive.iot.dto;

import java.time.LocalDateTime;

import com.hifive.iot.entity.CameraQualityMetric;

public record PdmQualityMetricResponse(
	Long metricId,
	Long cameraId,
	Integer laneId,
	LocalDateTime bucketStart,
	LocalDateTime bucketEnd,
	Double avgOcrConfidence,
	Double successRate,
	Double missingRate,
	Double matchRate,
	Double mismatchRate,
	Integer eventCount
) {
	public static PdmQualityMetricResponse from(CameraQualityMetric metric) {
		return new PdmQualityMetricResponse(
			metric.getMetricId(),
			metric.getCamera().getCameraId(),
			metric.getLaneId(),
			metric.getBucketStart(),
			metric.getBucketEnd(),
			metric.getAvgOcrConfidence(),
			metric.getSuccessRate(),
			metric.getMissingRate(),
			metric.getMatchRate(),
			metric.getMismatchRate(),
			metric.getEventCount()
		);
	}
}
