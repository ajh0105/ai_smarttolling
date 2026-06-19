package com.hifive.iot.service;

import java.time.LocalDateTime;
import java.util.List;

import com.hifive.iot.dto.IngressStatusRequest;
import com.hifive.iot.entity.IngressStatusHistory;
import com.hifive.iot.entity.IngressStatusLatest;
import com.hifive.iot.repository.IngressStatusLatestRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IngressStatusService {
	private final IngressStatusLatestRepository latestRepository;

	public IngressStatusService(IngressStatusLatestRepository latestRepository) {
		this.latestRepository = latestRepository;
	}

	@Transactional
	public IngressStatusLatest save(IngressStatusRequest request) {
		String ingressId = request.ingressId() == null ? "python-webtransport-ingress" : request.ingressId();
		IngressStatusLatest latest = latestRepository.findByIngressId(ingressId)
			.orElseGet(() -> new IngressStatusLatest(ingressId));
		apply(latest, request);
		IngressStatusLatest saved = latestRepository.save(latest);
		return saved;
	}

	public List<IngressStatusLatest> latest() {
		return latestRepository.findAll();
	}

	public List<IngressStatusHistory> recentEvents() {
		return List.of();
	}

	private void apply(IngressStatusLatest status, IngressStatusRequest request) {
		status.apply(
			request.uptimeSec(),
			request.receivedEvents(),
			request.ackedEvents(),
			request.retryEvents(),
			request.rejectedEvents(),
			request.malformedFrames(),
			request.networkTransitionEvents(),
			request.edgeStatusEvents(),
			request.activeConnections(),
			request.totalConnections(),
			request.lastEventId(),
			request.lastPayloadBytes(),
			StatusValue.text(request.springForward(), "status"),
			StatusValue.integer(request.springForward(), "status_code"),
			StatusValue.text(request.edgeStatusForward(), "status"),
			StatusValue.text(request.latestEdgeStatus(), "device_id"),
			StatusValue.bool(request.latestEdgeStatus(), "stale"),
			LocalDateTime.now()
		);
	}
}
