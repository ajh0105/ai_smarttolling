package com.hifive.iot.repository;

import java.util.Optional;

import com.hifive.iot.entity.EdgeDevice;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EdgeDeviceRepository extends JpaRepository<EdgeDevice, Long> {
	Optional<EdgeDevice> findByDeviceId(String deviceId);
	long countByActiveTrue();
}
