package com.hifive.iot.controller;

import com.hifive.iot.dto.PdmAnalysisResultRequest;
import com.hifive.iot.dto.PdmAnalysisResultResponse;
import com.hifive.iot.service.PdmAnalysisResultService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pdm/analysis-results")
public class PdmAnalysisResultController {

	private final PdmAnalysisResultService pdmAnalysisResultService;

	@PostMapping
	public ResponseEntity<PdmAnalysisResultResponse> create(
		@RequestBody PdmAnalysisResultRequest request
	) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(pdmAnalysisResultService.save(request));
	}
}
