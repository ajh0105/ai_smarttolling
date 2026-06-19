package com.hifive.iot.repository;

import java.util.List;

import com.hifive.iot.entity.TollZoneRecord;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TollZoneRepository extends JpaRepository<TollZoneRecord, Long> {

	List<TollZoneRecord> findByActiveTrueOrderByTollZoneIdAsc();
}
