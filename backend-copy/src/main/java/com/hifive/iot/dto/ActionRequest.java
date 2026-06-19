package com.hifive.iot.dto;

public record ActionRequest(
	String actor,
	String assignee,
	String memo,
	String reason
) {
}
