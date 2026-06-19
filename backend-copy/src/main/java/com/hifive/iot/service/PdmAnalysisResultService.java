package com.hifive.iot.service;

import java.time.LocalDateTime;
import java.util.Set;

import com.hifive.iot.dto.PdmAnalysisResultRequest;
import com.hifive.iot.dto.PdmAnalysisResultResponse;
import com.hifive.iot.entity.CameraDevice;
import com.hifive.iot.entity.MaintenanceAlert;
import com.hifive.iot.entity.PdmAnalysisResult;
import com.hifive.iot.repository.CameraDeviceRepository;
import com.hifive.iot.repository.CameraLaneMappingRepository;
import com.hifive.iot.repository.MaintenanceAlertRepository;
import com.hifive.iot.repository.PdmAnalysisResultRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PdmAnalysisResultService {

	private static final Set<String> RISK_LEVELS = Set.of("NORMAL", "WARNING", "CRITICAL");
	private static final Set<String> MODEL_TYPES = Set.of(
		"RULE_BASED",
		"ISOLATION_FOREST",
		"XGBOOST",
		"LSTM"
	);

	private final CameraDeviceRepository cameraDeviceRepository;
	private final CameraLaneMappingRepository cameraLaneMappingRepository;
	private final PdmAnalysisResultRepository pdmAnalysisResultRepository;
	private final MaintenanceAlertRepository maintenanceAlertRepository;

	@Transactional
	public PdmAnalysisResultResponse save(PdmAnalysisResultRequest request) {
		validateRequest(request);

		CameraDevice camera = cameraDeviceRepository.findById(request.cameraId())
			.orElseThrow(() -> new IllegalArgumentException("camera not found"));
		if (request.laneId() != null
			&& !cameraLaneMappingRepository.existsByCameraAndLaneId(camera, request.laneId())) {
			throw new IllegalArgumentException("camera does not manage the requested lane");
		}

		String riskLevel = request.riskLevel().trim().toUpperCase();
		PdmAnalysisResult saved = pdmAnalysisResultRepository.save(PdmAnalysisResult.builder()
			.camera(camera)
			.laneId(request.laneId())
			.analysisStart(request.analysisStart())
			.analysisEnd(request.analysisEnd())
			.healthScore(request.healthScore())
			.riskLevel(riskLevel)
			.modelType(request.modelType().trim().toUpperCase())
			.modelVersion(trimToNull(request.modelVersion()))
			.reasonCode(trimToNull(request.reasonCode()))
			.reasonText(trimToNull(request.reasonText()))
			.recommendedAction(trimToNull(request.recommendedAction()))
			.trendSummary(trimToNull(request.trendSummary()))
			.analyzedAt(LocalDateTime.now())
			.build());

		if (!"NORMAL".equals(riskLevel)) {
			maintenanceAlertRepository.save(createAlert(saved, camera));
		}

		return PdmAnalysisResultResponse.from(saved);
	}

	private void validateRequest(PdmAnalysisResultRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("request body is required");
		}
		if (!StringUtils.hasText(request.modelType())
			|| !MODEL_TYPES.contains(request.modelType().trim().toUpperCase())) {
			throw new IllegalArgumentException("unsupported modelType");
		}
		if (request.cameraId() == null) {
			throw new IllegalArgumentException("cameraId is required");
		}
		if (request.analysisStart() == null || request.analysisEnd() == null) {
			throw new IllegalArgumentException("analysis period is required");
		}
		if (request.analysisStart().isAfter(request.analysisEnd())) {
			throw new IllegalArgumentException("analysisStart must be before analysisEnd");
		}
		if (request.healthScore() == null
			|| request.healthScore() < 0
			|| request.healthScore() > 100) {
			throw new IllegalArgumentException("healthScore must be between 0 and 100");
		}
		if (!StringUtils.hasText(request.riskLevel())
			|| !RISK_LEVELS.contains(request.riskLevel().trim().toUpperCase())) {
			throw new IllegalArgumentException("riskLevel must be NORMAL, WARNING, or CRITICAL");
		}
	}

	private MaintenanceAlert createAlert(PdmAnalysisResult analysis, CameraDevice camera) {
		String message = StringUtils.hasText(analysis.getReasonText())
			? analysis.getReasonText()
			: camera.getCameraName() + " 품질 저하가 감지되었습니다.";

		return MaintenanceAlert.builder()
			.analysis(analysis)
			.camera(camera)
			.laneId(analysis.getLaneId())
			.riskLevel(analysis.getRiskLevel())
			.alertTitle(camera.getCameraName() + " " + analysis.getRiskLevel() + " 알림")
			.alertMessage(message)
			.reasonText(analysis.getReasonText())
			.recommendedAction(analysis.getRecommendedAction())
			.status("CREATED")
			.build();
	}

	private String trimToNull(String value) {
		return StringUtils.hasText(value) ? value.trim() : null;
	}
}
