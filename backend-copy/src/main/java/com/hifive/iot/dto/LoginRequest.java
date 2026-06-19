package com.hifive.iot.dto;

public record LoginRequest(
	String email,
	String password
) {
}
