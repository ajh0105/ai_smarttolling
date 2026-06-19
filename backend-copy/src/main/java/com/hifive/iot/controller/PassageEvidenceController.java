package com.hifive.iot.controller;

import com.hifive.iot.dto.PassageEvidenceResponse;
import com.hifive.iot.service.PassageEvidenceService;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/ingest")
public class PassageEvidenceController {
	private final PassageEvidenceService service;

	public PassageEvidenceController(PassageEvidenceService service) {
		this.service = service;
	}

	@PostMapping(value = "/passage-evidence", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public PassageEvidenceResponse upload(
		@RequestHeader(value = "X-Event-Id", required = false) String headerEventId,
		@RequestHeader(value = "X-Evidence-Kind", required = false) String headerKind,
		@RequestParam(required = false) String eventId,
		@RequestParam(required = false) String kind,
		@RequestPart("file") MultipartFile file
	) {
		String resolvedEventId = headerEventId == null || headerEventId.isBlank() ? eventId : headerEventId;
		String resolvedKind = headerKind == null || headerKind.isBlank() ? kind : headerKind;
		return service.save(resolvedEventId, resolvedKind, file);
	}

	@GetMapping(value = "/passage-evidence/{eventId}/{kind}.jpg", produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity<Resource> image(@PathVariable String eventId, @PathVariable String kind) {
		return ResponseEntity.ok(service.image(eventId, kind));
	}
}
