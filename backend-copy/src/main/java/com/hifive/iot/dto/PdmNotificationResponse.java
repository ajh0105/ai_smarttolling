package com.hifive.iot.dto;

import java.time.LocalDateTime;

import com.hifive.iot.entity.NotificationLog;

public record PdmNotificationResponse(
	Long notificationId,
	Long alertId,
	String receiverEmail,
	String sendStatus,
	String failureReason,
	LocalDateTime sentAt,
	LocalDateTime createdAt
) {
	public static PdmNotificationResponse from(NotificationLog notification) {
		return new PdmNotificationResponse(
			notification.getNotificationId(),
			notification.getAlert().getAlertId(),
			notification.getReceiverEmail(),
			notification.getSendStatus(),
			notification.getFailureReason(),
			notification.getSentAt(),
			notification.getCreatedAt()
		);
	}
}
