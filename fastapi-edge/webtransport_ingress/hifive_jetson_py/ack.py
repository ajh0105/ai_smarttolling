from __future__ import annotations

import json
from dataclasses import dataclass
from typing import Any


ACK = "ack"
RETRY = "retry"
REJECT = "reject"


@dataclass(frozen=True)
class AckMessage:
    status: str
    event_id: str
    detail: str = ""

    @property
    def accepted(self) -> bool:
        return self.status == ACK


def encode_ack(status: str, event_id: str, detail: str = "") -> bytes:
    body = {"status": status, "event_id": event_id, "detail": detail}
    return json.dumps(body, separators=(",", ":")).encode("utf-8")


def decode_ack(data: bytes) -> AckMessage:
    try:
        body: dict[str, Any] = json.loads(data.decode("utf-8"))
    except Exception as exc:
        raise ValueError("invalid ack body") from exc
    status = str(body.get("status", ""))
    event_id = str(body.get("event_id", ""))
    detail = str(body.get("detail", ""))
    if status not in {ACK, RETRY, REJECT}:
        raise ValueError(f"invalid ack status: {status}")
    if not event_id:
        raise ValueError("missing ack event_id")
    return AckMessage(status=status, event_id=event_id, detail=detail)
