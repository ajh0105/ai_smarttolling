package com.hifive.iot.repository;

import java.util.List;
import java.util.Optional;

import com.hifive.iot.entity.SystemAlert;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemAlertRepository extends JpaRepository<SystemAlert, Long> {
	Optional<SystemAlert> findByAlertId(String alertId);
	List<SystemAlert> findTop50ByOrderByCreatedAtDesc();
	long countByStatus(String status);
}
