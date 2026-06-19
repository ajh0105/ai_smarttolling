package com.hifive.iot.dto;

import com.hifive.iot.entity.Member;

public record MemberResponse(
	String email,
	String memberName,
	String plateNumber,
	String role,
	String assignedDashboardId
) {

	public static MemberResponse from(Member member) {
		return new MemberResponse(
			member.email(),
			member.memberName(),
			member.plateNumber(),
			member.role(),
			member.assignedDashboardId()
		);
	}
}
