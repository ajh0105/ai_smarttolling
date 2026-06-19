package com.hifive.iot.controller;

import java.lang.management.ManagementFactory;
import java.util.LinkedHashMap;
import java.util.Map;

import com.hifive.iot.repository.EdgeStatusLatestRepository;
import com.hifive.iot.repository.IngressStatusLatestRepository;
import com.hifive.iot.repository.PassageEventRepository;
import com.hifive.iot.repository.SystemAlertRepository;
import com.hifive.iot.service.SystemPipelineService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/system")
public class AdminSystemController {
	private final EdgeStatusLatestRepository edgeRepository;
	private final IngressStatusLatestRepository ingressRepository;
	private final PassageEventRepository passageEventRepository;
	private final SystemAlertRepository alertRepository;
	private final SystemPipelineService systemPipelineService;

	public AdminSystemController(EdgeStatusLatestRepository edgeRepository, IngressStatusLatestRepository ingressRepository,
		PassageEventRepository passageEventRepository, SystemAlertRepository alertRepository,
		SystemPipelineService systemPipelineService) {
		this.edgeRepository = edgeRepository;
		this.ingressRepository = ingressRepository;
		this.passageEventRepository = passageEventRepository;
		this.alertRepository = alertRepository;
		this.systemPipelineService = systemPipelineService;
	}

	@GetMapping("/summary")
	public Map<String, Object> summary() {
		Map<String, Object> response = new LinkedHashMap<>();
		response.put("totalEdges", edgeRepository.count());
		response.put("normalEdges", edgeRepository.countByAliveTrue());
		response.put("staleEdges", edgeRepository.countByStaleTrue());
		response.put("sourceRunningEdges", edgeRepository.countBySourceRunningTrue());
		response.put("ingressStatus", ingressRepository.findAll());
		response.put("backendStatus", "OK");
		response.put("backendUptimeMs", ManagementFactory.getRuntimeMXBean().getUptime());
		response.put("pipelineStatus", "Jetson -> Ingress -> Spring -> DB");
		response.put("videoReceiver", systemPipelineService.getVideoReceiverNode());
		response.put("passageEventCount", passageEventRepository.count());
		response.put("recentAlerts", alertRepository.findTop50ByOrderByCreatedAtDesc());
		return response;
	}

	@GetMapping("/pipeline")
	public Map<String, Object> pipeline() {
		return systemPipelineService.getPipeline();
	}
}
