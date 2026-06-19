package com.hifive.iot.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import com.hifive.iot.repository.EdgeStatusLatestRepository;
import com.hifive.iot.repository.InspectionTaskRepository;
import com.hifive.iot.repository.PassageEventRepository;
import com.hifive.iot.repository.SettlementCandidateRepository;
import com.hifive.iot.repository.SystemAlertRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/operator/dashboard")
public class OperatorDashboardController {
	private final PassageEventRepository passageEventRepository;
	private final InspectionTaskRepository inspectionTaskRepository;
	private final SettlementCandidateRepository settlementCandidateRepository;
	private final EdgeStatusLatestRepository edgeStatusLatestRepository;
	private final SystemAlertRepository alertRepository;

	public OperatorDashboardController(PassageEventRepository passageEventRepository,
		InspectionTaskRepository inspectionTaskRepository, SettlementCandidateRepository settlementCandidateRepository,
		EdgeStatusLatestRepository edgeStatusLatestRepository, SystemAlertRepository alertRepository) {
		this.passageEventRepository = passageEventRepository;
		this.inspectionTaskRepository = inspectionTaskRepository;
		this.settlementCandidateRepository = settlementCandidateRepository;
		this.edgeStatusLatestRepository = edgeStatusLatestRepository;
		this.alertRepository = alertRepository;
	}

	@GetMapping("/summary")
	public Map<String, Object> summary() {
		LocalDateTime from = LocalDate.now().atStartOfDay();
		LocalDateTime to = LocalDate.now().plusDays(1).atStartOfDay();
		var recentValidPassages = passageEventRepository.findTop50ByEventTimeIsNotNullAndPlateTextIsNotNullAndLaneNoIsNotNullOrderByEventTimeDesc();
		long todayPassageCount = recentValidPassages.stream()
			.filter(event -> event.getReceivedAt().isAfter(from) && event.getReceivedAt().isBefore(to))
			.count();
		return Map.of(
			"todayPassageCount", todayPassageCount,
			"reviewPendingCount", inspectionTaskRepository.countByStatus("PENDING"),
			"settlementReadyCount", settlementCandidateRepository.countByStatus("READY"),
			"aliveEdgeCount", edgeStatusLatestRepository.countByAliveTrue(),
			"recentPassages", recentValidPassages,
			"fieldAlerts", alertRepository.findTop50ByOrderByCreatedAtDesc()
		);
	}
}
