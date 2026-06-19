package com.hifive.iot.controller;

import java.util.List;

import com.hifive.iot.entity.EdgeStatusLatest;
import com.hifive.iot.service.EdgeStatusService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/operator/device-status")
public class OperatorDeviceStatusController {
	private final EdgeStatusService service;

	public OperatorDeviceStatusController(EdgeStatusService service) {
		this.service = service;
	}

	@GetMapping
	public List<EdgeStatusLatest> list() {
		return service.latest();
	}
}
