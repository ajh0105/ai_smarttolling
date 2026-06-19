package com.hifive.iot.controller;

import java.util.List;

import com.hifive.iot.dto.CompanyRequest;
import com.hifive.iot.dto.CompanyResponse;
import com.hifive.iot.entity.Member;
import com.hifive.iot.service.CompanyService;
import com.hifive.iot.service.MemberService;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/companies")
public class AdminCompanyController {

	private static final String LOGIN_MEMBER = "loginMember";

	private final CompanyService companyService;

	public AdminCompanyController(CompanyService companyService) {
		this.companyService = companyService;
	}

	@GetMapping
	public List<CompanyResponse> companies(HttpSession session) {
		requireMasterAdmin(session);
		return companyService.findCompanies();
	}

	@PostMapping
	public CompanyResponse create(@RequestBody CompanyRequest request, HttpSession session) {
		requireMasterAdmin(session);
		return companyService.create(request);
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
