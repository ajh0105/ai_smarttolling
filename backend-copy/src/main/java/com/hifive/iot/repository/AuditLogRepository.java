package com.hifive.iot.repository;

import java.util.List;

import com.hifive.iot.entity.AuditLog;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
	List<AuditLog> findTop50ByOrderByCreatedAtDesc();
}
