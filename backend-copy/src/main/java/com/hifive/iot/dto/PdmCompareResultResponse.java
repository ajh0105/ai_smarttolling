package com.hifive.iot.dto;

import java.time.LocalDateTime;

import com.hifive.iot.entity.CameraCompareResult;

public record PdmCompareResultResponse(
	Long compareId,
	String eventGroupKey,
	Integer laneId,
	String laneName,
	String frontCameraCode,
	String rearCameraCode,
	String frontPlateText,
	String rearPlateText,
	Boolean isMatched,
	String mismatchType,
	Double confidenceGap,
	LocalDateTime comparedAt
) {
	public static PdmCompareResultResponse from(CameraCompareResult result) {
		return new PdmCompareResultResponse(
			result.getCompareId(),
			result.getEventGroupKey(),
			result.getLaneId(),
			result.getLaneId() + "차로",
			result.getFrontCamera().getCameraCode(),
			result.getRearCamera().getCameraCode(),
			result.getFrontPlateText(),
			result.getRearPlateText(),
			result.getIsMatched(),
			result.getMismatchType(),
			result.getConfidenceGap(),
			result.getComparedAt()
		);
	}
}
