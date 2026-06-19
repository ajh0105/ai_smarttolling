package com.hifive.iot.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.hifive.iot.entity.PassageEventRecord;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PassageEventRepository extends JpaRepository<PassageEventRecord, Long> {

	boolean existsByEventId(String eventId);

	java.util.Optional<PassageEventRecord> findByEventId(String eventId);

	java.util.List<PassageEventRecord> findTop50ByOrderByEventTimeDesc();

	java.util.List<PassageEventRecord> findTop50ByEventTimeIsNotNullAndPlateTextIsNotNullAndLaneNoIsNotNullOrderByEventTimeDesc();

	List<PassageEventRecord> findByVehiclePassIdAndCameraRoleOrderByEventTimeAsc(
		String vehiclePassId,
		String cameraRole
	);

	List<PassageEventRecord> findByLaneNoAndCameraRoleAndEventTimeBetweenOrderByEventTimeAsc(
		Integer laneNo,
		String cameraRole,
		LocalDateTime from,
		LocalDateTime to
	);
}
