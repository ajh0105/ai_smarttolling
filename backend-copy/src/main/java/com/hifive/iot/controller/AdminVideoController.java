package com.hifive.iot.controller;

import com.hifive.iot.dto.VideoStatusResponse;
import com.hifive.iot.entity.Member;
import com.hifive.iot.service.MemberService;
import com.hifive.iot.service.VideoStreamService;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin/video")
public class AdminVideoController {
	private static final String LOGIN_MEMBER = "loginMember";

	private final VideoStreamService videoStreamService;

	public AdminVideoController(VideoStreamService videoStreamService) {
		this.videoStreamService = videoStreamService;
	}

	@GetMapping("/status")
	public VideoStatusResponse status(HttpSession session) {
		requireMasterAdmin(session);
		return videoStreamService.getVideoStatus();
	}

	private void requireMasterAdmin(HttpSession session) {
		Member member = (Member) session.getAttribute(LOGIN_MEMBER);
		if (member == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "login required");
		}
		if (!member.isMasterAdmin() && !MemberService.MASTER_ADMIN_EMAIL.equalsIgnoreCase(member.email())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "admin permission required");
		}
	}
}
