package com.hifive.iot.controller;

import java.time.LocalDateTime;
import java.util.List;

import com.hifive.iot.dto.PdmQualityMetricResponse;
import com.hifive.iot.service.PdmQualityMetricService;

import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pdm/cameras")
public class PdmQualityMetricController {

	private final PdmQualityMetricService pdmQualityMetricService;

	@GetMapping("/{cameraId}/quality-metrics")
	public ResponseEntity<List<PdmQualityMetricResponse>> getQualityMetrics(
		@PathVariable Long cameraId,
		@RequestParam(required = false) Integer laneId,
		@RequestParam(required = false)
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
		@RequestParam(required = false)
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
	) {
		return ResponseEntity.ok(
			pdmQualityMetricService.getQualityMetrics(cameraId, laneId, from, to)
		);
	}
}
