package com.hifive.iot.dto;

import java.time.LocalDateTime;

import com.hifive.iot.entity.MaintenanceAlert;

public record PdmAlertResponse(
	Long alertId,
	Long analysisId,
	Long cameraId,
	String cameraCode,
	Integer laneId,
	Double healthScore,
	String riskLevel,
	String alertTitle,
	String alertMessage,
	String reasonText,
	String recommendedAction,
	String status,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {
	public static PdmAlertResponse from(MaintenanceAlert alert) {
		return new PdmAlertResponse(
			alert.getAlertId(),
			alert.getAnalysis().getAnalysisId(),
			alert.getCamera().getCameraId(),
			alert.getCamera().getCameraCode(),
			alert.getLaneId(),
			alert.getAnalysis().getHealthScore(),
			alert.getRiskLevel(),
			alert.getAlertTitle(),
			alert.getAlertMessage(),
			alert.getReasonText(),
			alert.getRecommendedAction(),
			alert.getStatus(),
			alert.getCreatedAt(),
			alert.getUpdatedAt()
		);
	}
}
