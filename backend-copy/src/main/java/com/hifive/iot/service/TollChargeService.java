package com.hifive.iot.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.hifive.iot.dto.PlateRecognitionRequest;
import com.hifive.iot.dto.TollChargeResponse;
import com.hifive.iot.dto.TollDecisionResponse;
import com.hifive.iot.entity.GpsTelemetryRecord;
import com.hifive.iot.entity.TollHistoryRecord;
import com.hifive.iot.entity.TollZoneRecord;
import com.hifive.iot.mapper.TollMapper;
import com.hifive.iot.repository.GpsTelemetryRepository;
import com.hifive.iot.repository.TollHistoryRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class TollChargeService {

	private final GpsTelemetryRepository gpsTelemetryRepository;
	private final TollHistoryRepository tollHistoryRepository;
	private final TollZoneService tollZoneService;

	public TollChargeService(
		GpsTelemetryRepository gpsTelemetryRepository,
		TollHistoryRepository tollHistoryRepository,
		TollZoneService tollZoneService
	) {
		this.gpsTelemetryRepository = gpsTelemetryRepository;
		this.tollHistoryRepository = tollHistoryRepository;
		this.tollZoneService = tollZoneService;
	}

	@Transactional
	public TollDecisionResponse evaluateGpsTelemetry(GpsTelemetryRecord telemetry) {
		if (!StringUtils.hasText(telemetry.getPlateNumber())) {
			return new TollDecisionResponse(false, "GPS telemetry saved without plate recognition", null);
		}
		return chargeIfInsideZone(
			telemetry.getPlateNumber(),
			telemetry,
			"gps_with_plate",
			null
		);
	}

	@Transactional
	public TollDecisionResponse recognizePlate(PlateRecognitionRequest request) {
		if (!StringUtils.hasText(request.plateNumber())) {
			throw new IllegalArgumentException("plateNumber is required");
		}
		if (!StringUtils.hasText(request.gpsDeviceId())) {
			throw new IllegalArgumentException("gpsDeviceId is required");
		}

		Optional<GpsTelemetryRecord> latestGps = gpsTelemetryRepository
			.findTopByGpsDeviceIdOrderByCapturedAtDesc(request.gpsDeviceId().trim());

		if (latestGps.isEmpty()) {
			return new TollDecisionResponse(false, "No GPS telemetry exists for device", null);
		}

		return chargeIfInsideZone(
			request.plateNumber().trim(),
			latestGps.get(),
			"plate_recognition",
			request.plateConfidence()
		);
	}

	@Transactional(readOnly = true)
	public List<TollChargeResponse> latestCharges() {
		return tollHistoryRepository.findTop50ByOrderByChargedAtDesc()
			.stream()
			.map(TollMapper::toChargeResponse)
			.toList();
	}

	private TollDecisionResponse chargeIfInsideZone(
		String plateNumber,
		GpsTelemetryRecord telemetry,
		String sourceType,
		Double plateConfidence
	) {
		if (telemetry.getLatitude() == null || telemetry.getLongitude() == null) {
			return new TollDecisionResponse(false, "Latest GPS telemetry has no fix", null);
		}

		Optional<TollZoneRecord> matchedZone = tollZoneService.activeZoneRecords()
			.stream()
			.filter(zone -> zone.contains(telemetry.getLatitude(), telemetry.getLongitude()))
			.findFirst();

		if (matchedZone.isEmpty()) {
			return new TollDecisionResponse(false, "GPS point is outside every toll zone", null);
		}

		if (tollHistoryRepository.existsByPlateNumberAndGpsTelemetryId(plateNumber, telemetry.getGpsTelemetryId())) {
			return new TollDecisionResponse(false, "Duplicate toll decision for same plate and GPS point", null);
		}

		TollZoneRecord zone = matchedZone.get();
		TollHistoryRecord saved = tollHistoryRepository.save(new TollHistoryRecord(
			plateNumber,
			telemetry.getGpsDeviceId(),
			telemetry.getLaneId(),
			zone.getTollZoneId(),
			telemetry.getGpsTelemetryId(),
			zone.getBaseFare(),
			"pending",
			sourceType,
			plateConfidence,
			LocalDateTime.now()
		));

		return new TollDecisionResponse(true, "GPS point is inside toll zone and plate is recognized", TollMapper.toChargeResponse(saved));
	}
}
