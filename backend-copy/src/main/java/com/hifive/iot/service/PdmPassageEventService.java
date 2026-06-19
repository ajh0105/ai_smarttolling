package com.hifive.iot.service;

import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hifive.iot.dto.IngestResultResponse;
import com.hifive.iot.dto.PdmPassageEventRequest;
import com.hifive.iot.entity.CameraDevice;
import com.hifive.iot.entity.PassageEventRecord;
import com.hifive.iot.repository.CameraDeviceRepository;
import com.hifive.iot.repository.CameraLaneMappingRepository;
import com.hifive.iot.repository.PassageEventRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PdmPassageEventService {

	private static final Set<String> EVENT_STATUSES = Set.of("ACCEPT", "REVIEW", "REJECT");

	private final PassageEventRepository passageEventRepository;
	private final CameraDeviceRepository cameraDeviceRepository;
	private final CameraLaneMappingRepository cameraLaneMappingRepository;
	private final PdmCompareService pdmCompareService;
	private final ObjectMapper objectMapper;

	@Transactional
	public IngestResultResponse save(PdmPassageEventRequest request) {
		validateRequest(request);

		String eventId = request.eventUuid().trim();
		byte[] payload = toJsonBytes(request);
		if (passageEventRepository.existsByEventId(eventId)) {
			return new IngestResultResponse(eventId, "duplicate", true, payload.length, LocalDateTime.now());
		}

		CameraDevice camera = cameraDeviceRepository.findByCameraCode(request.cameraCode().trim())
			.orElseThrow(() -> new IllegalArgumentException("camera not found"));
		validateCameraMapping(camera, request);

		PassageEventRecord saved = passageEventRepository.save(PassageEventRecord.demo(
			eventId,
			payload,
			request.deviceId().trim(),
			camera.getCameraCode(),
			camera.getDirection(),
			request.laneId(),
			request.plateText().trim(),
			request.plateConfidence(),
			request.candidateCount(),
			request.agreementRatio(),
			Boolean.TRUE.equals(request.needsReview()),
			request.eventStatus().trim().toUpperCase(),
			request.eventTime(),
			LocalDateTime.now()
		));
		pdmCompareService.match(saved, camera);

		return new IngestResultResponse(
			saved.getEventId(),
			"accepted",
			false,
			saved.getPayloadSizeBytes(),
			saved.getReceivedAt()
		);
	}

	private void validateRequest(PdmPassageEventRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("request body is required");
		}
		if (!StringUtils.hasText(request.eventUuid())) {
			throw new IllegalArgumentException("eventUuid is required");
		}
		if (!StringUtils.hasText(request.deviceId())) {
			throw new IllegalArgumentException("deviceId is required");
		}
		if (!StringUtils.hasText(request.cameraCode())) {
			throw new IllegalArgumentException("cameraCode is required");
		}
		if (request.laneId() == null) {
			throw new IllegalArgumentException("laneId is required");
		}
		if (!StringUtils.hasText(request.plateText())) {
			throw new IllegalArgumentException("plateText is required");
		}
		if (request.eventTime() == null) {
			throw new IllegalArgumentException("eventTime is required");
		}
		if (!StringUtils.hasText(request.eventStatus())
			|| !EVENT_STATUSES.contains(request.eventStatus().trim().toUpperCase())) {
			throw new IllegalArgumentException("eventStatus must be ACCEPT, REVIEW, or REJECT");
		}
	}

	private void validateCameraMapping(CameraDevice camera, PdmPassageEventRequest request) {
		if (StringUtils.hasText(camera.getSourceDeviceId())
			&& !camera.getSourceDeviceId().equals(request.deviceId().trim())) {
			throw new IllegalArgumentException("deviceId does not match camera mapping");
		}
		if (!cameraLaneMappingRepository.existsByCameraAndLaneId(camera, request.laneId())) {
			throw new IllegalArgumentException("camera does not manage the requested lane");
		}
	}

	private byte[] toJsonBytes(PdmPassageEventRequest request) {
		try {
			return objectMapper.writeValueAsBytes(request);
		} catch (JsonProcessingException exception) {
			throw new IllegalStateException("failed to serialize demo passage event", exception);
		}
	}
}
