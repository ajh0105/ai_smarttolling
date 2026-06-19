package com.hifive.iot.controller;

import com.hifive.iot.dto.AuthResponse;
import com.hifive.iot.dto.LoginRequest;
import com.hifive.iot.dto.MemberResponse;
import com.hifive.iot.dto.SignUpRequest;
import com.hifive.iot.service.MemberService;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private static final String LOGIN_MEMBER = "loginMember";

	private final MemberService memberService;

	public AuthController(MemberService memberService) {
		this.memberService = memberService;
	}

	@PostMapping("/signup")
	public ResponseEntity<AuthResponse> signUp(@RequestBody SignUpRequest signUpRequest) {
		boolean created = memberService.signUp(signUpRequest);
		if (!created) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(new AuthResponse(false, "\uc774\ubbf8 \uc0ac\uc6a9 \uc911\uc778 \uc774\uba54\uc77c\uc785\ub2c8\ub2e4.", null));
		}
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(new AuthResponse(true, "\ud68c\uc6d0\uac00\uc785\uc774 \uc644\ub8cc\ub418\uc5c8\uc2b5\ub2c8\ub2e4.", null));
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
		return memberService.login(loginRequest.email(), loginRequest.password())
			.map(member -> {
				session.setAttribute(LOGIN_MEMBER, member);
				return ResponseEntity.ok(new AuthResponse(true, "\ub85c\uadf8\uc778\ub418\uc5c8\uc2b5\ub2c8\ub2e4.", MemberResponse.from(member)));
			})
			.orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(new AuthResponse(false, "\uc774\uba54\uc77c \ub610\ub294 \ube44\ubc00\ubc88\ud638\ub97c \ud655\uc778\ud574 \uc8fc\uc138\uc694.", null)));
	}

	@PostMapping("/logout")
	public AuthResponse logout(HttpSession session) {
		session.invalidate();
		return new AuthResponse(true, "\ub85c\uadf8\uc544\uc6c3\ub418\uc5c8\uc2b5\ub2c8\ub2e4.", null);
	}
}
