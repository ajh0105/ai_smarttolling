package com.hifive.iot.service;

import java.util.List;

import com.hifive.iot.dto.PdmNotificationResponse;
import com.hifive.iot.repository.NotificationLogRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PdmNotificationService {

	private final NotificationLogRepository notificationLogRepository;

	public List<PdmNotificationResponse> getNotifications(Long alertId) {
		if (alertId == null) {
			return notificationLogRepository.findTop50ByOrderByCreatedAtDesc()
				.stream()
				.map(PdmNotificationResponse::from)
				.toList();
		}

		return notificationLogRepository.findByAlert_AlertIdOrderByCreatedAtDesc(alertId)
			.stream()
			.map(PdmNotificationResponse::from)
			.toList();
	}
}
