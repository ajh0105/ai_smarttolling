package com.hifive.iot.controller;

import java.util.List;
import java.util.Map;

import com.hifive.iot.entity.GpsJudgement;
import com.hifive.iot.repository.GpsJudgementRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/operator/gps-judgements")
public class OperatorGpsJudgementController {
	private final GpsJudgementRepository repository;

	public OperatorGpsJudgementController(GpsJudgementRepository repository) {
		this.repository = repository;
	}

	@GetMapping
	public List<GpsJudgement> list() {
		return repository.findTop50ByOrderByJudgedAtDesc();
	}

	@GetMapping("/{eventId}")
	public GpsJudgement detail(@PathVariable String eventId) {
		return repository.findByEventId(eventId).orElseThrow(() -> new IllegalArgumentException("gps judgement not found"));
	}

	@PostMapping("/{eventId}/recalculate")
	public Map<String, Object> recalculate(@PathVariable String eventId) {
		return Map.of("status", "accepted", "eventId", eventId);
	}
}
