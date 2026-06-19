package com.hifive.iot.service;

import java.time.LocalDateTime;
import java.util.List;

import com.hifive.iot.dto.CompanyRequest;
import com.hifive.iot.dto.CompanyResponse;
import com.hifive.iot.entity.CompanyRecord;
import com.hifive.iot.repository.CompanyRepository;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class CompanyService {

	private final CompanyRepository companyRepository;

	public CompanyService(CompanyRepository companyRepository) {
		this.companyRepository = companyRepository;
	}

	@PostConstruct
	@Transactional
	public void ensureDemoCompanies() {
		if (companyRepository.count() > 0) {
			return;
		}
		companyRepository.save(new CompanyRecord("하이패스 서울(주)", "김서울", "02-1234-5678", "seoul@hipass.com", 5, "정상", LocalDateTime.now()));
		companyRepository.save(new CompanyRecord("수원 하이패스(주)", "이수원", "031-234-5678", "suwon@hipass.com", 4, "정상", LocalDateTime.now()));
		companyRepository.save(new CompanyRecord("대전 하이패스(주)", "박대전", "042-345-6789", "daejeon@hipass.com", 3, "주의", LocalDateTime.now()));
		companyRepository.save(new CompanyRecord("대구 하이패스(주)", "최대구", "053-456-7890", "daegu@hipass.com", 4, "정상", LocalDateTime.now()));
		companyRepository.save(new CompanyRecord("부산 하이패스(주)", "정부산", "051-567-8901", "busan@hipass.com", 6, "정상", LocalDateTime.now()));
	}

	@Transactional(readOnly = true)
	public List<CompanyResponse> findCompanies() {
		return companyRepository.findAllByOrderByCompanyIdAsc().stream()
			.map(CompanyResponse::from)
			.toList();
	}

	@Transactional
	public CompanyResponse create(CompanyRequest request) {
		validate(request);
		if (companyRepository.existsByEmail(request.email().trim())) {
			throw new IllegalArgumentException("company email already exists");
		}
		CompanyRecord saved = companyRepository.save(new CompanyRecord(
			request.name().trim(),
			request.owner().trim(),
			request.phone().trim(),
			request.email().trim(),
			request.centers(),
			StringUtils.hasText(request.status()) ? request.status().trim() : "정상",
			LocalDateTime.now()
		));
		return CompanyResponse.from(saved);
	}

	private void validate(CompanyRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("company request is required");
		}
		if (!StringUtils.hasText(request.name())) {
			throw new IllegalArgumentException("company name is required");
		}
		if (!StringUtils.hasText(request.owner())) {
			throw new IllegalArgumentException("owner is required");
		}
		if (!StringUtils.hasText(request.phone())) {
			throw new IllegalArgumentException("phone is required");
		}
		if (!StringUtils.hasText(request.email())) {
			throw new IllegalArgumentException("email is required");
		}
		if (request.centers() == null || request.centers() < 0) {
			throw new IllegalArgumentException("centers must be zero or positive");
		}
	}
}
