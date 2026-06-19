package com.hifive.iot.controller;

import java.util.List;

import com.hifive.iot.dto.EdgeStatusRequest;
import com.hifive.iot.entity.EdgeStatusLatest;
import com.hifive.iot.service.EdgeStatusService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EdgeStatusController {
	private final EdgeStatusService service;

	public EdgeStatusController(EdgeStatusService service) {
		this.service = service;
	}

	@PostMapping("/api/edge/status")
	@ResponseStatus(HttpStatus.CREATED)
	public EdgeStatusLatest create(@RequestBody EdgeStatusRequest request) {
		return service.save(request);
	}

	@GetMapping("/api/admin/edges")
	public List<EdgeStatusLatest> edges() {
		return service.latest();
	}

	@GetMapping("/api/admin/edges/{deviceId}")
	public EdgeStatusLatest edge(@PathVariable String deviceId) {
		return service.find(deviceId);
	}
}
