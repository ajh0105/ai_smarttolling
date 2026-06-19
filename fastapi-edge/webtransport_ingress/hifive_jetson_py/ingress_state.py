from __future__ import annotations

import time
from dataclasses import dataclass, field
from threading import Lock
from typing import Any

VIDEO_FRAME_STALE_MS = 3000


@dataclass
class IngressStats:
    started_at_ns: int = 0
    received_events: int = 0
    acked_events: int = 0
    retry_events: int = 0
    rejected_events: int = 0
    malformed_frames: int = 0
    network_transition_events: int = 0
    edge_status_events: int = 0
    preview_frame_events: int = 0
    evidence_events: int = 0
    unreliable_datagram_events: int = 0
    edge_status_forward_ok: int = 0
    edge_status_forward_fail: int = 0
    ingress_status_forward_ok: int = 0
    ingress_status_forward_fail: int = 0
    evidence_forward_ok: int = 0
    evidence_forward_fail: int = 0
    active_connections: int = 0
    total_connections: int = 0
    last_event_id: str = ""
    last_error: str = ""
    last_payload_bytes: int = 0
    last_connection_closed_ms: int = 0
    spring_forward: dict[str, Any] = field(default_factory=dict)
    edge_status_forward: dict[str, Any] = field(default_factory=dict)
    ingress_status_forward: dict[str, Any] = field(default_factory=dict)
    evidence_forward: dict[str, Any] = field(default_factory=dict)
    last_network_transition: dict[str, Any] = field(default_factory=dict)
    last_edge_status: dict[str, Any] = field(default_factory=dict)
    last_evidence: dict[str, Any] = field(default_factory=dict)
    last_unreliable_datagram: dict[str, Any] = field(default_factory=dict)
    latest_preview_frame: dict[str, Any] = field(default_factory=dict)
    latest_preview_payload: bytes = field(default=b"", repr=False)
    video_receiver_events: int = 0
    video_frame_events: int = 0
    active_video_viewers: int = 0
    video_receiver: dict[str, Any] = field(default_factory=dict)
    latest_video_frame: dict[str, Any] = field(default_factory=dict)
    latest_video_frame_payload: bytes = field(default=b"", repr=False)
    max_recent_events: int = 20
    recent_events: list[dict[str, Any]] = field(default_factory=list)

    def __post_init__(self) -> None:
        if not self.started_at_ns:
            self.started_at_ns = time.time_ns()
        self._lock = Lock()

    def mark_received(self, event_id: str, payload_bytes: int = 0) -> None:
        with self._lock:
            self.received_events += 1
            self.last_event_id = event_id
            self.last_payload_bytes = int(payload_bytes)

    def mark_connection_open(self) -> None:
        with self._lock:
            self.active_connections += 1
            self.total_connections += 1

    def mark_connection_closed(self) -> None:
        with self._lock:
            self.active_connections = max(0, self.active_connections - 1)
            self.last_connection_closed_ms = int(time.time() * 1000)

    def mark_spring_forward(
        self,
        event_id: str,
        status: str,
        status_code: int | None,
        detail: str,
    ) -> None:
        with self._lock:
            self.spring_forward = {
                "event_id": event_id,
                "status": status,
                "status_code": status_code,
                "detail": detail,
                "ts_ms": int(time.time() * 1000),
            }

    def mark_edge_status_forward(
        self,
        event_id: str,
        status: str,
        status_code: int | None,
        detail: str,
    ) -> None:
        with self._lock:
            if status == "accepted":
                self.edge_status_forward_ok += 1
            else:
                self.edge_status_forward_fail += 1
            self.edge_status_forward = {
                "event_id": event_id,
                "status": status,
                "status_code": status_code,
                "detail": detail,
                "ts_ms": int(time.time() * 1000),
            }

    def mark_ingress_status_forward(
        self,
        event_id: str,
        status: str,
        status_code: int | None,
        detail: str,
    ) -> None:
        with self._lock:
            if status == "accepted":
                self.ingress_status_forward_ok += 1
            else:
                self.ingress_status_forward_fail += 1
            self.ingress_status_forward = {
                "event_id": event_id,
                "status": status,
                "status_code": status_code,
                "detail": detail,
                "ts_ms": int(time.time() * 1000),
            }

    def mark_evidence_forward(
        self,
        event_id: str,
        evidence_kind: str,
        status: str,
        status_code: int | None,
        detail: str,
    ) -> None:
        with self._lock:
            if status == "accepted":
                self.evidence_forward_ok += 1
            else:
                self.evidence_forward_fail += 1
            self.evidence_forward = {
                "event_id": event_id,
                "kind": evidence_kind,
                "status": status,
                "status_code": status_code,
                "detail": detail,
                "ts_ms": int(time.time() * 1000),
            }

    def mark_ack(self, event_id: str) -> None:
        with self._lock:
            self.acked_events += 1
            self.last_event_id = event_id
            self.last_error = ""
            self._append_recent_locked(event_id, "ack", "", self.last_payload_bytes)

    def mark_retry(self, event_id: str, detail: str) -> None:
        with self._lock:
            self.retry_events += 1
            self.last_event_id = event_id
            self.last_error = detail
            self._append_recent_locked(event_id, "retry", detail, self.last_payload_bytes)

    def mark_reject(self, event_id: str, detail: str) -> None:
        with self._lock:
            self.rejected_events += 1
            self.last_event_id = event_id
            self.last_error = detail
            self._append_recent_locked(event_id, "reject", detail, self.last_payload_bytes)

    def mark_malformed(self, detail: str) -> None:
        with self._lock:
            self.malformed_frames += 1
            self.last_error = detail
            self._append_recent_locked("unknown", "malformed", detail, 0)

    def mark_network_transition(
        self,
        event_id: str,
        detail: dict[str, Any],
        payload_bytes: int = 0,
    ) -> None:
        with self._lock:
            self.network_transition_events += 1
            self.acked_events += 1
            self.last_event_id = event_id
            self.last_error = ""
            self.last_payload_bytes = int(payload_bytes)
            self.last_network_transition = dict(detail)
            self._append_recent_locked(
                event_id,
                "network_transition",
                self._network_transition_detail(detail),
                self.last_payload_bytes,
            )

    def mark_edge_status(
        self,
        event_id: str,
        detail: dict[str, Any],
        payload_bytes: int = 0,
    ) -> None:
        with self._lock:
            self.edge_status_events += 1
            self.acked_events += 1
            self.last_event_id = event_id
            self.last_error = ""
            self.last_payload_bytes = int(payload_bytes)
            self.last_edge_status = dict(detail)
            self._append_recent_locked(
                event_id,
                "edge_status",
                self._edge_status_detail(detail),
                self.last_payload_bytes,
            )

    def mark_unreliable_datagram(
        self,
        event_id: str,
        payload_bytes: int = 0,
        payload: bytes | None = None,
    ) -> None:
        with self._lock:
            ts_ms = int(time.time() * 1000)
            self.unreliable_datagram_events += 1
            self.last_unreliable_datagram = {
                "event_id": event_id,
                "payload_bytes": int(payload_bytes),
                "ts_ms": ts_ms,
            }
            if payload and event_id.startswith("preview-frame-") and payload.startswith(b"\xff\xd8"):
                self.latest_preview_payload = bytes(payload)
                self.latest_preview_frame = {
                    "event_id": event_id,
                    "payload_bytes": int(payload_bytes),
                    "ts_ms": ts_ms,
                    "content_type": "image/jpeg",
                }
            self._append_recent_locked(
                event_id,
                "unreliable_datagram",
                "latest-only datagram received",
                int(payload_bytes),
            )

    def mark_preview_frame(self, event_id: str, payload: bytes) -> None:
        with self._lock:
            ts_ms = int(time.time() * 1000)
            self.preview_frame_events += 1
            self.last_event_id = event_id
            self.last_error = ""
            self.last_payload_bytes = len(payload)
            self.latest_preview_payload = bytes(payload)
            self.latest_preview_frame = {
                "event_id": event_id,
                "payload_bytes": len(payload),
                "ts_ms": ts_ms,
                "content_type": "image/jpeg",
                "transport": "webtransport_stream_latest",
            }
            self._append_recent_locked(
                event_id,
                "preview_frame",
                "latest preview frame received",
                len(payload),
            )

    def mark_evidence(self, event_id: str, evidence_kind: str, payload_bytes: int) -> None:
        with self._lock:
            ts_ms = int(time.time() * 1000)
            self.evidence_events += 1
            self.acked_events += 1
            self.last_event_id = event_id
            self.last_error = ""
            self.last_payload_bytes = int(payload_bytes)
            self.last_evidence = {
                "event_id": event_id,
                "kind": evidence_kind,
                "payload_bytes": int(payload_bytes),
                "ts_ms": ts_ms,
                "content_type": "image/jpeg",
            }
            self._append_recent_locked(
                event_id,
                "evidence",
                f"{evidence_kind} received",
                int(payload_bytes),
            )

    def mark_video_receiver(self, status: str, detail: dict[str, Any]) -> None:
        with self._lock:
            now_ms = int(time.time() * 1000)
            normalized = dict(detail)
            if status != "RUNNING":
                normalized.setdefault("connected", False)
                normalized.setdefault("streamStatus", status)
                normalized.setdefault("stream_status", status)
            self.video_receiver_events += 1
            self.video_receiver = {
                "status": status,
                "ts_ms": now_ms,
                **normalized,
            }
            self._append_recent_locked(
                f"video-receiver-{now_ms}",
                "video_receiver",
                status,
                0,
            )

    def mark_video_frame(self, payload: bytes, fps: float | None = None) -> None:
        with self._lock:
            now_ms = int(time.time() * 1000)
            self.video_frame_events += 1
            self.latest_video_frame_payload = bytes(payload)
            self.latest_video_frame = {
                "payload_bytes": len(payload),
                "ts_ms": now_ms,
                "content_type": "image/jpeg",
            }
            self.video_receiver = {
                **dict(self.video_receiver),
                "status": "RUNNING",
                "ts_ms": now_ms,
                "enabled": True,
                "connected": True,
                "streamStatus": "RUNNING",
                "stream_status": "RUNNING",
                "transport": "SRT/H264 -> MJPEG",
                "fps": round(float(fps), 3) if fps is not None else self.video_receiver.get("fps"),
                "lastFrameTsMs": now_ms,
                "last_frame_ts_ms": now_ms,
                "lastFrameAgeMs": 0,
                "last_frame_age_ms": 0,
                "activeViewers": self.active_video_viewers,
                "active_viewers": self.active_video_viewers,
                "last_error": "",
                "lastError": "",
            }

    def mark_video_viewer_open(self) -> None:
        with self._lock:
            self.active_video_viewers += 1
            self.video_receiver = {
                **dict(self.video_receiver),
                "activeViewers": self.active_video_viewers,
                "active_viewers": self.active_video_viewers,
            }

    def mark_video_viewer_closed(self) -> None:
        with self._lock:
            self.active_video_viewers = max(0, self.active_video_viewers - 1)
            self.video_receiver = {
                **dict(self.video_receiver),
                "activeViewers": self.active_video_viewers,
                "active_viewers": self.active_video_viewers,
            }

    def snapshot(self) -> dict[str, Any]:
        with self._lock:
            uptime_sec = max(0.0, (time.time_ns() - self.started_at_ns) / 1_000_000_000)
            latest_edge_status = dict(self.last_edge_status)
            latest_ts_ms = int(latest_edge_status.get("ts_ms", 0) or 0)
            latest_age_ms = max(0, int(time.time() * 1000) - latest_ts_ms) if latest_ts_ms > 0 else 0
            if latest_edge_status:
                latest_edge_status["status_age_ms"] = latest_age_ms
                latest_edge_status["stale"] = latest_ts_ms <= 0 or latest_age_ms > 5000
            now_ms = int(time.time() * 1000)
            video_receiver = dict(self.video_receiver)
            video_ts_ms = int(video_receiver.get("ts_ms", 0) or 0)
            if video_receiver:
                video_age_ms = max(0, now_ms - video_ts_ms) if video_ts_ms > 0 else 0
                video_receiver["status_age_ms"] = video_age_ms
                latest_frame = dict(self.latest_video_frame)
                latest_frame_ts_ms = int(
                    video_receiver.get("lastFrameTsMs")
                    or video_receiver.get("last_frame_ts_ms")
                    or latest_frame.get("ts_ms")
                    or 0
                )
                latest_frame_age_ms = max(0, now_ms - latest_frame_ts_ms) if latest_frame_ts_ms > 0 else 0
                status_upper = str(video_receiver.get("status", "")).upper()
                has_hls = bool(video_receiver.get("hlsPlaylistUrl") or video_receiver.get("hls_playlist_url"))
                frame_stale_ms = 10000 if has_hls else VIDEO_FRAME_STALE_MS
                frame_stale = latest_frame_ts_ms <= 0 or latest_frame_age_ms > frame_stale_ms
                receiver_stale_ms = 10000 if has_hls else 5000
                receiver_stale = video_ts_ms <= 0 or video_age_ms > receiver_stale_ms
                running = status_upper in {"RUNNING", "CONNECTED", "STREAMING"}
                stale = receiver_stale or frame_stale or not running or video_receiver.get("connected") is not True
                video_receiver["lastFrameTsMs"] = latest_frame_ts_ms
                video_receiver["last_frame_ts_ms"] = latest_frame_ts_ms
                video_receiver["lastFrameAgeMs"] = latest_frame_age_ms
                video_receiver["last_frame_age_ms"] = latest_frame_age_ms
                video_receiver["stale"] = stale
                if stale:
                    video_receiver["connected"] = False
                video_receiver["activeViewers"] = self.active_video_viewers
                video_receiver["active_viewers"] = self.active_video_viewers
            return {
                "uptime_sec": round(uptime_sec, 3),
                "received_events": self.received_events,
                "acked_events": self.acked_events,
                "retry_events": self.retry_events,
                "rejected_events": self.rejected_events,
                "malformed_frames": self.malformed_frames,
                "network_transition_events": self.network_transition_events,
                "edge_status_events": self.edge_status_events,
                "preview_frame_events": self.preview_frame_events,
                "evidence_events": self.evidence_events,
                "unreliable_datagram_events": self.unreliable_datagram_events,
                "video_receiver_events": self.video_receiver_events,
                "video_frame_events": self.video_frame_events,
                "edge_status_forward_ok": self.edge_status_forward_ok,
                "edge_status_forward_fail": self.edge_status_forward_fail,
                "ingress_status_forward_ok": self.ingress_status_forward_ok,
                "ingress_status_forward_fail": self.ingress_status_forward_fail,
                "evidence_forward_ok": self.evidence_forward_ok,
                "evidence_forward_fail": self.evidence_forward_fail,
                "active_connections": self.active_connections,
                "total_connections": self.total_connections,
                "last_event_id": self.last_event_id,
                "last_error": self.last_error,
                "last_payload_bytes": self.last_payload_bytes,
                "last_connection_closed_ms": self.last_connection_closed_ms,
                "spring_forward": dict(self.spring_forward),
                "edge_status_forward": dict(self.edge_status_forward),
                "ingress_status_forward": dict(self.ingress_status_forward),
                "evidence_forward": dict(self.evidence_forward),
                "last_network_transition": dict(self.last_network_transition),
                "last_edge_status": dict(self.last_edge_status),
                "last_evidence": dict(self.last_evidence),
                "last_unreliable_datagram": dict(self.last_unreliable_datagram),
                "latest_preview_frame": dict(self.latest_preview_frame),
                "video_receiver": video_receiver,
                "latest_video_frame": dict(self.latest_video_frame),
                "latest_edge_status": latest_edge_status,
                "recent_events": list(self.recent_events),
            }

    def get_latest_preview(self) -> tuple[bytes, dict[str, Any]]:
        with self._lock:
            return bytes(self.latest_preview_payload), dict(self.latest_preview_frame)

    def get_latest_video_frame(self) -> tuple[bytes, dict[str, Any]]:
        with self._lock:
            meta = dict(self.latest_video_frame)
            ts_ms = int(meta.get("ts_ms", 0) or 0)
            age_ms = max(0, int(time.time() * 1000) - ts_ms) if ts_ms > 0 else 0
            meta["age_ms"] = age_ms
            meta["stale"] = ts_ms <= 0 or age_ms > VIDEO_FRAME_STALE_MS
            if meta["stale"]:
                return b"", meta
            return bytes(self.latest_video_frame_payload), meta

    def _append_recent_locked(self, event_id: str, status: str, detail: str, payload_bytes: int) -> None:
        self.recent_events.append(
            {
                "ts_ms": int(time.time() * 1000),
                "event_id": event_id,
                "status": status,
                "detail": detail,
                "payload_bytes": int(payload_bytes),
            }
        )
        if len(self.recent_events) > self.max_recent_events:
            del self.recent_events[: len(self.recent_events) - self.max_recent_events]

    @staticmethod
    def _network_transition_detail(detail: dict[str, Any]) -> str:
        outage_ms = detail.get("outage_ms", "")
        route_before = detail.get("route_before_failure", "")
        route_after = detail.get("route_after_recovery", "")
        recovered_event_id = detail.get("recovered_event_id", "")
        return (
            f"outage_ms={outage_ms} "
            f"recovered_event_id={recovered_event_id} "
            f"route_before={route_before} "
            f"route_after={route_after}"
        )[:600]

    @staticmethod
    def _edge_status_detail(detail: dict[str, Any]) -> str:
        runtime = detail.get("runtime") if isinstance(detail, dict) else {}
        transport = detail.get("transport") if isinstance(detail, dict) else {}
        if not isinstance(runtime, dict):
            runtime = {}
        if not isinstance(transport, dict):
            transport = {}
        return (
            f"device_id={detail.get('device_id', '')} "
            f"camera_id={detail.get('camera_id', '')} "
            f"frames={runtime.get('processed_frames', '')} "
            f"events={runtime.get('sent_events', '')} "
            f"active_path={transport.get('active_path', '')}"
        )[:600]
