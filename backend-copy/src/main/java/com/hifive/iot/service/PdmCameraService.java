package com.hifive.iot.service;

import java.util.List;
import java.util.Optional;

import com.hifive.iot.dto.PdmCameraDetailResponse;
import com.hifive.iot.dto.PdmCameraResponse;
import com.hifive.iot.entity.CameraDevice;
import com.hifive.iot.entity.CameraLaneMapping;
import com.hifive.iot.entity.CameraQualityMetric;
import com.hifive.iot.entity.PdmAnalysisResult;
import com.hifive.iot.repository.CameraDeviceRepository;
import com.hifive.iot.repository.CameraLaneMappingRepository;
import com.hifive.iot.repository.CameraQualityMetricRepository;
import com.hifive.iot.repository.PdmAnalysisResultRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PdmCameraService {

	private static final String UNKNOWN_RISK_LEVEL = "UNKNOWN";

	private final CameraDeviceRepository cameraDeviceRepository;
	private final CameraLaneMappingRepository cameraLaneMappingRepository;
	private final PdmAnalysisResultRepository pdmAnalysisResultRepository;
	private final CameraQualityMetricRepository cameraQualityMetricRepository;

	public List<PdmCameraResponse> getCameras() {
		return cameraDeviceRepository.findByIsActiveTrueOrderByCameraIdAsc()
			.stream()
			.map(this::toCameraResponse)
			.toList();
	}

	public PdmCameraDetailResponse getCameraDetail(Long cameraId) {
		CameraDevice camera = cameraDeviceRepository.findById(cameraId)
			.orElseThrow(() -> new IllegalArgumentException("camera not found"));
		List<CameraLaneMapping> mappings = findLaneMappings(camera);
		Optional<PdmAnalysisResult> analysis = latestAnalysis(camera);
		Optional<CameraQualityMetric> metric = cameraQualityMetricRepository
			.findTopByCamera_CameraIdOrderByBucketStartDesc(camera.getCameraId());

		return new PdmCameraDetailResponse(
			camera.getCameraId(),
			camera.getCameraCode(),
			camera.getCameraName(),
			camera.getDirection(),
			laneIds(mappings),
			laneNames(mappings),
			analysis.map(PdmAnalysisResult::getHealthScore).orElse(null),
			analysis.map(PdmAnalysisResult::getRiskLevel).orElse(UNKNOWN_RISK_LEVEL),
			analysis.map(PdmAnalysisResult::getModelType).orElse(null),
			analysis.map(PdmAnalysisResult::getModelVersion).orElse(null),
			metric.map(CameraQualityMetric::getAvgOcrConfidence).orElse(null),
			metric.map(CameraQualityMetric::getSuccessRate).orElse(null),
			metric.map(CameraQualityMetric::getMissingRate).orElse(null),
			metric.map(CameraQualityMetric::getMatchRate).orElse(null),
			metric.map(CameraQualityMetric::getMismatchRate).orElse(null),
			analysis.map(PdmAnalysisResult::getReasonText).orElse(null),
			analysis.map(PdmAnalysisResult::getRecommendedAction).orElse(null)
		);
	}

	private PdmCameraResponse toCameraResponse(CameraDevice camera) {
		List<CameraLaneMapping> mappings = findLaneMappings(camera);
		Optional<PdmAnalysisResult> analysis = latestAnalysis(camera);
		return new PdmCameraResponse(
			camera.getCameraId(),
			camera.getCameraCode(),
			camera.getCameraName(),
			camera.getDirection(),
			laneIds(mappings),
			laneNames(mappings),
			analysis.map(PdmAnalysisResult::getHealthScore).orElse(null),
			analysis.map(PdmAnalysisResult::getRiskLevel).orElse(UNKNOWN_RISK_LEVEL)
		);
	}

	private List<CameraLaneMapping> findLaneMappings(CameraDevice camera) {
		return cameraLaneMappingRepository.findByCamera_CameraIdOrderByLaneIdAsc(camera.getCameraId());
	}

	private Optional<PdmAnalysisResult> latestAnalysis(CameraDevice camera) {
		return pdmAnalysisResultRepository.findTopByCamera_CameraIdOrderByAnalyzedAtDesc(camera.getCameraId());
	}

	private List<Integer> laneIds(List<CameraLaneMapping> mappings) {
		return mappings.stream()
			.map(CameraLaneMapping::getLaneId)
			.toList();
	}

	private List<String> laneNames(List<CameraLaneMapping> mappings) {
		return mappings.stream()
			.map(mapping -> mapping.getLaneId() + "차로")
			.toList();
	}
}
