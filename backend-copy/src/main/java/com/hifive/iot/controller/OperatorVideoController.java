package com.hifive.iot.controller;

import com.hifive.iot.dto.VideoStatusResponse;
import com.hifive.iot.entity.Member;
import com.hifive.iot.service.VideoStreamService;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping("/api/operator/video")
public class OperatorVideoController {
	private static final String LOGIN_MEMBER = "loginMember";
	private static final String MJPEG_MEDIA_TYPE = "multipart/x-mixed-replace; boundary=frame";

	private final VideoStreamService videoStreamService;

	public OperatorVideoController(VideoStreamService videoStreamService) {
		this.videoStreamService = videoStreamService;
	}

	@GetMapping("/status")
	public VideoStatusResponse status(HttpSession session) {
		requireLogin(session);
		return videoStreamService.getVideoStatus();
	}

	@GetMapping("/stream")
	public ResponseEntity<StreamingResponseBody> stream(HttpSession session) {
		requireLogin(session);
		return ResponseEntity.ok()
			.header(HttpHeaders.CACHE_CONTROL, "no-store")
			.contentType(MediaType.parseMediaType(MJPEG_MEDIA_TYPE))
			.body(videoStreamService.streamVideo());
	}

	private Member requireLogin(HttpSession session) {
		Member member = (Member) session.getAttribute(LOGIN_MEMBER);
		if (member == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "login required");
		}
		return member;
	}
}
