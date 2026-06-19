package com.hifive.iot.controller;

import java.util.List;

import com.hifive.iot.dto.ActionRequest;
import com.hifive.iot.entity.InspectionTask;
import com.hifive.iot.service.InspectionTaskService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/operator/inspection/tasks")
public class OperatorInspectionController {
	private final InspectionTaskService service;

	public OperatorInspectionController(InspectionTaskService service) {
		this.service = service;
	}

	@GetMapping
	public List<InspectionTask> list(@RequestParam(required = false) String status) {
		return service.list(status);
	}

	@GetMapping("/{taskId}")
	public InspectionTask detail(@PathVariable String taskId) {
		return service.find(taskId);
	}

	@PostMapping("/{taskId}/approve")
	public InspectionTask approve(@PathVariable String taskId, @RequestBody(required = false) ActionRequest request) {
		return service.approve(taskId, request);
	}

	@PostMapping("/{taskId}/request-field-check")
	public InspectionTask fieldCheck(@PathVariable String taskId, @RequestBody(required = false) ActionRequest request) {
		return service.fieldCheck(taskId, request);
	}

	@PostMapping("/{taskId}/hold")
	public InspectionTask hold(@PathVariable String taskId, @RequestBody(required = false) ActionRequest request) {
		return service.hold(taskId, request);
	}
}
