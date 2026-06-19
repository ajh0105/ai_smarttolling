package com.hifive.iot.repository;

import java.util.List;
import java.util.Optional;

import com.hifive.iot.entity.InspectionTask;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InspectionTaskRepository extends JpaRepository<InspectionTask, Long> {
	Optional<InspectionTask> findByTaskId(String taskId);
	List<InspectionTask> findByStatusOrderByCreatedAtDesc(String status);
	List<InspectionTask> findTop50ByOrderByCreatedAtDesc();
	long countByStatus(String status);
}
