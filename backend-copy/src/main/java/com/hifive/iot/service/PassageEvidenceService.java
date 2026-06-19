package com.hifive.iot.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import com.hifive.iot.dto.PassageEvidenceResponse;
import com.hifive.iot.entity.PassageEvidence;
import com.hifive.iot.repository.PassageEventRepository;
import com.hifive.iot.repository.PassageEvidenceRepository;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PassageEvidenceService {
	private static final Set<String> KINDS = Set.of("event_image", "plate_crop");
	private final PassageEvidenceRepository evidenceRepository;
	private final PassageEventRepository eventRepository;
	private final Path root = Path.of("uploads", "evidence");

	public PassageEvidenceService(PassageEvidenceRepository evidenceRepository, PassageEventRepository eventRepository) {
		this.evidenceRepository = evidenceRepository;
		this.eventRepository = eventRepository;
	}

	@Transactional
	public PassageEvidenceResponse save(String eventId, String kind, MultipartFile file) {
		validate(eventId, kind, file);
		if (!eventRepository.existsByEventId(eventId)) {
			throw new IllegalArgumentException("passage event not found");
		}
		String safeEventId = safe(eventId);
		Path dir = root.resolve(safeEventId);
		Path path = dir.resolve(kind + ".jpg");
		try {
			Files.createDirectories(dir);
			file.transferTo(path);
		} catch (IOException exception) {
			throw new IllegalStateException("failed to save evidence file");
		}
		String url = "/api/ingest/passage-evidence/" + safeEventId + "/" + kind + ".jpg";
		PassageEvidence evidence = evidenceRepository.findByEventId(eventId)
			.orElseGet(() -> new PassageEvidence(eventId, null, null, dir.toString()));
		if ("event_image".equals(kind)) {
			evidence.updateEventImageUrl(url, dir.toString());
		} else {
			evidence.updateCropImageUrl(url, dir.toString());
		}
		return PassageEvidenceResponse.from(evidenceRepository.save(evidence));
	}

	public Resource image(String safeEventId, String kind) {
		if (!KINDS.contains(kind)) {
			throw new IllegalArgumentException("invalid evidence kind");
		}
		Path path = root.resolve(safe(safeEventId)).resolve(kind + ".jpg");
		if (!Files.exists(path)) {
			throw new IllegalArgumentException("evidence image not found");
		}
		try {
			return new UrlResource(path.toUri());
		} catch (IOException exception) {
			throw new IllegalStateException("failed to load evidence image");
		}
	}

	private void validate(String eventId, String kind, MultipartFile file) {
		if (eventId == null || eventId.isBlank()) {
			throw new IllegalArgumentException("eventId is required");
		}
		if (!KINDS.contains(kind)) {
			throw new IllegalArgumentException("invalid evidence kind");
		}
		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("evidence file is required");
		}
		String contentType = file.getContentType();
		if (contentType != null && !contentType.equalsIgnoreCase("image/jpeg")) {
			throw new IllegalArgumentException("evidence file must be jpeg");
		}
	}

	private String safe(String value) {
		return value.replaceAll("[^A-Za-z0-9._-]", "_");
	}
}
