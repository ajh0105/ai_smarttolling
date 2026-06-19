package com.hifive.iot.repository;

import java.util.List;

import com.hifive.iot.entity.MapMarkerRecord;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MapMarkerRepository extends JpaRepository<MapMarkerRecord, String> {

	List<MapMarkerRecord> findAllByOrderByMarkerNameAsc();
}
