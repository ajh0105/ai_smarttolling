from __future__ import annotations

import asyncio
import json
import logging
from collections import defaultdict
from dataclasses import dataclass
from typing import Any

from .ack import ACK, REJECT, RETRY, encode_ack
from .framing import FrameError, unpack_ready_event_frame
from .ingress_state import IngressStats
from .spring_forwarder import SpringEvidenceForwarder, SpringForwarder, SpringJsonForwarder


logger = logging.getLogger("hifive.ingress")

NETWORK_TRANSITION_EVENT_PREFIX = "network-transition-"
EDGE_STATUS_EVENT_PREFIX = "edge-status-"
PREVIEW_FRAME_EVENT_PREFIX = "preview-frame-"
EVIDENCE_EVENT_PREFIX = "evidence:"


def _decode_network_transition_payload(payload: bytes) -> dict[str, Any]:
    try:
        decoded = json.loads(payload.decode("utf-8"))
    except Exception as exc:
        return {
            "type": "network_transition",
            "parse_error": str(exc),
            "raw": payload[:200].decode("utf-8", errors="replace"),
        }
    if isinstance(decoded, dict):
        return decoded
    return {"type": "network_transition", "value": decoded}


def _decode_edge_status_payload(payload: bytes) -> dict[str, Any]:
    try:
        decoded = json.loads(payload.decode("utf-8"))
    except Exception as exc:
        return {
            "type": "edge_status",
            "parse_error": str(exc),
            "raw": payload[:200].decode("utf-8", errors="replace"),
        }
    if isinstance(decoded, dict):
        return decoded
    return {"type": "edge_status", "value": decoded}


def _parse_evidence_event_id(frame_event_id: str) -> tuple[str, str] | None:
    if not frame_event_id.startswith(EVIDENCE_EVENT_PREFIX):
        return None
    remainder = frame_event_id[len(EVIDENCE_EVENT_PREFIX) :]
    kind, sep, event_id = remainder.partition(":")
    if not sep or not kind or not event_id:
        return None
    return kind, event_id


@dataclass
class IngressSession:
    session_id: int
    forwarder: SpringForwarder
    edge_status_forwarder: SpringJsonForwarder | None
    evidence_forwarder: SpringEvidenceForwarder | None
    stats: IngressStats
    buffers: dict[int, bytearray]

    async def receive_stream(self, stream_id: int, data: bytes, stream_ended: bool) -> bytes | None:
        self.buffers[stream_id].extend(data)
        if not stream_ended:
            return None

        buffer = self.buffers.pop(stream_id, bytearray())
        try:
            frame = unpack_ready_event_frame(buffer)
        except FrameError as exc:
            detail = str(exc)
            self.stats.mark_malformed(detail)
            return encode_ack(REJECT, "unknown", detail)
        if frame is None:
            detail = "incomplete frame"
            self.stats.mark_malformed(detail)
            return encode_ack(REJECT, "unknown", detail)

        self.stats.mark_received(frame.event_id, payload_bytes=len(frame.payload))
        logger.info(
            "WebTransport event received event_id=%s bytes=%d session=%s stream=%s",
            frame.event_id,
            len(frame.payload),
            self.session_id,
            stream_id,
        )
        if frame.event_id.startswith(PREVIEW_FRAME_EVENT_PREFIX) and frame.payload.startswith(b"\xff\xd8"):
            self.stats.mark_preview_frame(frame.event_id, frame.payload)
            logger.info(
                "Preview frame received event_id=%s bytes=%d session=%s stream=%s",
                frame.event_id,
                len(frame.payload),
                self.session_id,
                stream_id,
            )
            return encode_ack(ACK, frame.event_id, "preview frame stored")

        evidence = _parse_evidence_event_id(frame.event_id)
        if evidence is not None:
            evidence_kind, passage_event_id = evidence
            if not frame.payload.startswith(b"\xff\xd8"):
                detail = "evidence payload must be jpeg"
                self.stats.mark_reject(frame.event_id, detail)
                return encode_ack(REJECT, frame.event_id, detail)
            if self.evidence_forwarder is None:
                self.stats.mark_evidence(frame.event_id, evidence_kind, len(frame.payload))
                logger.info(
                    "Evidence received event_id=%s passage_event_id=%s kind=%s bytes=%d",
                    frame.event_id,
                    passage_event_id,
                    evidence_kind,
                    len(frame.payload),
                )
                return encode_ack(ACK, frame.event_id, "evidence stored in ingress only")
            result = await self.evidence_forwarder.forward(passage_event_id, evidence_kind, frame.payload)
            if result.accepted:
                self.stats.mark_evidence_forward(
                    passage_event_id,
                    evidence_kind,
                    "accepted",
                    result.status_code,
                    result.detail,
                )
                self.stats.mark_evidence(frame.event_id, evidence_kind, len(frame.payload))
                logger.info(
                    "Evidence ACK event_id=%s passage_event_id=%s kind=%s detail=%s",
                    frame.event_id,
                    passage_event_id,
                    evidence_kind,
                    result.detail,
                )
                return encode_ack(ACK, frame.event_id, result.detail)
            if result.retryable:
                self.stats.mark_evidence_forward(
                    passage_event_id,
                    evidence_kind,
                    "retry",
                    result.status_code,
                    result.detail,
                )
                self.stats.mark_retry(frame.event_id, result.detail)
                return encode_ack(RETRY, frame.event_id, result.detail)
            self.stats.mark_evidence_forward(
                passage_event_id,
                evidence_kind,
                "rejected",
                result.status_code,
                result.detail,
            )
            self.stats.mark_reject(frame.event_id, result.detail)
            return encode_ack(REJECT, frame.event_id, result.detail)

        if frame.event_id.startswith(NETWORK_TRANSITION_EVENT_PREFIX):
            detail = _decode_network_transition_payload(frame.payload)
            self.stats.mark_network_transition(
                frame.event_id,
                detail,
                payload_bytes=len(frame.payload),
            )
            logger.info(
                "Network transition event_id=%s outage_ms=%s route_before=%s route_after=%s recovered_event_id=%s",
                frame.event_id,
                detail.get("outage_ms", ""),
                detail.get("route_before_failure", ""),
                detail.get("route_after_recovery", ""),
                detail.get("recovered_event_id", ""),
            )
            return encode_ack(ACK, frame.event_id, "network transition logged")

        if frame.event_id.startswith(EDGE_STATUS_EVENT_PREFIX):
            detail = _decode_edge_status_payload(frame.payload)
            self.stats.mark_edge_status(
                frame.event_id,
                detail,
                payload_bytes=len(frame.payload),
            )
            if self.edge_status_forwarder is not None:
                result = await self.edge_status_forwarder.forward(detail, frame.event_id)
                if result.accepted:
                    self.stats.mark_edge_status_forward(
                        frame.event_id,
                        "accepted",
                        result.status_code,
                        result.detail,
                    )
                elif result.retryable:
                    self.stats.mark_edge_status_forward(
                        frame.event_id,
                        "retry",
                        result.status_code,
                        result.detail,
                    )
                else:
                    self.stats.mark_edge_status_forward(
                        frame.event_id,
                        "rejected",
                        result.status_code,
                        result.detail,
                    )
            logger.info(
                "Edge status event_id=%s device_id=%s active_path=%s",
                frame.event_id,
                detail.get("device_id", ""),
                (detail.get("transport") or {}).get("active_path", "") if isinstance(detail.get("transport"), dict) else "",
            )
            return encode_ack(ACK, frame.event_id, "edge status logged")

        result = await self.forwarder.forward(frame.payload, frame.event_id)
        if result.accepted:
            self.stats.mark_spring_forward(frame.event_id, "accepted", result.status_code, result.detail)
        elif result.retryable:
            self.stats.mark_spring_forward(frame.event_id, "retry", result.status_code, result.detail)
        else:
            self.stats.mark_spring_forward(frame.event_id, "rejected", result.status_code, result.detail)
        if result.accepted:
            self.stats.mark_ack(frame.event_id)
            logger.info("WebTransport event ACK event_id=%s detail=%s", frame.event_id, result.detail)
            return encode_ack(ACK, frame.event_id, result.detail)
        if result.retryable:
            self.stats.mark_retry(frame.event_id, result.detail)
            logger.info("WebTransport event RETRY event_id=%s detail=%s", frame.event_id, result.detail)
            return encode_ack(RETRY, frame.event_id, result.detail)
        self.stats.mark_reject(frame.event_id, result.detail)
        logger.info("WebTransport event REJECT event_id=%s detail=%s", frame.event_id, result.detail)
        return encode_ack(REJECT, frame.event_id, result.detail)

    def receive_datagram(self, data: bytes) -> None:
        try:
            frame = unpack_ready_event_frame(bytearray(data))
        except FrameError as exc:
            self.stats.mark_malformed(f"datagram: {exc}")
            return
        if frame is None:
            self.stats.mark_malformed("datagram: incomplete frame")
            return
        self.stats.mark_unreliable_datagram(
            frame.event_id,
            payload_bytes=len(frame.payload),
            payload=frame.payload,
        )
        logger.info(
            "WebTransport datagram received event_id=%s bytes=%d session=%s",
            frame.event_id,
            len(frame.payload),
            self.session_id,
        )


class WebTransportIngressFactory:
    def __init__(
        self,
        *,
        path: str,
        forwarder: SpringForwarder,
        edge_status_forwarder: SpringJsonForwarder | None = None,
        evidence_forwarder: SpringEvidenceForwarder | None = None,
        stats: IngressStats,
    ) -> None:
        self.path = path if path.startswith("/") else f"/{path}"
        self.forwarder = forwarder
        self.edge_status_forwarder = edge_status_forwarder
        self.evidence_forwarder = evidence_forwarder
        self.stats = stats

    def create(self):  # type: ignore[no-untyped-def]
        from aioquic.asyncio.protocol import QuicConnectionProtocol
        from aioquic.h3.connection import H3Connection
        from aioquic.h3.events import DatagramReceived, HeadersReceived, WebTransportStreamDataReceived
        from aioquic.quic.events import ProtocolNegotiated, QuicEvent

        expected_path = self.path
        forwarder = self.forwarder
        edge_status_forwarder = self.edge_status_forwarder
        evidence_forwarder = self.evidence_forwarder
        stats = self.stats

        class IngressProtocol(QuicConnectionProtocol):
            def __init__(self, *args, **kwargs) -> None:  # type: ignore[no-untyped-def]
                super().__init__(*args, **kwargs)
                self.http: H3Connection | None = None
                self.sessions: dict[int, IngressSession] = {}
                stats.mark_connection_open()

            def connection_lost(self, exc) -> None:  # type: ignore[no-untyped-def]
                stats.mark_connection_closed()
                super().connection_lost(exc)

            def quic_event_received(self, event: QuicEvent) -> None:
                if isinstance(event, ProtocolNegotiated):
                    self.http = H3Connection(self._quic, enable_webtransport=True)
                if self.http is None:
                    return
                for http_event in self.http.handle_event(event):
                    if isinstance(http_event, HeadersReceived):
                        self._handle_headers(http_event)
                    elif isinstance(http_event, WebTransportStreamDataReceived):
                        asyncio.create_task(self._handle_webtransport_stream(http_event))
                    elif isinstance(http_event, DatagramReceived):
                        self._handle_webtransport_datagram(http_event)

            def _handle_headers(self, event: HeadersReceived) -> None:
                assert self.http is not None
                headers = dict(event.headers)
                method = headers.get(b":method", b"").decode("ascii", errors="ignore")
                protocol = headers.get(b":protocol", b"").decode("ascii", errors="ignore")
                path = headers.get(b":path", b"").decode("utf-8", errors="ignore")
                if method == "CONNECT" and protocol == "webtransport" and path == expected_path:
                    self.http.send_headers(
                        stream_id=event.stream_id,
                        headers=[
                            (b":status", b"200"),
                            (b"sec-webtransport-http3-draft", b"draft02"),
                        ],
                    )
                    self.sessions[event.stream_id] = IngressSession(
                        session_id=event.stream_id,
                        forwarder=forwarder,
                        edge_status_forwarder=edge_status_forwarder,
                        evidence_forwarder=evidence_forwarder,
                        stats=stats,
                        buffers=defaultdict(bytearray),
                    )
                else:
                    self.http.send_headers(stream_id=event.stream_id, headers=[(b":status", b"404")])
                    self.http.send_data(stream_id=event.stream_id, data=b"", end_stream=True)
                self.transmit()

            async def _handle_webtransport_stream(self, event: WebTransportStreamDataReceived) -> None:
                session = self.sessions.get(event.session_id)
                if session is None:
                    return
                response = await session.receive_stream(event.stream_id, event.data, event.stream_ended)
                if response is None:
                    return
                assert self.http is not None
                ack_stream_id = self.http.create_webtransport_stream(event.session_id)
                self._quic.send_stream_data(stream_id=ack_stream_id, data=response, end_stream=True)
                self.transmit()

            def _handle_webtransport_datagram(self, event: DatagramReceived) -> None:
                session = self.sessions.get(event.stream_id)
                if session is None:
                    stats.mark_malformed(f"datagram: unknown session stream_id={event.stream_id}")
                    logger.info(
                        "WebTransport datagram dropped unknown_session stream_id=%s bytes=%d",
                        event.stream_id,
                        len(event.data),
                    )
                    return
                session.receive_datagram(event.data)

        return IngressProtocol
