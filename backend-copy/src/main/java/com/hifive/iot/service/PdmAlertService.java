package com.hifive.iot.service;

import java.util.List;
import java.util.Set;

import com.hifive.iot.dto.PdmAlertResponse;
import com.hifive.iot.dto.PdmAlertStatusUpdateRequest;
import com.hifive.iot.entity.MaintenanceAlert;
import com.hifive.iot.repository.MaintenanceAlertRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PdmAlertService {

	private static final Set<String> ALERT_STATUSES = Set.of(
		"CREATED",
		"CHECKING",
		"RESOLVED",
		"FALSE_ALARM"
	);

	private final MaintenanceAlertRepository maintenanceAlertRepository;

	public List<PdmAlertResponse> getAlerts(String status) {
		if (!StringUtils.hasText(status)) {
			return maintenanceAlertRepository.findTop50ByOrderByCreatedAtDesc()
				.stream()
				.map(PdmAlertResponse::from)
				.toList();
		}

		String normalized = normalizeStatus(status);
		return maintenanceAlertRepository.findTop50ByStatusOrderByCreatedAtDesc(normalized)
			.stream()
			.map(PdmAlertResponse::from)
			.toList();
	}

	public PdmAlertResponse getAlert(Long alertId) {
		return PdmAlertResponse.from(findAlert(alertId));
	}

	@Transactional
	public PdmAlertResponse updateStatus(Long alertId, PdmAlertStatusUpdateRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("request body is required");
		}
		MaintenanceAlert alert = findAlert(alertId);
		alert.changeStatus(normalizeStatus(request.status()));
		return PdmAlertResponse.from(alert);
	}

	private MaintenanceAlert findAlert(Long alertId) {
		if (alertId == null) {
			throw new IllegalArgumentException("alertId is required");
		}
		return maintenanceAlertRepository.findById(alertId)
			.orElseThrow(() -> new IllegalArgumentException("alert not found"));
	}

	private String normalizeStatus(String status) {
		if (!StringUtils.hasText(status)) {
			throw new IllegalArgumentException("status is required");
		}
		String normalized = status.trim().toUpperCase();
		if (!ALERT_STATUSES.contains(normalized)) {
			throw new IllegalArgumentException("status must be CREATED, CHECKING, RESOLVED, or FALSE_ALARM");
		}
		return normalized;
	}
}
