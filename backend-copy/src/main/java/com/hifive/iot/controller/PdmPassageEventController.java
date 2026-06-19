package com.hifive.iot.controller;

import com.hifive.iot.dto.IngestResultResponse;
import com.hifive.iot.dto.PdmPassageEventRequest;
import com.hifive.iot.service.PdmPassageEventService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pdm/passage-events")
public class PdmPassageEventController {

	private final PdmPassageEventService pdmPassageEventService;

	@PostMapping
	public ResponseEntity<IngestResultResponse> create(@RequestBody PdmPassageEventRequest request) {
		IngestResultResponse response = pdmPassageEventService.save(request);
		HttpStatus status = response.duplicate() ? HttpStatus.CONFLICT : HttpStatus.CREATED;
		return ResponseEntity.status(status).body(response);
	}
}
