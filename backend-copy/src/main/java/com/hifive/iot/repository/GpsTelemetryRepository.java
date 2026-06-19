package com.hifive.iot.repository;

import java.util.List;
import java.util.Optional;

import com.hifive.iot.entity.GpsTelemetryRecord;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GpsTelemetryRepository extends JpaRepository<GpsTelemetryRecord, Long> {

	List<GpsTelemetryRecord> findTop50ByOrderByCapturedAtDesc();

	Optional<GpsTelemetryRecord> findTopByGpsDeviceIdOrderByCapturedAtDesc(String gpsDeviceId);
}
