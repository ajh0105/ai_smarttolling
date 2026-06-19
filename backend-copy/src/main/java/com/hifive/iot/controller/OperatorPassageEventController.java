package com.hifive.iot.controller;

import java.util.List;

import com.hifive.iot.dto.PassageEventResponse;
import com.hifive.iot.service.OperatorPassageService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/operator/passages")
public class OperatorPassageEventController {
	private final OperatorPassageService service;

	public OperatorPassageEventController(OperatorPassageService service) {
		this.service = service;
	}

	@GetMapping
	public List<PassageEventResponse> list() {
		return service.list();
	}

	@GetMapping("/{eventId}")
	public PassageEventResponse detail(@PathVariable String eventId) {
		return service.find(eventId);
	}
}
