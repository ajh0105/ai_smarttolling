package com.hifive.iot.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import com.hifive.iot.dto.EdgeStatusRequest;
import com.hifive.iot.entity.EdgeStatusLatest;
import com.hifive.iot.repository.EdgeStatusLatestRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class EdgeStatusService {
	private final EdgeStatusLatestRepository latestRepository;

	public EdgeStatusService(EdgeStatusLatestRepository latestRepository) {
		this.latestRepository = latestRepository;
	}

	@Transactional
	public EdgeStatusLatest save(EdgeStatusRequest request) {
		if (!StringUtils.hasText(request.deviceId())) {
			throw new IllegalArgumentException("device_id is required");
		}
		LocalDateTime receivedAt = LocalDateTime.now();
		LocalDateTime statusTs = StatusValue.epochMillis(request.tsMs());
		long ageMs = Math.max(0, Duration.between(statusTs, receivedAt).toMillis());
		boolean stale = ageMs > 30_000;
		boolean sourceRunning = "RUNNING".equalsIgnoreCase(StatusValue.text(request.source(), "state"));
		Long processedOcr = StatusValue.lng(request.runtime(), "processed_ocr_tasks");
		Long droppedOcr = StatusValue.lng(request.runtime(), "dropped_ocr_tasks");
		double dropRatio = processedOcr == null || processedOcr == 0 ? 0.0 : (double) zero(droppedOcr) / processedOcr;
		Long spoolCount = StatusValue.lng(request.spool(), "count");
		boolean spoolWarning = zero(spoolCount) > 0;
		boolean alive = !stale;
		String health = !alive ? "DOWN" : (spoolWarning || dropRatio > 0.1 ? "WARN" : "OK");

		EdgeStatusLatest latest = latestRepository.findByDeviceId(request.deviceId())
			.orElseGet(() -> new EdgeStatusLatest(request.deviceId()));
		apply(latest, request, statusTs, ageMs, stale, alive, sourceRunning, dropRatio, spoolWarning, health, receivedAt);
		return latestRepository.save(latest);
	}

	public List<EdgeStatusLatest> latest() {
		return latestRepository.findAll();
	}

	public EdgeStatusLatest find(String deviceId) {
		return latestRepository.findByDeviceId(deviceId)
			.orElseThrow(() -> new IllegalArgumentException("edge status not found"));
	}

	private void apply(EdgeStatusLatest status, EdgeStatusRequest request, LocalDateTime statusTs, long ageMs,
		boolean stale, boolean alive, boolean sourceRunning, double dropRatio, boolean spoolWarning, String health,
		LocalDateTime receivedAt) {
		status.apply(
			request.cameraId(),
			request.cameraRole(),
			StatusValue.text(request.source(), "mode"),
			StatusValue.text(request.source(), "value"),
			StatusValue.text(request.source(), "state"),
			StatusValue.lng(request.runtime(), "uptime_ms"),
			StatusValue.decimal(request.runtime(), "latest_fps"),
			StatusValue.decimal(request.runtime(), "latest_yolo_ms"),
			StatusValue.decimal(request.runtime(), "latest_ocr_ms"),
			StatusValue.lng(request.runtime(), "processed_frames"),
			StatusValue.lng(request.runtime(), "processed_ocr_tasks"),
			StatusValue.lng(request.runtime(), "dropped_ocr_tasks"),
			StatusValue.lng(request.runtime(), "yolo_detections"),
			StatusValue.lng(request.runtime(), "sent_events"),
			StatusValue.text(request.runtime(), "last_error"),
			StatusValue.text(request.transport(), "active_path"),
			StatusValue.bool(request.transport(), "failover_enabled"),
			StatusValue.bool(request.transport(), "outage_active"),
			StatusValue.lng(request.spool(), "count"),
			statusTs,
			ageMs,
			stale,
			alive,
			sourceRunning,
			dropRatio,
			spoolWarning,
			health,
			receivedAt
		);
	}

	private long zero(Long value) {
		return value == null ? 0L : value;
	}
}
