package com.hifive.iot.service;

import java.time.LocalDateTime;
import java.util.List;

import com.hifive.iot.dto.MapMarkerRequest;
import com.hifive.iot.dto.MapMarkerResponse;
import com.hifive.iot.entity.MapMarkerRecord;
import com.hifive.iot.repository.MapMarkerRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class MapMarkerService {

	private final MapMarkerRepository mapMarkerRepository;

	public MapMarkerService(MapMarkerRepository mapMarkerRepository) {
		this.mapMarkerRepository = mapMarkerRepository;
	}

	@Transactional(readOnly = true)
	public List<MapMarkerResponse> findMarkers() {
		return mapMarkerRepository.findAllByOrderByMarkerNameAsc().stream()
			.map(MapMarkerResponse::from)
			.toList();
	}

	@Transactional
	public List<MapMarkerResponse> saveMarkers(List<MapMarkerRequest> requests) {
		if (requests == null) {
			throw new IllegalArgumentException("markers is required");
		}

		for (MapMarkerRequest request : requests) {
			validate(request);
			MapMarkerRecord record = mapMarkerRepository.findById(request.name())
				.orElseGet(() -> new MapMarkerRecord(
					request.name().trim(),
					request.x(),
					request.y(),
					request.labelX(),
					request.labelY(),
					LocalDateTime.now()
				));
			record.update(request.x(), request.y(), request.labelX(), request.labelY());
			mapMarkerRepository.save(record);
		}

		return findMarkers();
	}

	private void validate(MapMarkerRequest request) {
		if (request == null || !StringUtils.hasText(request.name())) {
			throw new IllegalArgumentException("marker name is required");
		}
		validatePercent("x", request.x());
		validatePercent("y", request.y());
		validateOffset("labelX", request.labelX());
		validateOffset("labelY", request.labelY());
	}

	private void validatePercent(String fieldName, Double value) {
		if (value == null || value < 0 || value > 100) {
			throw new IllegalArgumentException(fieldName + " must be between 0 and 100");
		}
	}

	private void validateOffset(String fieldName, Double value) {
		if (value == null || value < -300 || value > 300) {
			throw new IllegalArgumentException(fieldName + " must be between -300 and 300");
		}
	}
}
