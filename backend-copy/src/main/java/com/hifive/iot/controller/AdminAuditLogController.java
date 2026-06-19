package com.hifive.iot.controller;

import java.util.List;

import com.hifive.iot.entity.AuditLog;
import com.hifive.iot.repository.AuditLogRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/audit-logs")
public class AdminAuditLogController {
	private final AuditLogRepository repository;

	public AdminAuditLogController(AuditLogRepository repository) {
		this.repository = repository;
	}

	@GetMapping
	public List<AuditLog> list() {
		return repository.findTop50ByOrderByCreatedAtDesc();
	}
}
