package com.hifive.iot.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.hifive.iot.entity.SettlementCandidate;
import com.hifive.iot.repository.SettlementCandidateRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SettlementService {
	private final SettlementCandidateRepository repository;

	public SettlementService(SettlementCandidateRepository repository) {
		this.repository = repository;
	}

	public List<SettlementCandidate> candidates() {
		return repository.findTop50ByOrderByCreatedAtDesc();
	}

	public Map<String, Object> summary(LocalDate date) {
		LocalDate target = date == null ? LocalDate.now() : date;
		LocalDateTime from = target.atStartOfDay();
		LocalDateTime to = target.plusDays(1).atStartOfDay();
		List<SettlementCandidate> rows = repository.findByCreatedAtBetween(from, to);
		int total = rows.stream().mapToInt(row -> row.getAmount() == null ? 0 : row.getAmount()).sum();
		return Map.of(
			"totalToll", total,
			"payableCount", rows.stream().filter(row -> "READY".equals(row.getStatus())).count(),
			"gpsOutOfZoneCount", rows.stream().filter(row -> "OUT_OF_ZONE".equals(row.getGpsJudgementStatus())).count(),
			"reviewPendingAmount", rows.stream()
				.filter(row -> "REVIEW_REQUIRED".equals(row.getStatus()))
				.mapToInt(row -> row.getAmount() == null ? 0 : row.getAmount())
				.sum(),
			"confirmedCount", repository.countByStatus("CONFIRMED"),
			"pendingCount", repository.countByStatus("READY"),
			"holdCount", repository.countByStatus("HOLD")
		);
	}

	@Transactional
	public SettlementCandidate confirm(String eventId) {
		SettlementCandidate candidate = repository.findByEventId(eventId)
			.orElseThrow(() -> new IllegalArgumentException("settlement candidate not found"));
		candidate.confirm();
		return candidate;
	}
}
