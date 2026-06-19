"""Spring Boot REST client for ingress forwarding."""
from __future__ import annotations

import asyncio
import json
from dataclasses import dataclass
from typing import Any
from urllib import error, request

from app.core.config import Settings


@dataclass
class SpringForwardError(Exception):
    status_code: int
    message: str


class SpringRestClient:
    def __init__(self, settings: Settings) -> None:
        self._settings = settings

    async def forward_passage_event(self, event_id: str, payload: bytes) -> dict[str, Any]:
        return await asyncio.to_thread(self._post_protobuf, event_id, payload)

    async def forward_plate_recognition(self, payload: dict[str, Any]) -> dict[str, Any]:
        return await asyncio.to_thread(
            self._post_json,
            "/api/toll/plate-recognitions",
            payload,
        )

    def _post_protobuf(self, event_id: str, payload: bytes) -> dict[str, Any]:
        url = self._settings.spring_rest_base_url.rstrip("/") + "/api/ingest/passage-events"
        req = request.Request(
            url,
            data=payload,
            headers={
                "Content-Type": "application/x-protobuf",
                "X-Event-Id": event_id,
            },
            method="POST",
        )
        try:
            with request.urlopen(req, timeout=self._settings.spring_rest_timeout_sec) as resp:
                body = resp.read().decode("utf-8")
                return json.loads(body) if body else {"status": "ack", "eventId": event_id}
        except error.HTTPError as exc:
            if exc.code == 409:
                return {"status": "ack", "eventId": event_id, "duplicate": True}
            if 400 <= exc.code < 500:
                raise SpringForwardError(status_code=400, message="Spring rejected passage event") from exc
            raise SpringForwardError(status_code=503, message="Spring ingest unavailable") from exc
        except (error.URLError, TimeoutError) as exc:
            raise SpringForwardError(status_code=503, message="Spring ingest unavailable") from exc

    def _post_json(self, path: str, payload: dict[str, Any]) -> dict[str, Any]:
        url = self._settings.spring_rest_base_url.rstrip("/") + path
        body = json.dumps(payload, ensure_ascii=False).encode("utf-8")
        req = request.Request(
            url,
            data=body,
            headers={"Content-Type": "application/json"},
            method="POST",
        )
        try:
            with request.urlopen(req, timeout=self._settings.spring_rest_timeout_sec) as resp:
                response_body = resp.read().decode("utf-8")
                return json.loads(response_body) if response_body else {"status": "ok"}
        except error.HTTPError as exc:
            if 400 <= exc.code < 500:
                raise SpringForwardError(status_code=400, message="Spring rejected plate recognition") from exc
            raise SpringForwardError(status_code=503, message="Spring toll API unavailable") from exc
        except (error.URLError, TimeoutError) as exc:
            raise SpringForwardError(status_code=503, message="Spring toll API unavailable") from exc
