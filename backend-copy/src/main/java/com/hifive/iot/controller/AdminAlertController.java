package com.hifive.iot.controller;

import java.util.List;

import com.hifive.iot.dto.ActionRequest;
import com.hifive.iot.entity.SystemAlert;
import com.hifive.iot.repository.SystemAlertRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/alerts")
public class AdminAlertController {
	private final SystemAlertRepository repository;

	public AdminAlertController(SystemAlertRepository repository) {
		this.repository = repository;
	}

	@GetMapping
	public List<SystemAlert> list() {
		return repository.findTop50ByOrderByCreatedAtDesc();
	}

	@GetMapping("/{alertId}")
	public SystemAlert detail(@PathVariable String alertId) {
		return find(alertId);
	}

	@PostMapping("/{alertId}/ack")
	public SystemAlert ack(@PathVariable String alertId) {
		SystemAlert alert = find(alertId);
		alert.acknowledge();
		return repository.save(alert);
	}

	@PostMapping("/{alertId}/assign")
	public SystemAlert assign(@PathVariable String alertId, @RequestBody ActionRequest request) {
		SystemAlert alert = find(alertId);
		alert.assign(request == null ? null : request.assignee());
		return repository.save(alert);
	}

	@PostMapping("/{alertId}/resolve")
	public SystemAlert resolve(@PathVariable String alertId) {
		SystemAlert alert = find(alertId);
		alert.resolve();
		return repository.save(alert);
	}

	private SystemAlert find(String alertId) {
		return repository.findByAlertId(alertId).orElseThrow(() -> new IllegalArgumentException("alert not found"));
	}
}
