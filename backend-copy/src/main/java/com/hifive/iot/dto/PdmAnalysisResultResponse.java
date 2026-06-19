package com.hifive.iot.dto;

import java.time.LocalDateTime;

import com.hifive.iot.entity.PdmAnalysisResult;

public record PdmAnalysisResultResponse(
	Long analysisId,
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
	String trendSummary,
	LocalDateTime analyzedAt
) {
	public static PdmAnalysisResultResponse from(PdmAnalysisResult result) {
		return new PdmAnalysisResultResponse(
			result.getAnalysisId(),
			result.getCamera().getCameraId(),
			result.getLaneId(),
			result.getAnalysisStart(),
			result.getAnalysisEnd(),
			result.getHealthScore(),
			result.getRiskLevel(),
			result.getModelType(),
			result.getModelVersion(),
			result.getReasonCode(),
			result.getReasonText(),
			result.getRecommendedAction(),
			result.getTrendSummary(),
			result.getAnalyzedAt()
		);
	}
}
