package com.hifive.iot.service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.hifive.iot.dto.PdmCompareResultResponse;
import com.hifive.iot.entity.CameraCompareResult;
import com.hifive.iot.entity.CameraDevice;
import com.hifive.iot.entity.PassageEventRecord;
import com.hifive.iot.repository.CameraCompareResultRepository;
import com.hifive.iot.repository.CameraDeviceRepository;
import com.hifive.iot.repository.PassageEventRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PdmCompareService {

	private static final long MATCH_WINDOW_SECONDS = 10;
	private static final Set<String> RESULT_FILTERS = Set.of("MATCHED", "MISMATCHED");

	private final PassageEventRepository passageEventRepository;
	private final CameraDeviceRepository cameraDeviceRepository;
	private final CameraCompareResultRepository cameraCompareResultRepository;

	@Transactional
	public void match(PassageEventRecord event) {
		findCamera(event).ifPresent(camera -> match(event, camera));
	}

	@Transactional
	public void match(PassageEventRecord event, CameraDevice camera) {
		if (!canMatch(event, camera) || isAlreadyMatched(event, camera.getDirection())) {
			return;
		}

		String counterpartRole = "FRONT".equals(camera.getDirection()) ? "REAR" : "FRONT";
		List<PassageEventRecord> candidates = findCandidates(event, counterpartRole);
		Optional<PassageEventRecord> counterpart = candidates.stream()
			.filter(candidate -> Objects.equals(event.getLaneNo(), candidate.getLaneNo()))
			.filter(candidate -> candidate.getEventTime() != null)
			.filter(candidate -> !isAlreadyMatched(candidate, counterpartRole))
			.min(candidateComparator(event));

		if (counterpart.isEmpty()) {
			return;
		}

		PassageEventRecord counterpartEvent = counterpart.get();
		CameraDevice counterpartCamera = findCamera(counterpartEvent).orElse(null);
		if (counterpartCamera == null) {
			return;
		}

		PassageEventRecord frontEvent = "FRONT".equals(camera.getDirection()) ? event : counterpartEvent;
		PassageEventRecord rearEvent = "REAR".equals(camera.getDirection()) ? event : counterpartEvent;
		CameraDevice frontCamera = "FRONT".equals(camera.getDirection()) ? camera : counterpartCamera;
		CameraDevice rearCamera = "REAR".equals(camera.getDirection()) ? camera : counterpartCamera;
		boolean matched = samePlate(frontEvent, rearEvent);

		cameraCompareResultRepository.save(CameraCompareResult.builder()
			.eventGroupKey(createGroupKey(frontEvent, rearEvent))
			.frontEvent(frontEvent)
			.rearEvent(rearEvent)
			.laneId(event.getLaneNo())
			.frontCamera(frontCamera)
			.rearCamera(rearCamera)
			.frontPlateText(frontEvent.getPlateText())
			.rearPlateText(rearEvent.getPlateText())
			.isMatched(matched)
			.mismatchType(matched ? null : mismatchType(frontEvent, rearEvent))
			.confidenceGap(confidenceGap(frontEvent, rearEvent))
			.comparedAt(LocalDateTime.now())
			.build());
	}

	public List<PdmCompareResultResponse> getCompareResults(
		Integer laneId,
		String result,
		LocalDateTime from,
		LocalDateTime to
	) {
		if (from != null && to != null && from.isAfter(to)) {
			throw new IllegalArgumentException("from must be before to");
		}

		Boolean isMatched = null;
		if (StringUtils.hasText(result)) {
			String normalized = result.trim().toUpperCase();
			if (!RESULT_FILTERS.contains(normalized)) {
				throw new IllegalArgumentException("result must be MATCHED or MISMATCHED");
			}
			isMatched = "MATCHED".equals(normalized);
		}

		// 필터 없을 때 단순 쿼리 사용 (PostgreSQL null timestamp 타입 추론 오류 방지)
		if (laneId == null && isMatched == null && from == null && to == null) {
			return cameraCompareResultRepository.findTop50ByOrderByComparedAtDesc()
				.stream()
				.map(PdmCompareResultResponse::from)
				.toList();
		}

		return cameraCompareResultRepository
			.findCompareResults(laneId, isMatched, from, to, PageRequest.of(0, 50))
			.stream()
			.map(PdmCompareResultResponse::from)
			.toList();
	}

	private boolean canMatch(PassageEventRecord event, CameraDevice camera) {
		return event != null
			&& camera != null
			&& event.getLaneNo() != null
			&& event.getEventTime() != null
			&& ("FRONT".equals(camera.getDirection()) || "REAR".equals(camera.getDirection()));
	}

	private List<PassageEventRecord> findCandidates(PassageEventRecord event, String counterpartRole) {
		if (StringUtils.hasText(event.getVehiclePassId())) {
			return passageEventRepository.findByVehiclePassIdAndCameraRoleOrderByEventTimeAsc(
				event.getVehiclePassId(),
				counterpartRole
			);
		}

		return passageEventRepository.findByLaneNoAndCameraRoleAndEventTimeBetweenOrderByEventTimeAsc(
			event.getLaneNo(),
			counterpartRole,
			event.getEventTime().minusSeconds(MATCH_WINDOW_SECONDS),
			event.getEventTime().plusSeconds(MATCH_WINDOW_SECONDS)
		);
	}

	private Comparator<PassageEventRecord> candidateComparator(PassageEventRecord event) {
		return Comparator
			.comparing((PassageEventRecord candidate) -> !samePlate(event, candidate))
			.thenComparingLong(candidate ->
				Math.abs(Duration.between(event.getEventTime(), candidate.getEventTime()).toMillis()));
	}

	private boolean isAlreadyMatched(PassageEventRecord event, String role) {
		if ("FRONT".equals(role)) {
			return cameraCompareResultRepository.existsByFrontEvent(event);
		}
		return cameraCompareResultRepository.existsByRearEvent(event);
	}

	private Optional<CameraDevice> findCamera(PassageEventRecord event) {
		if (StringUtils.hasText(event.getCameraId())) {
			Optional<CameraDevice> camera = cameraDeviceRepository.findByCameraCode(event.getCameraId());
			if (camera.isPresent()) {
				return camera;
			}
		}
		if (StringUtils.hasText(event.getDeviceId())) {
			return cameraDeviceRepository.findBySourceDeviceId(event.getDeviceId());
		}
		return Optional.empty();
	}

	private boolean samePlate(PassageEventRecord first, PassageEventRecord second) {
		return StringUtils.hasText(first.getPlateText())
			&& first.getPlateText().equals(second.getPlateText());
	}

	private String mismatchType(PassageEventRecord frontEvent, PassageEventRecord rearEvent) {
		if (!StringUtils.hasText(frontEvent.getPlateText())) {
			return "FRONT_MISSING";
		}
		if (!StringUtils.hasText(rearEvent.getPlateText())) {
			return "REAR_MISSING";
		}
		return "PLATE_MISMATCH";
	}

	private Double confidenceGap(PassageEventRecord frontEvent, PassageEventRecord rearEvent) {
		if (frontEvent.getPlateConfidence() == null || rearEvent.getPlateConfidence() == null) {
			return null;
		}
		double gap = Math.abs(frontEvent.getPlateConfidence() - rearEvent.getPlateConfidence());
		return Math.round(gap * 10000.0) / 10000.0;
	}

	private String createGroupKey(PassageEventRecord frontEvent, PassageEventRecord rearEvent) {
		if (StringUtils.hasText(frontEvent.getVehiclePassId())
			&& frontEvent.getVehiclePassId().equals(rearEvent.getVehiclePassId())) {
			return frontEvent.getVehiclePassId();
		}

		String source = frontEvent.getEventId() + "|" + rearEvent.getEventId();
		return "GRP-" + UUID.nameUUIDFromBytes(source.getBytes(StandardCharsets.UTF_8));
	}
}
