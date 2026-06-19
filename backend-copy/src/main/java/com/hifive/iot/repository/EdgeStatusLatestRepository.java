package com.hifive.iot.repository;

import java.util.Optional;

import com.hifive.iot.entity.EdgeStatusLatest;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EdgeStatusLatestRepository extends JpaRepository<EdgeStatusLatest, Long> {
	Optional<EdgeStatusLatest> findByDeviceId(String deviceId);
	long countByAliveTrue();
	long countByStaleTrue();
	long countBySourceRunningTrue();
}
