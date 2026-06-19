package com.hifive.iot.repository;

import java.util.List;

import com.hifive.iot.entity.MaintenanceAlert;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MaintenanceAlertRepository extends JpaRepository<MaintenanceAlert, Long> {

	List<MaintenanceAlert> findTop50ByOrderByCreatedAtDesc();

	List<MaintenanceAlert> findByStatusOrderByCreatedAtDesc(String status);

	List<MaintenanceAlert> findTop50ByStatusOrderByCreatedAtDesc(String status);
}
