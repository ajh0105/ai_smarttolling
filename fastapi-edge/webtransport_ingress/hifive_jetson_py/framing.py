from __future__ import annotations

from dataclasses import dataclass
import struct


HEADER_SIZE = 4
MAX_FRAME_SIZE = 4 * 1024 * 1024
EVENT_ID_SIZE = 2
MAX_EVENT_ID_SIZE = 512


class FrameError(RuntimeError):
    pass


@dataclass(frozen=True)
class EventFrame:
    event_id: str
    payload: bytes


def pack_frame(payload: bytes) -> bytes:
    if not payload:
        raise FrameError("empty payload")
    if len(payload) > MAX_FRAME_SIZE:
        raise FrameError(f"payload too large: {len(payload)}")
    return struct.pack("!I", len(payload)) + payload


def pack_event_frame(event_id: str, payload: bytes) -> bytes:
    event_id_bytes = event_id.encode("utf-8")
    if not event_id_bytes:
        raise FrameError("empty event_id")
    if len(event_id_bytes) > MAX_EVENT_ID_SIZE:
        raise FrameError(f"event_id too large: {len(event_id_bytes)}")
    body = struct.pack("!H", len(event_id_bytes)) + event_id_bytes + payload
    return pack_frame(body)


def unpack_ready_frame(buffer: bytearray) -> bytes | None:
    if len(buffer) < HEADER_SIZE:
        return None
    (size,) = struct.unpack("!I", bytes(buffer[:HEADER_SIZE]))
    if size <= 0 or size > MAX_FRAME_SIZE:
        raise FrameError(f"invalid frame size: {size}")
    if len(buffer) < HEADER_SIZE + size:
        return None
    payload = bytes(buffer[HEADER_SIZE : HEADER_SIZE + size])
    del buffer[: HEADER_SIZE + size]
    return payload


def unpack_ready_event_frame(buffer: bytearray) -> EventFrame | None:
    body = unpack_ready_frame(buffer)
    if body is None:
        return None
    if len(body) < EVENT_ID_SIZE:
        raise FrameError("missing event_id size")
    (event_id_size,) = struct.unpack("!H", body[:EVENT_ID_SIZE])
    if event_id_size <= 0 or event_id_size > MAX_EVENT_ID_SIZE:
        raise FrameError(f"invalid event_id size: {event_id_size}")
    start = EVENT_ID_SIZE
    end = start + event_id_size
    if len(body) <= end:
        raise FrameError("missing protobuf payload")
    event_id = body[start:end].decode("utf-8")
    payload = body[end:]
    return EventFrame(event_id=event_id, payload=payload)
