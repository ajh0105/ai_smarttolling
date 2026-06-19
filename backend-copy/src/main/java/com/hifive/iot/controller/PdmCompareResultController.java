package com.hifive.iot.controller;

import java.time.LocalDateTime;
import java.util.List;

import com.hifive.iot.dto.PdmCompareResultResponse;
import com.hifive.iot.service.PdmCompareService;

import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pdm/compare-results")
public class PdmCompareResultController {

	private final PdmCompareService pdmCompareService;

	@GetMapping
	public ResponseEntity<List<PdmCompareResultResponse>> getCompareResults(
		@RequestParam(required = false) Integer laneId,
		@RequestParam(required = false) String result,
		@RequestParam(required = false)
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
		@RequestParam(required = false)
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
	) {
		return ResponseEntity.ok(pdmCompareService.getCompareResults(laneId, result, from, to));
	}
}
