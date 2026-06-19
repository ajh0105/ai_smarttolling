package com.hifive.iot.service;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

import com.hifive.iot.dto.IngestResultResponse;
import com.hifive.iot.dto.PassageEventPayload;
import com.hifive.iot.entity.InspectionTask;
import com.hifive.iot.entity.PassageEventRecord;
import com.hifive.iot.entity.SettlementCandidate;
import com.hifive.iot.repository.InspectionTaskRepository;
import com.hifive.iot.repository.PassageEventRepository;
import com.hifive.iot.repository.SettlementCandidateRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class PassageEventIngestService {

	private static final Pattern PLATE_PATTERN = Pattern.compile("^\\d{2,3}[가-힣]\\d{4}$");

	private final PassageEventRepository passageEventRepository;
	private final ProtobufPassageEventDecoder decoder;
	private final InspectionTaskRepository inspectionTaskRepository;
	private final SettlementCandidateRepository settlementCandidateRepository;
	private final PdmCompareService pdmCompareService;

	public PassageEventIngestService(
		PassageEventRepository passageEventRepository,
		ProtobufPassageEventDecoder decoder,
		InspectionTaskRepository inspectionTaskRepository,
		SettlementCandidateRepository settlementCandidateRepository,
		PdmCompareService pdmCompareService
	) {
		this.passageEventRepository = passageEventRepository;
		this.decoder = decoder;
		this.inspectionTaskRepository = inspectionTaskRepository;
		this.settlementCandidateRepository = settlementCandidateRepository;
		this.pdmCompareService = pdmCompareService;
	}

	@Transactional
	public IngestResultResponse ingest(String eventId, byte[] payload) {
		if (!StringUtils.hasText(eventId)) {
			throw new IllegalArgumentException("X-Event-Id header is required");
		}
		if (payload == null || payload.length == 0) {
			throw new IllegalArgumentException("protobuf payload is required");
		}

		String normalizedEventId = eventId.trim();
		if (passageEventRepository.existsByEventId(normalizedEventId)) {
			return new IngestResultResponse(normalizedEventId, "duplicate", true, payload.length, LocalDateTime.now());
		}

		PassageEventPayload decoded = decoder.decode(payload);
		validateDecoded(normalizedEventId, decoded);
		boolean needsReview = needsReview(decoded);
		String reviewReason = reviewReason(decoded, needsReview);
		PassageEventRecord saved = passageEventRepository.save(new PassageEventRecord(
			normalizedEventId,
			payload,
			decoded.deviceId(),
			decoded.cameraId(),
			decoded.cameraGroupId(),
			decoded.cameraRole(),
			decoded.timestamp(),
			decoded.direction(),
			decoded.laneNo(),
			decoded.globalLaneNo(),
			decoded.localTrackId(),
			decoded.vehiclePassId(),
			decoded.vehicleConfidence(),
			decoded.plateText(),
			decoded.plateConfidence(),
			decoded.candidateCount(),
			decoded.agreementRatio(),
			decoded.bboxX(),
			decoded.bboxY(),
			decoded.bboxW(),
			decoded.bboxH(),
			decoded.bboxCoord(),
			needsReview,
			reviewReason,
			decoded.schemaVersion(),
			"GPS_PENDING",
			needsReview ? "REVIEW_REQUIRED" : "PAYMENT_CANDIDATE",
			needsReview ? "PENDING" : "AUTO_ACCEPTED",
			LocalDateTime.now()
		));
		if (needsReview) {
			inspectionTaskRepository.save(new InspectionTask(
				saved.getEventId(),
				saved.getPlateText(),
				saved.getDeviceId(),
				saved.getLaneNo(),
				reviewReason,
				"WARNING"
			));
		}
		settlementCandidateRepository.save(new SettlementCandidate(
			saved.getEventId(),
			saved.getPlateText(),
			saved.getDeviceId(),
			saved.getLaneNo(),
			saved.getDirection(),
			saved.getEventTime(),
			saved.getGpsJudgementStatus(),
			saved.getPaymentDecision(),
			0,
			needsReview ? "REVIEW_REQUIRED" : "READY"
		));
		pdmCompareService.match(saved);
		return new IngestResultResponse(
			saved.getEventId(),
			"accepted",
			false,
			saved.getPayloadSizeBytes(),
			saved.getReceivedAt()
		);
	}

	private void validateDecoded(String headerEventId, PassageEventPayload decoded) {
		if (!headerEventId.equals(decoded.eventId())) {
			throw new IllegalArgumentException("X-Event-Id must match protobuf event_id");
		}
		if (!StringUtils.hasText(decoded.eventId()) || !StringUtils.hasText(decoded.plateText())
			|| decoded.timestamp() == null || !StringUtils.hasText(decoded.deviceId())
			|| !StringUtils.hasText(decoded.cameraId()) || decoded.laneNo() == null) {
			throw new IllegalArgumentException("invalid protobuf or required field missing");
		}
	}

	private boolean needsReview(PassageEventPayload decoded) {
		return Boolean.TRUE.equals(decoded.needsReview())
			|| !PLATE_PATTERN.matcher(decoded.plateText()).matches()
			|| (decoded.plateConfidence() != null && decoded.plateConfidence() < 0.85);
	}

	private String reviewReason(PassageEventPayload decoded, boolean needsReview) {
		if (StringUtils.hasText(decoded.reviewReason())) {
			return decoded.reviewReason();
		}
		if (!needsReview) {
			return null;
		}
		if (!PLATE_PATTERN.matcher(decoded.plateText()).matches()) {
			return "plate pattern mismatch";
		}
		if (decoded.plateConfidence() != null && decoded.plateConfidence() < 0.85) {
			return "plate confidence below threshold";
		}
		return "edge requested review";
	}
}
