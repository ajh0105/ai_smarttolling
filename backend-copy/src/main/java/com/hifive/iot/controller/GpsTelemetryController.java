package com.hifive.iot.controller;

import java.util.List;

import com.hifive.iot.dto.GpsTelemetryRequest;
import com.hifive.iot.dto.GpsTelemetryResponse;
import com.hifive.iot.service.GpsTelemetryService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gps/telemetry")
public class GpsTelemetryController {

	private final GpsTelemetryService gpsTelemetryService;

	public GpsTelemetryController(GpsTelemetryService gpsTelemetryService) {
		this.gpsTelemetryService = gpsTelemetryService;
	}

	@GetMapping("/latest")
	public List<GpsTelemetryResponse> latest() {
		return gpsTelemetryService.latest();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public GpsTelemetryResponse create(@RequestBody GpsTelemetryRequest request) {
		return gpsTelemetryService.save(request);
	}
}
