package com.hifive.iot.repository;

import java.util.List;
import java.util.Optional;

import com.hifive.iot.entity.CameraDevice;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CameraDeviceRepository extends JpaRepository<CameraDevice, Long> {

	Optional<CameraDevice> findByCameraCode(String cameraCode);

	Optional<CameraDevice> findBySourceDeviceId(String sourceDeviceId);

	List<CameraDevice> findByIsActiveTrueOrderByCameraIdAsc();

	long countByIsActiveTrue();
}
