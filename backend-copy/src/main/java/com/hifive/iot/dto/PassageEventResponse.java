package com.hifive.iot.dto;

import java.time.LocalDateTime;

import com.hifive.iot.entity.PassageEventRecord;
import com.hifive.iot.entity.PassageEvidence;

public record PassageEventResponse(
	String eventId,
	String plateText,
	Integer laneNo,
	String direction,
	LocalDateTime eventTime,
	String gpsJudgementStatus,
	String paymentDecision,
	String inspectionStatus,
	Double plateConfidence,
	Double bboxX,
	Double bboxY,
	Double bboxW,
	Double bboxH,
	String eventImageUrl,
	String cropImageUrl
) {
	public static PassageEventResponse from(PassageEventRecord event, PassageEvidence evidence) {
		return new PassageEventResponse(
			event.getEventId(),
			event.getPlateText(),
			event.getLaneNo(),
			event.getDirection(),
			event.getEventTime(),
			event.getGpsJudgementStatus(),
			event.getPaymentDecision(),
			event.getInspectionStatus(),
			event.getPlateConfidence(),
			event.getBboxX(),
			event.getBboxY(),
			event.getBboxW(),
			event.getBboxH(),
			evidence == null ? null : evidence.getEventImageUrl(),
			evidence == null ? null : evidence.getCropImageUrl()
		);
	}
}
