package com.hifive.iot.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.hifive.iot.dto.SignUpRequest;
import com.hifive.iot.entity.Member;
import com.hifive.iot.repository.MemberRepository;

import jakarta.annotation.PostConstruct;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class MemberService {

	public static final String MASTER_ADMIN_EMAIL = "admin@hifive.com";
	private static final String MASTER_ADMIN_PASSWORD = "admin1234";
	private static final String DEFAULT_DASHBOARD_ID = "RC-DEMO-CENTER";

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	public MemberService(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	@PostConstruct
	@Transactional
	public void ensureMasterAdmin() {
		String email = normalizeEmail(MASTER_ADMIN_EMAIL);
		Optional<Member> existing = memberRepository.findById(email);
		if (existing.isPresent()) {
			Member admin = existing.get();
			if (!admin.isMasterAdmin()) {
				admin.promoteToMasterAdmin(passwordEncoder.encode(MASTER_ADMIN_PASSWORD));
			}
			return;
		}
		memberRepository.save(new Member(
			email,
			passwordEncoder.encode(MASTER_ADMIN_PASSWORD),
			"admin",
			null,
			"MASTER_ADMIN",
			null,
			LocalDateTime.now()
		));
	}

	@Transactional
	public boolean signUp(SignUpRequest signUpRequest) {
		validateSignUp(signUpRequest);
		String email = normalizeEmail(signUpRequest.email());
		Member member = new Member(
			email,
			passwordEncoder.encode(signUpRequest.password()),
			signUpRequest.memberName().trim(),
			trimToNull(signUpRequest.plateNumber()),
			"BRANCH_OPERATOR",
			DEFAULT_DASHBOARD_ID,
			LocalDateTime.now()
		);
		if (memberRepository.existsById(email)) {
			return false;
		}
		memberRepository.save(member);
		return true;
	}

	@Transactional(readOnly = true)
	public Optional<Member> login(String email, String password) {
		String normalizedEmail = normalizeEmail(email);
		return memberRepository.findById(normalizedEmail)
			.filter(member -> passwordEncoder.matches(password, member.passwordHash()));
	}

	@Transactional(readOnly = true)
	public List<Member> findMembers() {
		return memberRepository.findAllByOrderByCreatedAtDesc();
	}

	@Transactional
	public Member assignDashboard(String email, String dashboardId) {
		Member member = memberRepository.findById(normalizeEmail(email))
			.orElseThrow(() -> new IllegalArgumentException("member not found"));
		if (member.isMasterAdmin()) {
			throw new IllegalArgumentException("master admin dashboard cannot be assigned");
		}
		member.assignDashboard(trimToNull(dashboardId));
		return member;
	}

	private void validateSignUp(SignUpRequest request) {
		if (!StringUtils.hasText(request.email()) || !request.email().contains("@")) {
			throw new IllegalArgumentException("valid email is required");
		}
		if (!StringUtils.hasText(request.password()) || request.password().length() < 6) {
			throw new IllegalArgumentException("password must be at least 6 characters");
		}
		if (!StringUtils.hasText(request.memberName())) {
			throw new IllegalArgumentException("memberName is required");
		}
	}

	private String normalizeEmail(String email) {
		return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
	}

	private String trimToNull(String value) {
		if (!StringUtils.hasText(value)) {
			return null;
		}
		return value.trim();
	}
}
