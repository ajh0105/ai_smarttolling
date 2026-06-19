package com.hifive.iot.service;

import java.util.List;
import java.util.Map;

import com.hifive.iot.repository.EdgeStatusLatestRepository;
import com.hifive.iot.repository.IngressStatusLatestRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SystemPipelineService {
	private final EdgeStatusLatestRepository edgeStatusLatestRepository;
	private final IngressStatusLatestRepository ingressStatusLatestRepository;
	private final VideoStreamService videoStreamService;

	public SystemPipelineService(EdgeStatusLatestRepository edgeStatusLatestRepository,
		IngressStatusLatestRepository ingressStatusLatestRepository, VideoStreamService videoStreamService) {
		this.edgeStatusLatestRepository = edgeStatusLatestRepository;
		this.ingressStatusLatestRepository = ingressStatusLatestRepository;
		this.videoStreamService = videoStreamService;
	}

	public Map<String, Object> getPipeline() {
		return Map.of(
			"nodes", List.of(
				Map.of(
					"name", "Jetson Edge",
					"status", edgeStatusLatestRepository.countByAliveTrue() > 0 ? "OK" : "WAITING",
					"aliveEdges", edgeStatusLatestRepository.countByAliveTrue(),
					"staleEdges", edgeStatusLatestRepository.countByStaleTrue()
				),
				Map.of(
					"name", "Python Ingress",
					"status", ingressStatusLatestRepository.count() > 0 ? "OK" : "WAITING",
					"latest", ingressStatusLatestRepository.findAll()
				),
				getVideoReceiverNode(),
				Map.of("name", "Spring Backend", "status", "OK"),
				Map.of("name", "PostgreSQL", "status", "UNKNOWN")
			)
		);
	}

	public Map<String, Object> getVideoReceiverNode() {
		try {
			return videoStreamService.getPipelineVideoNode();
		} catch (ResponseStatusException exception) {
			return Map.of(
				"name", "Video Receiver",
				"status", "WAITING",
				"connected", false,
				"lastError", exception.getReason() == null ? "Ingress video unavailable" : exception.getReason()
			);
		}
	}
}
