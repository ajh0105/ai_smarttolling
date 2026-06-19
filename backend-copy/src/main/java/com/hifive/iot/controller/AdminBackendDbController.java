package com.hifive.iot.controller;

import java.lang.management.ManagementFactory;
import java.util.Map;

import com.hifive.iot.repository.GpsTelemetryRepository;
import com.hifive.iot.repository.InspectionTaskRepository;
import com.hifive.iot.repository.PassageEventRepository;
import com.hifive.iot.repository.SettlementCandidateRepository;
import com.hifive.iot.repository.TollHistoryRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminBackendDbController {
	private final PassageEventRepository passageEventRepository;
	private final GpsTelemetryRepository gpsTelemetryRepository;
	private final SettlementCandidateRepository settlementCandidateRepository;
	private final InspectionTaskRepository inspectionTaskRepository;
	private final TollHistoryRepository tollHistoryRepository;

	public AdminBackendDbController(PassageEventRepository passageEventRepository, GpsTelemetryRepository gpsTelemetryRepository,
		SettlementCandidateRepository settlementCandidateRepository, InspectionTaskRepository inspectionTaskRepository,
		TollHistoryRepository tollHistoryRepository) {
		this.passageEventRepository = passageEventRepository;
		this.gpsTelemetryRepository = gpsTelemetryRepository;
		this.settlementCandidateRepository = settlementCandidateRepository;
		this.inspectionTaskRepository = inspectionTaskRepository;
		this.tollHistoryRepository = tollHistoryRepository;
	}

	@GetMapping("/api/admin/backend/status")
	public Map<String, Object> backendStatus() {
		return Map.of(
			"backendStatus", "OK",
			"backendUptimeMs", ManagementFactory.getRuntimeMXBean().getUptime(),
			"apiResponseP95Ms", 0,
			"validationFailureRate", 0,
			"duplicateBlockCount", 0,
			"saveSuccessRate", 1
		);
	}

	@GetMapping("/api/admin/db/status")
	public Map<String, Object> dbStatus() {
		return Map.of(
			"dbConnectionStatus", "UNKNOWN",
			"passageEventsRowCount", passageEventRepository.count(),
			"gpsTelemetryRowCount", gpsTelemetryRepository.count(),
			"settlementCandidatesRowCount", settlementCandidateRepository.count(),
			"reviewTasksRowCount", inspectionTaskRepository.count(),
			"tollHistoryRowCount", tollHistoryRepository.count()
		);
	}
}
