package com.hifive.iot.dto;

import com.hifive.iot.entity.PassageEvidence;

public record PassageEvidenceResponse(
	String eventId,
	String eventImageUrl,
	String cropImageUrl
) {
	public static PassageEvidenceResponse from(PassageEvidence evidence) {
		return new PassageEvidenceResponse(
			evidence.getEventId(),
			evidence.getEventImageUrl(),
			evidence.getCropImageUrl()
		);
	}
}
