package com.hifive.iot.dto;

public record AuthResponse(
	boolean success,
	String message,
	MemberResponse member
) {
}
