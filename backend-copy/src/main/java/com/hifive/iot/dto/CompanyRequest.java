package com.hifive.iot.dto;

public record CompanyRequest(
	String name,
	String owner,
	String phone,
	String email,
	Integer centers,
	String status
) {
}
