package com.hifive.iot.dto;

public record SignUpRequest(
	String email,
	String password,
	String memberName,
	String plateNumber
) {
}
