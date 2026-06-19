package com.hifive.iot.service;

import java.util.List;

import com.hifive.iot.dto.PdmDashboardSummaryResponse;
import com.hifive.iot.entity.CameraDevice;
import com.hifive.iot.entity.PdmAnalysisResult;
import com.hifive.iot.repository.CameraDeviceRepository;
import com.hifive.iot.repository.PdmAnalysisResultRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PdmDashboardSummaryService {

	private final CameraDeviceRepository cameraDeviceRepository;
	private final PdmAnalysisResultRepository pdmAnalysisResultRepository;

	public PdmDashboardSummaryResponse getSummary() {
		List<CameraDevice> cameras = cameraDeviceRepository.findByIsActiveTrueOrderByCameraIdAsc();
		long normalCount = 0;
		long warningCount = 0;
		long criticalCount = 0;
		double healthScoreSum = 0.0;
		long healthScoreCount = 0;

		for (CameraDevice camera : cameras) {
			PdmAnalysisResult analysis = pdmAnalysisResultRepository
				.findTopByCamera_CameraIdOrderByAnalyzedAtDesc(camera.getCameraId())
				.orElse(null);
			if (analysis == null) {
				continue;
			}

			if ("NORMAL".equals(analysis.getRiskLevel())) {
				normalCount++;
			} else if ("WARNING".equals(analysis.getRiskLevel())) {
				warningCount++;
			} else if ("CRITICAL".equals(analysis.getRiskLevel())) {
				criticalCount++;
			}

			if (analysis.getHealthScore() != null) {
				healthScoreSum += analysis.getHealthScore();
				healthScoreCount++;
			}
		}

		return new PdmDashboardSummaryResponse(
			cameras.size(),
			normalCount,
			warningCount,
			criticalCount,
			average(healthScoreSum, healthScoreCount)
		);
	}

	private Double average(double sum, long count) {
		if (count == 0) {
			return 0.0;
		}
		return Math.round((sum / count) * 10.0) / 10.0;
	}
}
