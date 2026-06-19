package com.hifive.iot.repository;

import java.util.List;

import com.hifive.iot.entity.CameraDevice;
import com.hifive.iot.entity.CameraLaneMapping;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CameraLaneMappingRepository extends JpaRepository<CameraLaneMapping, Long> {

	List<CameraLaneMapping> findByCamera_CameraIdOrderByLaneIdAsc(Long cameraId);

	boolean existsByCameraAndLaneId(CameraDevice camera, Integer laneId);
}
