package com.hifive.iot.controller;

import java.util.Map;

import com.hifive.iot.repository.AuditLogRepository;
import com.hifive.iot.repository.CompanyRepository;
import com.hifive.iot.repository.EdgeStatusLatestRepository;
import com.hifive.iot.repository.MapMarkerRepository;
import com.hifive.iot.repository.SystemAlertRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {
	private final CompanyRepository companyRepository;
	private final MapMarkerRepository mapMarkerRepository;
	private final EdgeStatusLatestRepository edgeStatusLatestRepository;
	private final SystemAlertRepository alertRepository;
	private final AuditLogRepository auditLogRepository;

	public AdminDashboardController(CompanyRepository companyRepository, MapMarkerRepository mapMarkerRepository,
		EdgeStatusLatestRepository edgeStatusLatestRepository, SystemAlertRepository alertRepository,
		AuditLogRepository auditLogRepository) {
		this.companyRepository = companyRepository;
		this.mapMarkerRepository = mapMarkerRepository;
		this.edgeStatusLatestRepository = edgeStatusLatestRepository;
		this.alertRepository = alertRepository;
		this.auditLogRepository = auditLogRepository;
	}

	@GetMapping("/summary")
	public Map<String, Object> summary() {
		return Map.of(
			"totalCompanies", companyRepository.count(),
			"totalSites", mapMarkerRepository.count(),
			"normalEdges", edgeStatusLatestRepository.countByAliveTrue(),
			"staleEdges", edgeStatusLatestRepository.countByStaleTrue(),
			"overallSystemStatus", alertRepository.countByStatus("OPEN") > 0 ? "WARN" : "OK",
			"recentAlerts", alertRepository.findTop50ByOrderByCreatedAtDesc(),
			"recentAuditLogs", auditLogRepository.findTop50ByOrderByCreatedAtDesc()
		);
	}
}
