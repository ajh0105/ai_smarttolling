from __future__ import annotations

import asyncio
import json
import urllib.error
import urllib.request
from dataclasses import dataclass
from typing import Any


@dataclass(frozen=True)
class ForwardResult:
    accepted: bool
    retryable: bool
    status_code: int | None
    detail: str = ""


@dataclass
class SpringForwarder:
    endpoint: str
    timeout_sec: float = 3.0
    ingest_key: str = ""
    dry_run: bool = False

    async def forward(self, payload: bytes, event_id: str) -> ForwardResult:
        if self.dry_run:
            return ForwardResult(True, False, 200, "dry-run")
        if not self.endpoint:
            return ForwardResult(False, True, None, "missing spring endpoint")

        try:
            return await self._forward_with_httpx(payload, event_id)
        except ImportError:
            return await asyncio.to_thread(self._forward_with_urllib, payload, event_id)

    async def _forward_with_httpx(self, payload: bytes, event_id: str) -> ForwardResult:
        import httpx

        headers = self._headers(event_id)
        async with httpx.AsyncClient(timeout=self.timeout_sec) as client:
            try:
                response = await client.post(self.endpoint, content=payload, headers=headers)
            except httpx.RequestError as exc:
                return ForwardResult(False, True, None, str(exc))
        return self._result_from_status(response.status_code, response.text[:200])

    def _forward_with_urllib(self, payload: bytes, event_id: str) -> ForwardResult:
        request = urllib.request.Request(
            self.endpoint,
            data=payload,
            method="POST",
            headers=self._headers(event_id),
        )
        try:
            with urllib.request.urlopen(request, timeout=self.timeout_sec) as response:
                body = response.read(200).decode("utf-8", errors="replace")
                return self._result_from_status(int(response.status), body)
        except urllib.error.HTTPError as exc:
            body = exc.read(200).decode("utf-8", errors="replace")
            return self._result_from_status(int(exc.code), body)
        except Exception as exc:
            return ForwardResult(False, True, None, str(exc))

    def _headers(self, event_id: str) -> dict[str, str]:
        headers = {
            "content-type": "application/x-protobuf",
            "x-event-id": event_id,
            "x-ingest-source": "python-webtransport-ingress",
        }
        if self.ingest_key:
            headers["x-internal-ingest-key"] = self.ingest_key
        return headers

    def _result_from_status(self, status_code: int, detail: str) -> ForwardResult:
        if 200 <= status_code < 300:
            return ForwardResult(True, False, status_code, detail)
        if status_code == 409:
            return ForwardResult(True, False, status_code, "duplicate")
        if status_code == 400 and "passage event not found" in detail.lower():
            return ForwardResult(False, True, status_code, detail)
        if 400 <= status_code < 500:
            return ForwardResult(False, False, status_code, detail)
        return ForwardResult(False, True, status_code, detail)


@dataclass
class SpringJsonForwarder:
    endpoint: str
    timeout_sec: float = 3.0
    ingest_key: str = ""
    dry_run: bool = False

    async def forward(self, payload: dict[str, Any], event_id: str) -> ForwardResult:
        if self.dry_run:
            return ForwardResult(True, False, 200, "dry-run")
        if not self.endpoint:
            return ForwardResult(False, True, None, "missing spring endpoint")

        try:
            return await self._forward_with_httpx(payload, event_id)
        except ImportError:
            return await asyncio.to_thread(self._forward_with_urllib, payload, event_id)

    async def _forward_with_httpx(self, payload: dict[str, Any], event_id: str) -> ForwardResult:
        import httpx

        headers = self._headers(event_id)
        async with httpx.AsyncClient(timeout=self.timeout_sec) as client:
            try:
                response = await client.post(
                    self.endpoint,
                    content=json.dumps(payload, ensure_ascii=False, separators=(",", ":")).encode("utf-8"),
                    headers=headers,
                )
            except httpx.RequestError as exc:
                return ForwardResult(False, True, None, str(exc))
        return self._result_from_status(response.status_code, response.text[:200])

    def _forward_with_urllib(self, payload: dict[str, Any], event_id: str) -> ForwardResult:
        request = urllib.request.Request(
            self.endpoint,
            data=json.dumps(payload, ensure_ascii=False, separators=(",", ":")).encode("utf-8"),
            method="POST",
            headers=self._headers(event_id),
        )
        try:
            with urllib.request.urlopen(request, timeout=self.timeout_sec) as response:
                body = response.read(200).decode("utf-8", errors="replace")
                return self._result_from_status(int(response.status), body)
        except urllib.error.HTTPError as exc:
            body = exc.read(200).decode("utf-8", errors="replace")
            return self._result_from_status(int(exc.code), body)
        except Exception as exc:
            return ForwardResult(False, True, None, str(exc))

    def _headers(self, event_id: str) -> dict[str, str]:
        headers = {
            "content-type": "application/json",
            "x-event-id": event_id,
            "x-ingest-source": "python-webtransport-ingress",
        }
        if self.ingest_key:
            headers["x-internal-ingest-key"] = self.ingest_key
        return headers

    def _result_from_status(self, status_code: int, detail: str) -> ForwardResult:
        if 200 <= status_code < 300:
            return ForwardResult(True, False, status_code, detail)
        if status_code == 409:
            return ForwardResult(True, False, status_code, "duplicate")
        if 400 <= status_code < 500:
            return ForwardResult(False, False, status_code, detail)
        return ForwardResult(False, True, status_code, detail)


@dataclass
class SpringEvidenceForwarder:
    endpoint: str
    timeout_sec: float = 3.0
    ingest_key: str = ""
    dry_run: bool = False

    async def forward(self, event_id: str, kind: str, payload: bytes) -> ForwardResult:
        if self.dry_run:
            return ForwardResult(True, False, 200, "dry-run")
        if not self.endpoint:
            return ForwardResult(False, True, None, "missing spring endpoint")
        try:
            return await self._forward_with_httpx(event_id, kind, payload)
        except ImportError:
            return await asyncio.to_thread(self._forward_with_urllib, event_id, kind, payload)

    async def _forward_with_httpx(self, event_id: str, kind: str, payload: bytes) -> ForwardResult:
        import httpx

        headers = self._headers(event_id, kind)
        files = {
            "file": (f"{kind}.jpg", payload, "image/jpeg"),
        }
        data = {
            "eventId": event_id,
            "kind": kind,
        }
        async with httpx.AsyncClient(timeout=self.timeout_sec) as client:
            try:
                response = await client.post(self.endpoint, data=data, files=files, headers=headers)
            except httpx.RequestError as exc:
                return ForwardResult(False, True, None, str(exc))
        return self._result_from_status(response.status_code, response.text[:200])

    def _forward_with_urllib(self, event_id: str, kind: str, payload: bytes) -> ForwardResult:
        boundary = "----hifive-evidence-boundary"
        body = self._multipart_body(boundary, event_id, kind, payload)
        headers = self._headers(event_id, kind)
        headers["content-type"] = f"multipart/form-data; boundary={boundary}"
        request = urllib.request.Request(
            self.endpoint,
            data=body,
            method="POST",
            headers=headers,
        )
        try:
            with urllib.request.urlopen(request, timeout=self.timeout_sec) as response:
                response_body = response.read(200).decode("utf-8", errors="replace")
                return self._result_from_status(int(response.status), response_body)
        except urllib.error.HTTPError as exc:
            response_body = exc.read(200).decode("utf-8", errors="replace")
            return self._result_from_status(int(exc.code), response_body)
        except Exception as exc:
            return ForwardResult(False, True, None, str(exc))

    def _headers(self, event_id: str, kind: str) -> dict[str, str]:
        headers = {
            "x-event-id": event_id,
            "x-evidence-kind": kind,
            "x-ingest-source": "python-webtransport-ingress",
        }
        if self.ingest_key:
            headers["x-internal-ingest-key"] = self.ingest_key
        return headers

    def _multipart_body(self, boundary: str, event_id: str, kind: str, payload: bytes) -> bytes:
        parts = [
            f"--{boundary}\r\n"
            'Content-Disposition: form-data; name="eventId"\r\n\r\n'
            f"{event_id}\r\n",
            f"--{boundary}\r\n"
            'Content-Disposition: form-data; name="kind"\r\n\r\n'
            f"{kind}\r\n",
            f"--{boundary}\r\n"
            f'Content-Disposition: form-data; name="file"; filename="{kind}.jpg"\r\n'
            "Content-Type: image/jpeg\r\n\r\n",
        ]
        return "".join(parts).encode("utf-8") + payload + f"\r\n--{boundary}--\r\n".encode("utf-8")

    def _result_from_status(self, status_code: int, detail: str) -> ForwardResult:
        if 200 <= status_code < 300:
            return ForwardResult(True, False, status_code, detail)
        if status_code == 409:
            return ForwardResult(True, False, status_code, "duplicate")
        if status_code == 400 and "passage event not found" in detail.lower():
            return ForwardResult(False, True, status_code, detail)
        if 400 <= status_code < 500:
            return ForwardResult(False, False, status_code, detail)
        return ForwardResult(False, True, status_code, detail)
