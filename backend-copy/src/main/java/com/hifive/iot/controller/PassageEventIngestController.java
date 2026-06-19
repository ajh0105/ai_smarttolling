package com.hifive.iot.controller;

import com.hifive.iot.dto.IngestResultResponse;
import com.hifive.iot.service.PassageEventIngestService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ingest")
public class PassageEventIngestController {

	private static final String PROTOBUF_MEDIA_TYPE = "application/x-protobuf";

	private final PassageEventIngestService passageEventIngestService;

	public PassageEventIngestController(PassageEventIngestService passageEventIngestService) {
		this.passageEventIngestService = passageEventIngestService;
	}

	@PostMapping(
		value = "/passage-events",
		consumes = {PROTOBUF_MEDIA_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE}
	)
	public ResponseEntity<IngestResultResponse> ingest(
		@RequestHeader("X-Event-Id") String eventId,
		@RequestBody byte[] payload
	) {
		IngestResultResponse response = passageEventIngestService.ingest(eventId, payload);
		HttpStatus status = response.duplicate() ? HttpStatus.CONFLICT : HttpStatus.OK;
		return ResponseEntity.status(status).body(response);
	}
}
