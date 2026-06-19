package com.hifive.iot.dto;

import com.hifive.iot.entity.CompanyRecord;

public record CompanyResponse(
	Long id,
	String name,
	String owner,
	String phone,
	String email,
	Integer centers,
	String status
) {
	public static CompanyResponse from(CompanyRecord record) {
		return new CompanyResponse(
			record.getCompanyId(),
			record.getCompanyName(),
			record.getOwnerName(),
			record.getPhone(),
			record.getEmail(),
			record.getCenterCount(),
			record.getStatus()
		);
	}
}
