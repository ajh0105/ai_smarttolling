package com.hifive.iot.config;

import java.time.LocalDateTime;
import java.util.List;

import com.hifive.iot.entity.CameraCompareResult;
import com.hifive.iot.entity.CameraDevice;
import com.hifive.iot.entity.CameraLaneMapping;
import com.hifive.iot.entity.CameraQualityMetric;
import com.hifive.iot.entity.MaintenanceAlert;
import com.hifive.iot.entity.PdmAnalysisResult;
import com.hifive.iot.repository.CameraCompareResultRepository;
import com.hifive.iot.repository.CameraDeviceRepository;
import com.hifive.iot.repository.CameraLaneMappingRepository;
import com.hifive.iot.repository.CameraQualityMetricRepository;
import com.hifive.iot.repository.MaintenanceAlertRepository;
import com.hifive.iot.repository.PdmAnalysisResultRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PdmDataInitializer implements ApplicationRunner {

	private final CameraDeviceRepository cameraDeviceRepository;
	private final CameraLaneMappingRepository cameraLaneMappingRepository;
	private final PdmAnalysisResultRepository pdmAnalysisResultRepository;
	private final MaintenanceAlertRepository maintenanceAlertRepository;
	private final CameraQualityMetricRepository cameraQualityMetricRepository;
	private final CameraCompareResultRepository cameraCompareResultRepository;

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		CameraDevice frontCamera = findOrCreateCamera("CAM-F-01", "전방 카메라", "FRONT");
		CameraDevice rearCamera  = findOrCreateCamera("CAM-R-01", "후방 카메라", "REAR");

		createLaneMappings(frontCamera);
		createLaneMappings(rearCamera);

		if (pdmAnalysisResultRepository.count() > 0) {
			return;
		}

		LocalDateTime now = LocalDateTime.now();

		// ── 현재 분석 결과 ────────────────────────────────────────────
		PdmAnalysisResult frontAnalysis = pdmAnalysisResultRepository.save(
			PdmAnalysisResult.builder()
				.camera(frontCamera)
				.laneId(null)
				.analysisStart(now.minusMinutes(5))
				.analysisEnd(now)
				.healthScore(91.0)
				.riskLevel("NORMAL")
				.modelType("RULE_BASED")
				.modelVersion("v1.0")
				.reasonCode("NORMAL_OP")
				.reasonText("정상 운영 중")
				.recommendedAction("정기 점검 유지")
				.trendSummary("안정적인 인식 품질 유지 중")
				.analyzedAt(now)
				.build()
		);

		PdmAnalysisResult rearAnalysis = pdmAnalysisResultRepository.save(
			PdmAnalysisResult.builder()
				.camera(rearCamera)
				.laneId(null)
				.analysisStart(now.minusMinutes(5))
				.analysisEnd(now)
				.healthScore(68.0)
				.riskLevel("WARNING")
				.modelType("RULE_BASED")
				.modelVersion("v1.0")
				.reasonCode("OCR_DEGRADATION")
				.reasonText("후방 카메라 OCR 신뢰도 저하")
				.recommendedAction("렌즈 청소 및 초점 상태 확인")
				.trendSummary("OCR 신뢰도가 30분간 지속 하락 추세")
				.analyzedAt(now)
				.build()
		);

		// ── 과거 분석 결과 (전방 일시 경고 → 이후 정상 회복) ──────────
		PdmAnalysisResult frontPrevAnalysis = pdmAnalysisResultRepository.save(
			PdmAnalysisResult.builder()
				.camera(frontCamera)
				.laneId(null)
				.analysisStart(now.minusMinutes(45))
				.analysisEnd(now.minusMinutes(40))
				.healthScore(82.0)
				.riskLevel("WARNING")
				.modelType("RULE_BASED")
				.modelVersion("v1.0")
				.reasonCode("SUCCESS_RATE_LOW")
				.reasonText("조명 환경 변화로 인한 인식 성공률 저하")
				.recommendedAction("모니터링 유지")
				.trendSummary("일시적 조명 변화 감지")
				.analyzedAt(now.minusMinutes(40))
				.build()
		);

		// ── 알림 ──────────────────────────────────────────────────────
		maintenanceAlertRepository.save(
			MaintenanceAlert.builder()
				.analysis(rearAnalysis)
				.camera(rearCamera)
				.laneId(1)
				.riskLevel("WARNING")
				.alertTitle("OCR 신뢰도 연속 하락")
				.alertMessage("후방 카메라 OCR 신뢰도가 30분간 지속적으로 하락하고 있습니다.")
				.reasonText("렌즈 오염 의심")
				.recommendedAction("렌즈 청소")
				.status("CREATED")
				.build()
		);

		maintenanceAlertRepository.save(
			MaintenanceAlert.builder()
				.analysis(rearAnalysis)
				.camera(rearCamera)
				.laneId(2)
				.riskLevel("WARNING")
				.alertTitle("전후방 일치율 임계값 미달")
				.alertMessage("전후방 번호판 일치율이 기준값(80%) 이하로 하락하였습니다.")
				.reasonText("카메라 정렬 이상")
				.recommendedAction("각도 재조정")
				.status("CHECKING")
				.build()
		);

		maintenanceAlertRepository.save(
			MaintenanceAlert.builder()
				.analysis(frontPrevAnalysis)
				.camera(frontCamera)
				.laneId(1)
				.riskLevel("WARNING")
				.alertTitle("인식 성공률 소폭 저하")
				.alertMessage("전방 카메라 인식 성공률이 일시적으로 저하되었습니다.")
				.reasonText("조명 환경 변화")
				.recommendedAction("모니터링 유지")
				.status("RESOLVED")
				.build()
		);

		// ── 품질 지표 추세 (7개 버킷 × 2 카메라, 5분 간격) ──────────────
		for (int i = 6; i >= 0; i--) {
			LocalDateTime bStart = now.minusMinutes((long)(i + 1) * 5);
			LocalDateTime bEnd   = now.minusMinutes((long) i * 5);
			int step = 6 - i; // 0(가장 과거) → 6(현재에 가까운)

			// 전방: 안정적 고품질 (최신 버킷으로 갈수록 소폭 개선)
			cameraQualityMetricRepository.save(CameraQualityMetric.builder()
				.camera(frontCamera)
				.laneId(null)
				.bucketStart(bStart)
				.bucketEnd(bEnd)
				.avgOcrConfidence(94.2 + step * 0.1)
				.successRate(96.8 + step * 0.05)
				.missingRate(Math.max(0.5, 1.2 - step * 0.01))
				.matchRate(94.1 + step * 0.05)
				.mismatchRate(Math.max(1.0, 2.4 - step * 0.05))
				.eventCount(42)
				.build()
			);

			// 후방: 하락 추세 (최신 버킷으로 갈수록 악화)
			double decline = step * 1.5;
			cameraQualityMetricRepository.save(CameraQualityMetric.builder()
				.camera(rearCamera)
				.laneId(null)
				.bucketStart(bStart)
				.bucketEnd(bEnd)
				.avgOcrConfidence(80.0 - decline)
				.successRate(88.0 - decline)
				.missingRate(3.5 + decline * 0.5)
				.matchRate(83.0 - decline)
				.mismatchRate(5.0 + decline * 0.5)
				.eventCount(40)
				.build()
			);
		}

		// ── 전후방 비교 결과 (5건) ─────────────────────────────────────
		LocalDateTime baseTime = now.minusMinutes(2);

		cameraCompareResultRepository.save(CameraCompareResult.builder()
			.eventGroupKey("GRP-0001").laneId(1)
			.frontCamera(frontCamera).rearCamera(rearCamera)
			.frontPlateText("123가4567").rearPlateText("123가4567")
			.isMatched(true).mismatchType(null).confidenceGap(0.02)
			.comparedAt(baseTime.minusSeconds(60))
			.build());

		cameraCompareResultRepository.save(CameraCompareResult.builder()
			.eventGroupKey("GRP-0002").laneId(2)
			.frontCamera(frontCamera).rearCamera(rearCamera)
			.frontPlateText("456나8901").rearPlateText("456나8901")
			.isMatched(true).mismatchType(null).confidenceGap(0.01)
			.comparedAt(baseTime.minusSeconds(75))
			.build());

		cameraCompareResultRepository.save(CameraCompareResult.builder()
			.eventGroupKey("GRP-0003").laneId(1)
			.frontCamera(frontCamera).rearCamera(rearCamera)
			.frontPlateText("789다2345").rearPlateText("789가2345")
			.isMatched(false).mismatchType("CHAR_DIFF").confidenceGap(0.18)
			.comparedAt(baseTime.minusSeconds(92))
			.build());

		cameraCompareResultRepository.save(CameraCompareResult.builder()
			.eventGroupKey("GRP-0004").laneId(2)
			.frontCamera(frontCamera).rearCamera(rearCamera)
			.frontPlateText("012라6789").rearPlateText(null)
			.isMatched(false).mismatchType("REAR_MISSING").confidenceGap(null)
			.comparedAt(baseTime.minusSeconds(106))
			.build());

		cameraCompareResultRepository.save(CameraCompareResult.builder()
			.eventGroupKey("GRP-0005").laneId(1)
			.frontCamera(frontCamera).rearCamera(rearCamera)
			.frontPlateText("345마0123").rearPlateText("345마0123")
			.isMatched(true).mismatchType(null).confidenceGap(0.03)
			.comparedAt(baseTime.minusSeconds(120))
			.build());
	}

	private CameraDevice findOrCreateCamera(String cameraCode, String cameraName, String direction) {
		return cameraDeviceRepository.findByCameraCode(cameraCode)
			.orElseGet(() -> cameraDeviceRepository.save(
				CameraDevice.builder()
					.cameraCode(cameraCode)
					.cameraName(cameraName)
					.direction(direction)
					.sourceDeviceId(null)
					.isActive(true)
					.build()
			));
	}

	private void createLaneMappings(CameraDevice camera) {
		for (Integer laneId : List.of(1, 2)) {
			if (!cameraLaneMappingRepository.existsByCameraAndLaneId(camera, laneId)) {
				cameraLaneMappingRepository.save(
					CameraLaneMapping.builder()
						.camera(camera)
						.laneId(laneId)
						.build()
				);
			}
		}
	}
}
