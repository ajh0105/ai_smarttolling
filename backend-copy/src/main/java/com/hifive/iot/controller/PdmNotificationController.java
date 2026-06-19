package com.hifive.iot.controller;

import java.util.List;

import com.hifive.iot.dto.PdmNotificationResponse;
import com.hifive.iot.service.PdmNotificationService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pdm/notifications")
public class PdmNotificationController {

	private final PdmNotificationService pdmNotificationService;

	@GetMapping
	public ResponseEntity<List<PdmNotificationResponse>> getNotifications(
		@RequestParam(required = false) Long alertId
	) {
		return ResponseEntity.ok(pdmNotificationService.getNotifications(alertId));
	}
}
