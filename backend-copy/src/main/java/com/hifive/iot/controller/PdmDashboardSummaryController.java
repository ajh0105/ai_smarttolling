package com.hifive.iot.controller;

import com.hifive.iot.dto.PdmDashboardSummaryResponse;
import com.hifive.iot.service.PdmDashboardSummaryService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pdm/dashboard")
public class PdmDashboardSummaryController {

	private final PdmDashboardSummaryService pdmDashboardSummaryService;

	@GetMapping("/summary")
	public ResponseEntity<PdmDashboardSummaryResponse> getSummary() {
		return ResponseEntity.ok(pdmDashboardSummaryService.getSummary());
	}
}
