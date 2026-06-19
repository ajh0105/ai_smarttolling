package com.hifive.iot.dto;

import java.time.LocalDateTime;

public record PdmAnalysisResultRequest(
	Long cameraId,
	Integer laneId,
	LocalDateTime analysisStart,
	LocalDateTime analysisEnd,
	Double healthScore,
	String riskLevel,
	String modelType,
	String modelVersion,
	String reasonCode,
	String reasonText,
	String recommendedAction,
	String trendSummary
) {
}
