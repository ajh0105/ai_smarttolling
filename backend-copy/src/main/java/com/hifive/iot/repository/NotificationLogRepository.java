package com.hifive.iot.repository;

import java.util.List;

import com.hifive.iot.entity.NotificationLog;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {

	List<NotificationLog> findTop50ByOrderByCreatedAtDesc();

	List<NotificationLog> findByAlert_AlertIdOrderByCreatedAtDesc(Long alertId);
}
