package com.hifive.iot.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.hifive.iot.entity.SettlementCandidate;
import com.hifive.iot.service.SettlementService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/operator/settlements")
public class OperatorSettlementController {
	private final SettlementService service;

	public OperatorSettlementController(SettlementService service) {
		this.service = service;
	}

	@GetMapping("/summary")
	public Map<String, Object> summary(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		return service.summary(date);
	}

	@GetMapping("/candidates")
	public List<SettlementCandidate> candidates() {
		return service.candidates();
	}

	@PostMapping("/{eventId}/confirm")
	public SettlementCandidate confirm(@PathVariable String eventId) {
		return service.confirm(eventId);
	}

	@PostMapping("/export")
	public Map<String, Object> export() {
		return Map.of("status", "accepted", "message", "export job placeholder");
	}
}
