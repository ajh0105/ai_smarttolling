package com.hifive.iot.controller;

import java.util.List;

import com.hifive.iot.dto.IngressStatusRequest;
import com.hifive.iot.entity.IngressStatusHistory;
import com.hifive.iot.entity.IngressStatusLatest;
import com.hifive.iot.service.IngressStatusService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IngressStatusController {
	private final IngressStatusService service;

	public IngressStatusController(IngressStatusService service) {
		this.service = service;
	}

	@PostMapping("/api/ingress/status")
	@ResponseStatus(HttpStatus.CREATED)
	public IngressStatusLatest create(@RequestBody IngressStatusRequest request) {
		return service.save(request);
	}

	@GetMapping("/api/admin/ingress/status")
	public List<IngressStatusLatest> status() {
		return service.latest();
	}

	@GetMapping("/api/admin/ingress/events/recent")
	public List<IngressStatusHistory> recentEvents() {
		return service.recentEvents();
	}

	@GetMapping("/api/admin/ingress/transitions")
	public List<IngressStatusHistory> transitions() {
		return service.recentEvents();
	}
}
