package com.hifive.iot.service;

import java.time.LocalDateTime;
import java.util.List;

import com.hifive.iot.dto.TollZoneRequest;
import com.hifive.iot.dto.TollZoneResponse;
import com.hifive.iot.entity.TollZoneRecord;
import com.hifive.iot.mapper.TollMapper;
import com.hifive.iot.repository.TollZoneRepository;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class TollZoneService {

	private final TollZoneRepository tollZoneRepository;

	public TollZoneService(TollZoneRepository tollZoneRepository) {
		this.tollZoneRepository = tollZoneRepository;
	}

	@PostConstruct
	@Transactional
	public void ensureDemoZone() {
		if (tollZoneRepository.count() > 0) {
			return;
		}
		tollZoneRepository.save(new TollZoneRecord(
			"RC-DEMO-TOLL-ZONE",
			37.4010,
			37.4016,
			127.1043,
			127.1051,
			1200,
			true,
			LocalDateTime.now()
		));
	}

	@Transactional(readOnly = true)
	public List<TollZoneRecord> activeZoneRecords() {
		return tollZoneRepository.findByActiveTrueOrderByTollZoneIdAsc();
	}

	@Transactional(readOnly = true)
	public List<TollZoneResponse> activeZones() {
		return activeZoneRecords().stream()
			.map(TollMapper::toZoneResponse)
			.toList();
	}

	@Transactional
	public TollZoneResponse create(TollZoneRequest request) {
		validate(request);
		ZoneBoundary boundary = resolveBoundary(request);
		TollZoneRecord saved = tollZoneRepository.save(new TollZoneRecord(
			request.zoneName().trim(),
			boundary.minLatitude(),
			boundary.maxLatitude(),
			boundary.minLongitude(),
			boundary.maxLongitude(),
			request.baseFare(),
			true,
			LocalDateTime.now()
		));
		return TollMapper.toZoneResponse(saved);
	}

	private void validate(TollZoneRequest request) {
		if (!StringUtils.hasText(request.zoneName())) {
			throw new IllegalArgumentException("zoneName is required");
		}
		if (!hasBoxBoundary(request) && !hasCornerBoundary(request)) {
			throw new IllegalArgumentException("zone boundary is required");
		}
		ZoneBoundary boundary = resolveBoundary(request);
		if (boundary.minLatitude() < -90 || boundary.maxLatitude() > 90) {
			throw new IllegalArgumentException("latitude must be between -90 and 90");
		}
		if (boundary.minLongitude() < -180 || boundary.maxLongitude() > 180) {
			throw new IllegalArgumentException("longitude must be between -180 and 180");
		}
		if (request.baseFare() == null || request.baseFare() < 0) {
			throw new IllegalArgumentException("baseFare must be zero or positive");
		}
	}

	private ZoneBoundary resolveBoundary(TollZoneRequest request) {
		if (hasCornerBoundary(request)) {
			double minLatitude = min(
				request.northWestLatitude(),
				request.northEastLatitude(),
				request.southEastLatitude(),
				request.southWestLatitude()
			);
			double maxLatitude = max(
				request.northWestLatitude(),
				request.northEastLatitude(),
				request.southEastLatitude(),
				request.southWestLatitude()
			);
			double minLongitude = min(
				request.northWestLongitude(),
				request.northEastLongitude(),
				request.southEastLongitude(),
				request.southWestLongitude()
			);
			double maxLongitude = max(
				request.northWestLongitude(),
				request.northEastLongitude(),
				request.southEastLongitude(),
				request.southWestLongitude()
			);
			return new ZoneBoundary(minLatitude, maxLatitude, minLongitude, maxLongitude);
		}
		return new ZoneBoundary(
			Math.min(request.minLatitude(), request.maxLatitude()),
			Math.max(request.minLatitude(), request.maxLatitude()),
			Math.min(request.minLongitude(), request.maxLongitude()),
			Math.max(request.minLongitude(), request.maxLongitude())
		);
	}

	private boolean hasBoxBoundary(TollZoneRequest request) {
		return request.minLatitude() != null && request.maxLatitude() != null
			&& request.minLongitude() != null && request.maxLongitude() != null;
	}

	private boolean hasCornerBoundary(TollZoneRequest request) {
		return request.northWestLatitude() != null && request.northWestLongitude() != null
			&& request.northEastLatitude() != null && request.northEastLongitude() != null
			&& request.southEastLatitude() != null && request.southEastLongitude() != null
			&& request.southWestLatitude() != null && request.southWestLongitude() != null;
	}

	private double min(Double... values) {
		double result = values[0];
		for (Double value : values) {
			result = Math.min(result, value);
		}
		return result;
	}

	private double max(Double... values) {
		double result = values[0];
		for (Double value : values) {
			result = Math.max(result, value);
		}
		return result;
	}

	private record ZoneBoundary(
		double minLatitude,
		double maxLatitude,
		double minLongitude,
		double maxLongitude
	) {
	}
}
