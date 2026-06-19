package com.hifive.iot.controller;

import java.util.List;

import com.hifive.iot.dto.MemberDashboardAssignmentRequest;
import com.hifive.iot.dto.MemberResponse;
import com.hifive.iot.entity.Member;
import com.hifive.iot.service.MemberService;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/members")
public class AdminMemberController {

	private static final String LOGIN_MEMBER = "loginMember";

	private final MemberService memberService;

	public AdminMemberController(MemberService memberService) {
		this.memberService = memberService;
	}

	@GetMapping
	public List<MemberResponse> members(HttpSession session) {
		requireMasterAdmin(session);
		return memberService.findMembers().stream()
			.map(MemberResponse::from)
			.toList();
	}

	@PatchMapping("/{email}/dashboard")
	public MemberResponse assignDashboard(
		@PathVariable String email,
		@RequestBody MemberDashboardAssignmentRequest request,
		HttpSession session
	) {
		requireMasterAdmin(session);
		return MemberResponse.from(memberService.assignDashboard(email, request.dashboardId()));
	}

	private void requireMasterAdmin(HttpSession session) {
		Member member = (Member) session.getAttribute(LOGIN_MEMBER);
		if (member == null || (!member.isMasterAdmin() && !MemberService.MASTER_ADMIN_EMAIL.equalsIgnoreCase(member.email()))) {
			throw new AdminAccessDeniedException();
		}
	}

	@ResponseStatus(HttpStatus.FORBIDDEN)
	private static class AdminAccessDeniedException extends RuntimeException {
	}
}
