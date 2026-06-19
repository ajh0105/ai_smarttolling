package com.hifive.iot.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import com.hifive.iot.dto.GpsTelemetryRequest;
import com.hifive.iot.dto.GpsTelemetryResponse;
import com.hifive.iot.entity.GpsTelemetryRecord;
import com.hifive.iot.mapper.GpsTelemetryMapper;
import com.hifive.iot.repository.GpsTelemetryRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class GpsTelemetryService {

	private static final DateTimeFormatter TERMUX_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private final GpsTelemetryRepository gpsTelemetryRepository;
	private final TollChargeService tollChargeService;

	public GpsTelemetryService(GpsTelemetryRepository gpsTelemetryRepository, TollChargeService tollChargeService) {
		this.gpsTelemetryRepository = gpsTelemetryRepository;
		this.tollChargeService = tollChargeService;
	}

	@Transactional
	public GpsTelemetryResponse save(GpsTelemetryRequest request) {
		validate(request);
		GpsTelemetryRecord saved = gpsTelemetryRepository.save(new GpsTelemetryRecord(
			defaultDeviceId(request.gpsDeviceId()),
			blankToNull(request.plateNumber()),
			blankToNull(request.edgeNodeId()),
			blankToNull(request.laneId()),
			request.trackId(),
			request.latitude(),
			request.longitude(),
			resolveSpeedKmh(request),
			defaultDouble(request.heading()),
			request.altitudeM(),
			request.accuracyM(),
			blankToNull(request.provider()),
			resolveFixStatus(request),
			blankToNull(request.statusMessage()),
			blankToNull(request.rawSentence()),
			resolveCapturedAt(request),
			LocalDateTime.now()
		));
		tollChargeService.evaluateGpsTelemetry(saved);
		return GpsTelemetryMapper.toResponse(saved);
	}

	@Transactional(readOnly = true)
	public List<GpsTelemetryResponse> latest() {
		return gpsTelemetryRepository.findTop50ByOrderByCapturedAtDesc()
			.stream()
			.map(GpsTelemetryMapper::toResponse)
			.toList();
	}

	private void validate(GpsTelemetryRequest request) {
		if (request.latitude() == null || request.longitude() == null) {
			if (isNoFix(request)) {
				return;
			}
			throw new IllegalArgumentException("latitude and longitude are required unless fixStatus is NO_FIX");
		}
		if (request.latitude() < -90 || request.latitude() > 90) {
			throw new IllegalArgumentException("latitude must be between -90 and 90");
		}
		if (request.longitude() < -180 || request.longitude() > 180) {
			throw new IllegalArgumentException("longitude must be between -180 and 180");
		}
	}

	private String defaultDeviceId(String gpsDeviceId) {
		return StringUtils.hasText(gpsDeviceId) ? gpsDeviceId.trim() : "PHONE-DEMO-01";
	}

	private String blankToNull(String value) {
		return StringUtils.hasText(value) ? value.trim() : null;
	}

	private Double defaultDouble(Double value) {
		return value == null ? 0.0 : value;
	}

	private Double resolveSpeedKmh(GpsTelemetryRequest request) {
		if (request.speedKmh() != null) {
			return request.speedKmh();
		}
		if (request.speed() != null) {
			return request.speed() * 3.6;
		}
		return 0.0;
	}

	private String resolveFixStatus(GpsTelemetryRequest request) {
		if (StringUtils.hasText(request.fixStatus())) {
			return request.fixStatus().trim().toUpperCase();
		}
		return request.latitude() == null || request.longitude() == null ? "NO_FIX" : "FIXED";
	}

	private boolean isNoFix(GpsTelemetryRequest request) {
		return StringUtils.hasText(request.fixStatus())
			&& "NO_FIX".equalsIgnoreCase(request.fixStatus().trim());
	}

	private LocalDateTime resolveCapturedAt(GpsTelemetryRequest request) {
		if (request.capturedAt() != null) {
			return request.capturedAt().atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
		}
		if (StringUtils.hasText(request.time())) {
			try {
				return LocalDateTime.parse(request.time().trim(), TERMUX_TIME_FORMAT);
			} catch (DateTimeParseException ignored) {
				return LocalDateTime.now();
			}
		}
		return LocalDateTime.now();
	}
}
