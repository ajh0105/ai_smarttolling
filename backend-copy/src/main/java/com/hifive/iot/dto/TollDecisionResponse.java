package com.hifive.iot.dto;

public record TollDecisionResponse(
	boolean charged,
	String reason,
	TollChargeResponse charge
) {
}
