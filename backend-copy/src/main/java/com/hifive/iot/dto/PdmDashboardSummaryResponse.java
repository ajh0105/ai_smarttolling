package com.hifive.iot.dto;

public record PdmDashboardSummaryResponse(
	long totalCameraCount,
	long normalCameraCount,
	long warningCameraCount,
	long criticalCameraCount,
	Double averageHealthScore
) {
}
