package com.hifive.iot.dto;

import java.util.List;

public record PdmCameraDetailResponse(
	Long cameraId,
	String cameraCode,
	String cameraName,
	String direction,
	List<Integer> laneIds,
	List<String> laneNames,
	Double healthScore,
	String riskLevel,
	String modelType,
	String modelVersion,
	Double avgOcrConfidence,
	Double successRate,
	Double missingRate,
	Double matchRate,
	Double mismatchRate,
	String reasonText,
	String recommendedAction
) {
}
