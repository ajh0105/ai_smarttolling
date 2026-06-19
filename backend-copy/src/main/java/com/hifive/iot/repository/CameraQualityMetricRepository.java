package com.hifive.iot.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.hifive.iot.entity.CameraQualityMetric;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CameraQualityMetricRepository extends JpaRepository<CameraQualityMetric, Long> {

	List<CameraQualityMetric> findByCamera_CameraIdOrderByBucketStartAsc(Long cameraId);

	Optional<CameraQualityMetric> findTopByCamera_CameraIdOrderByBucketStartDesc(Long cameraId);

	@Query("""
		select metric
		from CameraQualityMetric metric
		where metric.camera.cameraId = :cameraId
		  and (:laneId is null or metric.laneId = :laneId)
		  and (:fromTime is null or metric.bucketEnd >= :fromTime)
		  and (:toTime is null or metric.bucketStart <= :toTime)
		order by metric.bucketStart asc
		""")
	List<CameraQualityMetric> findQualityMetrics(
		@Param("cameraId") Long cameraId,
		@Param("laneId") Integer laneId,
		@Param("fromTime") LocalDateTime from,
		@Param("toTime") LocalDateTime to,
		Pageable pageable
	);
}
