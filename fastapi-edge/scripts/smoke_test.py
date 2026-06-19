"""Smoke test for the Python Ingress operational API."""
from __future__ import annotations

import json
import urllib.request


BASE_URL = "http://localhost:8000"


def get(path: str) -> tuple[int, str]:
    with urllib.request.urlopen(BASE_URL + path, timeout=2) as resp:
        return resp.status, resp.read().decode("utf-8")


def main() -> None:
    for path in ("/healthz", "/status", "/metrics"):
        status, body = get(path)
        print(f"{path}: {status}")
        if body.strip().startswith("{"):
            print(json.dumps(json.loads(body), ensure_ascii=False, indent=2))
        else:
            print(body.strip())


if __name__ == "__main__":
    main()
