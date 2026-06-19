package com.hifive.iot.controller;

import java.util.List;

import com.hifive.iot.dto.PdmAlertResponse;
import com.hifive.iot.dto.PdmAlertStatusUpdateRequest;
import com.hifive.iot.service.PdmAlertService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pdm/alerts")
public class PdmAlertController {

	private final PdmAlertService pdmAlertService;

	@GetMapping
	public ResponseEntity<List<PdmAlertResponse>> getAlerts(
		@RequestParam(required = false) String status
	) {
		return ResponseEntity.ok(pdmAlertService.getAlerts(status));
	}

	@GetMapping("/{alertId}")
	public ResponseEntity<PdmAlertResponse> getAlert(@PathVariable Long alertId) {
		return ResponseEntity.ok(pdmAlertService.getAlert(alertId));
	}

	@PatchMapping("/{alertId}/status")
	public ResponseEntity<PdmAlertResponse> updateStatus(
		@PathVariable Long alertId,
		@RequestBody PdmAlertStatusUpdateRequest request
	) {
		return ResponseEntity.ok(pdmAlertService.updateStatus(alertId, request));
	}
}
