package com.hifive.iot.service;

import java.time.LocalDateTime;
import java.util.List;

import com.hifive.iot.dto.PdmQualityMetricResponse;
import com.hifive.iot.repository.CameraDeviceRepository;
import com.hifive.iot.repository.CameraQualityMetricRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PdmQualityMetricService {

	private final CameraDeviceRepository cameraDeviceRepository;
	private final CameraQualityMetricRepository cameraQualityMetricRepository;

	public List<PdmQualityMetricResponse> getQualityMetrics(
		Long cameraId,
		Integer laneId,
		LocalDateTime from,
		LocalDateTime to
	) {
		if (cameraId == null) {
			throw new IllegalArgumentException("cameraId is required");
		}
		if (from != null && to != null && from.isAfter(to)) {
			throw new IllegalArgumentException("from must be before to");
		}
		if (cameraDeviceRepository.findById(cameraId).isEmpty()) {
			throw new IllegalArgumentException("camera not found");
		}

		// 필터 없을 때 단순 쿼리 사용 (PostgreSQL null 타입 추론 오류 방지)
		if (laneId == null && from == null && to == null) {
			return cameraQualityMetricRepository
				.findByCamera_CameraIdOrderByBucketStartAsc(cameraId)
				.stream()
				.map(PdmQualityMetricResponse::from)
				.toList();
		}

		return cameraQualityMetricRepository
			.findQualityMetrics(cameraId, laneId, from, to, Pageable.ofSize(100))
			.stream()
			.map(PdmQualityMetricResponse::from)
			.toList();
	}
}
