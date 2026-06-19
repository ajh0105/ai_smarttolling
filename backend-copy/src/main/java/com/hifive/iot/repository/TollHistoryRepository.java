package com.hifive.iot.repository;

import java.util.List;

import com.hifive.iot.entity.TollHistoryRecord;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TollHistoryRepository extends JpaRepository<TollHistoryRecord, Long> {

	boolean existsByPlateNumberAndGpsTelemetryId(String plateNumber, Long gpsTelemetryId);

	List<TollHistoryRecord> findTop50ByOrderByChargedAtDesc();
}
