package com.hifive.iot.dto;

import java.util.List;

public record PdmCameraResponse(
	Long cameraId,
	String cameraCode,
	String cameraName,
	String direction,
	List<Integer> laneIds,
	List<String> laneNames,
	Double healthScore,
	String riskLevel
) {
}
