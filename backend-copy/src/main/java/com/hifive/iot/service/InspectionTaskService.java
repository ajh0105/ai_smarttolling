package com.hifive.iot.service;

import java.util.List;

import com.hifive.iot.dto.ActionRequest;
import com.hifive.iot.entity.InspectionTask;
import com.hifive.iot.repository.InspectionTaskRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InspectionTaskService {
	private final InspectionTaskRepository repository;

	public InspectionTaskService(InspectionTaskRepository repository) {
		this.repository = repository;
	}

	public List<InspectionTask> list(String status) {
		return status == null ? repository.findTop50ByOrderByCreatedAtDesc() : repository.findByStatusOrderByCreatedAtDesc(status);
	}

	public InspectionTask find(String taskId) {
		return repository.findByTaskId(taskId).orElseThrow(() -> new IllegalArgumentException("inspection task not found"));
	}

	@Transactional
	public InspectionTask approve(String taskId, ActionRequest request) {
		InspectionTask task = find(taskId);
		task.resolve("APPROVED", actor(request), request == null ? null : request.memo());
		return task;
	}

	@Transactional
	public InspectionTask hold(String taskId, ActionRequest request) {
		InspectionTask task = find(taskId);
		task.resolve("HOLD", actor(request), request == null ? null : request.memo());
		return task;
	}

	@Transactional
	public InspectionTask fieldCheck(String taskId, ActionRequest request) {
		InspectionTask task = find(taskId);
		task.resolve("FIELD_CHECK_REQUESTED", actor(request), request == null ? null : request.memo());
		return task;
	}

	private String actor(ActionRequest request) {
		return request == null || request.actor() == null ? "operator" : request.actor();
	}
}
